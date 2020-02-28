package io.dcbn.backend.evidence_formula.model;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EvidenceFormulaValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FormulaConstraint {

    String message() default "Invalid Formula";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}

