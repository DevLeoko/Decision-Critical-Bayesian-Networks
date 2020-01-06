package io.dcbn.backend.graph;

import lombok.Getter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
public class NodeDependency {

    @Id
    @GeneratedValue
    private long id;

    @OneToMany
    private List<Node> parents;

    @OneToMany
    private List<Node> parentsTm1;

    @Convert(converter = ProbabilityConverter.class)
    private double[][] probabilities;
}
