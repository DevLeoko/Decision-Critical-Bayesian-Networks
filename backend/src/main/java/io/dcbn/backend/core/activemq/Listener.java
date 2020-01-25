package io.dcbn.backend.core.activemq;

import io.dcbn.backend.core.AoiHandler;
import io.dcbn.backend.core.VesselHandler;
import io.dcbn.backend.datamodel.JsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;

@Service
public class Listener {

    private final AoiHandler aoiHandler;
    private final VesselHandler vesselHandler;

    @Autowired
    public Listener(AoiHandler aoiHandler, VesselHandler vesselHandler) {
        this.aoiHandler = aoiHandler;
        this.vesselHandler = vesselHandler;
    }

    @JmsListener(destination = "aoi.queue", containerFactory = "listenerContainerFactory")
    public void receiveAoi(@Payload final String json) throws JMSException {
        aoiHandler.handleAoi(JsonMapper.fromJsonToAreaOfInterest(json));
    }

    @JmsListener(destination = "vessel.queue", containerFactory = "listenerContainerFactory")
    public void receiveVessel(@Payload final String json) throws JMSException {
        vesselHandler.handleVessel(JsonMapper.fromJsonToVessel(json));
    }
}