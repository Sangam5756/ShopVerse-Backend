package org.ecommerce.paymentservice.dtos;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequestDTO {

    private Long orderId;
    private Long customerId;
    private double amount;
}
