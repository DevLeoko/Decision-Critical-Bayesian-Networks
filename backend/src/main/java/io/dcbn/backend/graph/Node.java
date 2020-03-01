package io.dcbn.backend.graph;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.MatchesPattern;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


/**
 * This class represents a node of a Dynamic Bayesian Network (DBN)
 */
@Data
@Entity
@NoArgsConstructor
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "name",
        scope = Node.class)
@JsonTypeInfo(defaultImpl = Node.class, use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(@JsonSubTypes.Type(value = ValueNode.class, name = "ValueNode"))
public class Node {

    @Id
    @GeneratedValue
    @JsonIgnore
    private long id;

    @NotEmpty
    private String name;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private NodeDependency timeZeroDependency;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private NodeDependency timeTDependency;

    //rgb color in Hexadecimal
    @MatchesPattern("#[a-fA-F0-9]{6}")
    @NotBlank
    private String color;

    private String evidenceFormulaName;

    @NotNull
    private StateType stateType;

    @Embedded
    @NotNull
    private Position position;

    public Node(String name, NodeDependency timeZeroDependency, NodeDependency timeTDependency, String color,
                String evidenceFormulaName, StateType stateType, Position position) {
        this.id = 0;
        this.name = name;
        this.timeZeroDependency = timeZeroDependency;
        this.timeTDependency = timeTDependency;
        this.color = color;
        this.evidenceFormulaName = evidenceFormulaName;
        this.stateType = stateType;
        this.position = position;
    }

    /**
     * Method to indicate whether the node holds values.
     *
     * @return false because no values.
     */
    @JsonIgnore
    public boolean isValueNode() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Node)) {
            return false;
        }
        Node node = (Node) o;
        if (id != node.getId() || !name.equals(node.getName()) || !timeTDependency.equals(node.getTimeTDependency())
                || !timeZeroDependency.equals(node.getTimeZeroDependency()) || !color.equals(node.getColor())
                || stateType != node.stateType
                || !position.equals(node.position)) {
            return false;
        }
        return (evidenceFormulaName == null || node.getEvidenceFormulaName() == null || evidenceFormulaName.equals(node.getEvidenceFormulaName()))
                && (evidenceFormulaName != null || node.getEvidenceFormulaName() == null)
                && (evidenceFormulaName == null || node.getEvidenceFormulaName() != null);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
