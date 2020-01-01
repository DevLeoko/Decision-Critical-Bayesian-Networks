package io.dcbn.backend.graph;

import javax.persistence.*;

@Entity
public class Graph {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    private int timeSlices;
    @OneToMany
    private Node[] nodes;

    //Getters
    public Node[] getNodes() {
        return nodes;
    }

    /**
     * Returns the node with the given name
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
