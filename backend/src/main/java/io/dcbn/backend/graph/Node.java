package io.dcbn.backend.graph;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
public class Node {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne
    @Getter
    private NodeDependency timeZeroDependency;

    @OneToOne
    @Getter
    @Setter
    private NodeDependency timeTDependency;

    //rgb color in Hexadecimal
    private String color;
    @Getter
    private String name;
    private String evidenceFormulaName;

    @Getter
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
        this.evidenceFormulaName = evidenceName;
        this.color = color;
        this.position = position;
        this.stateType = stateType;
    }

}
