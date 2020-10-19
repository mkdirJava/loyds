package com.wilson.loyds.ordering.repository.stock.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
public class StockItem {
    @Id
    private UUID id;
    private int amountInStock;
    private BigDecimal unitPrice;
}
