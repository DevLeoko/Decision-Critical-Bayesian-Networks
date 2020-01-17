package io.dcbn.backend.evidenceFormula.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import de.fraunhofer.iosb.iad.maritime.datamodel.Vessel;
import io.dcbn.backend.evidenceFormula.model.EvidenceFormula;
import io.dcbn.backend.evidenceFormula.services.visitors.BooleanVisitor;
import io.dcbn.backend.evidenceFormula.services.visitors.FunctionWrapper;
import io.dcbn.backend.evidenceFormulas.FormulaLexer;
import io.dcbn.backend.evidenceFormulas.FormulaParser;
import java.util.Map;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service to evaluate {@link EvidenceFormula}.
 */
@Service
public class EvidenceFormulaEvaluator {

  private final Map<String, FunctionWrapper> functions;

  @Autowired
  public EvidenceFormulaEvaluator(
      Map<String, FunctionWrapper> functions) {
    this.functions = functions;
  }

  /**
   * Evaluates the given EvidenceFormula with the variables contained in the JsonNode.
   * @param json the json object containing the variables used during evaluation.
   * @param evidenceFormula the evidence formula to evaluate.
   * @return the boolean value of the evaluated formula.
   */
  public boolean evaluate(JsonNode json, EvidenceFormula evidenceFormula) {
    ObjectMapper mapper = new JsonMapper();
    Vessel vessel = mapper.convertValue(json, Vessel.class);
    return evaluateInternal(vessel, evidenceFormula);
  }

  /**
   * Evaluates the given EvidenceFormula with the variables contained in the Vessel.
   * @param vessel the vessel object containing the variables used during evaluation.
   * @param evidenceFormula the evidence formula to evaluate.
   * @return the boolean value of the evaluated formula.
   */
  public boolean evaluate(Vessel vessel, EvidenceFormula evidenceFormula) {
    return evaluateInternal(vessel, evidenceFormula);
  }

  // Converts an object to Map<String, Object> and passes it to the evaluator.
  private boolean evaluateInternal(Object object, EvidenceFormula evidenceFormula) {
    // FIXME: Wenn im JSON Objekt Ganzzahlen übergeben werden, gibt es einen Fehler, da Jackson diese zu Integers deserialisiert.
    ObjectMapper mapper = new JsonMapper();
    Map<String, Object> variables = mapper.convertValue(object,
        new TypeReference<Map<String, Object>>() {});
    return evaluateInternal(variables, evidenceFormula);
  }

  // Does the actual evaluation.
  private boolean evaluateInternal(Map<String, Object> variables, EvidenceFormula evidenceFormula) {
    FormulaLexer lexer = new FormulaLexer(CharStreams.fromString(evidenceFormula.getFormula()));
    FormulaParser parser = new FormulaParser(new CommonTokenStream(lexer));

    FormulaParser.FormulaContext tree = parser.formula();
    return new BooleanVisitor(variables, functions).visit(tree);
  }

}
