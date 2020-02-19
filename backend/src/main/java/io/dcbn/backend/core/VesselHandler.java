package io.dcbn.backend.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.fraunhofer.iosb.iad.maritime.datamodel.Vessel;
import io.dcbn.backend.core.activemq.Producer;
import io.dcbn.backend.datamodel.Outcome;
import io.dcbn.backend.inference.InferenceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Handles a new Vessel
 */
@Service
public class VesselHandler {

    private final VesselCache vesselCache;
    private final InferenceManager inferenceManager;
    private final Producer producer;

    @Autowired
    public VesselHandler(VesselCache vesselCache, InferenceManager inferenceManager, Producer producer) {
        this.inferenceManager = inferenceManager;
        this.vesselCache = vesselCache;
        this.producer = producer;
    }

    /**
     * Inserts the given vessel into the cache. Calculates the inference for the given vessel and sends the Outcomes
     * to the producer
     *
     * @param vessel New Vessel to be handled
     * @throws JsonProcessingException
     */
    public void handleVessel(Vessel vessel) throws JsonProcessingException {
        vesselCache.insert(vessel);
        List<Outcome> outcomes = inferenceManager.calculateInference(vessel.getUuid());
        for (Outcome outcome : outcomes) {
            producer.send(outcome);
        }
    }
}
