# Binary Template Engine Project

A multi-module Maven project that provides a binary template engine for generating binary byte arrays from JSON data.

## Project Structure

```
binary-template-parent/
├── binary-template-engine/    # Core engine module
│   └── src/main/java/com/binarytemplate/engine/
│       ├── BinaryTemplateEngine.java
│       ├── CompiledTemplate.java
│       └── TemplateConfig.java
└── binary-template-sample/    # Sample usage module
    ├── src/main/java/com/binarytemplate/sample/
    │   └── SampleApp.java
    └── src/main/resources/
        ├── template-config.json
        └── source-data.json
```

## Modules

### binary-template-engine

The core engine that converts JSON data to binary format using configuration templates.

**Key Features:**
- Precompiled templates for optimal performance
- Support for multiple data types (byte, short, int, long, string)
- Configurable field offsets and default values
- Big Endian byte order

See [binary-template-engine/README.md](binary-template-engine/README.md) for detailed documentation.

### binary-template-sample

Sample application demonstrating how to use the engine with:
- `template-config.json` - Basic configuration with FreeMarker expressions
- `source-data.json` - Simple input JSON data
- `advanced-config.json` - Advanced configuration showcasing JsonPath and complex expressions
- `advanced-data.json` - Complex nested JSON data
- `SampleApp.java` - Comprehensive demo showing all features

## Quick Start

### Build the project

```bash
mvn clean install
```

### Run the sample

```bash
cd binary-template-sample
mvn exec:java -Dexec.mainClass="com.binarytemplate.sample.SampleApp"
```

The demo will show:
1. **Basic Template** - FreeMarker expressions with arithmetic and string transformations
2. **Advanced Features** - JsonPath queries, nested object access, array operations, conditional logic
3. **Template Reuse** - Efficient precompiled template reuse with different data

### Use in your project

Add dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.binarytemplate</groupId>
    <artifactId>binary-template-engine</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Usage Example

```java
BinaryTemplateEngine engine = new BinaryTemplateEngine();

// Compile configuration once
CompiledTemplate template = engine.compile(configJson);

// Generate binary data (reuse template for multiple data sets)
byte[] output = engine.generate(template, dataJson);
```

## Requirements

- Java 11 or higher
- Maven 3.6 or higher

## License

See LICENSE file for details.
