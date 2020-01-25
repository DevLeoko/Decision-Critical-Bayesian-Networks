package io.dcbn.backend.evidenceFormula.services.exceptions;

import lombok.Getter;

public class TypeMismatchException extends EvaluationException {

  @Getter
  private Class<?> expectedType;

  @Getter
  private Class<?> actualType;

  public TypeMismatchException(Class<?> expectedType, Class<?> actualType) {
    this(expectedType, actualType, 0, 0);
  }

  public TypeMismatchException(Class<?> expectedType, Class<?> actualType, int line, int col) {
    super(line, col);
    this.expectedType = expectedType;
    this.actualType = actualType;
  }

}
