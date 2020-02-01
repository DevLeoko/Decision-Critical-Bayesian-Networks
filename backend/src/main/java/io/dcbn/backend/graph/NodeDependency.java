package io.dcbn.backend.graph;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
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
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id",
        scope = NodeDependency.class)
@Data
public class NodeDependency {

    @Id
    @GeneratedValue
    private long id;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    @JsonIdentityReference(alwaysAsId = true)
    private List<Node> parents;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    @JsonIdentityReference(alwaysAsId = true)
    private List<Node> parentsTm1;

    @Convert(converter = ProbabilityConverter.class)
    private double[][] probabilities;

    public NodeDependency(List<Node> parents, List<Node> parentsTm1, double[][] probabilities) {
        this.id = 0;
        this.parents = parents;
        this.parentsTm1 = parentsTm1;
        this.probabilities = probabilities;
    }
}
