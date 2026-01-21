package org.ecommerce.product.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class NotificationEvent {

    private String eventType;   // PRODUCT_CREATED, PRODUCT_UPDATED, PRODUCT_DELETED
    private String userEmail;
    private String message;
    private Instant timestamp;
}
