package com.wilson.loyds.ordering.service.order.handler;

import com.wilson.loyds.ordering.repository.order.model.OrderEntity;
import com.wilson.loyds.ordering.repository.order.model.OrderEntryEntity;
import com.wilson.loyds.ordering.repository.order.repo.OrderRepo;
import com.wilson.loyds.ordering.repository.stock.repo.StockRepo;
import com.wilson.loyds.ordering.service.email.IEmailService;
import com.wilson.loyds.ordering.service.jms.listener.model.Order;
import com.wilson.loyds.ordering.service.jms.listener.model.OrderEntryMessage;
import com.wilson.loyds.ordering.service.order.handler.exception.OrderItemNotInStockException;
import com.wilson.loyds.ordering.service.order.model.OrderProcessResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * base class for the orderHandlers, this has before and after actions
 */
public abstract class OrderHandler {

    private StockRepo stockRepo;
    private OrderRepo orderRepo;
    private IEmailService iEmailService;
    SpecialActions action;

    protected OrderHandler(final StockRepo stockRepo, final OrderRepo orderRepo, final IEmailService iEmailService) {
        this.stockRepo = stockRepo;
        this.orderRepo = orderRepo;
        this.iEmailService = iEmailService;
    }

    /**
     * action is used as a trigger point for the strategy design pattern.
     *
     * @return
     */
    public String getAction() {
        return action.toString();
    }

    /**
     * overridden by child classes
     *
     * @param tenantId
     * @param order
     */
    public abstract void processOrderAction(final String tenantId, final Order order);

    public final OrderProcessResult processOrder(final String tenantId, final Order order) {
        OrderEntity orderEntity = beforeProcessOrder(tenantId, order);
        processOrderAction(tenantId, order);
        afterProcessOrder(orderEntity);
        return OrderProcessResult.builder().order(order).paymentDateTime(LocalDateTime.now()).build();
    }

    /**
     * before the action starts save down the order and then send an email
     *
     * @param tenantId
     * @param order
     * @return
     */
    private OrderEntity beforeProcessOrder(final String tenantId, final Order order) {
        OrderEntity orderEntity = orderRepo.findByMessageId(order.getMessageId())
                .orElseGet(() -> orderRepo.save(convertToPersistence(tenantId, order)));
        iEmailService.sendEmail(order.getRecipientEmail(), orderEntity.getId().toString());
        return orderEntity;
    }

    /**
     * after the action update the order as ordered
     *
     * @param savedEntity
     */
    private void afterProcessOrder(final OrderEntity savedEntity) {
        savedEntity.setOrdered(true);
        orderRepo.save(savedEntity);
    }

    /**
     * save the payload in as a persistence domain
     *
     * @param tenantId
     * @param order
     * @return
     */
    private OrderEntity convertToPersistence(final String tenantId, final Order order) {
        validate(order);
        return OrderEntity.builder()
                .ordered(false)
                .tenantId(tenantId)
                .messageId(order.getMessageId())
                .orderEntryEntity(order.getOrderEntry()
                        .stream()
                        .map(OrderHandler::concertToPersistence)
                        .collect(Collectors.toList()))
                .total(order.getOrderEntry()
                        .stream()
                        .map(orderEntryMessage -> orderEntryMessage.getAmount()
                                .multiply(orderEntryMessage.getItemPrice()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .build();
    }

    /**
     * validate the order
     *
     * @param order
     */
    private void validate(final Order order) {
        order.getOrderEntry()
                .parallelStream()
                .map(orderEntryMessage -> this.stockRepo.findById(orderEntryMessage.getItemId())
                        .orElseThrow(OrderItemNotInStockException::new));
    }

    ;

    /**
     * convert the order entry to persistence
     *
     * @param orderEntryMessage
     * @return
     */
    private static OrderEntryEntity concertToPersistence(final OrderEntryMessage orderEntryMessage) {
        return OrderEntryEntity.builder()
                .amount(orderEntryMessage.getAmount())
                .productId(orderEntryMessage.getItemId())
                .itemPrice(orderEntryMessage.getItemPrice())
                .build();
    }
}