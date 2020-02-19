package io.dcbn.backend.graph;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

/**
 * This class represents the position of the node in the frontend graph-view.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Position {
    private Double x;
    private Double y;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Position)) {
            return false;
        }
        Position position = (Position) o;

        return Math.abs(x - position.getX()) <= 0.01 && Math.abs(y - position.getY()) <= 0.01;
    }
}
