package io.dcbn.backend.evidenceFormula.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvidenceFormula {

  @Id
  @GeneratedValue
  private long id;

  @NotEmpty
  @Column(unique = true)
  private String name;

  @FormulaConstraint
  private String formula;

}
