package io.dcbn.backend.evidenceFormula.services.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TypeMismatchException extends EvaluationException {

  private Class<?> expectedType;
  private Class<?> actualType;
  private int line;
  private int col;

}
