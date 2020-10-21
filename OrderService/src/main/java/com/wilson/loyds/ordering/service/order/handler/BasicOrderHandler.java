package com.wilson.loyds.ordering.service.order.handler;

import com.wilson.loyds.ordering.service.jms.listener.model.Order;
import com.wilson.loyds.ordering.repository.order.repo.OrderRepo;
import com.wilson.loyds.ordering.repository.stock.repo.StockRepo;
import com.wilson.loyds.ordering.service.email.IEmailService;
import com.wilson.loyds.ordering.service.payment.IPaymentService;
import com.wilson.loyds.ordering.service.payment.model.Payment;
import com.wilson.loyds.ordering.service.stock.IStockService;
import org.springframework.stereotype.Component;

/**
 * Basic order handler for handling payment and stock adjustments
 */
@Component
public class BasicOrderHandler extends OrderHandler {

    private IPaymentService paymentApi;
    private IStockService iStockService;

    public BasicOrderHandler(final OrderRepo orderRepo,
            final StockRepo stockRepo,
            final IEmailService iEmailService,
            final IPaymentService paymentApi,
            final IStockService iStockService) {
        super(stockRepo, orderRepo, iEmailService);
        this.paymentApi = paymentApi;
        this.iStockService = iStockService;
    }

    /**
     * Basic action to handle payment and adjust stocks
     *
     * @param tenantId
     * @param order
     */
    @Override
    public void processOrderAction(final String tenantId, final Order order) {
        // handle payment
        this.paymentApi.handlePayment(tenantId, Payment.builder().order(order).build());
        // handle stock
        this.iStockService.adjustStockLevels(order);
    }
}
