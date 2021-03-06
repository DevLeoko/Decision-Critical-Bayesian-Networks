package io.dcbn.backend.core.activemq;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.dcbn.backend.core.AoiHandler;
import io.dcbn.backend.core.VesselHandler;
import io.dcbn.backend.datamodel.JsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

/**
 * Listener class that dequeues Vessels and AreaOfInterests from the activemq and forwards it to the specific handler.
 */
@Service
public class Listener {

    private final AoiHandler aoiHandler;
    private final VesselHandler vesselHandler;

    /**
     * Constructor takes aoiHandler and vesselHandler
     *
     * @param aoiHandler
     * @param vesselHandler
     */
    @Autowired
    public Listener(AoiHandler aoiHandler, VesselHandler vesselHandler) {
        this.aoiHandler = aoiHandler;
        this.vesselHandler = vesselHandler;
    }

    /**
     * Takes Vessel from activemq as Json String and forwards it to the vessel handler.
     *
     * @param vessel Vessel as Json String
     * @throws JsonProcessingException
     */
    @JmsListener(destination = "${spring.activemq.queue.vessel}", containerFactory="jsaFactory")
    public void receiveVessel(String vessel) throws JsonProcessingException {
        vesselHandler.handleVessel(JsonMapper.fromJsonToVessel(vessel));
    }

    /**
     * Takes AreaOfInterest from activemq as Json String and forwards it to the aoi handler.
     *
     * @param aoi AreaOfInterest as Json String
     */
    @JmsListener(destination = "${spring.activemq.queue.aoi}", containerFactory="jsaFactory")
    public void receiveAoi(String aoi) {
        aoiHandler.handleAoi(JsonMapper.fromJsonToAreaOfInterest(aoi));
    }
}