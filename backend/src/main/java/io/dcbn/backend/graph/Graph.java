package io.dcbn.backend.graph;

import lombok.*;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.List;


/**
 * This class represents a Dynamic Bayesian Network (DBN)
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode
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
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @Valid
    private List<Node> nodes;

    public Graph(String name, int timeSlices, List<Node> nodes) {
        this.id = 0;
        this.name = name;
        this.timeSlices = timeSlices;
        this.nodes = nodes;
    }
}
