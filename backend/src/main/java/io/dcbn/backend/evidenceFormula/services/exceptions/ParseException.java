package io.dcbn.backend.evidenceFormula.services.exceptions;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;

@JsonTypeName("parse")
public class ParseException extends EvaluationException {

    @Getter
    private String offendingText;

    public ParseException(String offendingText) {
        this(offendingText, 0, 0);
    }

    public ParseException(String offendingText, int line, int col) {
        super(line, col);
        this.offendingText = offendingText;
    }
}
