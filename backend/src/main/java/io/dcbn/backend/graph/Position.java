package io.dcbn.backend.graph;

import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents the position of the node in the frontend graph-view.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Position {

  private double x;
  private double y;
}
