package io.dcbn.backend.graph;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;

/**
 * This extension of a node represents a node with a evidence (Input) of with calculated values (Output).
 */
@AllArgsConstructor
@NoArgsConstructor
public class ValueNode extends Node {

    @Getter
    @Setter
    private double[][] value;

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

    public boolean checkValuesAreStates() {
        for (double[] timeSliceValue : value) {
            double sum = 0;
            for (double valueForState : timeSliceValue) {
                sum += valueForState;
                if (valueForState != Math.floor(valueForState)) {
                    return false;
                }
            }
            if (sum != 1.0) {
                return false;
            }
        }
        return true;
    }

    public int getIndexOfState(int timeSlice) {
        double[] arrayToCheck = value[timeSlice];
        for (int i = 0; i < arrayToCheck.length; i++) {
            if (arrayToCheck[i] == 1.0) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ValueNode)) {
            return false;
        }
        ValueNode node = (ValueNode) o;
        return  super.equals(o) && Arrays.deepEquals(value, node.getValue());
    }
}
