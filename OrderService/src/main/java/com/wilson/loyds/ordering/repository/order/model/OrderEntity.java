package com.wilson.loyds.ordering.repository.order.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {

    @Id
    @GeneratedValue
    private UUID id;
    private String messageId;
    private String tenantId;
    @OneToMany
    private List<OrderEntryEntity> orderEntryEntity;
    private boolean ordered;
    private BigDecimal total;

}
