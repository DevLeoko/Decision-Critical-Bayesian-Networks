package io.dcbn.backend.evidenceFormula.model;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = EvidenceFormulaExistsValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EvidenceFormulaExistsConstraint {

  String message() default "Non existent evidence formula.";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};

}
