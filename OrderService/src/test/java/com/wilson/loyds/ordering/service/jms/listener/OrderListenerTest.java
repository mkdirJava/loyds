package com.wilson.loyds.ordering.service.jms.listener;

import com.wilson.loyds.ordering.service.jms.listener.model.Order;
import com.wilson.loyds.ordering.service.jms.pubilsher.IJmsFacade;
import com.wilson.loyds.ordering.service.order.IOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.MessageHeaders;

import java.util.Map;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class OrderListenerTest {

    private OrderListener classUnderTest;
    @Mock
    private IOrderService iOrderService;
    @Mock
    private IJmsFacade jmsFacade;
    private static final String TENANT_KEY = "TENANT_KEY";

    @BeforeEach
    void setup(){
        MockitoAnnotations.initMocks(this);
        classUnderTest = new OrderListener(jmsFacade,iOrderService);
    }


    @DisplayName("when an invalid message is given then a jms message should be put on the failed queue")
    @Test
    public void invalidMessage(){
        MessageHeaders messageHeaders = new MessageHeaders(Map.of());
        Order order = new Order();
        this.classUnderTest.receiveOrder(messageHeaders,order);
        verify(this.jmsFacade).sendUnSuccessfulMessage(null,order);
        verifyNoMoreInteractions(this.jmsFacade);
        verifyNoMoreInteractions(this.iOrderService);
    }
    @DisplayName("when given a valid order but processed incorrectly then a jms message should be put on the failed queue")
    @Test
    public void failedOrderProcess(){
        String tenantId = "TENANT_1";
        MessageHeaders messageHeaders = new MessageHeaders(Map.of(TENANT_KEY,tenantId));
        Order order = new Order();
        doThrow(new RuntimeException()).when(this.iOrderService).processOrder(tenantId,order);
        this.classUnderTest.receiveOrder(messageHeaders,order);
        verify(this.jmsFacade).sendUnSuccessfulMessage(null,order);
        verify(this.iOrderService).processOrder(tenantId,order);
        verifyNoMoreInteractions(this.jmsFacade);
        verifyNoMoreInteractions(this.iOrderService);
    }
    @DisplayName("When given a valid order then the order should be processed and a success message put on the success queue")
    @Test
    public void successOrderProcess(){

        String tenantId = "TENANT_1";
        MessageHeaders messageHeaders = new MessageHeaders(Map.of(TENANT_KEY,tenantId));
        Order order = new Order();
        this.classUnderTest.receiveOrder(messageHeaders,order);
        verify(this.jmsFacade).sendSuccessfulMessage(tenantId,order);
        verify(this.iOrderService).processOrder(tenantId,order);
        verifyNoMoreInteractions(this.jmsFacade);
        verifyNoMoreInteractions(this.iOrderService);

    }



}