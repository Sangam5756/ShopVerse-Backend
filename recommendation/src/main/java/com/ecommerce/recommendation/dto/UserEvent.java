package com.ecommerce.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEvent {

    private String userEmail;     // who did the action
    private String eventType;     // VIEW_PRODUCT, ADD_TO_CART, ORDER_PLACED
    private String entityId;      // productId
    private LocalDateTime timestamp;
}
