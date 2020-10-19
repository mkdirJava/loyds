package com.wilson.loyds.ordering.service.jms.listener.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;
/**
 * Message on the order entry message as a POJO
 */
@Data
@Builder
public class OrderEntryMessage {
    private UUID itemId;
    private BigDecimal amount;
    private BigDecimal itemPrice;
}