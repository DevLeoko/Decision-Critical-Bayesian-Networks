package io.dcbn.backend.evidenceFormula.services.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

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
