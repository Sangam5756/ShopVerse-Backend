package org.ecommerce.paymentservice.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PaymentResponseDTO {

    private String paymentId;
    private String razorpayOrderId;
    private double amount;
    private String status;
}