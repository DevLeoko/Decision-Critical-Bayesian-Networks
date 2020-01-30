package io.dcbn.backend.graph;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;


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

    public Graph(String name, int timeSlices, List<Node> nodes) {
        this.id = 0;
        this.timeSlices = timeSlices;
        this.nodes = nodes;
    }
}
