package com.wilson.loyds.ordering.service.order.handler;

import com.wilson.loyds.ordering.service.jms.listener.model.Order;
import com.wilson.loyds.ordering.repository.order.repo.OrderRepo;
import com.wilson.loyds.ordering.repository.stock.repo.StockRepo;
import com.wilson.loyds.ordering.service.email.IEmailService;
import com.wilson.loyds.ordering.service.payment.IPaymentService;
import com.wilson.loyds.ordering.service.payment.model.Payment;
import com.wilson.loyds.ordering.service.stock.IStockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class BasicOrderHandlerTest {

    private BasicOrderHandler classUnderTest;
    @Mock
    private IPaymentService paymentApi;
    @Mock
    private IStockService iStockService;
    @Mock
    private OrderRepo orderRepo;
    @Mock
    private StockRepo stockRepo;
    @Mock
    private IEmailService iEmailService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
        this.classUnderTest = new BasicOrderHandler(orderRepo, stockRepo, iEmailService, paymentApi, iStockService);
    }

    @Test
    void basicProcessOrder() {
        String tenantId = "tenantId";
        Order order = new Order();
        this.classUnderTest.processOrderAction(tenantId, order);
        verify(this.paymentApi).handlePayment(tenantId, Payment.builder().order(order).build());
        verify(this.iStockService).adjustStockLevels(order);
        verifyNoMoreInteractions(this.paymentApi);
        verifyNoMoreInteractions(this.iStockService);
        verifyNoMoreInteractions(this.orderRepo);
        verifyNoMoreInteractions(this.stockRepo);
        verifyNoMoreInteractions(this.iEmailService);
    }

}