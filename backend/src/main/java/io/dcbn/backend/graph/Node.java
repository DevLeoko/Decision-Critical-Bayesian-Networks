package io.dcbn.backend.graph;

import io.dcbn.backend.evidenceFormula.model.EvidenceFormula;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.MatchesPattern;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Node {

    @Id
    @GeneratedValue
    private long id;

    @NotEmpty
    private String name;

    @OneToOne
    private NodeDependency timeZeroDependency;

    @OneToOne
    private NodeDependency timeTDependency;

    //rgb color in Hexadecimal
    @MatchesPattern("#[a-fA-F0-9]{6}")
    private String color;

    @ManyToOne
    private EvidenceFormula evidenceFormula;

    @NotNull
    private StateType stateType;

    @Embedded
    @NotNull
    private Position position;
}
