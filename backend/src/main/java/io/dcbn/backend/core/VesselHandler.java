package io.dcbn.backend.core;

import de.fraunhofer.iosb.iad.maritime.datamodel.Vessel;
import io.dcbn.backend.core.activemq.Producer;
import io.dcbn.backend.datamodel.Outcome;
import io.dcbn.backend.inference.InferenceManager;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
      vesselCache.insert(vessel);
      List<Outcome> outcomes = inferenceManager.calculateInference(vessel.getUuid());
      for(Outcome outcome : outcomes) {
          Producer.sendOutcome(outcome);
      }
    }
}
