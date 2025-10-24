package com.binarytemplate.engine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Integration tests for the complete Binary Template Engine
 */
class IntegrationTest {

    private BinaryTemplateEngine engine;

    @BeforeEach
    void setUp() {
        engine = new BinaryTemplateEngine();
    }

    @Test
    @DisplayName("Should handle complete real-world scenario")
    void shouldHandleCompleteRealWorldScenario() {
        String config = """
            {
              "totalSize": 100,
              "fields": [
                {
                  "name": "header",
                  "value": "PROTO",
                  "offset": 0,
                  "type": "string",
                  "length": 5,
                  "encoding": "UTF-8"
                },
                {
                  "name": "version",
                  "value": "${version}",
                  "offset": 5,
                  "type": "byte"
                },
                {
                  "name": "userId",
                  "value": "$.user.id",
                  "offset": 6,
                  "type": "int"
                },
                {
                  "name": "userName",
                  "value": "${user.name?upper_case}",
                  "offset": 10,
                  "type": "string",
                  "length": 30,
                  "encoding": "UTF-8"
                },
                {
                  "name": "itemCount",
                  "value": "$.items.length()",
                  "offset": 40,
                  "type": "short"
                },
                {
                  "name": "totalPrice",
                  "value": "${items[0].price + items[1].price}",
                  "offset": 42,
                  "type": "float"
                },
                {
                  "name": "discountedPrice",
                  "value": "${(totalAmount > 1000)?then(totalAmount * 0.9, totalAmount)}",
                  "offset": 46,
                  "type": "float"
                },
                {
                  "name": "isActive",
                  "value": "${user.active?c}",
                  "offset": 50,
                  "type": "boolean"
                },
                {
                  "name": "timestamp",
                  "value": "${timestamp}",
                  "offset": 51,
                  "type": "long"
                },
                {
                  "name": "checksum",
                  "value": "${checksum}",
                  "offset": 59,
                  "type": "bytes",
                  "length": 4
                }
              ]
            }
            """;

        String data = """
            {
              "version": 3,
              "user": {
                "id": 12345,
                "name": "alice_smith",
                "active": true
              },
              "items": [
                {
                  "name": "Product A",
                  "price": 500
                },
                {
                  "name": "Product B",
                  "price": 750
                }
              ],
              "totalAmount": 1250,
              "timestamp": 1729785600000,
              "checksum": "DEADBEEF"
            }
            """;

        // Test compilation
        CompiledTemplate template = engine.compile(config);
        assertNotNull(template);
        assertEquals(100, template.getTotalSize());
        assertEquals(10, template.getFieldWriters().size());

        // Test generation
        byte[] result = engine.generate(template, data);
        assertEquals(100, result.length);

        // Verify results
        ByteBuffer buffer = ByteBuffer.wrap(result);
        buffer.order(ByteOrder.BIG_ENDIAN);

        // Check header
        byte[] headerBytes = new byte[5];
        buffer.position(0);
        buffer.get(headerBytes);
        assertEquals("PROTO", new String(headerBytes));

        // Check version
        assertEquals(3, buffer.get(5));

        // Check user ID (JsonPath)
        assertEquals(12345, buffer.getInt(6));

        // Check user name (FreeMarker transformation)
        byte[] nameBytes = new byte[11];
        buffer.position(10);
        buffer.get(nameBytes);
        assertEquals("ALICE_SMITH", new String(nameBytes));

        // Check item count (JsonPath array length)
        assertEquals(2, buffer.getShort(40));

        // Check total price (FreeMarker arithmetic)
        assertEquals(1250.0f, buffer.getFloat(42), 0.01f);

        // Check discounted price (FreeMarker conditional)
        assertEquals(1125.0f, buffer.getFloat(46), 0.01f); // 1250 * 0.9

        // Check boolean
        assertEquals(1, buffer.get(50)); // true

        // Check timestamp
        assertEquals(1729785600000L, buffer.getLong(51));

        // Check checksum bytes
        assertEquals((byte) 0xDE, buffer.get(59));
        assertEquals((byte) 0xAD, buffer.get(60));
        assertEquals((byte) 0xBE, buffer.get(61));
        assertEquals((byte) 0xEF, buffer.get(62));
    }

    @Test
    @DisplayName("Should handle template reuse efficiently")
    void shouldHandleTemplateReuseEfficiently() {
        String config = """
            {
              "totalSize": 20,
              "fields": [
                {
                  "name": "id",
                  "value": "${id}",
                  "offset": 0,
                  "type": "int"
                },
                {
                  "name": "name",
                  "value": "${name?upper_case}",
                  "offset": 4,
                  "type": "string",
                  "length": 10
                },
                {
                  "name": "score",
                  "value": "${score * 2}",
                  "offset": 14,
                  "type": "short"
                }
              ]
            }
            """;

        // Compile once
        CompiledTemplate template = engine.compile(config);

        // Use multiple times with different data
        String data1 = """
            {
              "id": 1,
              "name": "alice",
              "score": 50
            }
            """;

        String data2 = """
            {
              "id": 2,
              "name": "bob",
              "score": 75
            }
            """;

        byte[] result1 = engine.generate(template, data1);
        byte[] result2 = engine.generate(template, data2);

        // Verify first result
        ByteBuffer buffer1 = ByteBuffer.wrap(result1);
        buffer1.order(ByteOrder.BIG_ENDIAN);
        assertEquals(1, buffer1.getInt(0));
        assertEquals("ALICE", new String(result1, 4, 5));
        assertEquals(100, buffer1.getShort(14));

        // Verify second result
        ByteBuffer buffer2 = ByteBuffer.wrap(result2);
        buffer2.order(ByteOrder.BIG_ENDIAN);
        assertEquals(2, buffer2.getInt(0));
        assertEquals("BOB", new String(result2, 4, 3));
        assertEquals(150, buffer2.getShort(14));
    }

    @Test
    @DisplayName("Should handle default values in complex scenario")
    void shouldHandleDefaultValuesInComplexScenario() {
        String config = """
            {
              "totalSize": 20,
              "fields": [
                {
                  "name": "id",
                  "value": "${id}",
                  "offset": 0,
                  "type": "int",
                  "defaultValue": 999
                },
                {
                  "name": "status",
                  "value": "${status}",
                  "offset": 4,
                  "type": "byte",
                  "defaultValue": 1
                },
                {
                  "name": "name",
                  "value": "${name}",
                  "offset": 5,
                  "type": "string",
                  "length": 10,
                  "defaultValue": "unknown"
                }
              ]
            }
            """;

        // Data with missing fields
        String data = """
            {
              "name": "alice"
            }
            """;

        CompiledTemplate template = engine.compile(config);
        byte[] result = engine.generate(template, data);

        ByteBuffer buffer = ByteBuffer.wrap(result);
        buffer.order(ByteOrder.BIG_ENDIAN);

        // Should use default values
        assertEquals(999, buffer.getInt(0));
        assertEquals(1, buffer.get(4));
        assertEquals("alice", new String(result, 5, 5));
    }

    @Test
    @DisplayName("Should handle mixed expression types")
    void shouldHandleMixedExpressionTypes() {
        String config = """
            {
              "totalSize": 30,
              "fields": [
                {
                  "name": "literal",
                  "value": "HEADER",
                  "offset": 0,
                  "type": "string",
                  "length": 6
                },
                {
                  "name": "jsonPath",
                  "value": "$.user.id",
                  "offset": 6,
                  "type": "int"
                },
                {
                  "name": "freeMarker",
                  "value": "${score * 1.5}",
                  "offset": 10,
                  "type": "float"
                },
                {
                  "name": "conditional",
                  "value": "${(active)?then(1, 0)}",
                  "offset": 14,
                  "type": "byte"
                }
              ]
            }
            """;

        String data = """
            {
              "user": {
                "id": 42
              },
              "score": 80,
              "active": true
            }
            """;

        CompiledTemplate template = engine.compile(config);
        byte[] result = engine.generate(template, data);

        ByteBuffer buffer = ByteBuffer.wrap(result);
        buffer.order(ByteOrder.BIG_ENDIAN);

        assertEquals("HEADER", new String(result, 0, 6));
        assertEquals(42, buffer.getInt(6));
        assertEquals(120.0f, buffer.getFloat(10), 0.01f);
        assertEquals(1, buffer.get(14));
    }
}