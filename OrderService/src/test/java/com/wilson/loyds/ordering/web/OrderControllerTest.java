package com.wilson.loyds.ordering.web;

import com.wilson.loyds.order.domain.OrderDto;
import com.wilson.loyds.ordering.repository.order.model.OrderEntity;
import com.wilson.loyds.ordering.repository.order.model.OrderEntryEntity;
import com.wilson.loyds.ordering.repository.order.repo.OrderRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class OrderControllerTest {

    private OrderController classUnderTest;
    @Mock
    private OrderRepo orderRepo;
    private UUID orderId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        classUnderTest = new OrderController(orderRepo);

    }

    @Test
    void successfulGetRequest() {
        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID orderEntryId = UUID.randomUUID();
        BigDecimal unitCost = new BigDecimal(22);
        List<OrderEntryEntity> orderEntries = List.of(OrderEntryEntity.builder()
                .id(orderEntryId)
                .itemPrice(new BigDecimal(22))
                .amount(new BigDecimal(1))
                .productId(productId)
                .build());
        OrderEntity orderEntity = OrderEntity.builder()
                .id(orderId)
                .total(new BigDecimal(22))
                .orderEntryEntity(orderEntries)
                .build();
        when(this.orderRepo.findById(orderId)).thenReturn(Optional.of(orderEntity));
        ResponseEntity<OrderDto> orderById = classUnderTest.getOrderById(orderId.toString());

        assertAll(() -> assertTrue(orderById.getStatusCode().value() == 200),
                () -> assertTrue(orderById.getBody().getOrderId().equalsIgnoreCase(orderId.toString())),
                () -> assertEquals(1, orderById.getBody().getOrderEntries().size()),
                () -> assertEquals(1, orderById.getBody().getOrderEntries().get(0).getAmount()),
                () -> assertEquals(productId.toString(), orderById.getBody().getOrderEntries().get(0).getProductId()),
                () -> assertEquals(unitCost.toString(), orderById.getBody().getOrderEntries().get(0).getUnitCost()));

        verify(this.orderRepo).findById(orderId);
        verifyNoMoreInteractions(this.orderRepo);

    }

    @Test
    void notFoundGetRequest() {
        when(this.orderRepo.findById(orderId)).thenReturn(Optional.empty());
        ResponseEntity<OrderDto> orderById = classUnderTest.getOrderById(orderId.toString());
        assertEquals(404, orderById.getStatusCode().value());
        verify(this.orderRepo).findById(orderId);
        verifyNoMoreInteractions(this.orderRepo);
    }

}