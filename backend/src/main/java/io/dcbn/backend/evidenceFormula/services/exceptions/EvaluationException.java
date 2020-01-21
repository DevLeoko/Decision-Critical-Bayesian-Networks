package io.dcbn.backend.evidenceFormula.services.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"message", "localizedMessage", "cause", "suppressed", "stackTrace"})
public abstract class EvaluationException extends RuntimeException {
}
