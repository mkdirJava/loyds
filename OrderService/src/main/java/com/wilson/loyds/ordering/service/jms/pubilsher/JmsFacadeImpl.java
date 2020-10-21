package com.wilson.loyds.ordering.service.jms.pubilsher;

import org.springframework.stereotype.Component;

/**
 * Facade for sending messages
 */
@Component
public class JmsFacadeImpl implements IJmsFacade {

    private OrderAcceptedPublisher orderAcceptedPublisher;
    private OrderRejectedPublisher orderRejectedPublisher;

    public JmsFacadeImpl(final OrderAcceptedPublisher orderAcceptedPublisher,
            final OrderRejectedPublisher orderRejectedPublisher) {
        this.orderAcceptedPublisher = orderAcceptedPublisher;
        this.orderRejectedPublisher = orderRejectedPublisher;
    }

    @Override
    public void sendSuccessfulMessage(final String tenantId, final Object object) {
        this.orderAcceptedPublisher.sendOrderAccepted(tenantId, object);
    }

    @Override
    public void sendUnSuccessfulMessage(final String tenantId, final Object object) {
        this.orderRejectedPublisher.sendOrderRejected(tenantId, object);
    }
}
