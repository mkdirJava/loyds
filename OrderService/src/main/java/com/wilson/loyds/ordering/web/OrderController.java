package com.wilson.loyds.ordering.web;

import com.wilson.loyds.order.api.OrderApi;
import com.wilson.loyds.order.domain.OrderDto;
import com.wilson.loyds.order.domain.OrderEntryDto;
import com.wilson.loyds.ordering.repository.order.model.OrderEntity;
import com.wilson.loyds.ordering.repository.order.model.OrderEntryEntity;
import com.wilson.loyds.ordering.repository.order.repo.OrderRepo;
import feign.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Http access for read only view of orders, the orderId would be emailed out to the individual
 */
@Controller
public class OrderController implements OrderApi {

    @Value("${order.stuff}")
    private String message;

    private OrderRepo orderRepo;

    public OrderController(final OrderRepo orderRepo) {
        this.orderRepo = orderRepo;
    }

    @Override
    public ResponseEntity<OrderDto> getOrderById(final String orderId) {
        UUID orderUUID;
        try{
            orderUUID = UUID.fromString(orderId);
        }catch(Exception e){
            return ResponseEntity.notFound().build();
        }
        OrderDto orderDto = this.orderRepo.findById(orderUUID)
                .filter(orderEntity -> orderEntity != null)
                .map(OrderController::persistenceToDto)
                .stream()
                .findFirst()
                .orElse(null);

        if(message != null){
            OrderDto order = new OrderDto();
            order.setOrderId(message);
            return ResponseEntity.ok(order);
        }
        if (orderDto == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(orderDto);
        }
    }

    /**
     * Take from persistence to Web DTO
     *
     * @param orderEntity
     * @return
     */
    private static OrderDto persistenceToDto(final OrderEntity orderEntity) {
        OrderDto orderDto = new OrderDto();
        orderDto.setTotalCost(orderEntity.getTotal().toString());
        orderDto.setOrderId(orderEntity.getId().toString());
        orderDto.setOrderEntries(orderEntriesToDto(orderEntity.getOrderEntryEntity()));
        return orderDto;
    }

    /**
     * Handle a list of order entries
     *
     * @param orderEntryEntity
     * @return
     */
    private static List<OrderEntryDto> orderEntriesToDto(final List<OrderEntryEntity> orderEntryEntity) {
        return orderEntryEntity.parallelStream().map(OrderController::orderEntryToDto).collect(Collectors.toList());
    }

    /**
     * handle a singular order entry
     *
     * @param orderEntryEntity
     * @return
     */
    private static OrderEntryDto orderEntryToDto(final OrderEntryEntity orderEntryEntity) {
        OrderEntryDto orderEntryDto = new OrderEntryDto();
        orderEntryDto.setAmount(orderEntryEntity.getAmount().intValue());
        orderEntryDto.setProductId(orderEntryEntity.getProductId().toString());
        orderEntryDto.setUnitCost(orderEntryEntity.getItemPrice().toString());
        return orderEntryDto;
    }
}
