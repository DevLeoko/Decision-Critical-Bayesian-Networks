package io.dcbn.backend.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.fraunhofer.iosb.iad.maritime.datamodel.Vessel;
import io.dcbn.backend.authentication.repositories.DcbnUserRepository;
import io.dcbn.backend.core.activemq.Producer;
import io.dcbn.backend.datamodel.Outcome;
import io.dcbn.backend.inference.InferenceManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jms.core.JmsTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class VesselHandlerTest {

    private VesselHandler vesselHandler;
    private VesselCache vesselCache;
    private InferenceManager inferenceManager;
    private Producer producer;
    private Outcome outcome1;

    @BeforeEach
    public void setUp() {
        vesselCache = new VesselCache(5);

        inferenceManager = mock(InferenceManager.class);
        outcome1 = new Outcome("outcome", 0, null, null, null);
        List<Outcome> outcomes = new ArrayList<>();
        outcomes.add(outcome1);
        when(inferenceManager.calculateInference("vessel")).thenReturn(outcomes);

        producer = mock(Producer.class);

        vesselHandler = new VesselHandler(vesselCache, inferenceManager, producer);
    }

    @Test
    public void handleTest() throws JsonProcessingException {
        vesselHandler.handleVessel(new Vessel("vessel", 0));
        verify(producer, times(1)).send(outcome1);
    }
}
