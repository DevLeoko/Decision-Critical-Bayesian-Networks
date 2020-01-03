package io.dcbn.backend.graph;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private double x;
    private double y;

    public Position() {
        //TODO make constructor
    }

    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
