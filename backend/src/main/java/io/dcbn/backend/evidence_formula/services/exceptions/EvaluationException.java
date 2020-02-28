package io.dcbn.backend.evidence_formula.services.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
@JsonIgnoreProperties({"message", "localizedMessage", "cause", "suppressed", "stackTrace"})
public abstract class EvaluationException extends RuntimeException {

    @Getter
    @Setter
    protected int line;

    @Getter
    @Setter
    protected int col;

    public EvaluationException(int line, int col) {
        this.line = line;
        this.col = col;
    }
}
