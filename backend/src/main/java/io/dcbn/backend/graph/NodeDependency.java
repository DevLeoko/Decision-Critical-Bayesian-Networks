package io.dcbn.backend.graph;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

/**
 * This class represents the dependencies of a node :Parents at same Time, at Time - 1 and the probabilities
 * in the same order required by amidst.
 */
@Entity
@NoArgsConstructor
@Getter
@Data
@EqualsAndHashCode
public class NodeDependency {

    @Id
    @GeneratedValue
    @JsonIgnore
    private long id;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    @JsonIdentityReference(alwaysAsId = true)
    private List<Node> parents;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    @JsonIdentityReference(alwaysAsId = true)
    private List<Node> parentsTm1;

    @Convert(converter = ProbabilityConverter.class)
    @Column(length = 65536)
    private double[][] probabilities;

    public NodeDependency(List<Node> parents, List<Node> parentsTm1, double[][] probabilities) {
        this.id = 0;
        this.parents = parents;
        this.parentsTm1 = parentsTm1;
        this.probabilities = probabilities;
    }
}
