package com.wilson.loyds.ordering.service.stock;

import com.wilson.loyds.ordering.service.jms.listener.model.Order;
import com.wilson.loyds.ordering.repository.stock.model.StockItem;
import com.wilson.loyds.ordering.repository.stock.repo.StockRepo;
import com.wilson.loyds.ordering.service.stock.exception.InvalidStockRequestedException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * StockService to handle adjustments of stock
 */
@Component
public class StockServiceImpl implements IStockService {

    private StockRepo stockRepo;

    public StockServiceImpl(final StockRepo stockRepo) {
        this.stockRepo = stockRepo;
    }

    /**
     * Adjust stock repo against the order
     *
     * @param order
     */
    @Override
    public void adjustStockLevels(final Order order) {
        validate(order);
        order.getOrderEntry().stream().forEach(orderEntry -> {
            StockItem stockItem = stockRepo.findById(orderEntry.getItemId()).get();
            stockItem.setAmountInStock(stockItem.getAmountInStock() - order.getAmount());
            stockRepo.save(stockItem);
        });
    }

    /**
     * Validation of order to ensure the order comprises of known stock
     *
     * @param order
     */
    private void validate(final Order order) {
        List<UUID> invalidStockIds = order.getOrderEntry().stream().map(orderEntry -> {
            Optional<StockItem> stockItem = stockRepo.findById(orderEntry.getItemId());
            if (stockItem.isEmpty()) {
                return orderEntry.getItemId();
            }
            return null;
        }).filter(itemId -> itemId != null).collect(Collectors.toList());

        if (!invalidStockIds.isEmpty()) {
            String invalidStockIdsCommaSeparated = invalidStockIds.stream()
                    .map(stockItemId -> stockItemId.toString())
                    .collect(Collectors.joining(","));
            throw new InvalidStockRequestedException(String.format("Stock requested is not found %s",
                    invalidStockIdsCommaSeparated));
        }
    }
}
