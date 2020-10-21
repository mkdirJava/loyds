package com.wilson.loyds.ordering.service.jms.pubilsher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * A class to publish JMS rejected messages
 */
@Component
class OrderRejectedPublisher {

    private JmsTemplate jmsTemplate;

    @Value("${queues.order-rejected}")
    private String destination;

    @Autowired
    public OrderRejectedPublisher(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    /**
     * @param tenantId
     * @param orderRejected
     */
    public void sendOrderRejected(final String tenantId, final Object orderRejected) {
        this.jmsTemplate.convertAndSend(destination, orderRejected, message -> {
            message.setStringProperty("TENANT_KEY", tenantId);
            return message;
        });
    }
}
