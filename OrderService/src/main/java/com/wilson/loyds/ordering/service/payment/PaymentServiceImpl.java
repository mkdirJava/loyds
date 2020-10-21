package com.wilson.loyds.ordering.service.payment;

import com.wilson.loyds.ordering.service.jms.listener.model.OrderEntryMessage;
import com.wilson.loyds.ordering.service.payment.exception.PaymentUnsuccessfulException;
import com.wilson.loyds.ordering.service.payment.model.Payment;
import com.wilson.loyds.ordering.service.payment.model.PaymentResult;
import com.wilson.loyds.payment.api.PaymentApi;
import com.wilson.loyds.payment.domain.OrderEntryDto;
import com.wilson.loyds.payment.domain.PaymentDto;
import com.wilson.loyds.payment.domain.PaymentResultDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Wrapper to handle feign client
 */
@Component
public class PaymentServiceImpl implements IPaymentService {

    private PaymentApi paymentApi;

    public PaymentServiceImpl(final PaymentApi paymentApi) {
        this.paymentApi = paymentApi;
    }

    /**
     * logic to determine if a payment call is successful or not
     *
     * @param tenantId
     * @param payment
     * @return
     */
    @Override
    public PaymentResult handlePayment(final String tenantId, final Payment payment) {
        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setTenantId(tenantId);
        List<OrderEntryDto> orderEntryDtos = payment.getOrder()
                .getOrderEntry()
                .stream()
                .map(PaymentServiceImpl::convertToPaymentDto)
                .collect(Collectors.toList());
        paymentDto.setOrderEntry(orderEntryDtos);
        BigDecimal total = orderEntryDtos.stream()
                .map(orderEntryDto -> orderEntryDto.getItemPrice().multiply(orderEntryDto.getAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        paymentDto.setTotalCost(total);
        ResponseEntity<PaymentResultDto> paymentResultDtoResponseEntity = this.paymentApi.payForClient(tenantId,
                paymentDto);
        if (paymentResultDtoResponseEntity.getStatusCode().value() != (200)) {
            throw new PaymentUnsuccessfulException("payment resulted in a non 200 Http response");
        }
        PaymentResultDto body = paymentResultDtoResponseEntity.getBody();
        if (!body.isSuccess()) {
            throw new PaymentUnsuccessfulException("payment is not successful");
        }
        return PaymentResult.builder().successful(body.isSuccess()).build();
    }

    private static OrderEntryDto convertToPaymentDto(final OrderEntryMessage orderEntry) {
        OrderEntryDto orderEntryDto = new OrderEntryDto();
        orderEntryDto.setAmount(orderEntry.getAmount());
        orderEntryDto.setItemId(orderEntry.getItemId().toString());
        orderEntryDto.setItemPrice(orderEntry.getItemPrice());
        return orderEntryDto;
    }
}
