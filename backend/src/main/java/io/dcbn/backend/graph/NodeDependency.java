package io.dcbn.backend.graph;

import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
public class NodeDependency {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToMany
    @Getter
    private List<Node> parents;

    @OneToMany
    @Getter
    private List<Node> parentsTm1;

    private String probabilities;

    public NodeDependency() {
        //TODO make constructor
    }

    public NodeDependency(List<Node> parents, List<Node> parentsTm1, double[][] probabilities) {
        this.parents = parents;
        this.parentsTm1 = parentsTm1;
        this.probabilities = Arrays.deepToString(probabilities);
    }

    //Getters
    public List<Node> getParents() {
        return this.parents;
    }

    public List<Node> getParentsTm1() {
        return this.parentsTm1;
    }

    public double[][] getProbabilities() {
        //TODO nicht effizient
        String stringToArray = this.probabilities;
        stringToArray = stringToArray.replaceAll("\\[", "");
        String[] splitedStrings = stringToArray.split("\\]");
        ArrayList<String> listOfValues = new ArrayList<>();
        for (String string : splitedStrings) {
            string = string.replaceAll(",", "");
            if (string.charAt(0) == ' ') {
                string = string.substring(1, string.length());
            }
            listOfValues.add(string.split(" ")[0]);
            listOfValues.add(string.split(" ")[1]);
        }
        int stringIndex = 0;
        double[][] result = new double[listOfValues.size() / 2][2];
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[i].length; j++) {
                result[i][j] = Double.parseDouble(listOfValues.get(stringIndex));
                stringIndex++;
            }
        }
        return result;
    }

}
