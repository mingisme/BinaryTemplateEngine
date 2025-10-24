# Binary Template Engine

A high-performance binary template engine that generates binary byte arrays from JSON data using precompiled configuration templates.

## Features

- **Precompiled Templates**: Compile configurations once, reuse multiple times for optimal performance
- **Type Support**: int, short, byte, long, string with configurable encoding
- **Default Values**: Specify default values for missing fields
- **Fixed Layout**: Define exact byte offsets for each field

## Configuration Format

The configuration is a JSON string that defines the binary layout with FreeMarker-style expressions:

```json
{
  "totalSize": 100,
  "fields": [
    {
      "name": "fieldName",
      "value": "${sourceField}",
      "offset": 0,
      "type": "int|short|byte|long|string",
      "length": 10,
      "encoding": "UTF-8",
      "defaultValue": 0
    }
  ]
}
```

### Configuration Fields

- **totalSize** (required): Total size of the output binary array in bytes
- **fields** (required): Array of field definitions

### Field Definition

- **name** (required): Field identifier for documentation
- **value** (optional): Expression to extract/compute value from JSON data (FreeMarker-style)
  - Simple reference: `${fieldName}`
  - Arithmetic: `${count + 10}`, `${value * 2}`, `${total - offset}`
  - String concat: `${"prefix" + fieldName + "suffix"}`
  - If omitted, uses `name` as the field key
- **offset** (required): Byte offset where this field starts (0-based)
- **type** (required): Data type - `byte`, `short`, `int`, `long`, `float`, `double`, `boolean`, `char`, `string`, `bytes`
- **length** (required for string): Maximum length in bytes for string fields
- **encoding** (optional, default: UTF-8): Character encoding for string fields
- **defaultValue** (optional): Default value if field is missing in JSON data

## Usage

### Basic Usage (Direct Generation)

```java
BinaryTemplateEngine engine = new BinaryTemplateEngine();

String config = "{\"totalSize\":20,\"fields\":[...]}";
String data = "{\"id\":123,\"name\":\"Test\"}";

byte[] result = engine.generate(config, data);
```

### Optimized Usage (Precompiled Template)

```java
BinaryTemplateEngine engine = new BinaryTemplateEngine();

// Compile once
String config = "{\"totalSize\":20,\"fields\":[...]}";
CompiledTemplate template = engine.compile(config);

// Reuse many times
String data1 = "{\"id\":123,\"name\":\"Test1\"}";
byte[] result1 = engine.generate(template, data1);

String data2 = "{\"id\":456,\"name\":\"Test2\"}";
byte[] result2 = engine.generate(template, data2);
```

## Example Configuration with All Data Types

```json
{
  "totalSize": 100,
  "fields": [
    {
      "name": "version",
      "value": "${protocolVersion}",
      "offset": 0,
      "type": "byte"
    },
    {
      "name": "id",
      "value": "${messageId}",
      "offset": 1,
      "type": "int"
    },
    {
      "name": "price",
      "value": "${basePrice * 1.15}",
      "offset": 5,
      "type": "float"
    },
    {
      "name": "isActive",
      "value": "${status == 'active'}",
      "offset": 9,
      "type": "boolean"
    },
    {
      "name": "grade",
      "value": "${gradeChar}",
      "offset": 10,
      "type": "char"
    },
    {
      "name": "name",
      "value": "${username?upper_case}",
      "offset": 12,
      "type": "string",
      "length": 20,
      "encoding": "UTF-8"
    },
    {
      "name": "checksum",
      "value": "${checksumBytes}",
      "offset": 32,
      "type": "bytes",
      "length": 16
    },
    {
      "name": "timestamp",
      "value": "${timestamp}",
      "offset": 48,
      "type": "long"
    },
    {
      "name": "accuracy",
      "value": "${precision}",
      "offset": 56,
      "type": "double"
    }
  ]
}
```

### Expression Examples

The engine uses **FreeMarker** for powerful template expressions and **JsonPath** for complex JSON queries.

#### FreeMarker Expressions

- **Direct field reference**: `${fieldName}` - Gets value from JSON field
- **Arithmetic operations**: 
  - `${count + 10}` - Add 10 to count
  - `${value * 2}` - Multiply by 2
  - `${total - offset}` - Subtraction
  - `${amount / 100}` - Division
- **String operations**: 
  - `${"User: " + username}` - Concatenation
  - `${username?upper_case}` - Convert to uppercase
  - `${text?lower_case}` - Convert to lowercase
  - `${name?cap_first}` - Capitalize first letter
- **Conditionals**: 
  - `${status == 1 ? "active" : "inactive"}` - Ternary operator
  - `${(priority > 5)?then(10, 5)}` - Conditional value
- **Number formatting**:
  - `${price?string["0.00"]}` - Format decimal
  - `${count?c}` - Computer format (no grouping)
- **Default values**:
  - `${field!"default"}` - Use default if field is missing
  - `${field!0}` - Default to 0

#### JsonPath Queries

- **Simple path**: `$.user.name` - Navigate nested objects
- **Array access**: `$.items[0].price` - Access array elements
- **Array filter**: `$.items[?(@.price > 100)]` - Filter arrays
- **Wildcard**: `$.items[*].name` - Get all names from array
- **Deep scan**: `$..price` - Find all price fields recursively

#### Combined Examples

```json
{
  "name": "header",
  "value": "${$.config.version * 10}",
  "type": "int"
}
```

- **Literal values**: `"constant"` - Use a constant string (without ${})

## Supported Data Types

| Type | Size | Description |
|------|------|-------------|
| **byte** | 1 byte | Signed 8-bit integer (-128 to 127) |
| **short** | 2 bytes | Signed 16-bit integer (-32,768 to 32,767) |
| **int** | 4 bytes | Signed 32-bit integer |
| **long** | 8 bytes | Signed 64-bit integer |
| **float** | 4 bytes | Single precision floating point |
| **double** | 8 bytes | Double precision floating point |
| **boolean** | 1 byte | True (1) or False (0) |
| **char** | 2 bytes | Single UTF-16 character |
| **string** | Variable | UTF-8/UTF-16 string (requires `length` parameter) |
| **bytes** | Variable | Raw byte array (requires `length` parameter) |

## Notes

- All numeric types use Big Endian byte order
- String fields are padded with zeros if the actual string is shorter than the specified length
- String fields are truncated if the actual string is longer than the specified length
- Unspecified bytes in the output array are initialized to zero
