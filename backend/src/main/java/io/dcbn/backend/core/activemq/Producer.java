package io.dcbn.backend.core.activemq;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.dcbn.backend.datamodel.JsonMapper;
import io.dcbn.backend.datamodel.Outcome;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;

@Service
public class Producer {

    JmsTemplate jmsTemplate;

    @Autowired
    public Producer(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @Value("${spring.activemq.queue.outcome}")
    String outcomeQueue;

    public void send(Outcome outcome) throws JsonProcessingException {
        jmsTemplate.convertAndSend(outcomeQueue, JsonMapper.toJson(outcome));
    }

    @Value("${spring.activemq.queue.error}")
    String errorQueue;

    public void send(String errorMessage){
        jmsTemplate.convertAndSend(errorQueue, errorMessage);
    }
}