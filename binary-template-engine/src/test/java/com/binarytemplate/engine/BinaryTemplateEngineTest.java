package com.binarytemplate.engine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Unit tests for BinaryTemplateEngine
 */
class BinaryTemplateEngineTest {

    private BinaryTemplateEngine engine;

    @BeforeEach
    void setUp() {
        engine = new BinaryTemplateEngine();
    }

    @Nested
    @DisplayName("Template Compilation Tests")
    class CompilationTests {

        @Test
        @DisplayName("Should compile valid configuration")
        void shouldCompileValidConfiguration() {
            String config = """
                {
                  "totalSize": 10,
                  "fields": [
                    {
                      "name": "id",
                      "value": "${id}",
                      "offset": 0,
                      "type": "int"
                    }
                  ]
                }
                """;

            CompiledTemplate template = engine.compile(config);

            assertNotNull(template);
            assertEquals(10, template.getTotalSize());
            assertEquals(1, template.getFieldWriters().size());
        }

        @Test
        @DisplayName("Should throw exception for null configuration")
        void shouldThrowExceptionForNullConfiguration() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> engine.compile(null)
            );
            assertEquals("Configuration JSON cannot be null or empty", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception for empty configuration")
        void shouldThrowExceptionForEmptyConfiguration() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> engine.compile("")
            );
            assertEquals("Configuration JSON cannot be null or empty", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception for negative total size")
        void shouldThrowExceptionForNegativeTotalSize() {
            String config = """
                {
                  "totalSize": -1,
                  "fields": [
                    {
                      "name": "id",
                      "offset": 0,
                      "type": "int"
                    }
                  ]
                }
                """;

            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> engine.compile(config)
            );
            assertEquals("Total size must be positive", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception for negative field offset")
        void shouldThrowExceptionForNegativeFieldOffset() {
            String config = """
                {
                  "totalSize": 10,
                  "fields": [
                    {
                      "name": "id",
                      "offset": -1,
                      "type": "int"
                    }
                  ]
                }
                """;

            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> engine.compile(config)
            );
            assertTrue(exception.getMessage().contains("Field offset cannot be negative"));
        }

        @Test
        @DisplayName("Should throw exception for buffer overflow")
        void shouldThrowExceptionForBufferOverflow() {
            String config = """
                {
                  "totalSize": 3,
                  "fields": [
                    {
                      "name": "id",
                      "offset": 0,
                      "type": "int"
                    }
                  ]
                }
                """;

            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> engine.compile(config)
            );
            assertTrue(exception.getMessage().contains("extends beyond buffer size"));
        }
    }

    @Nested
    @DisplayName("Data Generation Tests")
    class GenerationTests {

        @Test
        @DisplayName("Should generate binary data with all numeric types")
        void shouldGenerateAllNumericTypes() {
            String config = """
                {
                  "totalSize": 30,
                  "fields": [
                    {"name": "byteField", "value": "${byteVal}", "offset": 0, "type": "byte"},
                    {"name": "shortField", "value": "${shortVal}", "offset": 1, "type": "short"},
                    {"name": "intField", "value": "${intVal}", "offset": 3, "type": "int"},
                    {"name": "longField", "value": "${longVal}", "offset": 7, "type": "long"},
                    {"name": "floatField", "value": "${floatVal}", "offset": 15, "type": "float"},
                    {"name": "doubleField", "value": "${doubleVal}", "offset": 19, "type": "double"}
                  ]
                }
                """;

            String data = """
                {
                  "byteVal": 127,
                  "shortVal": 32000,
                  "intVal": 2000000000,
                  "longVal": 9000000000000000000,
                  "floatVal": 3.14,
                  "doubleVal": 2.718281828
                }
                """;

            CompiledTemplate template = engine.compile(config);
            byte[] result = engine.generate(template, data);

            assertEquals(30, result.length);

            ByteBuffer buffer = ByteBuffer.wrap(result);
            buffer.order(ByteOrder.BIG_ENDIAN);

            assertEquals(127, buffer.get(0));
            assertEquals(32000, buffer.getShort(1));
            assertEquals(2000000000, buffer.getInt(3));
            assertEquals(9000000000000000000L, buffer.getLong(7));
            assertEquals(3.14f, buffer.getFloat(15), 0.001f);
            assertEquals(2.718281828, buffer.getDouble(19), 0.000001);
        }

        @Test
        @DisplayName("Should generate binary data with boolean and char types")
        void shouldGenerateBooleanAndCharTypes() {
            String config = """
                {
                  "totalSize": 10,
                  "fields": [
                    {"name": "boolField", "value": "${boolVal?c}", "offset": 0, "type": "boolean"},
                    {"name": "charField", "value": "${charVal}", "offset": 1, "type": "char"}
                  ]
                }
                """;

            String data = """
                {
                  "boolVal": true,
                  "charVal": "A"
                }
                """;

            CompiledTemplate template = engine.compile(config);
            byte[] result = engine.generate(template, data);

            ByteBuffer buffer = ByteBuffer.wrap(result);
            buffer.order(ByteOrder.BIG_ENDIAN);

            assertEquals(1, buffer.get(0)); // true = 1
            assertEquals('A', buffer.getChar(1));
        }

        @Test
        @DisplayName("Should generate binary data with string type")
        void shouldGenerateStringType() {
            String config = """
                {
                  "totalSize": 20,
                  "fields": [
                    {"name": "stringField", "value": "${text}", "offset": 0, "type": "string", "length": 10}
                  ]
                }
                """;

            String data = """
                {
                  "text": "Hello"
                }
                """;

            CompiledTemplate template = engine.compile(config);
            byte[] result = engine.generate(template, data);

            String resultString = new String(result, 0, 5);
            assertEquals("Hello", resultString);
            
            // Check zero padding
            for (int i = 5; i < 10; i++) {
                assertEquals(0, result[i]);
            }
        }

        @Test
        @DisplayName("Should generate binary data with bytes type")
        void shouldGenerateBytesType() {
            String config = """
                {
                  "totalSize": 10,
                  "fields": [
                    {"name": "bytesField", "value": "${hexData}", "offset": 0, "type": "bytes", "length": 4}
                  ]
                }
                """;

            String data = """
                {
                  "hexData": "DEADBEEF"
                }
                """;

            CompiledTemplate template = engine.compile(config);
            byte[] result = engine.generate(template, data);

            assertEquals((byte) 0xDE, result[0]);
            assertEquals((byte) 0xAD, result[1]);
            assertEquals((byte) 0xBE, result[2]);
            assertEquals((byte) 0xEF, result[3]);
        }

        @Test
        @DisplayName("Should use default values when fields are missing")
        void shouldUseDefaultValues() {
            String config = """
                {
                  "totalSize": 10,
                  "fields": [
                    {"name": "id", "value": "${id}", "offset": 0, "type": "int", "defaultValue": 42}
                  ]
                }
                """;

            String data = "{}"; // Empty data

            CompiledTemplate template = engine.compile(config);
            byte[] result = engine.generate(template, data);

            ByteBuffer buffer = ByteBuffer.wrap(result);
            buffer.order(ByteOrder.BIG_ENDIAN);

            assertEquals(42, buffer.getInt(0));
        }

        @Test
        @DisplayName("Should throw exception for null template")
        void shouldThrowExceptionForNullTemplate() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> engine.generate((CompiledTemplate) null, "{}")
            );
            assertEquals("Template cannot be null", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception for null data")
        void shouldThrowExceptionForNullData() {
            String config = """
                {
                  "totalSize": 4,
                  "fields": [
                    {"name": "id", "offset": 0, "type": "int"}
                  ]
                }
                """;

            CompiledTemplate template = engine.compile(config);

            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> engine.generate(template, null)
            );
            assertEquals("Data JSON cannot be null or empty", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Expression Tests")
    class ExpressionTests {

        @Test
        @DisplayName("Should evaluate arithmetic expressions")
        void shouldEvaluateArithmeticExpressions() {
            String config = """
                {
                  "totalSize": 8,
                  "fields": [
                    {"name": "doubled", "value": "${value * 2}", "offset": 0, "type": "int"},
                    {"name": "sum", "value": "${a + b}", "offset": 4, "type": "int"}
                  ]
                }
                """;

            String data = """
                {
                  "value": 21,
                  "a": 10,
                  "b": 15
                }
                """;

            CompiledTemplate template = engine.compile(config);
            byte[] result = engine.generate(template, data);

            ByteBuffer buffer = ByteBuffer.wrap(result);
            buffer.order(ByteOrder.BIG_ENDIAN);

            assertEquals(42, buffer.getInt(0)); // 21 * 2
            assertEquals(25, buffer.getInt(4)); // 10 + 15
        }

        @Test
        @DisplayName("Should evaluate conditional expressions")
        void shouldEvaluateConditionalExpressions() {
            String config = """
                {
                  "totalSize": 8,
                  "fields": [
                    {"name": "result1", "value": "${(score > 80)?then(1, 0)}", "offset": 0, "type": "int"},
                    {"name": "result2", "value": "${(score > 80)?then(1, 0)}", "offset": 4, "type": "int"}
                  ]
                }
                """;

            String data1 = """
                {
                  "score": 95
                }
                """;

            String data2 = """
                {
                  "score": 65
                }
                """;

            CompiledTemplate template = engine.compile(config);
            
            byte[] result1 = engine.generate(template, data1);
            ByteBuffer buffer1 = ByteBuffer.wrap(result1);
            buffer1.order(ByteOrder.BIG_ENDIAN);
            assertEquals(1, buffer1.getInt(0)); // score > 80
            
            byte[] result2 = engine.generate(template, data2);
            ByteBuffer buffer2 = ByteBuffer.wrap(result2);
            buffer2.order(ByteOrder.BIG_ENDIAN);
            assertEquals(0, buffer2.getInt(0)); // score <= 80
        }

        @Test
        @DisplayName("Should evaluate string transformation expressions")
        void shouldEvaluateStringTransformations() {
            String config = """
                {
                  "totalSize": 20,
                  "fields": [
                    {"name": "upper", "value": "${name?upper_case}", "offset": 0, "type": "string", "length": 10}
                  ]
                }
                """;

            String data = """
                {
                  "name": "alice"
                }
                """;

            CompiledTemplate template = engine.compile(config);
            byte[] result = engine.generate(template, data);

            String resultString = new String(result, 0, 5);
            assertEquals("ALICE", resultString);
        }
    }

    @Nested
    @DisplayName("JsonPath Tests")
    class JsonPathTests {

        @Test
        @DisplayName("Should evaluate JsonPath expressions")
        void shouldEvaluateJsonPathExpressions() {
            String config = """
                {
                  "totalSize": 8,
                  "fields": [
                    {"name": "userId", "value": "$.user.id", "offset": 0, "type": "int"},
                    {"name": "itemCount", "value": "$.items.length()", "offset": 4, "type": "int"}
                  ]
                }
                """;

            String data = """
                {
                  "user": {
                    "id": 12345,
                    "name": "Alice"
                  },
                  "items": [
                    {"name": "Item1"},
                    {"name": "Item2"},
                    {"name": "Item3"}
                  ]
                }
                """;

            CompiledTemplate template = engine.compile(config);
            byte[] result = engine.generate(template, data);

            ByteBuffer buffer = ByteBuffer.wrap(result);
            buffer.order(ByteOrder.BIG_ENDIAN);

            assertEquals(12345, buffer.getInt(0));
            assertEquals(3, buffer.getInt(4));
        }
    }

    @Nested
    @DisplayName("Direct Generation Tests")
    class DirectGenerationTests {

        @Test
        @DisplayName("Should generate directly without precompilation")
        void shouldGenerateDirectly() {
            String config = """
                {
                  "totalSize": 4,
                  "fields": [
                    {"name": "id", "value": "${id}", "offset": 0, "type": "int"}
                  ]
                }
                """;

            String data = """
                {
                  "id": 999
                }
                """;

            byte[] result = engine.generate(config, data);

            ByteBuffer buffer = ByteBuffer.wrap(result);
            buffer.order(ByteOrder.BIG_ENDIAN);

            assertEquals(999, buffer.getInt(0));
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle empty fields list")
        void shouldHandleEmptyFieldsList() {
            String config = """
                {
                  "totalSize": 10,
                  "fields": []
                }
                """;

            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> engine.compile(config)
            );
            assertEquals("Fields list cannot be null or empty", exception.getMessage());
        }

        @Test
        @DisplayName("Should handle unknown field type")
        void shouldHandleUnknownFieldType() {
            String config = """
                {
                  "totalSize": 10,
                  "fields": [
                    {"name": "unknown", "offset": 0, "type": "unknown"}
                  ]
                }
                """;

            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> engine.compile(config)
            );
            assertTrue(exception.getMessage().contains("Unknown field type"));
        }

        @Test
        @DisplayName("Should handle string truncation")
        void shouldHandleStringTruncation() {
            String config = """
                {
                  "totalSize": 10,
                  "fields": [
                    {"name": "text", "value": "${longText}", "offset": 0, "type": "string", "length": 5}
                  ]
                }
                """;

            String data = """
                {
                  "longText": "This is a very long text that should be truncated"
                }
                """;

            CompiledTemplate template = engine.compile(config);
            byte[] result = engine.generate(template, data);

            String resultString = new String(result, 0, 5);
            assertEquals("This ", resultString);
        }

        @Test
        @DisplayName("Should handle invalid hex string")
        void shouldHandleInvalidHexString() {
            String config = """
                {
                  "totalSize": 10,
                  "fields": [
                    {"name": "bytes", "value": "${hexData}", "offset": 0, "type": "bytes", "length": 4}
                  ]
                }
                """;

            String data = """
                {
                  "hexData": "INVALID_HEX"
                }
                """;

            CompiledTemplate template = engine.compile(config);

            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> engine.generate(template, data)
            );
            assertTrue(exception.getMessage().contains("Failed to process field"));
        }
    }
}