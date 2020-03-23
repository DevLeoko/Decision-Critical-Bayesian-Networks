package io.dcbn.backend.evidence_formula.services.exceptions;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;

@JsonTypeName("typeMismatch")
public class TypeMismatchException extends EvaluationException {

    @Getter
    private final Class<?> expectedType;

    @Getter
    private final Class<?> actualType;

    public TypeMismatchException(Class<?> expectedType, Class<?> actualType) {
        this(expectedType, actualType, 0, 0);
    }

    public TypeMismatchException(Class<?> expectedType, Class<?> actualType, int line, int col) {
        super(line, col);
        this.expectedType = expectedType;
        this.actualType = actualType;
    }

}
