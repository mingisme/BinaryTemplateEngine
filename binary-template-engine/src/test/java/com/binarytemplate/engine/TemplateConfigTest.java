package com.binarytemplate.engine;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

/**
 * Unit tests for TemplateConfig
 */
class TemplateConfigTest {

    private final Gson gson = new Gson();

    @Test
    @DisplayName("Should deserialize complete configuration correctly")
    void shouldDeserializeCompleteConfigurationCorrectly() {
        String json = """
            {
              "totalSize": 100,
              "fields": [
                {
                  "name": "id",
                  "value": "${userId}",
                  "offset": 0,
                  "type": "int",
                  "defaultValue": 0
                },
                {
                  "name": "name",
                  "value": "${userName}",
                  "offset": 4,
                  "type": "string",
                  "length": 50,
                  "encoding": "UTF-8",
                  "defaultValue": "unknown"
                }
              ]
            }
            """;

        TemplateConfig config = gson.fromJson(json, TemplateConfig.class);

        assertEquals(100, config.getTotalSize());
        assertEquals(2, config.getFields().size());

        TemplateConfig.Field field1 = config.getFields().get(0);
        assertEquals("id", field1.getName());
        assertEquals("${userId}", field1.getValue());
        assertEquals(0, field1.getOffset());
        assertEquals("int", field1.getType());
        assertEquals(0.0, field1.getDefaultValue()); // Gson deserializes numbers as double

        TemplateConfig.Field field2 = config.getFields().get(1);
        assertEquals("name", field2.getName());
        assertEquals("${userName}", field2.getValue());
        assertEquals(4, field2.getOffset());
        assertEquals("string", field2.getType());
        assertEquals(50, field2.getLength());
        assertEquals("UTF-8", field2.getEncoding());
        assertEquals("unknown", field2.getDefaultValue());
    }

    @Test
    @DisplayName("Should deserialize minimal configuration correctly")
    void shouldDeserializeMinimalConfigurationCorrectly() {
        String json = """
            {
              "totalSize": 10,
              "fields": [
                {
                  "name": "id",
                  "offset": 0,
                  "type": "int"
                }
              ]
            }
            """;

        TemplateConfig config = gson.fromJson(json, TemplateConfig.class);

        assertEquals(10, config.getTotalSize());
        assertEquals(1, config.getFields().size());

        TemplateConfig.Field field = config.getFields().get(0);
        assertEquals("id", field.getName());
        assertNull(field.getValue());
        assertEquals(0, field.getOffset());
        assertEquals("int", field.getType());
        assertEquals(0, field.getLength());
        assertNull(field.getEncoding());
        assertNull(field.getDefaultValue());
    }

    @Test
    @DisplayName("Should serialize configuration correctly")
    void shouldSerializeConfigurationCorrectly() {
        TemplateConfig config = new TemplateConfig();
        config.setTotalSize(50);

        TemplateConfig.Field field = new TemplateConfig.Field();
        field.setName("testField");
        field.setValue("${testValue}");
        field.setOffset(10);
        field.setType("string");
        field.setLength(20);
        field.setEncoding("UTF-8");
        field.setDefaultValue("default");

        config.setFields(Arrays.asList(field));

        String json = gson.toJson(config);
        
        assertTrue(json.contains("\"totalSize\":50"));
        assertTrue(json.contains("\"name\":\"testField\""));
        assertTrue(json.contains("\"value\":\"${testValue}\""));
        assertTrue(json.contains("\"offset\":10"));
        assertTrue(json.contains("\"type\":\"string\""));
        assertTrue(json.contains("\"length\":20"));
        assertTrue(json.contains("\"encoding\":\"UTF-8\""));
        assertTrue(json.contains("\"defaultValue\":\"default\""));
    }

    @Test
    @DisplayName("Should handle all field setters and getters")
    void shouldHandleAllFieldSettersAndGetters() {
        TemplateConfig.Field field = new TemplateConfig.Field();
        
        field.setName("testName");
        assertEquals("testName", field.getName());
        
        field.setValue("testValue");
        assertEquals("testValue", field.getValue());
        
        field.setOffset(42);
        assertEquals(42, field.getOffset());
        
        field.setType("testType");
        assertEquals("testType", field.getType());
        
        field.setLength(100);
        assertEquals(100, field.getLength());
        
        field.setEncoding("UTF-16");
        assertEquals("UTF-16", field.getEncoding());
        
        field.setDefaultValue("testDefault");
        assertEquals("testDefault", field.getDefaultValue());
    }

    @Test
    @DisplayName("Should handle template config setters and getters")
    void shouldHandleTemplateConfigSettersAndGetters() {
        TemplateConfig config = new TemplateConfig();
        
        config.setTotalSize(200);
        assertEquals(200, config.getTotalSize());
        
        TemplateConfig.Field field = new TemplateConfig.Field();
        field.setName("test");
        
        config.setFields(Arrays.asList(field));
        assertEquals(1, config.getFields().size());
        assertEquals("test", config.getFields().get(0).getName());
    }
}