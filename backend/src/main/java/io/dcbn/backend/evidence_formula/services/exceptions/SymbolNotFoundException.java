package io.dcbn.backend.evidence_formula.services.exceptions;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;

@JsonTypeName("symbolNotFound")
public class SymbolNotFoundException extends EvaluationException {

    @Getter
    private final String symbolName;

    public SymbolNotFoundException(String symbolName) {
        this(symbolName, 0, 0);
    }

    public SymbolNotFoundException(String symbolName, int line, int col) {
        super(line, col);
        this.symbolName = symbolName;
    }

}
