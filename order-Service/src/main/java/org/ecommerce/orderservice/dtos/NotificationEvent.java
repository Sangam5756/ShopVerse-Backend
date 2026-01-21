package org.ecommerce.orderservice.dtos;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Builder
public class NotificationEvent  {

    private String eventType;     // ORDER_PLACED, ORDER_STATUS_UPDATED
    private String userEmail;
    private String message;
    private Instant timestamp;
}
