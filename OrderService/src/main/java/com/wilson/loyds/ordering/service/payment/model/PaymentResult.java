package com.wilson.loyds.ordering.service.payment.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PaymentResult {
    boolean successful;
}
