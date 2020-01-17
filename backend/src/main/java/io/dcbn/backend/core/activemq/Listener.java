package io.dcbn.backend.core.activemq;

import io.dcbn.backend.core.AoiHandler;
import io.dcbn.backend.core.VesselHandler;
import io.dcbn.backend.maritimedatamodel.JsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.*;

@Component
public class Listener {

    private final AoiHandler aoiHandler;
    private final VesselHandler vesselHandler;

    @Autowired
    public Listener(AoiHandler aoiHandler, VesselHandler vesselHandler){
        this.aoiHandler = aoiHandler;
        this.vesselHandler = vesselHandler;
    }

    @JmsListener(destination = "aoi.queue")
    public void receiveAoi(final String json) throws JMSException {
        aoiHandler.handleAoi(JsonMapper.fromJsonToAreaOfInterest(json));
    }

    @JmsListener(destination = "vessel.queue")
    public void receiveVessel(final String json) throws JMSException {
        vesselHandler.handleVessel(JsonMapper.fromJsonToVessel(json));
    }
}
