# Binary Template Engine - Advanced Examples

This document demonstrates the powerful expression capabilities using FreeMarker and JsonPath.

## Example 1: Basic Field Mapping

```json
{
  "name": "userId",
  "value": "${userId}",
  "offset": 0,
  "type": "int"
}
```

Input: `{"userId": 12345}`
Output: Writes `12345` as 4-byte integer at offset 0

## Example 2: Arithmetic Operations

```json
{
  "name": "doubledValue",
  "value": "${count * 2}",
  "offset": 0,
  "type": "int"
}
```

Input: `{"count": 50}`
Output: Writes `100` (50 × 2)

## Example 3: String Transformations

```json
{
  "name": "upperName",
  "value": "${username?upper_case}",
  "offset": 0,
  "type": "string",
  "length": 20
}
```

Input: `{"username": "john_doe"}`
Output: Writes `"JOHN_DOE"` as 20-byte string

### Other String Operations

- `${text?lower_case}` - Convert to lowercase
- `${name?cap_first}` - Capitalize first letter
- `${str?trim}` - Remove whitespace
- `${text?substring(0, 5)}` - Get substring
- `${str?length}` - Get string length

## Example 4: Conditional Logic

```json
{
  "name": "statusBonus",
  "value": "${(priority > 5)?then(status + 20, status + 10)}",
  "offset": 0,
  "type": "byte"
}
```

Input: `{"priority": 8, "status": 5}`
Output: Writes `25` (since priority > 5, uses status + 20)

## Example 5: JsonPath Queries

### Nested Object Access

```json
{
  "name": "userId",
  "value": "$.user.id",
  "offset": 0,
  "type": "int"
}
```

Input: `{"user": {"id": 999, "name": "Alice"}}`
Output: Writes `999`

### Array Access

```json
{
  "name": "firstPrice",
  "value": "$.items[0].price",
  "offset": 0,
  "type": "int"
}
```

Input: `{"items": [{"price": 100}, {"price": 200}]}`
Output: Writes `100`

### Array Length

```json
{
  "name": "itemCount",
  "value": "$.items.length()",
  "offset": 0,
  "type": "short"
}
```

Input: `{"items": [1, 2, 3, 4, 5]}`
Output: Writes `5`

## Example 6: Complex Calculations

```json
{
  "name": "totalPrice",
  "value": "${items[0].price + items[1].price + items[2].price}",
  "offset": 0,
  "type": "int"
}
```

Input: `{"items": [{"price": 100}, {"price": 200}, {"price": 300}]}`
Output: Writes `600`

## Example 7: Default Values

```json
{
  "name": "optionalField",
  "value": "${missingField!42}",
  "offset": 0,
  "type": "int"
}
```

Input: `{}` (field is missing)
Output: Writes `42` (default value)

## Example 8: Boolean to Integer

```json
{
  "name": "activeFlag",
  "value": "${isActive?then(1, 0)}",
  "offset": 0,
  "type": "byte"
}
```

Input: `{"isActive": true}`
Output: Writes `1`

## Example 9: String Concatenation

```json
{
  "name": "fullName",
  "value": "${firstName + \" \" + lastName}",
  "offset": 0,
  "type": "string",
  "length": 50
}
```

Input: `{"firstName": "John", "lastName": "Doe"}`
Output: Writes `"John Doe"`

## Example 10: Literal Constants

```json
{
  "name": "magicNumber",
  "value": "42",
  "offset": 0,
  "type": "int"
}
```

Output: Always writes `42` (constant value)

## Example 11: Percentage Calculations

```json
{
  "name": "discountedPrice",
  "value": "${originalPrice * 0.85}",
  "offset": 0,
  "type": "int"
}
```

Input: `{"originalPrice": 1000}`
Output: Writes `850` (15% discount)

## Example 12: Conditional Discount

```json
{
  "name": "finalPrice",
  "value": "${(amount > 1000)?then(amount * 0.9, amount)}",
  "offset": 0,
  "type": "int"
}
```

Input: `{"amount": 1500}`
Output: Writes `1350` (10% discount applied)

Input: `{"amount": 500}`
Output: Writes `500` (no discount)

## Complete Example Configuration

See `binary-template-sample/src/main/resources/advanced-config.json` for a comprehensive example that combines multiple techniques:

- Literal string constants
- Direct field references
- JsonPath nested object access
- Array operations
- Arithmetic calculations
- Conditional logic
- String transformations
- Boolean conversions

## FreeMarker Built-in Functions Reference

### String Functions
- `?upper_case` - Convert to uppercase
- `?lower_case` - Convert to lowercase
- `?cap_first` - Capitalize first letter
- `?capitalize` - Capitalize all words
- `?trim` - Remove whitespace
- `?length` - Get string length
- `?substring(start, end)` - Extract substring
- `?replace(old, new)` - Replace text

### Number Functions
- `?c` - Computer format (no grouping)
- `?string["0.00"]` - Format with pattern
- `?round` - Round to nearest integer
- `?floor` - Round down
- `?ceiling` - Round up
- `?abs` - Absolute value

### Boolean Functions
- `?then(ifTrue, ifFalse)` - Ternary operator
- `?string("yes", "no")` - Convert to string

### Comparison Operators
- `==` - Equal
- `!=` - Not equal
- `>` - Greater than
- `<` - Less than
- `>=` - Greater or equal
- `<=` - Less or equal

### Logical Operators
- `&&` - AND
- `||` - OR
- `!` - NOT

## JsonPath Syntax Reference

- `$` - Root object
- `.property` - Access property
- `['property']` - Access property (bracket notation)
- `[index]` - Access array element
- `[*]` - All array elements
- `..property` - Recursive descent
- `[?(@.price > 100)]` - Filter expression
- `.length()` - Array length
