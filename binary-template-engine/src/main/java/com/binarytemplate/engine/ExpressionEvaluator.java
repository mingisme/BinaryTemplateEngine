package com.binarytemplate.engine;

import com.google.gson.JsonObject;
import com.jayway.jsonpath.JsonPath;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Expression evaluator using FreeMarker and JsonPath for powerful template expressions
 * 
 * Supports:
 * - FreeMarker expressions: ${field}, ${field * 2}, ${field?upper_case}, etc.
 * - JsonPath queries: $.user.name, $.items[0].price, etc.
 * - Complex logic: conditionals, loops, built-in functions
 */
public class ExpressionEvaluator {
    
    private final Configuration freemarkerConfig;
    
    public ExpressionEvaluator() {
        this.freemarkerConfig = new Configuration(Configuration.VERSION_2_3_32);
        this.freemarkerConfig.setNumberFormat("computer");
        this.freemarkerConfig.setBooleanFormat("c");  // Use "true"/"false" for computer output
    }
    
    /**
     * Evaluate an expression against JSON data
     * 
     * @param expression Expression to evaluate (supports FreeMarker syntax and JsonPath)
     * @param data JSON data as JsonObject
     * @return Evaluated result
     */
    public Object evaluate(String expression, JsonObject data) {
        if (expression == null || expression.isEmpty()) {
            return null;
        }
        
        // Check if it's a JsonPath expression (starts with $.)
        if (expression.startsWith("$.")) {
            return evaluateJsonPath(expression, data);
        }
        
        // Check if it's a FreeMarker expression ${...}
        if (expression.contains("${")) {
            return evaluateFreeMarker(expression, data);
        }
        
        // Return as literal value
        return expression;
    }
    
    /**
     * Evaluate JsonPath expression
     */
    private Object evaluateJsonPath(String jsonPath, JsonObject data) {
        try {
            String jsonString = data.toString();
            return JsonPath.read(jsonString, jsonPath);
        } catch (Exception e) {
            // If JsonPath fails, return null
            return null;
        }
    }
    
    /**
     * Evaluate FreeMarker template expression
     */
    private Object evaluateFreeMarker(String expression, JsonObject data) {
        try {
            // Convert JsonObject to Map for FreeMarker
            Map<String, Object> dataModel = jsonToMap(data);
            
            // Create template from expression
            Template template = new Template("expr", new StringReader(expression), freemarkerConfig);
            
            // Process template
            StringWriter writer = new StringWriter();
            template.process(dataModel, writer);
            
            String result = writer.toString();
            
            // Try to convert result to appropriate type
            return parseResult(result);
            
        } catch (IOException | TemplateException e) {
            // If FreeMarker fails, return the original expression
            return expression;
        }
    }
    
    /**
     * Convert JsonObject to Map for FreeMarker
     */
    private Map<String, Object> jsonToMap(JsonObject json) {
        Map<String, Object> map = new HashMap<>();
        
        json.entrySet().forEach(entry -> {
            String key = entry.getKey();
            Object value = extractValue(entry.getValue());
            map.put(key, value);
        });
        
        return map;
    }
    
    /**
     * Extract value from JsonElement
     */
    private Object extractValue(com.google.gson.JsonElement element) {
        if (element.isJsonNull()) {
            return null;
        } else if (element.isJsonPrimitive()) {
            com.google.gson.JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isNumber()) {
                // Try to preserve number type
                try {
                    return primitive.getAsLong();
                } catch (NumberFormatException e) {
                    return primitive.getAsDouble();
                }
            } else if (primitive.isBoolean()) {
                return primitive.getAsBoolean();
            } else {
                return primitive.getAsString();
            }
        } else if (element.isJsonArray()) {
            com.google.gson.JsonArray array = element.getAsJsonArray();
            java.util.List<Object> list = new java.util.ArrayList<>();
            array.forEach(item -> list.add(extractValue(item)));
            return list;
        } else if (element.isJsonObject()) {
            return jsonToMap(element.getAsJsonObject());
        }
        return null;
    }
    
    /**
     * Parse string result to appropriate type
     */
    private Object parseResult(String result) {
        if (result == null || result.isEmpty()) {
            return result;
        }
        
        // Try to parse as number
        try {
            if (result.contains(".")) {
                return Double.parseDouble(result);
            } else {
                return Long.parseLong(result);
            }
        } catch (NumberFormatException e) {
            // Not a number, return as string
            return result;
        }
    }
}
