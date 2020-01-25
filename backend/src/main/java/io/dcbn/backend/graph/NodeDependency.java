package io.dcbn.backend.graph;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@JsonIdentityInfo(
    generator = ObjectIdGenerators.PropertyGenerator.class,
    property = "id",
    scope = NodeDependency.class)
@Data
public class NodeDependency {

  @Id
  @GeneratedValue
  private long id;

  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
  @JsonIdentityReference(alwaysAsId = true)
  private List<Node> parents;

  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
  @JsonIdentityReference(alwaysAsId = true)
  private List<Node> parentsTm1;

  @Convert(converter = ProbabilityConverter.class)
  private double[][] probabilities;
  
}
