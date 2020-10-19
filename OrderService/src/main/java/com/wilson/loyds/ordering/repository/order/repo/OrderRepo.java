package com.wilson.loyds.ordering.repository.order.repo;

import com.wilson.loyds.ordering.repository.order.model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepo extends JpaRepository<OrderEntity, UUID> {
    Optional<OrderEntity> findByMessageId(String messageId);
}
