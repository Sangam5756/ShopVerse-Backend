package org.ecommerce.paymentservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RazorpayCallbackDTO {

    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;
}