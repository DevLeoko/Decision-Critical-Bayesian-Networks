package io.dcbn.backend.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@EnableJms
public class ActiveMQConfig {

    @Value("${spring.activemq.broker-url}")
    private String BROKER_URL;

    @Value("${spring.activemq.user}.")
    private String BROKER_USERNAME;

    @Value("${spring.activemq.password}")
    private String BROKER_PASSWORD;

    @Bean
    public ActiveMQConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory connectionFactory =
                new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(BROKER_URL);
        connectionFactory.setPassword(BROKER_PASSWORD);
        connectionFactory.setUserName(BROKER_USERNAME);
        connectionFactory.setUseCompression(true);
        connectionFactory.setUseAsyncSend(true);
        return connectionFactory;
    }

    @Bean(name = "listenerContainerFactory")
    public DefaultJmsListenerContainerFactory vesselListenerContainer() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setBackOff(new FixedBackOff(5000, 0));
        return factory;
    }
}
