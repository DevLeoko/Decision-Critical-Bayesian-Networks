package io.dcbn.backend.evidenceFormula.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class EvidenceFormula {

  @Id
  @NotEmpty
  private String name;
  @FormulaConstraint
  private String formula;

}
