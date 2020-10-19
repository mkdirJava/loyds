package com.wilson.loyds.ordering.service.stock;

import com.wilson.loyds.ordering.service.jms.listener.model.Order;

public interface IStockService {

    void adjustStockLevels(Order order);
}
