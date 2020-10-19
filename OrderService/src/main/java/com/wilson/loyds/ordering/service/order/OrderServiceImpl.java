package com.wilson.loyds.ordering.service.order;

import com.wilson.loyds.customer.config.domain.CustomerConfigDto;
import com.wilson.loyds.ordering.service.jms.listener.model.Order;
import com.wilson.loyds.ordering.service.config.ICustomerConfigService;
import com.wilson.loyds.ordering.service.order.handler.BasicOrderHandler;
import com.wilson.loyds.ordering.service.order.handler.OrderHandler;
import com.wilson.loyds.ordering.service.order.model.OrderProcessResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Order service implementation to handle orders
 */
@Component
public class OrderServiceImpl implements IOrderService {

    private BasicOrderHandler basicOrderHandler;
    private List<OrderHandler> orderHandlers;
    private ICustomerConfigService customerConfigService;

    @Autowired
    public OrderServiceImpl(final BasicOrderHandler basicOrderHandler,
            final List<OrderHandler> orderHandlers,
            final ICustomerConfigService customerConfigService) {
        this.basicOrderHandler = basicOrderHandler;
        this.orderHandlers = orderHandlers;
        this.customerConfigService = customerConfigService;
    }

    /**
     * This uses customer config and handlers as a strategy design to pick an appropriate handler to use on the order
     * @param tenantId
     * @param order
     * @return
     */
    @Override
    public OrderProcessResult processOrder(String tenantId, final Order order) {
        CustomerConfigDto customerConfig = customerConfigService.getCustomerConfig(tenantId);
        OrderHandler handler = this.orderHandlers.stream()
                .filter(orderHandler -> orderHandler.getAction()
                        .equalsIgnoreCase(customerConfig.getSpecialAction().toString()))
                .findFirst()
                .orElseGet(() -> basicOrderHandler);
        return handler.processOrder(tenantId,order);
    }
}