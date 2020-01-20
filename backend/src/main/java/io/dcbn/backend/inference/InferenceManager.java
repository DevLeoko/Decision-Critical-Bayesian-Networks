package io.dcbn.backend.inference;

import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.distribution.UnivariateDistribution;
import eu.amidst.core.inference.InferenceAlgorithm;
import eu.amidst.dynamic.datastream.DynamicDataInstance;
import eu.amidst.dynamic.inference.FactoredFrontierForDBN;
import eu.amidst.dynamic.inference.InferenceEngineForDBN;
import eu.amidst.dynamic.utils.DynamicBayesianNetworkSampler;
import io.dcbn.backend.evidenceFormula.model.EvidenceFormula;
import io.dcbn.backend.graph.AmidstGraphAdapter;
import io.dcbn.backend.graph.Graph;
import io.dcbn.backend.graph.Node;
import lombok.Data;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Data
public class InferenceManager {

//    public List<Graph> calculateInference(String vesselUuid) {
//        //TODO take vessels from cache
//
//        vArraz
//        calculateInference(null, (i, formula) -> {
//            return nll
//        }, null);
//    }

    public Graph calculateInference(AmidstGraphAdapter adaptedGraph, BiFunction<Integer, EvidenceFormula, String> formulaResolver, Algorithm algorithm) {
        //Setting the results of the evidences
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
                    .forEach(var -> instance.setValue(adaptedGraph.getVariableByName(var.getName())
                            , var.getIndexOfSate(formulaResolver.apply(finalI, var.getEvidenceFormula()))));
            i++;
        }

        //Running inference
        InferenceAlgorithm inferenceAlgorithm = algorithm.getAlgorithm();
        inferenceAlgorithm.setParallelMode(true);
        FactoredFrontierForDBN FFalgorithm = new FactoredFrontierForDBN(inferenceAlgorithm);
        InferenceEngineForDBN.setInferenceAlgorithmForDBN(FFalgorithm);
        InferenceEngineForDBN.setModel(adaptedGraph.getDbn());


        //TODO create output graph
        int time = 0;
        UnivariateDistribution posterior = null;
        for (DynamicDataInstance instance : dataPredict) {
            //The InferenceEngineForDBN must be reset at the beginning of each Sequence.
            if (instance.getTimeID() == 0 && posterior != null) {
                InferenceEngineForDBN.reset();
                time = 0;
            }
            //We also set the evidence.
            InferenceEngineForDBN.addDynamicEvidence(instance);
            //Then we run inference
            InferenceEngineForDBN.runInference();

            //TODO create Copy of all nodes and set the
//            posterior = InferenceEngineForDBN.getFilteredPosterior(c);


            //We show the output
//            System.out.println("P(C|e[0:" + (time) + "]) = " + posterior);
//            System.out.println("P(A|e[0:" + (time++) + "]) = " + InferenceEngineForDBN.getFilteredPosterior(a));
        }
        return null; //TODO remove
    }

    private void assignEvaluatedEvidence(EvidenceFormula evidenceFormula) {

    }
}
