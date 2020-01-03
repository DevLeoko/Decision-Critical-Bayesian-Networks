package io.dcbn.backend.graph;

import javax.persistence.*;

@Entity
public class Node {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne
    private NodeDependency timeZeroDependency;

    @OneToOne
    private NodeDependency timeTDependency;

    //rgb color in Hexadecimal
    private String color;
    private String name;
    private String evidenceName;

    private StateType stateType;

    @ManyToOne
    private Position position;

    public Node() {
        //TODO make constructor
    }

    public Node(String name, NodeDependency timeZeroDependency, NodeDependency timeTDependency, StateType stateType, String evidenceName, String color, Position position) {
        this.name = name;
        this.timeZeroDependency = timeZeroDependency;
        this.timeTDependency = timeTDependency;
        this.evidenceName = evidenceName;
        this.color = color;
        this.position = position;
        this.stateType = stateType;
    }

    //Getters
    public String getName() {
        return name;
    }

    public StateType getStateType() {
        return stateType;
    }

    public NodeDependency getTimeZeroDependency() {
        return timeZeroDependency;
    }

    public NodeDependency getTimeTDependency() {
        return timeTDependency;
    }

    //Setters

    public void setTimeTDependency(NodeDependency timeTDependency) {
        this.timeTDependency = timeTDependency;
    }


}
