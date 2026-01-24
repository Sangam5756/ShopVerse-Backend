package org.ecommerce.paymentservice.dtos;

import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {

    private String userEmail;
    private String eventType;   // ORDER_PLACED, PAYMENT_SUCCESS
    private String role;
    private String message;
    private Instant timestamp;
}

