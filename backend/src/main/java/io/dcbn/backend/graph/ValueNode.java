package io.dcbn.backend.graph;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This extension of a node represents a node with a evidence (Input) of with calculated values (Output).
 */
@AllArgsConstructor
@NoArgsConstructor
public class ValueNode extends Node {

    @Getter
    @Setter
    private double[][] value;

    public ValueNode(String name, String color, StateType stateType, Position position, double[][] value) {
        super.setName(name);
        super.setColor(color);
        super.setStateType(stateType);
        super.setPosition(position);
        this.value = value;
    }

    public ValueNode(Node node, double[][] value) {
        super.setName(node.getName());
        super.setColor(node.getColor());
        super.setStateType(node.getStateType());
        super.setPosition(node.getPosition());
        super.setTimeZeroDependency(node.getTimeZeroDependency());
        super.setTimeTDependency(node.getTimeTDependency());
        super.setEvidenceFormulaName(node.getEvidenceFormulaName());
        this.value = value;
    }

    /**
     * Method to indicate whether the node holds values.
     *
     * @return true because has values.
     */
    @Override
    public boolean isValueNode() {
        return true;
    }
}
