package com.wilson.loyds.ordering.repository.stock.repo;

import com.wilson.loyds.ordering.repository.stock.model.StockItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StockRepo extends JpaRepository<StockItem, UUID> {
}
