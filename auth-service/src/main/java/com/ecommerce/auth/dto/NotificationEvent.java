package com.ecommerce.auth.dto;

import lombok.*;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationEvent {
    private String userEmail;
    private String message;
    private String evenType;
    private Instant timestamp;
}