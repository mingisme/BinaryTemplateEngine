package com.binarytemplate.engine;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for CompiledTemplate and FieldWriters
 */
class CompiledTemplateTest {

    @Nested
    @DisplayName("CompiledTemplate Tests")
    class CompiledTemplateTests {

        @Test
        @DisplayName("Should create compiled template with correct properties")
        void shouldCreateCompiledTemplateWithCorrectProperties() {
            CompiledTemplate.FieldWriter writer = new CompiledTemplate.IntFieldWriter("test", "${value}", 0, null);
            List<CompiledTemplate.FieldWriter> writers = Arrays.asList(writer);
            
            CompiledTemplate template = new CompiledTemplate(100, writers);
            
            assertEquals(100, template.getTotalSize());
            assertEquals(1, template.getFieldWriters().size());
            assertEquals(writer, template.getFieldWriters().get(0));
        }
    }

    @Nested
    @DisplayName("Numeric FieldWriter Tests")
    class NumericFieldWriterTests {

        @Test
        @DisplayName("Should write byte values correctly")
        void shouldWriteByteValuesCorrectly() {
            CompiledTemplate.ByteFieldWriter writer = new CompiledTemplate.ByteFieldWriter("test", "${value}", 0, null);
            ByteBuffer buffer = ByteBuffer.allocate(10);
            
            writer.write(buffer, (byte) 127);
            assertEquals(127, buffer.get(0));
            
            writer.write(buffer, -128);
            assertEquals(-128, buffer.get(0));
        }

        @Test
        @DisplayName("Should write short values correctly")
        void shouldWriteShortValuesCorrectly() {
            CompiledTemplate.ShortFieldWriter writer = new CompiledTemplate.ShortFieldWriter("test", "${value}", 0, null);
            ByteBuffer buffer = ByteBuffer.allocate(10);
            buffer.order(ByteOrder.BIG_ENDIAN);
            
            writer.write(buffer, (short) 32000);
            assertEquals(32000, buffer.getShort(0));
            
            writer.write(buffer, -32000);
            assertEquals(-32000, buffer.getShort(0));
        }

        @Test
        @DisplayName("Should write int values correctly")
        void shouldWriteIntValuesCorrectly() {
            CompiledTemplate.IntFieldWriter writer = new CompiledTemplate.IntFieldWriter("test", "${value}", 0, null);
            ByteBuffer buffer = ByteBuffer.allocate(10);
            buffer.order(ByteOrder.BIG_ENDIAN);
            
            writer.write(buffer, 2000000000);
            assertEquals(2000000000, buffer.getInt(0));
            
            writer.write(buffer, -2000000000);
            assertEquals(-2000000000, buffer.getInt(0));
        }

        @Test
        @DisplayName("Should write long values correctly")
        void shouldWriteLongValuesCorrectly() {
            CompiledTemplate.LongFieldWriter writer = new CompiledTemplate.LongFieldWriter("test", "${value}", 0, null);
            ByteBuffer buffer = ByteBuffer.allocate(20);
            buffer.order(ByteOrder.BIG_ENDIAN);
            
            writer.write(buffer, 9000000000000000000L);
            assertEquals(9000000000000000000L, buffer.getLong(0));
        }

        @Test
        @DisplayName("Should write float values correctly")
        void shouldWriteFloatValuesCorrectly() {
            CompiledTemplate.FloatFieldWriter writer = new CompiledTemplate.FloatFieldWriter("test", "${value}", 0, null);
            ByteBuffer buffer = ByteBuffer.allocate(10);
            buffer.order(ByteOrder.BIG_ENDIAN);
            
            writer.write(buffer, 3.14159f);
            assertEquals(3.14159f, buffer.getFloat(0), 0.00001f);
        }

        @Test
        @DisplayName("Should write double values correctly")
        void shouldWriteDoubleValuesCorrectly() {
            CompiledTemplate.DoubleFieldWriter writer = new CompiledTemplate.DoubleFieldWriter("test", "${value}", 0, null);
            ByteBuffer buffer = ByteBuffer.allocate(20);
            buffer.order(ByteOrder.BIG_ENDIAN);
            
            writer.write(buffer, 2.718281828459045);
            assertEquals(2.718281828459045, buffer.getDouble(0), 0.000000001);
        }
    }

    @Nested
    @DisplayName("Boolean FieldWriter Tests")
    class BooleanFieldWriterTests {

        @Test
        @DisplayName("Should write boolean values correctly")
        void shouldWriteBooleanValuesCorrectly() {
            CompiledTemplate.BooleanFieldWriter writer = new CompiledTemplate.BooleanFieldWriter("test", "${value}", 0, null);
            ByteBuffer buffer = ByteBuffer.allocate(10);
            
            writer.write(buffer, true);
            assertEquals(1, buffer.get(0));
            
            writer.write(buffer, false);
            assertEquals(0, buffer.get(0));
        }

        @Test
        @DisplayName("Should convert numbers to boolean")
        void shouldConvertNumbersToBoolean() {
            CompiledTemplate.BooleanFieldWriter writer = new CompiledTemplate.BooleanFieldWriter("test", "${value}", 0, null);
            ByteBuffer buffer = ByteBuffer.allocate(10);
            
            writer.write(buffer, 1);
            assertEquals(1, buffer.get(0));
            
            writer.write(buffer, 0);
            assertEquals(0, buffer.get(0));
            
            writer.write(buffer, 42);
            assertEquals(1, buffer.get(0));
        }

        @Test
        @DisplayName("Should convert strings to boolean")
        void shouldConvertStringsToBoolean() {
            CompiledTemplate.BooleanFieldWriter writer = new CompiledTemplate.BooleanFieldWriter("test", "${value}", 0, null);
            ByteBuffer buffer = ByteBuffer.allocate(10);
            
            writer.write(buffer, "true");
            assertEquals(1, buffer.get(0));
            
            writer.write(buffer, "false");
            assertEquals(0, buffer.get(0));
        }
    }

    @Nested
    @DisplayName("Character FieldWriter Tests")
    class CharacterFieldWriterTests {

        @Test
        @DisplayName("Should write character values correctly")
        void shouldWriteCharacterValuesCorrectly() {
            CompiledTemplate.CharFieldWriter writer = new CompiledTemplate.CharFieldWriter("test", "${value}", 0, null);
            ByteBuffer buffer = ByteBuffer.allocate(10);
            buffer.order(ByteOrder.BIG_ENDIAN);
            
            writer.write(buffer, 'A');
            assertEquals('A', buffer.getChar(0));
        }

        @Test
        @DisplayName("Should convert strings to character")
        void shouldConvertStringsToCharacter() {
            CompiledTemplate.CharFieldWriter writer = new CompiledTemplate.CharFieldWriter("test", "${value}", 0, null);
            ByteBuffer buffer = ByteBuffer.allocate(10);
            buffer.order(ByteOrder.BIG_ENDIAN);
            
            writer.write(buffer, "Hello");
            assertEquals('H', buffer.getChar(0));
            
            writer.write(buffer, "");
            assertEquals('\0', buffer.getChar(0));
        }

        @Test
        @DisplayName("Should convert numbers to character")
        void shouldConvertNumbersToCharacter() {
            CompiledTemplate.CharFieldWriter writer = new CompiledTemplate.CharFieldWriter("test", "${value}", 0, null);
            ByteBuffer buffer = ByteBuffer.allocate(10);
            buffer.order(ByteOrder.BIG_ENDIAN);
            
            writer.write(buffer, 65); // ASCII 'A'
            assertEquals('A', buffer.getChar(0));
        }
    }

    @Nested
    @DisplayName("String FieldWriter Tests")
    class StringFieldWriterTests {

        @Test
        @DisplayName("Should write string values correctly")
        void shouldWriteStringValuesCorrectly() {
            CompiledTemplate.StringFieldWriter writer = new CompiledTemplate.StringFieldWriter("test", "${value}", 0, 10, "UTF-8", null);
            ByteBuffer buffer = ByteBuffer.allocate(20);
            
            writer.write(buffer, "Hello");
            
            byte[] result = new byte[5];
            buffer.position(0);
            buffer.get(result);
            assertEquals("Hello", new String(result));
        }

        @Test
        @DisplayName("Should pad strings with zeros")
        void shouldPadStringsWithZeros() {
            CompiledTemplate.StringFieldWriter writer = new CompiledTemplate.StringFieldWriter("test", "${value}", 0, 10, "UTF-8", null);
            ByteBuffer buffer = ByteBuffer.allocate(20);
            
            writer.write(buffer, "Hi");
            
            assertEquals('H', buffer.get(0));
            assertEquals('i', buffer.get(1));
            assertEquals(0, buffer.get(2));
            assertEquals(0, buffer.get(3));
        }

        @Test
        @DisplayName("Should truncate long strings")
        void shouldTruncateLongStrings() {
            CompiledTemplate.StringFieldWriter writer = new CompiledTemplate.StringFieldWriter("test", "${value}", 0, 5, "UTF-8", null);
            ByteBuffer buffer = ByteBuffer.allocate(20);
            
            writer.write(buffer, "This is a very long string");
            
            byte[] result = new byte[5];
            buffer.position(0);
            buffer.get(result);
            assertEquals("This ", new String(result));
        }
    }

    @Nested
    @DisplayName("Bytes FieldWriter Tests")
    class BytesFieldWriterTests {

        @Test
        @DisplayName("Should write byte arrays correctly")
        void shouldWriteByteArraysCorrectly() {
            CompiledTemplate.BytesFieldWriter writer = new CompiledTemplate.BytesFieldWriter("test", "${value}", 0, 4, null);
            ByteBuffer buffer = ByteBuffer.allocate(10);
            
            byte[] input = {(byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF};
            writer.write(buffer, input);
            
            assertEquals((byte) 0xDE, buffer.get(0));
            assertEquals((byte) 0xAD, buffer.get(1));
            assertEquals((byte) 0xBE, buffer.get(2));
            assertEquals((byte) 0xEF, buffer.get(3));
        }

        @Test
        @DisplayName("Should parse hex strings correctly")
        void shouldParseHexStringsCorrectly() {
            CompiledTemplate.BytesFieldWriter writer = new CompiledTemplate.BytesFieldWriter("test", "${value}", 0, 4, null);
            ByteBuffer buffer = ByteBuffer.allocate(10);
            
            writer.write(buffer, "DEADBEEF");
            
            assertEquals((byte) 0xDE, buffer.get(0));
            assertEquals((byte) 0xAD, buffer.get(1));
            assertEquals((byte) 0xBE, buffer.get(2));
            assertEquals((byte) 0xEF, buffer.get(3));
        }

        @Test
        @DisplayName("Should handle hex strings with spaces")
        void shouldHandleHexStringsWithSpaces() {
            CompiledTemplate.BytesFieldWriter writer = new CompiledTemplate.BytesFieldWriter("test", "${value}", 0, 4, null);
            ByteBuffer buffer = ByteBuffer.allocate(10);
            
            writer.write(buffer, "DE AD BE EF");
            
            assertEquals((byte) 0xDE, buffer.get(0));
            assertEquals((byte) 0xAD, buffer.get(1));
            assertEquals((byte) 0xBE, buffer.get(2));
            assertEquals((byte) 0xEF, buffer.get(3));
        }

        @Test
        @DisplayName("Should convert number lists to bytes")
        void shouldConvertNumberListsToBytes() {
            CompiledTemplate.BytesFieldWriter writer = new CompiledTemplate.BytesFieldWriter("test", "${value}", 0, 4, null);
            ByteBuffer buffer = ByteBuffer.allocate(10);
            
            List<Integer> input = Arrays.asList(222, 173, 190, 239);
            writer.write(buffer, input);
            
            assertEquals((byte) 222, buffer.get(0));
            assertEquals((byte) 173, buffer.get(1));
            assertEquals((byte) 190, buffer.get(2));
            assertEquals((byte) 239, buffer.get(3));
        }

        @Test
        @DisplayName("Should pad bytes with zeros")
        void shouldPadBytesWithZeros() {
            CompiledTemplate.BytesFieldWriter writer = new CompiledTemplate.BytesFieldWriter("test", "${value}", 0, 6, null);
            ByteBuffer buffer = ByteBuffer.allocate(10);
            
            writer.write(buffer, "DEAD");
            
            assertEquals((byte) 0xDE, buffer.get(0));
            assertEquals((byte) 0xAD, buffer.get(1));
            assertEquals(0, buffer.get(2));
            assertEquals(0, buffer.get(3));
            assertEquals(0, buffer.get(4));
            assertEquals(0, buffer.get(5));
        }

        @Test
        @DisplayName("Should handle invalid hex strings")
        void shouldHandleInvalidHexStrings() {
            CompiledTemplate.BytesFieldWriter writer = new CompiledTemplate.BytesFieldWriter("test", "${value}", 0, 4, null);
            ByteBuffer buffer = ByteBuffer.allocate(10);
            
            assertThrows(IllegalArgumentException.class, () -> {
                writer.write(buffer, "INVALID");
            });
        }

        @Test
        @DisplayName("Should handle odd-length hex strings")
        void shouldHandleOddLengthHexStrings() {
            CompiledTemplate.BytesFieldWriter writer = new CompiledTemplate.BytesFieldWriter("test", "${value}", 0, 4, null);
            ByteBuffer buffer = ByteBuffer.allocate(10);
            
            assertThrows(IllegalArgumentException.class, () -> {
                writer.write(buffer, "DEA");
            });
        }
    }

    @Nested
    @DisplayName("FieldWriter Interface Tests")
    class FieldWriterInterfaceTests {

        @Test
        @DisplayName("Should return correct field properties")
        void shouldReturnCorrectFieldProperties() {
            CompiledTemplate.IntFieldWriter writer = new CompiledTemplate.IntFieldWriter("testField", "${testExpr}", 0, 42);
            
            assertEquals("testField", writer.getFieldName());
            assertEquals("${testExpr}", writer.getValueExpression());
            assertEquals(42, writer.getDefaultValue());
        }

        @Test
        @DisplayName("Should handle null value expression")
        void shouldHandleNullValueExpression() {
            CompiledTemplate.IntFieldWriter writer = new CompiledTemplate.IntFieldWriter("testField", null, 0, null);
            
            assertEquals("testField", writer.getFieldName());
            assertNull(writer.getValueExpression());
            assertNull(writer.getDefaultValue());
        }
    }
}