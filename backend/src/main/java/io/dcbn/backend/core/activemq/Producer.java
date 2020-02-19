package io.dcbn.backend.core.activemq;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.dcbn.backend.datamodel.JsonMapper;
import io.dcbn.backend.datamodel.Outcome;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;

/**
 * Producer class that enqueues Outcomes and error messages into the activemq
 */
@Service
public class Producer {

    JmsTemplate jmsTemplate;

    @Value("${spring.activemq.queue.error}")
    private String errorQueue;

    @Value("${spring.activemq.queue.outcome}")
    private String outcomeQueue;

    /**
     * Constructor that sets jmsTemplate
     *
     * @param jmsTemplate
     */
    @Autowired
    public Producer(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    /**
     * Takes an Outcome, converts it into a Json String and enqueues it into the activemq.
     *
     * @param outcome Outcome to be enqueued
     * @throws JsonProcessingException
     */
    public void send(Outcome outcome) throws JsonProcessingException {
        jmsTemplate.convertAndSend(outcomeQueue, JsonMapper.toJson(outcome));
    }

    /**
     * Takes an error message as string and enqueues it into the activemq.
     *
     * @param errorMessage Error message to be enqueued
     * @throws JsonProcessingException
     */
    public void send(String errorMessage){
        jmsTemplate.convertAndSend(errorQueue, errorMessage);
    }
}