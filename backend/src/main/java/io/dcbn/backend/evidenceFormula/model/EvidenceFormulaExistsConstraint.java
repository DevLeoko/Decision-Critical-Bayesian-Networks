package io.dcbn.backend.evidenceFormula.model;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EvidenceFormulaExistsValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EvidenceFormulaExistsConstraint {

    String message() default "Non existent evidence formula.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
