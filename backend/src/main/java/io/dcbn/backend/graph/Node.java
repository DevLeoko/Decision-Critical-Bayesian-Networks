package io.dcbn.backend.graph;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
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
    property = "id")
public class Node {

  @Id
  @GeneratedValue
  private long id;

  @NotEmpty
  private String name;

  @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
  private NodeDependency timeZeroDependency;

  @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
  private NodeDependency timeTDependency;

  //rgb color in Hexadecimal
  @MatchesPattern("#[a-fA-F0-9]{6}")
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
    public boolean isValueNode() {
        return false;
    }
}
