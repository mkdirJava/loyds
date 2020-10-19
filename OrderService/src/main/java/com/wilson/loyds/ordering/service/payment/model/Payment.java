package com.wilson.loyds.ordering.service.payment.model;

import com.wilson.loyds.ordering.service.jms.listener.model.Order;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Payment {
    private Order order;
}
