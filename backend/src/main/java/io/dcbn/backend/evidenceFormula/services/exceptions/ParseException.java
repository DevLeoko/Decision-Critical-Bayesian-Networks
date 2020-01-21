package io.dcbn.backend.evidenceFormula.services.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParseException extends EvaluationException {

  private String offendingText;
  private int line;
  private int col;

}
