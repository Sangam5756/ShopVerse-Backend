package com.ecommerce.analytics.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsEvent {

    private String eventType;      // USER_REGISTER, ORDER_PLACED
    private String service;        // auth, order, payment
    private String userEmail;
    private String entityId;       // orderId, productId
    private Double amount;         // payment amount
    private LocalDateTime timestamp;
    private Map<String, Object> metadata;
}

