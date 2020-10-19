package com.wilson.loyds.ordering.service.order.handler;

import com.wilson.loyds.ordering.service.jms.listener.model.Order;
import com.wilson.loyds.ordering.repository.order.repo.OrderRepo;
import com.wilson.loyds.ordering.repository.stock.repo.StockRepo;
import com.wilson.loyds.ordering.service.email.IEmailService;
import com.wilson.loyds.ordering.service.order.handler.exception.BlockingPaymentException;
import org.springframework.stereotype.Component;

/**
 * This does not handle payments or stock adjustments
 */
@Component
public class BlockPaymentOrderHandler extends OrderHandler {

    public BlockPaymentOrderHandler(final OrderRepo orderRepo,
            final StockRepo stockRepo,
            final IEmailService iEmailService) {
        super(stockRepo, orderRepo, iEmailService);
    }

    @Override
    public void processOrderAction(final String tenantId, final Order order) {
        throw new BlockingPaymentException(String.format("Blocking payment for tenant %s for order %s",
                tenantId,
                order));
    }
}
