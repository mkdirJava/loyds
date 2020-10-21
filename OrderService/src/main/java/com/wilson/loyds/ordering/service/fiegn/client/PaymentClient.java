package com.wilson.loyds.ordering.service.fiegn.client;

import com.wilson.loyds.payment.api.PaymentApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * A Fiegn client to handle payment request
 */
@FeignClient(name = "paymentClient", url = "${payment.url}")
public interface PaymentClient extends PaymentApi {
}
