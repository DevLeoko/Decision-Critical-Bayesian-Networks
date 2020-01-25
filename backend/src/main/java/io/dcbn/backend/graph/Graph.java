package io.dcbn.backend.graph;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;


/**
 * This class represents a Dynamic Bayesian Network (DBN)
 */
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

  @Min(1)
  @Max(1000)
  private int timeSlices;

  @Getter
  @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REFRESH}, orphanRemoval = true)
  @Valid
  private List<Node> nodes;
}
