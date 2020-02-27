package io.dcbn.backend.evidenceformula.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;

@Entity
@Data
@NoArgsConstructor
@FormulaConstraint
public class EvidenceFormula {

    @Id
    @GeneratedValue
    private long id;

    @NotEmpty
    @Column(unique = true)
    private String name;

    private String formula;

    public EvidenceFormula(String name, String formula) {
        this.name = name;
        this.formula = formula;
    }

}
