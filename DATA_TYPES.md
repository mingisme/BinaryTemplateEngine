# Binary Template Engine - Data Types Reference

## Complete Data Type Support

The Binary Template Engine supports 10 data types covering all common binary data needs:

### Numeric Types

#### byte (1 byte)
- **Range**: -128 to 127
- **Use**: Small integers, flags, status codes
- **Example**: `{"type": "byte", "value": "${statusCode}"}`

#### short (2 bytes)
- **Range**: -32,768 to 32,767
- **Use**: Medium integers, counts, port numbers
- **Example**: `{"type": "short", "value": "${port}"}`

#### int (4 bytes)
- **Range**: -2,147,483,648 to 2,147,483,647
- **Use**: Standard integers, IDs, counts
- **Example**: `{"type": "int", "value": "${userId}"}`

#### long (8 bytes)
- **Range**: -9,223,372,036,854,775,808 to 9,223,372,036,854,775,807
- **Use**: Large integers, timestamps, file sizes
- **Example**: `{"type": "long", "value": "${timestamp}"}`

#### float (4 bytes)
- **Precision**: ~7 decimal digits
- **Use**: Single precision decimals, percentages, ratios
- **Example**: `{"type": "float", "value": "${price * 1.15}"}`

#### double (8 bytes)
- **Precision**: ~15 decimal digits
- **Use**: High precision decimals, scientific calculations
- **Example**: `{"type": "double", "value": "${latitude}"}`

### Boolean Type

#### boolean (1 byte)
- **Values**: true (1) or false (0)
- **Use**: Flags, switches, binary states
- **Example**: `{"type": "boolean", "value": "${isActive?c}"}`
- **Note**: Use `?c` for boolean expressions in FreeMarker

### Character Types

#### char (2 bytes)
- **Encoding**: UTF-16
- **Use**: Single characters, grade letters, symbols
- **Example**: `{"type": "char", "value": "${gradeChar}"}`

#### string (variable length)
- **Encoding**: UTF-8 (default) or UTF-16
- **Requires**: `length` parameter
- **Use**: Text data, names, descriptions
- **Example**: 
```json
{
  "type": "string",
  "value": "${username?upper_case}",
  "length": 50,
  "encoding": "UTF-8"
}
```

### Binary Type

#### bytes (variable length)
- **Format**: Raw byte array
- **Requires**: `length` parameter
- **Input formats**:
  - Hex string: `"DEADBEEF0102"`
  - Byte array: `[222, 173, 190, 239, 1, 2]`
- **Use**: Checksums, hashes, binary data, UUIDs
- **Example**:
```json
{
  "type": "bytes",
  "value": "${checksum}",
  "length": 16
}
```

## Type Conversion Examples

### Arithmetic with Type Conversion
```json
{
  "name": "discountedPrice",
  "value": "${originalPrice * 0.85}",
  "type": "float"
}
```

### Boolean from Comparison
```json
{
  "name": "isPremium",
  "value": "${(accountLevel > 5)?c}",
  "type": "boolean"
}
```

### String Transformation
```json
{
  "name": "upperName",
  "value": "${name?upper_case}",
  "type": "string",
  "length": 30
}
```

### Conditional Value Selection
```json
{
  "name": "statusCode",
  "value": "${(isActive)?then(1, 0)}",
  "type": "byte"
}
```

## Byte Order

All multi-byte numeric types use **Big Endian** byte order (network byte order):
- Most significant byte first
- Standard for network protocols
- Compatible with Java's ByteBuffer default

## Default Values

All types support default values when the source field is missing:

```json
{
  "name": "priority",
  "value": "${priority}",
  "type": "short",
  "defaultValue": 5
}
```

## Type Safety

The engine automatically handles type conversions:
- Numbers → Any numeric type (with range checking)
- Strings → char (first character)
- Booleans → byte (1 or 0)
- Strings → bytes (hex parsing)

## Complete Example

```json
{
  "totalSize": 100,
  "fields": [
    {"name": "magic", "value": "0x4D474943", "offset": 0, "type": "int"},
    {"name": "version", "value": "${ver}", "offset": 4, "type": "byte"},
    {"name": "flags", "value": "${enabled?c}", "offset": 5, "type": "boolean"},
    {"name": "count", "value": "${items.length()}", "offset": 6, "type": "short"},
    {"name": "price", "value": "${cost * 1.2}", "offset": 8, "type": "float"},
    {"name": "timestamp", "value": "${time}", "offset": 12, "type": "long"},
    {"name": "accuracy", "value": "${precision}", "offset": 20, "type": "double"},
    {"name": "grade", "value": "${letter}", "offset": 28, "type": "char"},
    {"name": "name", "value": "${user?upper_case}", "offset": 30, "type": "string", "length": 32},
    {"name": "hash", "value": "${md5}", "offset": 62, "type": "bytes", "length": 16}
  ]
}
```

## Best Practices

1. **Use appropriate types**: Don't use `long` when `int` suffices
2. **Consider precision**: Use `double` for financial calculations
3. **String lengths**: Always specify adequate length for strings
4. **Boolean format**: Use `?c` for boolean expressions in FreeMarker
5. **Hex strings**: Format bytes as uppercase hex without spaces
6. **Default values**: Provide defaults for optional fields
7. **Byte alignment**: Consider alignment for performance (though not enforced)

## Performance Notes

- **Precompile templates**: Compile once, use many times
- **Fixed sizes**: All types except `string` and `bytes` have fixed sizes
- **Zero padding**: Strings and bytes are zero-padded to specified length
- **No compression**: Data is written as-is without compression
