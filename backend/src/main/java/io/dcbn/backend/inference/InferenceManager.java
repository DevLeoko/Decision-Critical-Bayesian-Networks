package io.dcbn.backend.inference;

import de.fraunhofer.iosb.iad.maritime.datamodel.Vessel;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.inference.InferenceAlgorithm;
import eu.amidst.core.variables.Variable;
import eu.amidst.dynamic.datastream.DynamicDataInstance;
import eu.amidst.dynamic.inference.FactoredFrontierForDBN;
import eu.amidst.dynamic.inference.InferenceEngineForDBN;
import eu.amidst.dynamic.utils.DynamicBayesianNetworkSampler;
import io.dcbn.backend.core.VesselCache;
import io.dcbn.backend.datamodel.Outcome;
import io.dcbn.backend.evidence_formula.model.EvidenceFormula;
import io.dcbn.backend.evidence_formula.repository.EvidenceFormulaRepository;
import io.dcbn.backend.evidence_formula.services.EvidenceFormulaEvaluator;
import io.dcbn.backend.graph.AmidstGraphAdapter;
import io.dcbn.backend.graph.Graph;
import io.dcbn.backend.graph.Node;
import io.dcbn.backend.graph.ValueNode;
import io.dcbn.backend.graph.repositories.GraphRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * This class handles all the calculations with Dynamic Bayesian Networks (DBN).
 */
@Service
public class InferenceManager {

    private VesselCache vesselCache;

    private GraphRepository graphRepository;
    private EvidenceFormulaRepository evidenceFormulaRepository;
    private EvidenceFormulaEvaluator evidenceFormulaEvaluator;

    private final Vessel defaultVessel;

    @Autowired
    public InferenceManager(VesselCache vesselCache, GraphRepository graphRepository,
                            EvidenceFormulaRepository evidenceFormulaRepository,
                            EvidenceFormulaEvaluator evidenceFormulaEvaluator) {
        this.vesselCache = vesselCache;
        this.graphRepository = graphRepository;
        this.evidenceFormulaRepository = evidenceFormulaRepository;
        this.evidenceFormulaEvaluator = evidenceFormulaEvaluator;

        // TODO: This is really dumb...
        defaultVessel = new Vessel("", 0);
        defaultVessel.setWidth(10.0);
        defaultVessel.setLength(10.0);
        defaultVessel.setDraught(10.0);
        defaultVessel.setHeading(10.0);
        defaultVessel.setCog(10.0);
        defaultVessel.setAltitude(10.0);
        defaultVessel.setLatitude(10.0);
        defaultVessel.setLongitude(10.0);
        defaultVessel.setSpeed(10.0);
        defaultVessel.setFiller(true);
        defaultVessel.setMmsi(0L);
        defaultVessel.setImo(0L);
        defaultVessel.setEta(0L);
        defaultVessel.setName("DEFAULT");
        defaultVessel.setCallsign("DEFAULT");
    }

    /**
     * This method is used to evaluate all graphs on the given {@link Vessel}
     *
     * @param vesselUuid the uuid of the {@link Vessel}.
     * @return TODO Complete
     */
    public List<Outcome> calculateInference(String vesselUuid) {
        List<Outcome> outcomes = new ArrayList<>();
        for (Graph graph : graphRepository.findAll()) {
            outcomes.add(calculateInference(graph, vesselUuid));
        }
        return outcomes;
    }

    public Outcome calculateInference(Graph graph, String vesselUuid) {
        Vessel[] vessels = vesselCache.getVesselsByUuid(vesselUuid);

        //Iterating over all graphs
        AmidstGraphAdapter adaptedGraph = new AmidstGraphAdapter(graph);
        Graph calculatedGraph = calculateInference(adaptedGraph, (i, formula) -> {
            Vessel vessel = vessels != null ? vessels[i] : defaultVessel;
            return evidenceFormulaEvaluator.evaluate(i, vessel, formula) ? "true" : "false";
        }, Algorithm.IMPORTANCE_SAMPLING);
        Set<Vessel> correlatedVessels = evidenceFormulaEvaluator.getCorrelatedVessels();
        correlatedVessels.add(vessels != null ? vessels[0] : defaultVessel);
        return new Outcome(UUID.randomUUID().toString(), System.currentTimeMillis(),
                calculatedGraph, correlatedVessels,
                evidenceFormulaEvaluator.getCorrelatedAois());
    }

    /**
     * This method set the given values to the adapted Graph {@link AmidstGraphAdapter}
     * and run the  {@link InferenceEngineForDBN} to return a new {@link Graph} with the calculated values.
     *
     * @param adaptedGraph    the adapted {@link Graph}
     * @param formulaResolver evaluates the given {@link EvidenceFormula} for the given time-step.
     * @param algorithm       The algorithm used by the {@link InferenceEngineForDBN}
     * @return a new graph with the calculated values.
     */
    public Graph calculateInference(AmidstGraphAdapter adaptedGraph,
                                    BiFunction<Integer, EvidenceFormula, String> formulaResolver, Algorithm algorithm) {
        DynamicBayesianNetworkSampler dynamicSampler = new DynamicBayesianNetworkSampler(
                adaptedGraph.getDbn());

        List<Node> nodes = adaptedGraph.getAdaptedGraph().getNodes();
        List<Node> nodesWithVirEvi = nodes.stream()
                .filter(var -> var.isValueNode() && ((ValueNode) var).getValue().length == 1).collect(Collectors.toList());

        //finding all the temporary child nodes fot the
        List<Variable> tempChildVariables = adaptedGraph.getDbn().getDynamicVariables().getListOfDynamicVariables().stream()
                .filter(var -> var.getName().startsWith("tempChild"))
                .collect(Collectors.toList());

        List<Node> nodesWithEvidenceFormula = nodes.stream()
                .filter(var -> !var.isValueNode() && var.getEvidenceFormulaName() != null && !var.getEvidenceFormulaName().equals("") && evidenceFormulaRepository.existsByName(var.getEvidenceFormulaName()))
                .collect(Collectors.toList());

        List<Node> nodesWithBinaryEvidence = nodes.stream()
                .filter(Node::isValueNode)
                .filter(var -> ((ValueNode) var).checkValuesAreStates() && ((ValueNode) var).getValue().length == adaptedGraph.getAdaptedGraph().getTimeSlices())
                .collect(Collectors.toList());

        //Hiding the variables we want to evaluate during inference calculations
        nodes.stream()
                .filter(var -> !nodesWithEvidenceFormula.contains(var) && !nodesWithBinaryEvidence.contains(var))
                .map(Node::getName)
                .map(adaptedGraph::getVariableByName)
                .forEach(dynamicSampler::setHiddenVar);
        DataStream<DynamicDataInstance> dataPredict = dynamicSampler
                .sampleToDataBase(1, adaptedGraph.getAdaptedGraph().getTimeSlices());

        return runInference(adaptedGraph, dataPredict, formulaResolver, tempChildVariables, nodes, algorithm);
    }

    /**
     * This method runs the inference Engine of AMIDST on the given graph
     * @param adaptedGraph the graph
     * @param dataPredict the evidences for each timestep
     * @param formulaResolver the evidence formulas
     * @param tempChildVariables the temporary child nodes for the virtual evidences
     * @param nodes the nodes of the graph
     * @return the new calculated graph with the values in the nodes
     */
    private Graph runInference(AmidstGraphAdapter adaptedGraph, DataStream<DynamicDataInstance> dataPredict,
                               BiFunction<Integer, EvidenceFormula, String> formulaResolver,
                               List<Variable> tempChildVariables,
                               List<Node> nodes, Algorithm algorithm) {
        List<Node> nodesWithBinaryEvidence = nodes.stream()
                .filter(Node::isValueNode)
                .filter(var -> ((ValueNode) var).checkValuesAreStates() && ((ValueNode) var).getValue().length == adaptedGraph.getAdaptedGraph().getTimeSlices())
                .collect(Collectors.toList());
        List<Node> nodesWithEvidenceFormula = nodes.stream()
                .filter(var -> !var.isValueNode() && var.getEvidenceFormulaName() != null && !var.getEvidenceFormulaName().equals("") && evidenceFormulaRepository.existsByName(var.getEvidenceFormulaName()))
                .collect(Collectors.toList());

        //Running inference
        InferenceAlgorithm inferenceAlgorithm = algorithm.getInferenceAlgorithm();
        inferenceAlgorithm.setParallelMode(true);
        FactoredFrontierForDBN ffAlgorithm = new FactoredFrontierForDBN(inferenceAlgorithm);
        InferenceEngineForDBN.setInferenceAlgorithmForDBN(ffAlgorithm);
        InferenceEngineForDBN.setModel(adaptedGraph.getDbn());

        //Creating the output graph
        List<Node> returnedNodes = new ArrayList<>();
        nodes.forEach(var -> returnedNodes
                .add(new ValueNode(var, new double[adaptedGraph.getAdaptedGraph().getTimeSlices()][2])));

        int time = 0;
        for (DynamicDataInstance instance : dataPredict) {
            //Setting the results of the evidence formulas
            for (Node node : nodesWithEvidenceFormula) {
                Variable variable = adaptedGraph.getVariableByName(node.getName());
                EvidenceFormula formula = evidenceFormulaRepository
                        .findByName(node.getEvidenceFormulaName()).orElse(null);
                int state = node.getStateType()
                        .getIndexOfState(formulaResolver.apply(time, formula));
                instance.setValue(variable, state);
            }

            //Setting the evidences
            for (Node node : nodesWithBinaryEvidence) {
                int state = ((ValueNode) node).getIndexOfState(time);
                Variable variable = adaptedGraph.getVariableByName(node.getName());
                instance.setValue(variable, state);
            }

            //Setting state 0 for every temporary child (JUST FOR 2 STATES)
            for (Variable variable : tempChildVariables) {
                instance.setValue(variable, 0);
            }

            //We set the evidence.
            InferenceEngineForDBN.addDynamicEvidence(instance);
            //Then we run inference
            InferenceEngineForDBN.runInference();

            //Setting the values calculated by the inference engine
            for (Node node : returnedNodes) {
                for (int i = 0; i < node.getStateType().getStates().length; i++) {
                    ((ValueNode) node).getValue()[time][i] = InferenceEngineForDBN
                            .getFilteredPosterior(adaptedGraph.getVariableByName(node.getName()))
                            .getProbability(i);
                }
            }
            time++;
        }

        return new Graph(adaptedGraph.getAdaptedGraph().getId(),
                adaptedGraph.getAdaptedGraph().getName()
                , adaptedGraph.getAdaptedGraph().getTimeSlices(), returnedNodes);
    }

}
