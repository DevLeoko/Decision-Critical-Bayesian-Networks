package io.dcbn.backend.graph;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

/**
 * This class represents the position of the node in the frontend graph-view.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@EqualsAndHashCode
public class Position {
    private Double x;
    private Double y;

    public boolean equalsApproximately(Position position) {
        return Math.abs(x - position.getX()) <= 0.01 && Math.abs(y - position.getY()) <= 0.01;
    }
}
