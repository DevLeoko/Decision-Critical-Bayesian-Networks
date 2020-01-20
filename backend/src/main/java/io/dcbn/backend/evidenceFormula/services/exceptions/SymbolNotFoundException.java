package io.dcbn.backend.evidenceFormula.services.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SymbolNotFoundException extends EvaluationException {

  private String symbolName;
  private int line;
  private int col;

}
