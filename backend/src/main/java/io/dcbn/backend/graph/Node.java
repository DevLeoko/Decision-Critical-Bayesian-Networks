package io.dcbn.backend.graph;

import javax.persistence.Entity;

@Entity
public class Node {

    private NodeDependency timeZeroDependency;
    private NodeDependency timeTDependency;
    private String name;
    private String evidenceName;
    //rgb color in Hexadecimal
    private String color;
    private final StateType STATE_TYPE = StateType.BOOLEAN;

    //Getters

    public String getName() {
        return name;
    }

    public StateType getStateType() {
        return STATE_TYPE;
    }

    public NodeDependency getTimeZeroDependency() {
        return timeZeroDependency;
    }

    public NodeDependency getTimeTDependency() {
        return timeTDependency;
    }


}
