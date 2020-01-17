package io.dcbn.backend.core.activemq;

import io.dcbn.backend.maritimedatamodel.JsonMapper;
import io.dcbn.backend.maritimedatamodel.Network;
import io.dcbn.backend.maritimedatamodel.Outcome;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
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
