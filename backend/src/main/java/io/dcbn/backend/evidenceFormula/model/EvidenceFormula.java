package io.dcbn.backend.evidenceFormula.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class EvidenceFormula {

  @Id
  private String name;
  @FormulaConstraint
  private String formula;

}
