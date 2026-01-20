package org.ecommerce.paymentservice.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class PaymentSummaryDTO {

    private Long paymentId;
    private Long orderId;
    private double amount;
    private String status;
    private LocalDateTime createdAt;
}