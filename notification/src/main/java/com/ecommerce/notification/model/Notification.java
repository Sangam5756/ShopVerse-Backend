package com.ecommerce.notification.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Notification {
    @Id
    private String id;

    private String eventType;
    private String userEmail;
    private String role;
    private String message;
    private LocalDateTime timestamp;
    private boolean read = false;
}
