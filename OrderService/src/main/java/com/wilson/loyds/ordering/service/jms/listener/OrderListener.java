package com.wilson.loyds.ordering.service.jms.listener;

import com.wilson.loyds.ordering.service.jms.listener.model.Order;
import com.wilson.loyds.ordering.service.jms.pubilsher.IJmsFacade;
import com.wilson.loyds.ordering.service.order.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Listener to the order queue
 */
@Component
public class OrderListener {

    private IJmsFacade jmsFacade;
    private IOrderService orderService;
    private static final String TENANT_KEY = "TENANT_KEY";

    @Autowired
    OrderListener(final IJmsFacade jmsFacade, final IOrderService orderService) {
        this.jmsFacade = jmsFacade;
        this.orderService = orderService;
    }

    /**
     * Listens to the queue.orders configuration queue.
     *
     * @param messageHeaders
     * @param order
     */
    @JmsListener(destination = "${queue.order")
    public void receiveOrder(MessageHeaders messageHeaders, @Payload Order order) {
        // validate the message header
        if (!validate(messageHeaders, order)) {
            // if this is invalid then return,
            // this will mean spring marks the message as consumed / acknowledged
            return;
        }
        // message is valid
        // try to process it
        String tenantId = (String) messageHeaders.get(TENANT_KEY);
        try {
            this.orderService.processOrder(tenantId, order);
        } catch (Exception e) {
            this.jmsFacade.sendUnSuccessfulMessage(null, order);
            return;
        }
        // send a success
        this.jmsFacade.sendSuccessfulMessage(tenantId, order);

    }

    /**
     * All valid messages shoudl have a tenant key
     *
     * @param messageHeaders
     * @param order
     * @return
     */
    private boolean validate(MessageHeaders messageHeaders, Order order) {
        if (!messageHeaders.containsKey(TENANT_KEY)) {
            // send a message to the unsuccessful queue stating the order is not successful
            this.jmsFacade.sendUnSuccessfulMessage(null, order);
            return false;
        }
        return true;
    }
}