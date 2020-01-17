package io.dcbn.backend.core;

import io.dcbn.backend.maritimedatamodel.JsonMapper;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.*;

@Component
public class ActivemqListener {

    private final AoiHandler aoiHandler;
    private final VesselHandler vesselHandler;

    @Autowired
    public ActivemqListener(AoiHandler aoiHandler, VesselHandler vesselHandler){
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
