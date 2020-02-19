package io.dcbn.backend.graph;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof NodeDependency)) {
            return false;
        }
        NodeDependency dependency = (NodeDependency) o;
        if (id != dependency.getId() || parents.size() != dependency.getParents().size()
                || parentsTm1.size() != dependency.getParentsTm1().size()
                || probabilities.length != dependency.getProbabilities().length
                || probabilities[0].length != dependency.getProbabilities()[0].length) {
            return false;
        }
        if (!dependency.getParents().containsAll(parents)) {
            return false;
        }
        if (!dependency.getParentsTm1().containsAll(parentsTm1)) {
            return false;
        }
        for (int i = 0; i < probabilities.length; i++) {
            for (int j = 0; j < probabilities[0].length; j++) {
                if (Math.abs(probabilities[i][j] - dependency.getProbabilities()[i][j]) > 0.01) {
                    return false;
                }
            }
        }
        return true;
    }
}
