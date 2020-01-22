package io.dcbn.backend.evidenceFormula.services.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

public class SymbolNotFoundException extends EvaluationException {

  @Getter
  private String symbolName;

  public SymbolNotFoundException(String symbolName) {
    this(symbolName, 0, 0);
  }

  public SymbolNotFoundException(String symbolName, int line, int col) {
    super(line, col);
    this.symbolName = symbolName;
  }

}
