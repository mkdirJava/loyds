package com.wilson.loyds.ordering.service.jms.listener.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Message on the order message as a POJO
 */
@Data
public class Order implements Serializable {

    private String messageId;
    private String recipientEmail;
    private int amount;
    private List<OrderEntryMessage> orderEntry;
    private BigDecimal unitCostAtPurchase;
    private boolean ordered;
}