package io.dcbn.backend.evidenceFormula.model;

import com.google.common.base.Strings;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EvidenceFormulaValidator implements ConstraintValidator<FormulaConstraint, String> {

  @Override
  public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
    // TODO: This is just an example implementation.
    return !Strings.isNullOrEmpty(s);
  }

}
