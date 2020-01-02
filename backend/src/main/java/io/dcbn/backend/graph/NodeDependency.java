package io.dcbn.backend.graph;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
public class NodeDependency {

    @OneToMany
    private Node[] parents;
    @OneToMany
    private Node[] parentsTm1;
    @OneToMany
    private double[][] probabilities;

    public NodeDependency() {
        //TODO make constructor
    }

    public NodeDependency(Node[] parents, Node[] parentsTm1, double[][] probabilities) {
        this.parents = parents;
        this.parentsTm1 = parentsTm1;
        this.probabilities = probabilities;
    }

    //Getters
    public Node[] getParents() {
        return this.parents;
    }

    public double[][] getProbabilities() {
        return this.probabilities;
    }

    public Node[] getParentsTm1() {
        return this.parentsTm1;
    }


}
