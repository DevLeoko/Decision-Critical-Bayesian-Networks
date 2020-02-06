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
}
