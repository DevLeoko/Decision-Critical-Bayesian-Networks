package io.dcbn.backend.evidenceFormula.model;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EvidenceFormulaValidator implements ConstraintValidator<FormulaConstraint, String> {

  @Override
  public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
    // TODO: This is just an example implementation.
    return s != null && s.contains("ship");
  }

}
