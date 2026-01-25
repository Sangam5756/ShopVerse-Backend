package org.ecommerce.orderservice.dtos;

import lombok.*;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsEvent {

    private String eventType;
    private String service;
    private String userEmail;
    private String entityId;
    private Double amount;
    private LocalDateTime timestamp;
    private Map<String, Object> metadata;
}
