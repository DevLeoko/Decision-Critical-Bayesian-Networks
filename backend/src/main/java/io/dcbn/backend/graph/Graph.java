package io.dcbn.backend.graph;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Entity
public class Graph {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotEmpty
    private String name;
    private int timeSlices;

    @OneToMany
    private List<Node> nodes;

    public Graph() {
        //TODO make constructor
    }

    public Graph(String name, int timeSlices, List<Node> nodes) {
        this.name = name;
        this.timeSlices = timeSlices;
        this.nodes = nodes;
    }

    //Getters
    public List<Node> getNodes() {
        return this.nodes;
    }

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
