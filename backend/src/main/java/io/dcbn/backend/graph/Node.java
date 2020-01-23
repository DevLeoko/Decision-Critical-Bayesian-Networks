package io.dcbn.backend.graph;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.dcbn.backend.evidenceFormula.model.EvidenceFormula;
import javax.annotation.MatchesPattern;
import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(
    generator = ObjectIdGenerators.PropertyGenerator.class,
    property = "name",
    scope = Node.class)
@JsonTypeInfo(defaultImpl = Node.class, use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(@JsonSubTypes.Type(value = ValueNode.class, name = "ValueNode"))
public class Node {

  @Id
  @GeneratedValue
  private long id;

  @NotEmpty
  private String name;

  @OneToOne(cascade = CascadeType.ALL)
  private NodeDependency timeZeroDependency;

  @OneToOne(cascade = CascadeType.ALL)
  private NodeDependency timeTDependency;

  //rgb color in Hexadecimal
  @MatchesPattern("#[a-fA-F0-9]{6}")
  @NotBlank
  private String color;

  @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
  private EvidenceFormula evidenceFormula;

  @NotNull
  private StateType stateType;

  @Embedded
  @NotNull
  private Position position;

  /**
   * Method to indicate whether the node holds values.
   *
   * @return false because no values.
   */
  @JsonIgnore
  public boolean isValueNode() {
    return false;
  }
}
