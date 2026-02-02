package com.ecommerce.auth.dto;

import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationEvent {
    private String eventType;
    private String userEmail;
    private String role;
    private String message;
    private LocalDateTime timestamp;
    private String resetToken;
}
