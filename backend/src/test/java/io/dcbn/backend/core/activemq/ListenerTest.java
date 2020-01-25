package io.dcbn.backend.core.activemq;

import de.fraunhofer.iosb.iad.maritime.datamodel.AreaOfInterest;
import io.dcbn.backend.datamodel.JsonMapper;
import de.fraunhofer.iosb.iad.maritime.datamodel.Vessel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.handler.annotation.SendTo;

public class ListenerTest {

    private Vessel vessel;
    private AreaOfInterest aoi;
    private Listener listener;

    @BeforeEach
    public void setUp() {
        vessel = new Vessel(System.currentTimeMillis(), "Vessel1");
        vessel.setSpeed(3.3);

        aoi = new AreaOfInterest("aoi1", null);
    }


    @SendTo("vessel.queue")
    private String sendTestVessel(Vessel vessel) {
        return JsonMapper.toJson(vessel);
    }

    @SendTo("aoi.queue")
    private String sendTestAoi(AreaOfInterest aoi) {
        return JsonMapper.toJson(aoi);
    }

    @Test
    public void receiveVesselTest() {
        sendTestVessel(vessel);


    }
}
