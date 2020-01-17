package io.dcbn.backend.core.activemq;

import io.dcbn.backend.maritimedatamodel.Network;
import io.dcbn.backend.maritimedatamodel.Outcome;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
public class Producer {

    @SendTo("network.queue")
    public static Network sendNetwork(Network network) {
        return network;
    }

    @SendTo("outcome.queue")
    public static Outcome sendOutcome(Outcome outcome) {
        return outcome;
    }

    @SendTo("error.queue")
    public static String sendErrorMessage(String errorMessage) {
        return errorMessage;
    }
}
