package io.dcbn.backend.graph;

import lombok.Data;

import javax.persistence.Embeddable;

@Data
@Embeddable
public class Position {
    private double x;
    private double y;
}
