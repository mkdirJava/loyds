package com.wilson.loyds.ordering.service.order.handler;

import com.wilson.loyds.ordering.service.jms.listener.model.Order;
import com.wilson.loyds.ordering.repository.order.repo.OrderRepo;
import com.wilson.loyds.ordering.repository.stock.repo.StockRepo;
import com.wilson.loyds.ordering.service.email.IEmailService;
import com.wilson.loyds.ordering.service.payment.IPaymentService;
import com.wilson.loyds.ordering.service.payment.model.Payment;
import com.wilson.loyds.ordering.service.stock.IStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Order before payment, handles the stock before handling payment
 */
@Component
public class OrderBeforePaymentOrderHandler extends OrderHandler {

    private IPaymentService paymentApi;
    private IStockService iStockService;

    @Autowired
    public OrderBeforePaymentOrderHandler(final OrderRepo orderRepo,
            final StockRepo stockRepo,
            final IPaymentService paymentApi,
            final IStockService iStockService,
            final IEmailService iEmailService) {
        super(stockRepo, orderRepo, iEmailService);
        this.paymentApi = paymentApi;
        this.iStockService = iStockService;
    }

    /**
     * This is the opposite of the basic action
     * @param tenantId
     * @param order
     */
    @Override
    public void processOrderAction(final String tenantId, final Order order) {
        // handle stock
        this.iStockService.adjustStockLevels(order);
        // handle payment
        this.paymentApi.handlePayment(tenantId, Payment.builder().order(order).build());
    }
}
