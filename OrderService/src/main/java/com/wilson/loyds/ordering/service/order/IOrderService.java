package com.wilson.loyds.ordering.service.order;

import com.wilson.loyds.ordering.service.jms.listener.model.Order;
import com.wilson.loyds.ordering.service.order.model.OrderProcessResult;

public interface IOrderService {
    OrderProcessResult processOrder(String tenantId, Order order);
}