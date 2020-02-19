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
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @Valid
    private List<Node> nodes;

    public Graph(String name, int timeSlices, List<Node> nodes) {
        this.id = 0;
        this.name = name;
        this.timeSlices = timeSlices;
        this.nodes = nodes;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Graph)) {
            return false;
        }
        Graph graph = (Graph) o;
        if (id !=graph.getId() || !name.equals(graph.getName()) || timeSlices != graph.getTimeSlices() || nodes.size() != graph.getNodes().size()) {
            return false;
        }
        return graph.getNodes().containsAll(nodes);
    }
}
