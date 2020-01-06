package io.dcbn.backend.graph;

import eu.amidst.core.distribution.Multinomial;
import eu.amidst.core.distribution.Multinomial_MultinomialParents;
import eu.amidst.core.models.ParentSet;
import eu.amidst.core.variables.Variable;
import eu.amidst.dynamic.models.DynamicBayesianNetwork;
import eu.amidst.dynamic.models.DynamicDAG;
import eu.amidst.dynamic.variables.DynamicVariables;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class represents the adapter from Graph to DynamicBayesianNetwork
 */
public class AmidstGraphAdapter {

    @Getter
    private DynamicBayesianNetwork dbn;

    private ArrayList<Variable> variables;

    /**
     * The constructor takes as input a (@link Graph) and adapt it to an amidst @link DynamicBayesianNetwork
     * with all the data inside(TODO link)
     *
     * @param graph the input graph to adapt
     */
    public AmidstGraphAdapter(Graph graph) {
        Graph graphDcbn = graph;
        DynamicVariables dynamicVariables = new DynamicVariables();
        variables = new ArrayList<>();

        //---------------------------------Creating all the variables------------------------------------------
        for (Node node : graphDcbn.getNodes()) {
            List<String> states = Arrays.asList(node.getStateType().getStates());
            Variable variable = dynamicVariables.newMultinomialDynamicVariable(node.getName(), states);
            variables.add(variable);
        }

        DynamicDAG dynamicDAG = new DynamicDAG(dynamicVariables);

        //----------------Setting parents for each variable (Creating structure of DBN)--------------------------
        for (Variable variable : variables) {
            //--------TIME 0-------------
            ParentSet variableParentSet0 = dynamicDAG.getParentSetTime0(variable);
            Node node = graphDcbn.getNodeByName(variable.getName());
            for (Node parentNode : node.getTimeZeroDependency().getParents()) {
                Variable parentVariable = getVariableByName(parentNode.getName());
                variableParentSet0.addParent(parentVariable);
            }
            //--------TIME T-------------
            ParentSet variableParentSetT = dynamicDAG.getParentSetTimeT(variable);
            //Time T
            for (Node parentNode : node.getTimeTDependency().getParents()) {
                Variable parentVariable = getVariableByName(parentNode.getName());
                variableParentSetT.addParent(parentVariable);
            }
            //Time T-1
            for (Node parentNode : node.getTimeTDependency().getParentsTm1()) {
                Variable parentVariable = getVariableByName(parentNode.getName());
                Variable parentVariableInterface = parentVariable.getInterfaceVariable();
                variableParentSetT.addParent(parentVariableInterface);
            }
        }
        this.dbn = new DynamicBayesianNetwork(dynamicDAG);
        //----------------------------------------Setting probabilities-------------------------------------------
        for (Node node : graphDcbn.getNodes()) {
            Variable variable = getVariableByName(node.getName());
            //If node has parents, use Multinomial_MultinomialParents
            //else, use Multinomial
            Multinomial_MultinomialParents multinomialParents;
            //-------Time 0--------
            if (dbn.getDynamicDAG().getParentSetTime0(variable).getParents().size() != 0) {
                multinomialParents = dbn.getConditionalDistributionTime0(variable);
                for (int i = 0; i < node.getTimeZeroDependency().getProbabilities().length; i++) {
                    multinomialParents.getMultinomial(i).setProbabilities(node.getTimeZeroDependency().getProbabilities()[i]);
                }
            } else {
                Multinomial variableMultinomial0 = this.dbn.getConditionalDistributionTime0(variable);
                variableMultinomial0.setProbabilities(node.getTimeZeroDependency().getProbabilities()[0]);

            }
            //-------Time T--------
            if (dbn.getDynamicDAG().getParentSetTimeT(variable).getParents().size() != 0) {
                multinomialParents = dbn.getConditionalDistributionTimeT(variable);
                for (int i = 0; i < node.getTimeTDependency().getProbabilities().length; i++) {
                    multinomialParents.getMultinomial(i).setProbabilities(node.getTimeTDependency().getProbabilities()[i]);
                }
            } else {
                Multinomial variableMultinomialT = this.dbn.getConditionalDistributionTimeT(variable);
                variableMultinomialT.setProbabilities(node.getTimeTDependency().getProbabilities()[0]);
            }
        }


    }

    /**
     * Returns the variable with the given name
     *
     * @param name the name of the variable
     * @return the variable with the given name
     */
    public Variable getVariableByName(String name) {
        for (Variable variable : this.variables) {
            if (variable.getName().equals(name)) {
                return variable;
            }
        }
        return null;
    }

}
