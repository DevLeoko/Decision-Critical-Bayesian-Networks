package io.dcbn.backend.graph;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Graph {

  @Id
  @GeneratedValue
  private long id;

  @NotEmpty
  private String name;

  private int timeSlices;

  @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
  @Getter
  private List<Node> nodes;

  /**
   * Returns the node with the given name
   *
   * @param name the name of the node to find
   * @return the node with the given name
   */
  public Node getNodeByName(String name) {
    for (Node node : nodes) {
      if (node.getName().equals(name)) {
        return node;
      }
    }
    return null;
  }
}
