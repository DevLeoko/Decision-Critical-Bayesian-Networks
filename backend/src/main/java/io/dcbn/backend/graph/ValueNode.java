package io.dcbn.backend.graph;


import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.swing.plaf.nimbus.State;

@AllArgsConstructor
public class ValueNode extends Node {
    @Getter
    private double[] value;

    public ValueNode(String name, String color, StateType stateType, Position position, double[] value) {
        super.setName(name);
        super.setColor(color);
        super.setStateType(stateType);
        super.setPosition(position);
        this.value = value;
    }

    @Override
    public boolean isValueNode() {
        return true;
    }
}
