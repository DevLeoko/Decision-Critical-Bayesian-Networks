package io.dcbn.backend.evidenceFormula.model;

import io.dcbn.backend.evidenceFormula.repository.EvidenceFormulaRepository;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Service;

@Service
public class EvidenceFormulaExistsValidator implements
    ConstraintValidator<EvidenceFormulaExistsConstraint, String> {

  private final EvidenceFormulaRepository repository;

  public EvidenceFormulaExistsValidator(
      EvidenceFormulaRepository repository) {
    this.repository = repository;
  }

  public boolean isValid(String name, ConstraintValidatorContext context) {
    return name == null || repository.existsByName(name);
  }

}
