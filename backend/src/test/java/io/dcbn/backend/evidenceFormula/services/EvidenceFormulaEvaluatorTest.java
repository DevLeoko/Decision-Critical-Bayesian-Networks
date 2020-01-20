package io.dcbn.backend.evidenceFormula.services;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import de.fraunhofer.iosb.iad.maritime.datamodel.Vessel;
import io.dcbn.backend.evidenceFormula.model.EvidenceFormula;
import io.dcbn.backend.evidenceFormula.services.exceptions.ParameterSizeMismatchException;
import io.dcbn.backend.evidenceFormula.services.exceptions.ParseException;
import io.dcbn.backend.evidenceFormula.services.exceptions.SymbolNotFoundException;
import io.dcbn.backend.evidenceFormula.services.exceptions.TypeMismatchException;
import io.dcbn.backend.evidenceFormula.services.visitors.FunctionWrapper;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EvidenceFormulaEvaluatorTest {

  private EvidenceFormulaEvaluator evaluator;

  private static Vessel vessel;
  private static Map<String, FunctionWrapper> testFunctions;

  @BeforeAll
  static void beforeAll() {
    vessel = new Vessel();
    vessel.setSpeed(5.0);
    vessel.setLongitude(12.0);

    testFunctions = new HashMap<>();
    testFunctions.put("inArea", new FunctionWrapper(Collections.singletonList(String.class), params -> "TEST_AREA".equals(params.get(0))));
    testFunctions.put("sum", new FunctionWrapper(Arrays.asList(Double.class, Double.class), params -> (double) params.get(0) + (double) params.get(1)));
    testFunctions.put("isTrue", new FunctionWrapper(Collections.singletonList(Boolean.class), params -> params.get(0)));
  }

  @BeforeEach
  void setUp() {
    evaluator = new EvidenceFormulaEvaluator(testFunctions);
  }

  @Test
  void testEvaluateWithJson() throws JsonProcessingException {
    JsonNode node = new JsonMapper().readTree("{\"speed\": 5, \"longitude\": 12}");
    EvidenceFormula formula = new EvidenceFormula();

    formula.setFormula("speed >= 5 & longitude = 2 + 2 * 5");
    assertTrue(evaluator.evaluate(node, formula));

    formula.setFormula("speed > 5 & longitude = 2 + 2 * 5");
    assertFalse(evaluator.evaluate(node, formula));
  }

  @Test
  void testEvaluateWithVessel() {
    EvidenceFormula formula = new EvidenceFormula();

    formula.setFormula("speed >= 5 & longitude = 2 + 2 * 5");
    assertTrue(evaluator.evaluate(vessel, formula));

    formula.setFormula("speed > 5 & longitude = 2 + 2 * 5");
    assertFalse(evaluator.evaluate(vessel, formula));
  }

  @Test
  void testUnknownFunctionThrowsException() {
    EvidenceFormula formula = new EvidenceFormula();
    formula.setFormula("true & unknownFunction()");

    SymbolNotFoundException ex = assertThrows(SymbolNotFoundException.class, () -> evaluator.evaluate(vessel, formula));
    assertEquals("unknownFunction", ex.getSymbolName());
    assertEquals(1, ex.getLine());
    assertEquals(7, ex.getCol());
  }

  @Test
  void testUnknownVariableThrowsException() {
    EvidenceFormula formula = new EvidenceFormula();
    formula.setFormula("true & unknownVariable > 5");

    TypeMismatchException ex = assertThrows(TypeMismatchException.class, () -> evaluator.evaluate(vessel, formula));
    assertEquals(Double.class, ex.getExpectedType());
    assertEquals(1, ex.getLine());
    assertEquals(7, ex.getCol());
  }

  @Test
  void testCallingFunctionWithWrongNumberOfArgumentsThrowsException() {
    EvidenceFormula formula = new EvidenceFormula();
    formula.setFormula("inArea(TEST_AREA, 2)");

    ParameterSizeMismatchException ex = assertThrows(
        ParameterSizeMismatchException.class, () -> evaluator.evaluate(vessel, formula));

    assertEquals("inArea", ex.getFunctionName());
    assertEquals(1, ex.getLine());
    assertEquals(0, ex.getCol());
    assertEquals(1, ex.getExpectedParameterSize());
    assertEquals(2, ex.getActualParameterSize());
  }

  @Test
  void testCallingFunctionWithWrongParameterTypeThrowsException() {
    EvidenceFormula formula = new EvidenceFormula();
    formula.setFormula("2 > 1 &  inArea(2)");

    TypeMismatchException ex = assertThrows(TypeMismatchException.class, () -> evaluator.evaluate(vessel, formula));
    assertEquals(String.class, ex.getExpectedType());
    assertEquals(Double.class, ex.getActualType());
    assertEquals(1, ex.getLine());
    assertEquals(9, ex.getCol());
  }

  @Test
  void testCallingFunctionWithCorrectParametersReturnsExpectedResults() {
    EvidenceFormula formula = new EvidenceFormula();

    formula.setFormula("inArea(TEST_AREA)");
    assertTrue(evaluator.evaluate(vessel, formula));

    formula.setFormula("inArea(AREA)");
    assertFalse(evaluator.evaluate(vessel, formula));
  }

  @Test
  void testCallingFunctionWithComplexArgumentsWorks() {
    EvidenceFormula formula = new EvidenceFormula();

    formula.setFormula("sum(2 * (2 + 3), sum(2, 1)) = 13");
    assertTrue(evaluator.evaluate(vessel, formula));

    formula.setFormula("sum(2 * (2 + 3), sum(2, 1)) = 12 & inArea(TEST_AREA)");
    assertFalse(evaluator.evaluate(vessel, formula));

    formula.setFormula("sum(2 * (2 - 5), sum(2, 1)) = -3 & inArea(TEST_AREA)");
    assertTrue(evaluator.evaluate(vessel, formula));

    formula.setFormula("sum(2 * (2 - 5), sum(2, 1)) = -3 & !inArea(TEST_AREA)");
    assertFalse(evaluator.evaluate(vessel, formula));
  }

  @Test
  void testNegationOfNumberExpressions() {
    EvidenceFormula formula = new EvidenceFormula();

    formula.setFormula("-sum(2, 1) = -3");
    assertTrue(evaluator.evaluate(vessel, formula));

    formula.setFormula("-speed = -5");
    assertTrue(evaluator.evaluate(vessel, formula));
  }

  @Test
  void testScientificNotation() {
    EvidenceFormula formula = new EvidenceFormula();

    formula.setFormula("2e2 = 200");
    assertTrue(evaluator.evaluate(vessel, formula));

    formula.setFormula("2e-2 = 0.02");
    assertTrue(evaluator.evaluate(vessel, formula));
  }

  @Test
  void testOtherOperators() {
    EvidenceFormula formula = new EvidenceFormula();

    formula.setFormula("5 / 2 = 2.5");
    assertTrue(evaluator.evaluate(vessel, formula));

    formula.setFormula("5 / 2 != 2.5");
    assertFalse(evaluator.evaluate(vessel, formula));

    formula.setFormula("5 / 2 < 2.5");
    assertFalse(evaluator.evaluate(vessel, formula));

    formula.setFormula("5 / 2 <= 2.5");
    assertTrue(evaluator.evaluate(vessel, formula));

    formula.setFormula("5 / 2 > 2.5");
    assertFalse(evaluator.evaluate(vessel, formula));

    formula.setFormula("5 / 2 >= 2.5");
    assertTrue(evaluator.evaluate(vessel, formula));

    formula.setFormula("true | false");
    assertTrue(evaluator.evaluate(vessel, formula));

    formula.setFormula("true & (false | true)");
    assertTrue(evaluator.evaluate(vessel, formula));

    formula.setFormula("true & !(false | true)");
    assertFalse(evaluator.evaluate(vessel, formula));

    formula.setFormula("true & !(isTrue(false) | true)");
    assertFalse(evaluator.evaluate(vessel, formula));

    formula.setFormula("true & sum(2, 1)");
    TypeMismatchException ex = assertThrows(TypeMismatchException.class, () -> evaluator.evaluate(vessel, formula));
    assertEquals(Boolean.class, ex.getExpectedType());
    assertEquals(Double.class, ex.getActualType());
    assertEquals(1, ex.getLine());
    assertEquals(7, ex.getCol());
  }

  @Test
  void testParseException() {
    EvidenceFormula formula = new EvidenceFormula();

    formula.setFormula("true & _Test");
    ParseException ex = assertThrows(ParseException.class, () -> evaluator.evaluate(vessel, formula));
    assertEquals("_", ex.getOffendingText());
    assertEquals(1, ex.getLine());
    assertEquals(7, ex.getCol());

    formula.setFormula("true & ()");
    ex = assertThrows(ParseException.class, () -> evaluator.evaluate(vessel, formula));
    assertEquals(")", ex.getOffendingText());
    assertEquals(1, ex.getLine());
    assertEquals(8, ex.getCol());

    formula.setFormula("true && ()");
    ex = assertThrows(ParseException.class, () -> evaluator.evaluate(vessel, formula));
    assertEquals("&", ex.getOffendingText());
    assertEquals(1, ex.getLine());

    formula.setFormula("true &");
    ex = assertThrows(ParseException.class, () -> evaluator.evaluate(vessel, formula));
    assertEquals("<EOF>", ex.getOffendingText());
    assertEquals(1, ex.getLine());
    assertEquals(6, ex.getCol());
  }
}