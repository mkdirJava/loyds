package com.wilson.loyds.ordering.service.jms.pubilsher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * A class to publish JMS accepted messages
 */
@Component
class OrderAcceptedPublisher {

    private JmsTemplate jmsTemplate;

    @Value("${queues.order-accepted}")
    private String destination;


    public OrderAcceptedPublisher(final JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    /**
     *
     * @param tenantId
     * @param orderAccepted
     */
    public void sendOrderAccepted(String tenantId, Object orderAccepted){
        this.jmsTemplate.convertAndSend(destination,orderAccepted,message -> {
            message.setStringProperty("TENANT_KEY", tenantId);
            return message;
        });

    }
}
