package io.dcbn.backend.inference;

import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.inference.InferenceAlgorithm;
import eu.amidst.core.variables.Variable;
import eu.amidst.dynamic.datastream.DynamicDataInstance;
import eu.amidst.dynamic.inference.FactoredFrontierForDBN;
import eu.amidst.dynamic.inference.InferenceEngineForDBN;
import eu.amidst.dynamic.utils.DynamicBayesianNetworkSampler;
import io.dcbn.backend.evidenceFormula.model.EvidenceFormula;
import io.dcbn.backend.graph.AmidstGraphAdapter;
import io.dcbn.backend.graph.Graph;
import io.dcbn.backend.graph.Node;
import io.dcbn.backend.graph.ValueNode;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Data
public class InferenceManager {

    public List<Graph> calculateInference(String vesselUuid) {
        //TODO take vessels from cache



        calculateInference(null, (i, formula) -> {
            return null;
        }, null);
    }

    public Graph calculateInference(AmidstGraphAdapter adaptedGraph, BiFunction<Integer, EvidenceFormula, String> formulaResolver, Algorithm algorithm) {
        DynamicBayesianNetworkSampler dynamicSampler = new DynamicBayesianNetworkSampler(adaptedGraph.getDbn());
        List<Node> nodes = adaptedGraph.getAdaptedGraph().getNodes();
        List<Node> nodesToEvaluate = nodes.stream()
                .filter(var -> !var.isValueNode() && var.getEvidenceFormula() == null)
                .collect(Collectors.toList());
        //Hiding the variables we want to evaluate during inference calculations TODO Bad english?
        nodesToEvaluate.stream()
                .map(Node::getName)
                .map(adaptedGraph::getVariableByName)
                .forEach(dynamicSampler::setHiddenVar);
        DataStream<DynamicDataInstance> dataPredict = dynamicSampler.sampleToDataBase(1, adaptedGraph.getAdaptedGraph().getTimeSlices());

        //Setting the results of the evidenceFormulas for each time-step
        int i = 0;
        for (DynamicDataInstance instance : dataPredict) {
            int finalI = i;
            nodes.stream()
                    .filter(var -> !var.isValueNode() && var.getEvidenceFormula() != null)
                    .forEach(var -> {
                        Variable variable = adaptedGraph.getVariableByName(var.getName());
                        int sate = var.getStateType().getIndexOfSate(formulaResolver.apply(finalI, var.getEvidenceFormula()));
                        instance.setValue(variable, sate);
                    });
            i++;
        }

        //Running inference
        InferenceAlgorithm inferenceAlgorithm = algorithm.getAlgorithm();
        inferenceAlgorithm.setParallelMode(true);
        FactoredFrontierForDBN FFalgorithm = new FactoredFrontierForDBN(inferenceAlgorithm);
        InferenceEngineForDBN.setInferenceAlgorithmForDBN(FFalgorithm);
        InferenceEngineForDBN.setModel(adaptedGraph.getDbn());

        //Creating the output graph
        List<Node> returnedNodes = new ArrayList<>();
        nodes.forEach(var -> returnedNodes.add(new

                ValueNode(var, new double[adaptedGraph.getAdaptedGraph().

                getTimeSlices()][2])));

        int time = 0;
        for (
                DynamicDataInstance instance : dataPredict) {
            //The InferenceEngineForDBN must be reset at the beginning of each Sequence.
            //We also set the evidence.
            InferenceEngineForDBN.addDynamicEvidence(instance);
            //Then we run inference
            InferenceEngineForDBN.runInference();

            //Setting the values calculated by the inference engine
            for (Node node : returnedNodes) {
                for (i = 0; i < node.getStateType().getStates().length; i++) {
                    ((ValueNode) node).getValue()[time][i] = InferenceEngineForDBN.getFilteredPosterior(adaptedGraph.getVariableByName(node.getName())).getProbability(i);
                }
            }

        }
        return new

                Graph(adaptedGraph.getAdaptedGraph().getId(), adaptedGraph.getAdaptedGraph().getName(), adaptedGraph.
                getAdaptedGraph().getTimeSlices(), returnedNodes);
    }
}
