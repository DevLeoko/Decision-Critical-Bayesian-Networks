package io.dcbn.backend.core.activemq;


import de.fraunhofer.iosb.iad.maritime.datamodel.JsonMapper;
import de.fraunhofer.iosb.iad.maritime.datamodel.Outcome;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

@Service
public class Producer {

    @SendTo("outcome.queue")
    public static String sendOutcome(Outcome outcome) {
        return JsonMapper.toJson(outcome);
    }

    @SendTo("error.queue")
    public static String sendErrorMessage(String errorMessage) {
        return errorMessage;
    }
}