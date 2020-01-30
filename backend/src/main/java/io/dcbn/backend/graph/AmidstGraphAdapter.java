package io.dcbn.backend.graph;

import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.distribution.Multinomial;
import eu.amidst.core.distribution.Multinomial_MultinomialParents;
import eu.amidst.core.inference.InferenceAlgorithm;
import eu.amidst.core.models.ParentSet;
import eu.amidst.core.variables.Variable;
import eu.amidst.dynamic.datastream.DynamicDataInstance;
import eu.amidst.dynamic.inference.FactoredFrontierForDBN;
import eu.amidst.dynamic.inference.InferenceEngineForDBN;
import eu.amidst.dynamic.models.DynamicBayesianNetwork;
import eu.amidst.dynamic.models.DynamicDAG;
import eu.amidst.dynamic.utils.DynamicBayesianNetworkSampler;
import eu.amidst.dynamic.variables.DynamicVariables;
import io.dcbn.backend.evidenceFormula.model.EvidenceFormula;
import io.dcbn.backend.inference.Algorithm;
import io.dcbn.backend.utils.Pair;
import lombok.Getter;

import java.util.*;

/**
 * This class represents the adapter from Graph to Dynamic Bayesian Network (DBN)
 */
public class AmidstGraphAdapter {

    private final String TEMP_CHILD = "tempChild";

    @Getter
    private DynamicBayesianNetwork dbn;
    @Getter
    private Graph adaptedGraph;

    private List<Pair<Variable, Node>> variables;

    @Getter
    private List<Variable> tempChildVariables;

    /**
     * The constructor takes as input a {@link Graph} and adapt it to an amidst {@link DynamicBayesianNetwork}
     * with all the data inside
     *
     * @param graph the input graph to adapt
     */
    public AmidstGraphAdapter(Graph graph) {
        this.adaptedGraph = graph;
        DynamicVariables dynamicVariables = new DynamicVariables();

        tempChildVariables = new ArrayList<>();
        variables = new ArrayList<>();

        //---------------------------------Creating all the variables------------------------------
        for (Node node : graph.getNodes()) {
            List<String> states = Arrays.asList(node.getStateType().getStates());
            Variable variable = dynamicVariables.newMultinomialDynamicVariable(node.getName(), states);
            variables.add(new Pair<>(variable, node));
            if (node.isValueNode() && ((ValueNode) node).getValue().length == 1) {
                Variable tempChild = dynamicVariables.newMultinomialDynamicVariable(TEMP_CHILD + node.getName(), states);
                tempChildVariables.add(tempChild);
            }
        }

        DynamicDAG dynamicDAG = new DynamicDAG(dynamicVariables);

        // Setting parents for each variable (Creating structure of DBN)
        for (Pair<Variable, Node> entry : variables) {
            Variable variable = entry.getKey();
            Node node = entry.getValue();

                //--------TIME 0-------------
                ParentSet variableParentSet0 = dynamicDAG.getParentSetTime0(variable);
                    node.getTimeZeroDependency().getParents().stream()
                            .map(Node::getName)
                            .map(this::getVariableByName)
                            .forEach(variableParentSet0::addParent);

                //--------TIME T-------------
                ParentSet variableParentSetT = dynamicDAG.getParentSetTimeT(variable);
                //Time T
                node.getTimeTDependency().getParents().stream()
                        .map(Node::getName)
                        .map(this::getVariableByName)
                        .forEach(variableParentSetT::addParent);
                //--------Time T-1------------
                node.getTimeTDependency().getParentsTm1().stream()
                        .map(Node::getName)
                        .map(this::getVariableByName)
                        .map(Variable::getInterfaceVariable)
                        .forEach(variableParentSetT::addParent);
                // case it is has a virtual Evidence
                if (node.isValueNode() && ((ValueNode) node).getValue().length == 1) {
                    Variable tempChild = tempChildVariables.stream()
                            .filter(var -> var.getName().equals(TEMP_CHILD + node.getName()))
                            .findAny().orElse(null);
                    dynamicDAG.getParentSetTime0(tempChild).addParent(variable);
                    dynamicDAG.getParentSetTimeT(tempChild).addParent(variable);
                }
        }

        dbn = new DynamicBayesianNetwork(dynamicDAG);
        //----------------------------------------Setting probabilities-------------------------------------------
        for (Node node : graph.getNodes()) {
            Variable variable = getVariableByName(node.getName());// TODO optional (#HannesDieserGott)?
            if (variable == null) {
                continue;
            }
            //-------Time 0--------

            boolean parentsT0Empty = dbn.getDynamicDAG().getParentSetTime0(variable).getParents().isEmpty();
            if (parentsT0Empty) {
                // case no parents
                double[][] probabilitiesT0 = node.getTimeZeroDependency().getProbabilities();
                Multinomial variableMultinomial0 = dbn.getConditionalDistributionTime0(variable);
                variableMultinomial0.setProbabilities(probabilitiesT0[0]);
            } else {
                //case has parents
                double[][] probabilitiesT0 = node.getTimeZeroDependency().getProbabilities();
                Multinomial_MultinomialParents multinomialParents = dbn.getConditionalDistributionTime0(variable);
                for (int i = 0; i < probabilitiesT0.length; i++) {
                    multinomialParents.getMultinomial(i).setProbabilities(probabilitiesT0[i]);
                }
            }
            //-------Time T--------

            boolean parentsTEmpty = dbn.getDynamicDAG().getParentSetTimeT(variable).getParents().isEmpty();
            if (parentsTEmpty) {
                // case no parents
                double[][] probabilitiesT = node.getTimeTDependency().getProbabilities();
                Multinomial variableMultinomialT = dbn.getConditionalDistributionTimeT(variable);
                variableMultinomialT.setProbabilities(probabilitiesT[0]);
            } else {
                //case has parents
                double[][] probabilitiesT = node.getTimeTDependency().getProbabilities();
                Multinomial_MultinomialParents multinomialParents = dbn.getConditionalDistributionTimeT(variable);
                for (int i = 0; i < probabilitiesT.length; i++) {
                    multinomialParents.getMultinomial(i).setProbabilities(probabilitiesT[i]);
                }
            }
        }

        for(Variable variable : tempChildVariables) {
            ValueNode node = (ValueNode) variables.stream()
                    .filter(var -> var.getKey().getName().equals(variable.getName().replace(TEMP_CHILD, "")))
                    .findAny().map(Pair::getValue).orElse(null);
            Multinomial_MultinomialParents multinomialParentsT0 = dbn.getConditionalDistributionTime0(variable);
            Multinomial_MultinomialParents multinomialParentsTT = dbn.getConditionalDistributionTimeT(variable);
            multinomialParentsT0.getMultinomial(0).setProbabilities(new double[] {node.getValue()[0][0], node.getValue()[0][1]});
            multinomialParentsT0.getMultinomial(1).setProbabilities(new double[] {node.getValue()[0][1], node.getValue()[0][0]});
            multinomialParentsTT.getMultinomial(0).setProbabilities(new double[] {node.getValue()[0][0], node.getValue()[0][1]});
            multinomialParentsTT.getMultinomial(1).setProbabilities(new double[] {node.getValue()[0][1], node.getValue()[0][0]});
        }
    }

    /**
     * Returns the variable with the given name
     *
     * @param name the name of the variable
     * @return the variable with the given name
     */
    public Variable getVariableByName(String name) {
        return variables.stream()
                .map(Pair::getKey)
                .filter(var -> var.getName().equals(name))
                .findAny().orElse(null);
    }
}