package io.dcbn.backend.evidenceFormula.services.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParameterSizeMismatchException extends EvaluationException {

  private String functionName;
  private int expectedParameterSize;
  private int actualParameterSize;

  private int line;
  private int col;

}
