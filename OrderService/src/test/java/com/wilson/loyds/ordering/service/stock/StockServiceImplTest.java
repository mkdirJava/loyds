package com.wilson.loyds.ordering.service.stock;

import com.wilson.loyds.ordering.service.jms.listener.model.Order;
import com.wilson.loyds.ordering.service.jms.listener.model.OrderEntryMessage;
import com.wilson.loyds.ordering.repository.stock.model.StockItem;
import com.wilson.loyds.ordering.repository.stock.repo.StockRepo;
import com.wilson.loyds.ordering.service.stock.exception.InvalidStockRequestedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class StockServiceImplTest {

    private StockServiceImpl classUnderTest;
    @Mock
    private StockRepo stockRepo;

    @BeforeEach
    void setup(){
        MockitoAnnotations.initMocks(this);
        classUnderTest = new StockServiceImpl(stockRepo);
    }

    @Test
    void adjustStockSuccessful(){
        UUID itemId = UUID.randomUUID();
        List<OrderEntryMessage> orderEntries = List.of(OrderEntryMessage.builder()
                .itemPrice(BigDecimal.valueOf(2.2))
                .amount(new BigDecimal(1))
                .itemId(itemId)
                .build());
        Order order = new Order();
        order.setAmount(1);
        order.setOrderEntry(orderEntries);

        StockItem stockItem = new StockItem();
        stockItem.setAmountInStock(1);

        when(this.stockRepo.findById(itemId)).thenReturn(Optional.of(stockItem));
        classUnderTest.adjustStockLevels(order);
        stockItem.setAmountInStock(0);
        verify(this.stockRepo,times(2)).findById(itemId);
        verify(this.stockRepo).save(stockItem);
        verifyNoMoreInteractions(this.stockRepo);

    }

    @Test
    void adjustStockFail(){
        UUID itemId = UUID.randomUUID();
        List<OrderEntryMessage> orderEntries = List.of(OrderEntryMessage.builder()
                .itemPrice(BigDecimal.valueOf(2.2))
                .amount(new BigDecimal(1))
                .itemId(itemId)
                .build());
        Order order = new Order();
        order.setAmount(1);
        order.setOrderEntry(orderEntries);

        StockItem stockItem = new StockItem();
        stockItem.setAmountInStock(1);

        when(this.stockRepo.findById(itemId)).thenReturn(Optional.empty());
        assertThrows(InvalidStockRequestedException.class,()->classUnderTest.adjustStockLevels(order));
        verify(this.stockRepo).findById(itemId);
        verifyNoMoreInteractions(this.stockRepo);
    }
}