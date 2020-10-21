package com.wilson.loyds.ordering.service.payment;

import com.wilson.loyds.ordering.service.payment.model.Payment;
import com.wilson.loyds.ordering.service.payment.model.PaymentResult;

public interface IPaymentService {
    PaymentResult handlePayment(String tenantId, Payment payment);
}
