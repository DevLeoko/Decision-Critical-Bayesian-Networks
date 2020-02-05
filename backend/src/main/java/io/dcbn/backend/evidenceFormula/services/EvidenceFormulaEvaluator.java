package io.dcbn.backend.evidenceFormula.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import de.fraunhofer.iosb.iad.maritime.datamodel.AreaOfInterest;
import de.fraunhofer.iosb.iad.maritime.datamodel.Vessel;
import io.dcbn.backend.evidenceFormula.model.EvidenceFormula;
import io.dcbn.backend.evidenceFormula.services.exceptions.ParseException;
import io.dcbn.backend.evidenceFormula.services.visitors.BooleanVisitor;
import io.dcbn.backend.evidenceFormulas.FormulaLexer;
import io.dcbn.backend.evidenceFormulas.FormulaParser;
import org.antlr.v4.runtime.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service to evaluate {@link EvidenceFormula}.
 */
@Service
public class EvidenceFormulaEvaluator {

    private static class ThrowingErrorListener extends BaseErrorListener {

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
                                int charPositionInLine, String msg, RecognitionException e)
                throws ParseException {
            Token token = (Token) offendingSymbol;

            ParseException exception = new ParseException(token.getText());
            exception.setLine(line);
            exception.setCol(charPositionInLine);
            throw exception;
        }
    }

    private final FunctionProvider functions;

    @Autowired
    public EvidenceFormulaEvaluator(
            FunctionProvider functions) {
        this.functions = functions;
    }

    public boolean evaluate(JsonNode json, EvidenceFormula evidenceFormula) {
        return evaluate(0, json, evidenceFormula);
    }

    /**
     * Evaluates the given EvidenceFormula with the variables contained in the JsonNode.
     *
     * @param timeSlice       the current time slice.
     * @param json            the json object containing the variables used during evaluation.
     * @param evidenceFormula the evidence formula to evaluate.
     * @return the boolean value of the evaluated formula.
     */
    public boolean evaluate(int timeSlice, JsonNode json, EvidenceFormula evidenceFormula) {
        ObjectMapper mapper = new JsonMapper();
        Vessel vessel = mapper.convertValue(json, Vessel.class);
        return evaluate(timeSlice, vessel, evidenceFormula);
    }

    public boolean evaluate(Vessel vessel, EvidenceFormula evidenceFormula) {
        return evaluate(0, vessel, evidenceFormula);
    }

    /**
     * Evaluates the given EvidenceFormula with the variables contained in the Vessel.
     *
     * @param timeSlice       the current time slice.
     * @param vessel          the vessel object containing the variables used during evaluation.
     * @param evidenceFormula the evidence formula to evaluate.
     * @return the boolean value of the evaluated formula.
     */
    public boolean evaluate(int timeSlice, Vessel vessel, EvidenceFormula evidenceFormula) {
        functions.setCurrentVessel(vessel);
        return evaluateInternal(timeSlice, vessel, evidenceFormula);
    }

    // Converts an object to Map<String, Object> and passes it to the evaluator.
    private boolean evaluateInternal(int timeSlice, Object object, EvidenceFormula evidenceFormula) {
        functions.setCurrentTimeSlice(timeSlice);
        ObjectMapper mapper = new JsonMapper();
        Map<String, Object> variables = mapper.convertValue(object,
                new TypeReference<Map<String, Object>>() {
                });
        return evaluateInternal(variables.entrySet().stream().filter(entry -> entry.getValue() != null).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)), evidenceFormula);
    }

    // Does the actual evaluation.
    private boolean evaluateInternal(Map<String, Object> variables, EvidenceFormula evidenceFormula) {
        FormulaLexer lexer = new FormulaLexer(CharStreams.fromString(evidenceFormula.getFormula()));
        FormulaParser parser = new FormulaParser(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(new ThrowingErrorListener());

        FormulaParser.FormulaContext tree = parser.formula();
        return new BooleanVisitor(variables, functions).visit(tree);
    }

    public Set<Vessel> getCorrelatedVessels() {
        return functions.getCorrelatedVessels();
    }

    public Set<AreaOfInterest> getCorrelatedAois() {
        return functions.getCorrelatedAois();
    }

    public void reset() {
        functions.reset();
    }
}
