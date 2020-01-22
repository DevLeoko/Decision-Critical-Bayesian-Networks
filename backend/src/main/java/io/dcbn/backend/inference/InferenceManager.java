package io.dcbn.backend.inference;

import de.fraunhofer.iosb.iad.maritime.datamodel.Vessel;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.inference.InferenceAlgorithm;
import eu.amidst.core.variables.Variable;
import eu.amidst.dynamic.datastream.DynamicDataInstance;
import eu.amidst.dynamic.inference.FactoredFrontierForDBN;
import eu.amidst.dynamic.inference.InferenceEngineForDBN;
import eu.amidst.dynamic.utils.DynamicBayesianNetworkSampler;
import io.dcbn.backend.core.AoiCache;
import io.dcbn.backend.core.VesselCache;
import io.dcbn.backend.datamodel.Outcome;
import io.dcbn.backend.evidenceFormula.model.EvidenceFormula;
import io.dcbn.backend.evidenceFormula.services.EvidenceFormulaEvaluator;
import io.dcbn.backend.graph.AmidstGraphAdapter;
import io.dcbn.backend.graph.Graph;
import io.dcbn.backend.graph.Node;
import io.dcbn.backend.graph.ValueNode;
import io.dcbn.backend.graph.repositories.GraphRepository;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Data
@Service
public class InferenceManager {

  private VesselCache vesselCache;
  private AoiCache aoiCache;

  private GraphRepository graphRepository;
  private EvidenceFormulaEvaluator evidenceFormulaEvaluator;

  @Autowired
  public InferenceManager(VesselCache vesselCache, GraphRepository graphRepository,
      EvidenceFormulaEvaluator evidenceFormulaEvaluator) {
    this.vesselCache = vesselCache;
    this.graphRepository = graphRepository;
    this.evidenceFormulaEvaluator = evidenceFormulaEvaluator;
  }

  public List<Outcome> calculateInference(String vesselUuid) {
    Vessel[] vessels = vesselCache.getVesselsByUuid(vesselUuid);
    List<Outcome> outcomes = new ArrayList<>();

    //Iterating over all graphs
    for (Graph graph : graphRepository.findAll()) {
      AmidstGraphAdapter adaptedGraph = new AmidstGraphAdapter(graph);
      Graph calculatedGraph = calculateInference(adaptedGraph, (i, formula) -> {
        Vessel vessel = vessels[i];

        return evidenceFormulaEvaluator.evaluate(i, vessel, formula) ? "true" : "false";
      }, Algorithm.IMPORTANCE_SAMPLING);
      Set<Vessel> correlatedVessels = evidenceFormulaEvaluator.getCorrelatedVessels();
      correlatedVessels.add(vessels[0]);
      Outcome outcome = new Outcome(UUID.randomUUID().toString(), System.currentTimeMillis(),
          calculatedGraph, correlatedVessels,
          evidenceFormulaEvaluator.getCorrelatedAois());
      //evidenceFormulaEvaluator.reset();
      outcomes.add(outcome);
    }
    return outcomes;
  }

  public Graph calculateInference(AmidstGraphAdapter adaptedGraph,
      BiFunction<Integer, EvidenceFormula, String> formulaResolver, Algorithm algorithm) {
    DynamicBayesianNetworkSampler dynamicSampler = new DynamicBayesianNetworkSampler(
        adaptedGraph.getDbn());
    List<Node> nodes = adaptedGraph.getAdaptedGraph().getNodes();
    List<Node> nodesToEvaluate = nodes.stream()
        .filter(var -> !var.isValueNode() && var.getEvidenceFormula() == null)
        .collect(Collectors.toList());
    //Hiding the variables we want to evaluate during inference calculations
    nodesToEvaluate.stream()
        .map(Node::getName)
        .map(adaptedGraph::getVariableByName)
        .forEach(dynamicSampler::setHiddenVar);
    DataStream<DynamicDataInstance> dataPredict = dynamicSampler
        .sampleToDataBase(1, adaptedGraph.getAdaptedGraph().getTimeSlices());

    //Setting the results of the evidenceFormulas for each time-step
    List<Node> nodesToSetValues = nodes.stream()
            .filter(var -> !var.isValueNode() && var.getEvidenceFormula() != null)
            .collect(Collectors.toList());

    int i = 0;
    for (DynamicDataInstance instance : dataPredict) {
      for (Node node: nodesToSetValues) {
        Variable variable = adaptedGraph.getVariableByName(node.getName());
        int state = node.getStateType()
                .getIndexOfSate(formulaResolver.apply(i, node.getEvidenceFormula()));
        instance.setValue(variable, state);
        System.out.println(instance.getValue(variable) + " " + variable.getName());
      }

      i++;
    }
    System.out.println("------------------------------------------------------------");
    for (DynamicDataInstance instance : dataPredict) {
      for(Node node: nodes)  {
        Variable variable = adaptedGraph.getVariableByName(node.getName());
        System.out.println(instance.getValue(variable) + " " + variable.getName());
      }
    }





    //TODO make better

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
    for (DynamicDataInstance instance : dataPredict) {
      //The InferenceEngineForDBN must be reset at the beginning of each Sequence.
      //We also set the evidence.
      InferenceEngineForDBN.addDynamicEvidence(instance);
      //Then we run inference
      InferenceEngineForDBN.runInference();

      //Setting the values calculated by the inference engine
      for (Node node : returnedNodes) {
        for (i = 0; i < node.getStateType().getStates().length; i++) {
          ((ValueNode) node).getValue()[time][i] = InferenceEngineForDBN
              .getFilteredPosterior(adaptedGraph.getVariableByName(node.getName()))
              .getProbability(i);
        }
      }
      time++;
    }
    for(Node node: returnedNodes) {
      System.out.println(node.getName());
      System.out.println(Arrays.deepToString(((ValueNode) node).getValue()));
    }
    return new Graph(adaptedGraph.getAdaptedGraph().getId(), adaptedGraph.getAdaptedGraph().getName()
            , adaptedGraph.getAdaptedGraph().getTimeSlices(), returnedNodes);
  }
}
