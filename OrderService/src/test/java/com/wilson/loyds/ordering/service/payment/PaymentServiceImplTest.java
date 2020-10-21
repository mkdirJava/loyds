package com.wilson.loyds.ordering.service.payment;

import com.wilson.loyds.ordering.service.jms.listener.model.Order;
import com.wilson.loyds.ordering.service.jms.listener.model.OrderEntryMessage;
import com.wilson.loyds.ordering.service.payment.exception.PaymentUnsuccessfulException;
import com.wilson.loyds.ordering.service.payment.model.Payment;
import com.wilson.loyds.ordering.service.payment.model.PaymentResult;
import com.wilson.loyds.payment.api.PaymentApi;
import com.wilson.loyds.payment.domain.PaymentResultDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class PaymentServiceImplTest {

    private PaymentServiceImpl classUnderTest;
    @Mock
    private PaymentApi paymentApi;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
        this.classUnderTest = new PaymentServiceImpl(paymentApi);
    }

    @Test
    void validPayment() {
        String tenantId = "TENANT_1";
        List<OrderEntryMessage> orderEntries = List.of(OrderEntryMessage.builder()
                .itemPrice(BigDecimal.valueOf(2.2))
                .amount(new BigDecimal(1))
                .itemId(UUID.randomUUID())
                .build());
        Order order = new Order();
        order.setAmount(1);
        order.setOrderEntry(orderEntries);
        Payment payment = Payment.builder().order(order).build();
        PaymentResultDto paymentResultDto = new PaymentResultDto();
        paymentResultDto.setSuccess(true);
        when(this.paymentApi.payForClient(eq(tenantId), any())).thenReturn(ResponseEntity.ok(paymentResultDto));
        PaymentResult paymentResult = classUnderTest.handlePayment(tenantId, payment);
        assertTrue(paymentResult.isSuccessful());
    }

    @Test
    void paymentFailTransmission() {
        String tenantId = "TENANT_1";
        List<OrderEntryMessage> orderEntries = List.of(OrderEntryMessage.builder()
                .itemPrice(BigDecimal.valueOf(2.2))
                .amount(new BigDecimal(1))
                .itemId(UUID.randomUUID())
                .build());
        Order order = new Order();
        order.setAmount(1);
        order.setOrderEntry(orderEntries);
        Payment payment = Payment.builder().order(order).build();
        when(this.paymentApi.payForClient(eq(tenantId), any())).thenReturn(ResponseEntity.badRequest().build());
        assertThrows(PaymentUnsuccessfulException.class, () -> classUnderTest.handlePayment(tenantId, payment));
    }

    @Test
    void paymentFailTransaction() {
        String tenantId = "TENANT_1";
        List<OrderEntryMessage> orderEntries = List.of(OrderEntryMessage.builder()
                .itemPrice(BigDecimal.valueOf(2.2))
                .amount(new BigDecimal(1))
                .itemId(UUID.randomUUID())
                .build());
        Order order = new Order();
        order.setAmount(1);
        order.setOrderEntry(orderEntries);
        Payment payment = Payment.builder().order(order).build();
        PaymentResultDto paymentResultDto = new PaymentResultDto();
        paymentResultDto.setSuccess(false);
        when(this.paymentApi.payForClient(eq(tenantId), any())).thenReturn(ResponseEntity.ok(paymentResultDto));
        assertThrows(PaymentUnsuccessfulException.class, () -> classUnderTest.handlePayment(tenantId, payment));
    }
}