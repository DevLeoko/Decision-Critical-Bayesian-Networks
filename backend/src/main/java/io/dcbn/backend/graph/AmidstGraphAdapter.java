package io.dcbn.backend.graph;

import eu.amidst.core.distribution.Multinomial;
import eu.amidst.core.distribution.Multinomial_MultinomialParents;
import eu.amidst.core.models.ParentSet;
import eu.amidst.core.variables.Variable;
import eu.amidst.dynamic.models.DynamicBayesianNetwork;
import eu.amidst.dynamic.models.DynamicDAG;
import eu.amidst.dynamic.variables.DynamicVariables;

import java.util.ArrayList;

public class AmidstGraphAdapter {

    private DynamicBayesianNetwork dbn;
    private Graph graphDcbn;

    private ArrayList<Variable> variables;

    public AmidstGraphAdapter(Graph graph) {
        this.graphDcbn = graph;
        DynamicVariables dynamicVariables = new DynamicVariables();
        variables = new ArrayList<>();

        for (Node node : graphDcbn.getNodes()) {
            Variable variable = dynamicVariables.newMultinomialDynamicVariable(node.getName(), node.getStateType().getState().length);
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
        for (Node node : this.graphDcbn.getNodes()) {
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

    public Variable getVariableByName(String name) {
        for (Variable variable : this.variables) {
            if (variable.getName().equals(name)) {
                return variable;
            }
        }
        return null;
    }

    //Getters
    public DynamicBayesianNetwork getDbn() {
        return dbn;
    }


}
