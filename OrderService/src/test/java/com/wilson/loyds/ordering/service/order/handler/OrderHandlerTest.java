package com.wilson.loyds.ordering.service.order.handler;

import com.wilson.loyds.ordering.repository.order.model.OrderEntity;
import com.wilson.loyds.ordering.repository.order.model.OrderEntryEntity;
import com.wilson.loyds.ordering.repository.order.repo.OrderRepo;
import com.wilson.loyds.ordering.repository.stock.model.StockItem;
import com.wilson.loyds.ordering.repository.stock.repo.StockRepo;
import com.wilson.loyds.ordering.service.email.IEmailService;
import com.wilson.loyds.ordering.service.jms.listener.model.Order;
import com.wilson.loyds.ordering.service.order.model.OrderProcessResult;
import com.wilson.loyds.ordering.service.payment.IPaymentService;
import com.wilson.loyds.ordering.service.payment.model.Payment;
import com.wilson.loyds.ordering.service.stock.IStockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class OrderHandlerTest {

    private OrderHandler classUnderTest;
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
    void baseOrderHandler() {

        String tenantId = "tenantId";
        String messageId = "messageId";
        UUID orderEntityId = UUID.randomUUID();
        String orderRecipient = "loyds@gmail.com";
        Order order = new Order();
        order.setMessageId(messageId);
        order.setRecipientEmail(orderRecipient);
        OrderEntity orderEntity = OrderEntity.builder().messageId(messageId).id(orderEntityId).build();
        when(orderRepo.findByMessageId(order.getMessageId())).thenReturn(Optional.of(orderEntity));

        LocalDateTime start = LocalDateTime.now();
        OrderProcessResult orderProcessResult = this.classUnderTest.processOrder(tenantId, order);
        LocalDateTime finished = LocalDateTime.now();

        assertTrue(orderProcessResult.getPaymentDateTime().isAfter(start) || orderProcessResult.getPaymentDateTime()
                .equals(start));
        assertTrue(orderProcessResult.getPaymentDateTime().isBefore(finished) || orderProcessResult.getPaymentDateTime()
                .equals(finished));

        verify(this.orderRepo).findByMessageId(messageId);
        verify(this.iEmailService).sendEmail(orderRecipient, orderEntityId.toString());
        verify(this.paymentApi).handlePayment(tenantId, Payment.builder().order(order).build());
        verify(this.iStockService).adjustStockLevels(order);
        orderEntity.setOrdered(true);
        verify(this.orderRepo).save(orderEntity);

        verifyNoMoreInteractions(this.paymentApi);
        verifyNoMoreInteractions(this.iStockService);
        verifyNoMoreInteractions(this.orderRepo);
        verifyNoMoreInteractions(this.stockRepo);
        verifyNoMoreInteractions(this.iEmailService);
    }

    @Test
    void baseOrderHandlerNewOrder() {

        UUID productId = UUID.randomUUID();
        String tenantId = "tenantId";
        String messageId = "messageId";
        UUID orderEntityId = UUID.randomUUID();
        String orderRecipient = "loyds@gmail.com";
        Order order = new Order();
        order.setMessageId(messageId);
        order.setRecipientEmail(orderRecipient);
        order.setOrderEntry(List.of());
        OrderEntryEntity orderEntryEntity = new OrderEntryEntity();
        orderEntryEntity.setAmount(BigDecimal.ONE);
        orderEntryEntity.setItemPrice(BigDecimal.ONE);
        orderEntryEntity.setProductId(productId);
        OrderEntity orderEntity = OrderEntity.builder()
                .messageId(messageId)
                .id(orderEntityId)
                .orderEntryEntity(List.of(orderEntryEntity))
                .build();
        when(orderRepo.save(any())).thenReturn(orderEntity);

        when(this.stockRepo.findById(productId)).thenReturn(Optional.of(StockItem.builder().build()));

        LocalDateTime start = LocalDateTime.now();
        OrderProcessResult orderProcessResult = this.classUnderTest.processOrder(tenantId, order);
        LocalDateTime finished = LocalDateTime.now();

        assertTrue(orderProcessResult.getPaymentDateTime().isAfter(start));
        assertTrue(orderProcessResult.getPaymentDateTime().isBefore(finished) || orderProcessResult.getPaymentDateTime()
                .equals(finished));

        verify(this.orderRepo, times(2)).save(any());
        verify(this.orderRepo).findByMessageId(messageId);
        verify(this.iEmailService).sendEmail(orderRecipient, orderEntityId.toString());
        verify(this.paymentApi).handlePayment(tenantId, Payment.builder().order(order).build());
        verify(this.iStockService).adjustStockLevels(order);
        orderEntity.setOrdered(true);
        verify(this.orderRepo).save(orderEntity);

        verifyNoMoreInteractions(this.paymentApi);
        verifyNoMoreInteractions(this.iStockService);
        verifyNoMoreInteractions(this.orderRepo);
        verifyNoMoreInteractions(this.stockRepo);
        verifyNoMoreInteractions(this.iEmailService);
    }

}