package com.wilson.loyds.ordering.service.order.model;

import com.wilson.loyds.ordering.service.jms.listener.model.Order;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class OrderProcessResult {
    private Order order;
    private LocalDateTime paymentDateTime;
}
