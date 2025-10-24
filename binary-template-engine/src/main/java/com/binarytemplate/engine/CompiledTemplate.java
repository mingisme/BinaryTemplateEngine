package com.binarytemplate.engine;

import java.nio.ByteBuffer;

import java.nio.charset.Charset;
import java.util.List;

/**
 * Precompiled template for efficient binary generation
 */
public class CompiledTemplate {
    private final int totalSize;
    private final List<FieldWriter> fieldWriters;

    public CompiledTemplate(int totalSize, List<FieldWriter> fieldWriters) {
        this.totalSize = totalSize;
        this.fieldWriters = fieldWriters;
    }

    public int getTotalSize() {
        return totalSize;
    }

    public List<FieldWriter> getFieldWriters() {
        return fieldWriters;
    }

    public interface FieldWriter {
        void write(ByteBuffer buffer, Object value);
        String getFieldName();
        String getValueExpression();
        Object getDefaultValue();
    }

    public static class IntFieldWriter implements FieldWriter {
        private final String name;
        private final String valueExpression;
        private final int offset;
        private final Object defaultValue;

        public IntFieldWriter(String name, String valueExpression, int offset, Object defaultValue) {
            this.name = name;
            this.valueExpression = valueExpression;
            this.offset = offset;
            this.defaultValue = defaultValue;
        }

        @Override
        public void write(ByteBuffer buffer, Object value) {
            buffer.position(offset);
            if (value instanceof Number) {
                buffer.putInt(((Number) value).intValue());
            }
        }

        @Override
        public String getFieldName() {
            return name;
        }

        @Override
        public String getValueExpression() {
            return valueExpression;
        }

        @Override
        public Object getDefaultValue() {
            return defaultValue;
        }
    }

    public static class ShortFieldWriter implements FieldWriter {
        private final String name;
        private final String valueExpression;
        private final int offset;
        private final Object defaultValue;

        public ShortFieldWriter(String name, String valueExpression, int offset, Object defaultValue) {
            this.name = name;
            this.valueExpression = valueExpression;
            this.offset = offset;
            this.defaultValue = defaultValue;
        }

        @Override
        public void write(ByteBuffer buffer, Object value) {
            buffer.position(offset);
            if (value instanceof Number) {
                buffer.putShort(((Number) value).shortValue());
            }
        }

        @Override
        public String getFieldName() {
            return name;
        }

        @Override
        public String getValueExpression() {
            return valueExpression;
        }

        @Override
        public Object getDefaultValue() {
            return defaultValue;
        }
    }

    public static class ByteFieldWriter implements FieldWriter {
        private final String name;
        private final String valueExpression;
        private final int offset;
        private final Object defaultValue;

        public ByteFieldWriter(String name, String valueExpression, int offset, Object defaultValue) {
            this.name = name;
            this.valueExpression = valueExpression;
            this.offset = offset;
            this.defaultValue = defaultValue;
        }

        @Override
        public void write(ByteBuffer buffer, Object value) {
            buffer.position(offset);
            if (value instanceof Number) {
                buffer.put(((Number) value).byteValue());
            }
        }

        @Override
        public String getFieldName() {
            return name;
        }

        @Override
        public String getValueExpression() {
            return valueExpression;
        }

        @Override
        public Object getDefaultValue() {
            return defaultValue;
        }
    }

    public static class LongFieldWriter implements FieldWriter {
        private final String name;
        private final String valueExpression;
        private final int offset;
        private final Object defaultValue;

        public LongFieldWriter(String name, String valueExpression, int offset, Object defaultValue) {
            this.name = name;
            this.valueExpression = valueExpression;
            this.offset = offset;
            this.defaultValue = defaultValue;
        }

        @Override
        public void write(ByteBuffer buffer, Object value) {
            buffer.position(offset);
            if (value instanceof Number) {
                buffer.putLong(((Number) value).longValue());
            }
        }

        @Override
        public String getFieldName() {
            return name;
        }

        @Override
        public String getValueExpression() {
            return valueExpression;
        }

        @Override
        public Object getDefaultValue() {
            return defaultValue;
        }
    }

    public static class FloatFieldWriter implements FieldWriter {
        private final String name;
        private final String valueExpression;
        private final int offset;
        private final Object defaultValue;

        public FloatFieldWriter(String name, String valueExpression, int offset, Object defaultValue) {
            this.name = name;
            this.valueExpression = valueExpression;
            this.offset = offset;
            this.defaultValue = defaultValue;
        }

        @Override
        public void write(ByteBuffer buffer, Object value) {
            buffer.position(offset);
            if (value instanceof Number) {
                buffer.putFloat(((Number) value).floatValue());
            }
        }

        @Override
        public String getFieldName() {
            return name;
        }

        @Override
        public String getValueExpression() {
            return valueExpression;
        }

        @Override
        public Object getDefaultValue() {
            return defaultValue;
        }
    }

    public static class DoubleFieldWriter implements FieldWriter {
        private final String name;
        private final String valueExpression;
        private final int offset;
        private final Object defaultValue;

        public DoubleFieldWriter(String name, String valueExpression, int offset, Object defaultValue) {
            this.name = name;
            this.valueExpression = valueExpression;
            this.offset = offset;
            this.defaultValue = defaultValue;
        }

        @Override
        public void write(ByteBuffer buffer, Object value) {
            buffer.position(offset);
            if (value instanceof Number) {
                buffer.putDouble(((Number) value).doubleValue());
            }
        }

        @Override
        public String getFieldName() {
            return name;
        }

        @Override
        public String getValueExpression() {
            return valueExpression;
        }

        @Override
        public Object getDefaultValue() {
            return defaultValue;
        }
    }

    public static class BooleanFieldWriter implements FieldWriter {
        private final String name;
        private final String valueExpression;
        private final int offset;
        private final Object defaultValue;

        public BooleanFieldWriter(String name, String valueExpression, int offset, Object defaultValue) {
            this.name = name;
            this.valueExpression = valueExpression;
            this.offset = offset;
            this.defaultValue = defaultValue;
        }

        @Override
        public void write(ByteBuffer buffer, Object value) {
            buffer.position(offset);
            boolean boolValue = false;
            if (value instanceof Boolean) {
                boolValue = (Boolean) value;
            } else if (value instanceof Number) {
                boolValue = ((Number) value).intValue() != 0;
            } else if (value instanceof String) {
                boolValue = Boolean.parseBoolean((String) value);
            }
            buffer.put((byte) (boolValue ? 1 : 0));
        }

        @Override
        public String getFieldName() {
            return name;
        }

        @Override
        public String getValueExpression() {
            return valueExpression;
        }

        @Override
        public Object getDefaultValue() {
            return defaultValue;
        }
    }

    public static class CharFieldWriter implements FieldWriter {
        private final String name;
        private final String valueExpression;
        private final int offset;
        private final Object defaultValue;

        public CharFieldWriter(String name, String valueExpression, int offset, Object defaultValue) {
            this.name = name;
            this.valueExpression = valueExpression;
            this.offset = offset;
            this.defaultValue = defaultValue;
        }

        @Override
        public void write(ByteBuffer buffer, Object value) {
            buffer.position(offset);
            char charValue = '\0';
            if (value instanceof Character) {
                charValue = (Character) value;
            } else if (value instanceof String) {
                String str = (String) value;
                if (!str.isEmpty()) {
                    charValue = str.charAt(0);
                }
            } else if (value instanceof Number) {
                charValue = (char) ((Number) value).intValue();
            }
            buffer.putChar(charValue);
        }

        @Override
        public String getFieldName() {
            return name;
        }

        @Override
        public String getValueExpression() {
            return valueExpression;
        }

        @Override
        public Object getDefaultValue() {
            return defaultValue;
        }
    }

    public static class BytesFieldWriter implements FieldWriter {
        private final String name;
        private final String valueExpression;
        private final int offset;
        private final int length;
        private final Object defaultValue;

        public BytesFieldWriter(String name, String valueExpression, int offset, int length, Object defaultValue) {
            this.name = name;
            this.valueExpression = valueExpression;
            this.offset = offset;
            this.length = length;
            this.defaultValue = defaultValue;
        }

        @Override
        public void write(ByteBuffer buffer, Object value) {
            buffer.position(offset);
            byte[] bytes = null;
            
            if (value instanceof byte[]) {
                bytes = (byte[]) value;
            } else if (value instanceof String) {
                // Assume hex string format: "AABBCCDD"
                String hexStr = ((String) value).replaceAll("\\s+", "");
                bytes = hexStringToBytes(hexStr);
            } else if (value instanceof java.util.List) {
                // List of numbers
                java.util.List<?> list = (java.util.List<?>) value;
                bytes = new byte[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    Object item = list.get(i);
                    if (item instanceof Number) {
                        bytes[i] = ((Number) item).byteValue();
                    }
                }
            }
            
            if (bytes != null) {
                int writeLen = Math.min(bytes.length, length);
                buffer.put(bytes, 0, writeLen);
                // Pad with zeros if needed
                for (int i = writeLen; i < length; i++) {
                    buffer.put((byte) 0);
                }
            } else {
                // Write zeros
                for (int i = 0; i < length; i++) {
                    buffer.put((byte) 0);
                }
            }
        }
        
        private byte[] hexStringToBytes(String hex) {
            if (hex.length() % 2 != 0) {
                throw new IllegalArgumentException("Hex string must have even length: " + hex);
            }
            
            int len = hex.length();
            byte[] data = new byte[len / 2];
            
            try {
                for (int i = 0; i < len; i += 2) {
                    int digit1 = Character.digit(hex.charAt(i), 16);
                    int digit2 = Character.digit(hex.charAt(i + 1), 16);
                    
                    if (digit1 == -1 || digit2 == -1) {
                        throw new IllegalArgumentException("Invalid hex character in: " + hex);
                    }
                    
                    data[i / 2] = (byte) ((digit1 << 4) + digit2);
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to parse hex string: " + hex, e);
            }
            
            return data;
        }

        @Override
        public String getFieldName() {
            return name;
        }

        @Override
        public String getValueExpression() {
            return valueExpression;
        }

        @Override
        public Object getDefaultValue() {
            return defaultValue;
        }
    }

    public static class StringFieldWriter implements FieldWriter {
        private final String name;
        private final String valueExpression;
        private final int offset;
        private final int length;
        private final Charset charset;
        private final Object defaultValue;

        public StringFieldWriter(String name, String valueExpression, int offset, int length, String encoding, Object defaultValue) {
            this.name = name;
            this.valueExpression = valueExpression;
            this.offset = offset;
            this.length = length;
            this.charset = Charset.forName(encoding);
            this.defaultValue = defaultValue;
        }

        @Override
        public void write(ByteBuffer buffer, Object value) {
            buffer.position(offset);
            String str = value != null ? value.toString() : "";
            byte[] bytes = str.getBytes(charset);
            int writeLen = Math.min(bytes.length, length);
            buffer.put(bytes, 0, writeLen);
            // Pad with zeros if needed
            for (int i = writeLen; i < length; i++) {
                buffer.put((byte) 0);
            }
        }

        @Override
        public String getFieldName() {
            return name;
        }

        @Override
        public String getValueExpression() {
            return valueExpression;
        }

        @Override
        public Object getDefaultValue() {
            return defaultValue;
        }
    }
}
