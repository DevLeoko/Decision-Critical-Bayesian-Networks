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

    //Getters

    public Node[] getParents() {
        return this.parents;
    }

    public double[][] getProbabilities() {
        return this.probabilities;
    }

    public Node[] getParentsTm1() {
        return parentsTm1;
    }


}
