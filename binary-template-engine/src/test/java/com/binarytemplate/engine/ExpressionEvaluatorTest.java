package com.binarytemplate.engine;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ExpressionEvaluator
 */
class ExpressionEvaluatorTest {

    private ExpressionEvaluator evaluator;
    private JsonObject testData;

    @BeforeEach
    void setUp() {
        evaluator = new ExpressionEvaluator();

        String jsonData = """
                {
                  "name": "alice",
                  "age": 25,
                  "score": 95.5,
                  "active": true,
                  "user": {
                    "id": 12345,
                    "email": "alice@example.com"
                  },
                  "items": [
                    {"name": "Item1", "price": 100},
                    {"name": "Item2", "price": 200}
                  ]
                }
                """;
        testData = JsonParser.parseString(jsonData).getAsJsonObject();
    }

    @Nested
    @DisplayName("FreeMarker Expression Tests")
    class FreeMarkerTests {

        @Test
        @DisplayName("Should evaluate simple field reference")
        void shouldEvaluateSimpleFieldReference() {
            Object result = evaluator.evaluate("${name}", testData);
            assertEquals("alice", result);
        }

        @Test
        @DisplayName("Should evaluate arithmetic expressions")
        void shouldEvaluateArithmeticExpressions() {
            Object result1 = evaluator.evaluate("${age * 2}", testData);
            assertEquals(50L, result1);

            Object result2 = evaluator.evaluate("${score + 4.5}", testData);
            assertEquals(100L, result2); // FreeMarker returns Long for integer results
        }

        @Test
        @DisplayName("Should evaluate string transformations")
        void shouldEvaluateStringTransformations() {
            Object result1 = evaluator.evaluate("${name?upper_case}", testData);
            assertEquals("ALICE", result1);

            Object result2 = evaluator.evaluate("${name?cap_first}", testData);
            assertEquals("Alice", result2);
        }

        @Test
        @DisplayName("Should evaluate conditional expressions")
        void shouldEvaluateConditionalExpressions() {
            Object result1 = evaluator.evaluate("${(age > 18)?then('adult', 'minor')}", testData);
            assertEquals("adult", result1);

            Object result2 = evaluator.evaluate("${(age < 18)?then('minor', 'adult')}", testData);
            assertEquals("adult", result2);
        }

        @Test
        @DisplayName("Should evaluate boolean expressions")
        void shouldEvaluateBooleanExpressions() {
            Object result1 = evaluator.evaluate("${active?c}", testData);
            assertEquals("true", result1);

            Object result2 = evaluator.evaluate("${(score > 90)?c}", testData);
            assertEquals("true", result2);
        }

        @Test
        @DisplayName("Should evaluate nested object access")
        void shouldEvaluateNestedObjectAccess() {
            Object result = evaluator.evaluate("${user.id}", testData);
            assertEquals(12345L, result);
        }

        @Test
        @DisplayName("Should evaluate array access")
        void shouldEvaluateArrayAccess() {
            Object result1 = evaluator.evaluate("${items[0].name}", testData);
            assertEquals("Item1", result1);

            Object result2 = evaluator.evaluate("${items[1].price}", testData);
            assertEquals(200L, result2);
        }

        @Test
        @DisplayName("Should handle missing fields gracefully")
        void shouldHandleMissingFieldsGracefully() {
            Object result = evaluator.evaluate("${missingField!'default'}", testData);
            assertEquals("default", result);
        }

        @Test
        @DisplayName("Should return null on FreeMarker error to trigger default values")
        void shouldReturnNullOnFreeMarkerError() {
            Object result = evaluator.evaluate("${invalid.syntax.}", testData);
            assertNull(result);
        }
    }

    @Nested
    @DisplayName("JsonPath Expression Tests")
    class JsonPathTests {

        @Test
        @DisplayName("Should evaluate simple JsonPath expressions")
        void shouldEvaluateSimpleJsonPathExpressions() {
            Object result1 = evaluator.evaluate("$.name", testData);
            assertEquals("alice", result1);

            Object result2 = evaluator.evaluate("$.age", testData);
            assertEquals(25, result2);
        }

        @Test
        @DisplayName("Should evaluate nested JsonPath expressions")
        void shouldEvaluateNestedJsonPathExpressions() {
            Object result = evaluator.evaluate("$.user.id", testData);
            assertEquals(12345, result);
        }

        @Test
        @DisplayName("Should evaluate array JsonPath expressions")
        void shouldEvaluateArrayJsonPathExpressions() {
            Object result1 = evaluator.evaluate("$.items[0].name", testData);
            assertEquals("Item1", result1);

            Object result2 = evaluator.evaluate("$.items[1].price", testData);
            assertEquals(200, result2);
        }

        @Test
        @DisplayName("Should evaluate array length")
        void shouldEvaluateArrayLength() {
            Object result = evaluator.evaluate("$.items.length()", testData);
            assertEquals(2, result);
        }

        @Test
        @DisplayName("Should return null for invalid JsonPath")
        void shouldReturnNullForInvalidJsonPath() {
            Object result = evaluator.evaluate("$.invalid.path", testData);
            assertNull(result);
        }
    }

    @Nested
    @DisplayName("Literal Value Tests")
    class LiteralValueTests {

        @Test
        @DisplayName("Should return literal strings")
        void shouldReturnLiteralStrings() {
            Object result = evaluator.evaluate("literal_value", testData);
            assertEquals("literal_value", result);
        }

        @Test
        @DisplayName("Should return literal numbers")
        void shouldReturnLiteralNumbers() {
            Object result = evaluator.evaluate("42", testData);
            assertEquals("42", result);
        }

        @Test
        @DisplayName("Should handle null and empty expressions")
        void shouldHandleNullAndEmptyExpressions() {
            Object result1 = evaluator.evaluate(null, testData);
            assertNull(result1);

            Object result2 = evaluator.evaluate("", testData);
            assertNull(result2);
        }
    }

    @Nested
    @DisplayName("Type Conversion Tests")
    class TypeConversionTests {

        @Test
        @DisplayName("Should parse numeric results correctly")
        void shouldParseNumericResultsCorrectly() {
            // Integer result
            Object result1 = evaluator.evaluate("${age}", testData);
            assertEquals(25L, result1);

            // Double result
            Object result2 = evaluator.evaluate("${score}", testData);
            assertEquals(95.5, result2);
        }

        @Test
        @DisplayName("Should preserve string results")
        void shouldPreserveStringResults() {
            Object result = evaluator.evaluate("${name}", testData);
            assertEquals("alice", result);
            assertTrue(result instanceof String);
        }

        @Test
        @DisplayName("Should handle boolean results")
        void shouldHandleBooleanResults() {
            Object result = evaluator.evaluate("${active?c}", testData);
            assertEquals("true", result);
        }
    }

    @Nested
    @DisplayName("Complex Expression Tests")
    class ComplexExpressionTests {

        @Test
        @DisplayName("Should evaluate complex arithmetic")
        void shouldEvaluateComplexArithmetic() {
            Object result = evaluator.evaluate("${(age * 2) + (score / 2)}", testData);
            assertEquals(97.75, result);
        }

        @Test
        @DisplayName("Should evaluate string concatenation")
        void shouldEvaluateStringConcatenation() {
            Object result = evaluator.evaluate("${name + '_' + age}", testData);
            assertEquals("alice_25", result);
        }

        @Test
        @DisplayName("Should evaluate nested conditionals")
        void shouldEvaluateNestedConditionals() {
            Object result = evaluator.evaluate("${(age > 18)?then((score > 90)?then('excellent', 'good'), 'young')}",
                    testData);
            assertEquals("excellent", result);
        }
    }
}