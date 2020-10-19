package com.wilson.loyds.ordering.repository.order.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class OrderEntryEntity {
    @Id
    @GeneratedValue
    private UUID id;
    private UUID productId;
    private BigDecimal amount;
    private BigDecimal itemPrice;

}
