package com.binarytemplate.engine;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * Binary Template Engine - generates binary data from JSON using precompiled
 * templates
 */
public class BinaryTemplateEngine {
    private final Gson gson = new Gson();
    private final ExpressionEvaluator evaluator = new ExpressionEvaluator();

    /**
     * Precompile a configuration for efficient reuse
     */
    public CompiledTemplate compile(String configJson) {
        if (configJson == null || configJson.trim().isEmpty()) {
            throw new IllegalArgumentException("Configuration JSON cannot be null or empty");
        }

        TemplateConfig config = gson.fromJson(configJson, TemplateConfig.class);
        validateConfig(config);

        List<CompiledTemplate.FieldWriter> writers = new ArrayList<>();

        for (TemplateConfig.Field field : config.getFields()) {
            CompiledTemplate.FieldWriter writer = createFieldWriter(field);
            writers.add(writer);
        }

        return new CompiledTemplate(config.getTotalSize(), writers);
    }

    /**
     * Validate configuration for common issues
     */
    private void validateConfig(TemplateConfig config) {
        if (config.getTotalSize() <= 0) {
            throw new IllegalArgumentException("Total size must be positive");
        }

        if (config.getFields() == null || config.getFields().isEmpty()) {
            throw new IllegalArgumentException("Fields list cannot be null or empty");
        }

        for (TemplateConfig.Field field : config.getFields()) {
            if (field.getOffset() < 0) {
                throw new IllegalArgumentException("Field offset cannot be negative: " + field.getName());
            }

            if (field.getType() == null || field.getType().trim().isEmpty()) {
                throw new IllegalArgumentException("Field type cannot be null or empty: " + field.getName());
            }

            // Check for buffer overflow
            int fieldSize = getFieldSize(field);
            if (field.getOffset() + fieldSize > config.getTotalSize()) {
                throw new IllegalArgumentException(
                        String.format("Field '%s' extends beyond buffer size: offset=%d, size=%d, total=%d",
                                field.getName(), field.getOffset(), fieldSize, config.getTotalSize()));
            }
        }
    }

    /**
     * Get the size in bytes for a field type
     */
    private int getFieldSize(TemplateConfig.Field field) {
        switch (field.getType().toLowerCase()) {
            case "byte":
            case "boolean":
                return 1;
            case "short":
            case "char":
                return 2;
            case "int":
            case "float":
                return 4;
            case "long":
            case "double":
                return 8;
            case "string":
            case "bytes":
                return field.getLength();
            default:
                throw new IllegalArgumentException("Unknown field type: " + field.getType());
        }
    }

    /**
     * Generate binary data using a precompiled template
     */
    public byte[] generate(CompiledTemplate template, String dataJson) {
        if (template == null) {
            throw new IllegalArgumentException("Template cannot be null");
        }
        if (dataJson == null || dataJson.trim().isEmpty()) {
            throw new IllegalArgumentException("Data JSON cannot be null or empty");
        }

        JsonObject data = gson.fromJson(dataJson, JsonObject.class);
        ByteBuffer buffer = ByteBuffer.allocate(template.getTotalSize());
        buffer.order(ByteOrder.BIG_ENDIAN);

        for (CompiledTemplate.FieldWriter writer : template.getFieldWriters()) {
            Object value;

            try {
                // If field has a value expression, evaluate it
                if (writer.getValueExpression() != null) {
                    value = evaluator.evaluate(writer.getValueExpression(), data);
                } else {
                    // Otherwise, extract from data by field name
                    value = extractValue(data, writer.getFieldName());
                }

                if (value == null) {
                    value = writer.getDefaultValue();
                }
                if (value != null) {
                    writer.write(buffer, value);
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to process field: " + writer.getFieldName(), e);
            }
        }

        return buffer.array();
    }

    /**
     * Generate binary data directly (without precompilation)
     */
    public byte[] generate(String configJson, String dataJson) {
        CompiledTemplate template = compile(configJson);
        return generate(template, dataJson);
    }

    private CompiledTemplate.FieldWriter createFieldWriter(TemplateConfig.Field field) {
        switch (field.getType().toLowerCase()) {
            case "int":
                return new CompiledTemplate.IntFieldWriter(
                        field.getName(), field.getValue(), field.getOffset(), field.getDefaultValue());
            case "short":
                return new CompiledTemplate.ShortFieldWriter(
                        field.getName(), field.getValue(), field.getOffset(), field.getDefaultValue());
            case "byte":
                return new CompiledTemplate.ByteFieldWriter(
                        field.getName(), field.getValue(), field.getOffset(), field.getDefaultValue());
            case "long":
                return new CompiledTemplate.LongFieldWriter(
                        field.getName(), field.getValue(), field.getOffset(), field.getDefaultValue());
            case "float":
                return new CompiledTemplate.FloatFieldWriter(
                        field.getName(), field.getValue(), field.getOffset(), field.getDefaultValue());
            case "double":
                return new CompiledTemplate.DoubleFieldWriter(
                        field.getName(), field.getValue(), field.getOffset(), field.getDefaultValue());
            case "boolean":
                return new CompiledTemplate.BooleanFieldWriter(
                        field.getName(), field.getValue(), field.getOffset(), field.getDefaultValue());
            case "char":
                return new CompiledTemplate.CharFieldWriter(
                        field.getName(), field.getValue(), field.getOffset(), field.getDefaultValue());
            case "string":
                String encoding = field.getEncoding() != null ? field.getEncoding() : "UTF-8";
                return new CompiledTemplate.StringFieldWriter(
                        field.getName(), field.getValue(), field.getOffset(), field.getLength(), encoding,
                        field.getDefaultValue());
            case "bytes":
                return new CompiledTemplate.BytesFieldWriter(
                        field.getName(), field.getValue(), field.getOffset(), field.getLength(),
                        field.getDefaultValue());
            default:
                throw new IllegalArgumentException("Unsupported type: " + field.getType());
        }
    }

    private Object extractValue(JsonObject data, String fieldName) {
        if (!data.has(fieldName)) {
            return null;
        }

        if (data.get(fieldName).isJsonPrimitive()) {
            if (data.get(fieldName).getAsJsonPrimitive().isNumber()) {
                return data.get(fieldName).getAsNumber();
            } else if (data.get(fieldName).getAsJsonPrimitive().isString()) {
                return data.get(fieldName).getAsString();
            }
        }
        return data.get(fieldName).toString();
    }
}
