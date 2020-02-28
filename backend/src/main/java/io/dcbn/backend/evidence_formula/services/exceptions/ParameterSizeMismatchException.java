package io.dcbn.backend.evidence_formula.services.exceptions;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;

@JsonTypeName("parameterSize")
public class ParameterSizeMismatchException extends EvaluationException {

    @Getter
    private final String functionName;

    @Getter
    private final int expectedParameterSize;

    @Getter
    private final int actualParameterSize;

    public ParameterSizeMismatchException(String functionName, int expectedParameterSize, int actualParameterSize) {
        this(functionName, expectedParameterSize, actualParameterSize, 0, 0);
    }

    public ParameterSizeMismatchException(String functionName, int expectedParameterSize,
                                          int actualParameterSize, int line, int col) {
        super(line, col);
        this.functionName = functionName;
        this.expectedParameterSize = expectedParameterSize;
        this.actualParameterSize = actualParameterSize;
    }
}
