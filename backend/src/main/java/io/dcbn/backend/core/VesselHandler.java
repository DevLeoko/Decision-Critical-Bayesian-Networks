package io.dcbn.backend.core;

import de.fraunhofer.iosb.iad.maritime.datamodel.Outcome;
import de.fraunhofer.iosb.iad.maritime.datamodel.Vessel;
import io.dcbn.backend.core.activemq.Producer;
import io.dcbn.backend.graph.Graph;
import io.dcbn.backend.inference.InferenceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VesselHandler {

    private final VesselCache vesselCache;
    private final InferenceManager inferenceManager;
    //private final Producer producer;

    @Autowired
    public VesselHandler(VesselCache vesselCache, InferenceManager inferenceManager/*, Producer producer*/){
        this.inferenceManager = inferenceManager;
        this.vesselCache = vesselCache;
        //this.producer = producer;
    }

    public void handleVessel(Vessel vessel) {
        List<Outcome> outcomes;
        vesselCache.insert(vessel);
        outcomes = inferenceManager.calculateInference(vessel.getUuid());
        for(Outcome outcome : outcomes) {
            Producer.sendOutcome(outcome);
        }
    }
}
