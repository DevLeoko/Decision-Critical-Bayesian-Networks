package io.dcbn.backend.core.activemq;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.dcbn.backend.core.AoiHandler;
import io.dcbn.backend.core.VesselHandler;
import io.dcbn.backend.datamodel.JsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
public class Listener {

    private final AoiHandler aoiHandler;
    private final VesselHandler vesselHandler;

    @Autowired
    public Listener(AoiHandler aoiHandler, VesselHandler vesselHandler) {
        this.aoiHandler = aoiHandler;
        this.vesselHandler = vesselHandler;
    }

    @JmsListener(destination = "${spring.activemq.queue.vessel}", containerFactory="jsaFactory")
    public void receiveVessel(String vessel) throws JsonProcessingException {
        vesselHandler.handleVessel(JsonMapper.fromJsonToVessel(vessel));
    }

    @JmsListener(destination = "${spring.activemq.queue.aoi}", containerFactory="jsaFactory")
    public void receiveAoi(String aoi) throws JsonProcessingException {
        aoiHandler.handleAoi(JsonMapper.fromJsonToAreaOfInterest(aoi));
    }
}