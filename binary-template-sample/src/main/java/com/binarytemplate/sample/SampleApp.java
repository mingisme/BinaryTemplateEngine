package com.binarytemplate.sample;

import com.binarytemplate.engine.BinaryTemplateEngine;
import com.binarytemplate.engine.CompiledTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Sample application demonstrating the Binary Template Engine
 */
public class SampleApp {

    public static void main(String[] args) {
        try {
            BinaryTemplateEngine engine = new BinaryTemplateEngine();

            System.out.println("╔════════════════════════════════════════════════════════════════╗");
            System.out.println("║     Binary Template Engine - Advanced Features Demo           ║");
            System.out.println("╚════════════════════════════════════════════════════════════════╝\n");

            // Demo 1: Basic Example
            basicExample(engine);

            // Demo 2: Advanced Features
            advancedExample(engine);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void basicExample(BinaryTemplateEngine engine) throws IOException {
        System.out.println("═══ DEMO 1: Basic Template with Expressions ═══\n");

        String configJson = loadResource("template-config.json");
        String sourceJson = loadResource("source-data.json");

        System.out.println("Configuration Features:");
        System.out.println("  • Arithmetic: ${priority * 2}");
        System.out.println("  • String transform: ${username?upper_case}");
        System.out.println("  • Conditional: ${(priority > 5)?then(status + 20, status + 10)}\n");

        CompiledTemplate template = engine.compile(configJson);
        byte[] result = engine.generate(template, sourceJson);

        printBasicOutput(result);

        // Save output
        Files.write(Paths.get("basic-output.bin"), result);
        System.out.println("Output saved to: basic-output.bin\n");
    }

    private static void advancedExample(BinaryTemplateEngine engine) throws IOException {
        System.out.println("\n═══ DEMO 2: Advanced Features with JsonPath ═══\n");

        String advancedConfig = loadResource("advanced-config.json");
        String advancedData = loadResource("advanced-data.json");

        System.out.println("Advanced Configuration Features:");
        System.out.println("  • Literal constants: \"PROTO\"");
        System.out.println("  • JsonPath nested access: $.user.id");
        System.out.println("  • JsonPath array length: $.items.length()");
        System.out.println("  • Array arithmetic: ${items[0].price + items[1].price}");
        System.out.println("  • Conditional discount: ${(totalAmount > 1000)?then(totalAmount * 0.9, totalAmount)}");
        System.out.println("  • Boolean conversion: ${user.active?then(1, 0)}\n");

        System.out.println("Input JSON:");
        System.out.println(advancedData);
        System.out.println();

        CompiledTemplate advancedTemplate = engine.compile(advancedConfig);
        byte[] advancedResult = engine.generate(advancedTemplate, advancedData);

        printAdvancedOutput(advancedResult);

        // Save output
        Files.write(Paths.get("advanced-output.bin"), advancedResult);
        System.out.println("\nOutput saved to: advanced-output.bin");

        // Demonstrate template reuse
        System.out.println("\n═══ DEMO 3: Reusing Compiled Template ═══\n");
        String newData = "{\"version\":5,\"user\":{\"id\":99999,\"name\":\"bob_jones\",\"active\":false},"
                + "\"items\":[{\"price\":300},{\"price\":400}],\"totalAmount\":700}";

        System.out.println("New Input: Different user with lower total amount");
        byte[] reusedResult = engine.generate(advancedTemplate, newData);
        printAdvancedOutput(reusedResult);

        // Demo 4: All Data Types
        allDataTypesExample(engine);
    }

    private static void allDataTypesExample(BinaryTemplateEngine engine) throws IOException {
        System.out.println("\n\n═══ DEMO 4: All Supported Data Types ═══\n");

        String typesConfig = loadResource("complete-types-config.json");
        String typesData = loadResource("complete-types-data.json");

        System.out.println("Supported Types:");
        System.out.println("  • byte (1 byte) - Signed 8-bit integer");
        System.out.println("  • short (2 bytes) - Signed 16-bit integer");
        System.out.println("  • int (4 bytes) - Signed 32-bit integer");
        System.out.println("  • long (8 bytes) - Signed 64-bit integer");
        System.out.println("  • float (4 bytes) - Single precision floating point");
        System.out.println("  • double (8 bytes) - Double precision floating point");
        System.out.println("  • boolean (1 byte) - True/False");
        System.out.println("  • char (2 bytes) - UTF-16 character");
        System.out.println("  • string (variable) - UTF-8/UTF-16 string");
        System.out.println("  • bytes (variable) - Raw byte array\n");

        CompiledTemplate typesTemplate = engine.compile(typesConfig);
        byte[] typesResult = engine.generate(typesTemplate, typesData);

        printCompleteTypesOutput(typesResult);

        Files.write(Paths.get("complete-types-output.bin"), typesResult);
        System.out.println("\nOutput saved to: complete-types-output.bin");
    }

    private static String loadResource(String filename) throws IOException {
        return new String(SampleApp.class.getClassLoader()
                .getResourceAsStream(filename).readAllBytes());
    }

    private static void printBasicOutput(byte[] data) {
        System.out.println("Binary output (" + data.length + " bytes):");

        // Print as hex
        System.out.print("Hex: ");
        for (int i = 0; i < Math.min(data.length, 50); i++) {
            System.out.printf("%02X ", data[i]);
            if ((i + 1) % 16 == 0)
                System.out.print("\n     ");
        }
        System.out.println();

        // Print field breakdown
        System.out.println("\nField breakdown:");
        System.out.println("  [0]    version:   " + data[0] + " (from ${protocolVersion})");
        System.out.println("  [1-4]  messageId: " + bytesToInt(data, 1) + " (from ${messageId})");
        System.out.println("  [5-6]  priority:  " + bytesToShort(data, 5) + " (from ${priority * 2})");
        System.out.println("  [7-26] username:  " + bytesToString(data, 7, 20) + " (from ${username?upper_case})");
        System.out.println("  [27-34] timestamp: " + bytesToLong(data, 27) + " (from ${timestamp})");
        System.out.println(
                "  [35]   status:    " + data[35] + " (from ${(priority > 5)?then(status + 20, status + 10)})");
    }

    private static void printAdvancedOutput(byte[] data) {
        System.out.println("Binary output (" + data.length + " bytes):");

        // Print as hex (first 60 bytes)
        System.out.print("Hex: ");
        for (int i = 0; i < Math.min(data.length, 60); i++) {
            System.out.printf("%02X ", data[i]);
            if ((i + 1) % 16 == 0)
                System.out.print("\n     ");
        }
        System.out.println();

        // Print field breakdown
        System.out.println("\nField breakdown:");
        System.out.println("  [0-4]   header:           \"" + bytesToString(data, 0, 5) + "\" (literal constant)");
        System.out.println("  [5]     version:          " + data[5] + " (from ${version})");
        System.out.println("  [6-9]   userId:           " + bytesToInt(data, 6) + " (from $.user.id - JsonPath)");
        System.out.println(
                "  [10-39] userName:         \"" + bytesToString(data, 10, 30) + "\" (from ${user.name?upper_case})");
        System.out.println(
                "  [40-41] itemCount:        " + bytesToShort(data, 40) + " (from $.items.length() - JsonPath)");
        System.out.println(
                "  [42-45] totalPrice:       " + bytesToInt(data, 42) + " (from ${items[0].price + items[1].price})");
        System.out.println("  [46-49] discountedPrice:  " + bytesToInt(data, 46)
                + " (from ${(totalAmount > 1000)?then(...)})");
        System.out.println("  [50]    statusCode:       " + data[50] + " (from ${user.active?then(1, 0)})");
    }

    private static int bytesToInt(byte[] bytes, int offset) {
        return ((bytes[offset] & 0xFF) << 24) |
                ((bytes[offset + 1] & 0xFF) << 16) |
                ((bytes[offset + 2] & 0xFF) << 8) |
                (bytes[offset + 3] & 0xFF);
    }

    private static short bytesToShort(byte[] bytes, int offset) {
        return (short) (((bytes[offset] & 0xFF) << 8) |
                (bytes[offset + 1] & 0xFF));
    }

    private static long bytesToLong(byte[] bytes, int offset) {
        return ((long) (bytes[offset] & 0xFF) << 56) |
                ((long) (bytes[offset + 1] & 0xFF) << 48) |
                ((long) (bytes[offset + 2] & 0xFF) << 40) |
                ((long) (bytes[offset + 3] & 0xFF) << 32) |
                ((long) (bytes[offset + 4] & 0xFF) << 24) |
                ((long) (bytes[offset + 5] & 0xFF) << 16) |
                ((long) (bytes[offset + 6] & 0xFF) << 8) |
                ((long) (bytes[offset + 7] & 0xFF));
    }

    private static String bytesToString(byte[] bytes, int offset, int length) {
        int end = offset;
        while (end < offset + length && bytes[end] != 0) {
            end++;
        }
        return new String(bytes, offset, end - offset);
    }

    private static float bytesToFloat(byte[] bytes, int offset) {
        int bits = bytesToInt(bytes, offset);
        return Float.intBitsToFloat(bits);
    }

    private static double bytesToDouble(byte[] bytes, int offset) {
        long bits = bytesToLong(bytes, offset);
        return Double.longBitsToDouble(bits);
    }

    private static char bytesToChar(byte[] bytes, int offset) {
        return (char) (((bytes[offset] & 0xFF) << 8) | (bytes[offset + 1] & 0xFF));
    }

    private static String bytesToHex(byte[] bytes, int offset, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = offset; i < offset + length && i < bytes.length; i++) {
            sb.append(String.format("%02X", bytes[i]));
        }
        return sb.toString();
    }

    private static void printCompleteTypesOutput(byte[] data) {
        System.out.println("Binary output (" + data.length + " bytes):");

        // Print as hex (first 70 bytes)
        System.out.print("Hex: ");
        for (int i = 0; i < Math.min(data.length, 70); i++) {
            System.out.printf("%02X ", data[i]);
            if ((i + 1) % 16 == 0)
                System.out.print("\n     ");
        }
        System.out.println();

        // Print field breakdown
        System.out.println("\nField breakdown:");
        System.out.println("  [0]     byte:             " + data[0]);
        System.out.println("  [1-2]   short:            " + bytesToShort(data, 1));
        System.out.println("  [3-6]   int:              " + bytesToInt(data, 3));
        System.out.println("  [7-14]  long:             " + bytesToLong(data, 7));
        System.out.println("  [15-18] float:            " + bytesToFloat(data, 15));
        System.out.println("  [19-26] double:           " + bytesToDouble(data, 19));
        System.out.println("  [27]    boolean:          " + (data[27] != 0 ? "true" : "false"));
        System.out.println("  [28-29] char:             '" + bytesToChar(data, 28) + "'");
        System.out.println("  [30-49] string:           \"" + bytesToString(data, 30, 20) + "\"");
        System.out.println("  [50-59] bytes:            " + bytesToHex(data, 50, 10));
        System.out.println("  [60-63] calculated float: " + bytesToFloat(data, 60) + " (price * 1.15)");
        System.out.println("  [64]    conditional bool: " + (data[64] != 0 ? "true" : "false") + " (score > 80)");
    }
}
