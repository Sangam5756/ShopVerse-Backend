package com.ecommerce.notification.consumer;

import com.ecommerce.notification.event.NotificationEvent;
import com.ecommerce.notification.model.Notification;
import com.ecommerce.notification.repository.NotificationRepository;
import com.ecommerce.notification.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    @Value("${productionUrl}")
    private String productionUrl;
    private final NotificationRepository repository;
    private final ObjectMapper objectMapper;
    private final EmailService emailService;

    @KafkaListener(
            topics = "notification-topic",
            groupId = "notification-group"
    )
    public void consume(ConsumerRecord<String, String> record) {
        log.info("RAW EVENT RECEIVED: key={}, value={}", record.key(), record.value());

        try {
            NotificationEvent event =
                    objectMapper.readValue(record.value(), NotificationEvent.class);

            log.info("DESERIALIZED EVENT: {}", event);

            Notification notification = Notification.builder()
                    .eventType(event.getEventType())
                    .userEmail(event.getUserEmail())
                    .role(event.getRole())
                    .message(event.getMessage())
                    .timestamp(event.getTimestamp())
                    .read(false)
                    .build();

            repository.save(notification);

            log.info("SAVED TO MONGO for {}", event.getUserEmail());

            // Send email notification
            sendEmailNotification(event);

        } catch (Exception e) {
            log.error("CONSUMER FAILED", e);
        }
    }

    private void sendEmailNotification(NotificationEvent event) {
        try {
            Map<String, Object> emailVariables = new HashMap<>();
            emailVariables.put("customerName", event.getUserEmail().split("@")[0]);
            emailVariables.put("userEmail", event.getUserEmail());
            emailVariables.put("eventType", event.getEventType());
            emailVariables.put("role", event.getRole());
            emailVariables.put("message", event.getMessage());
            emailVariables.put("timestamp", event.getTimestamp().toString());

            // Extract additional data from message based on event type
            switch (event.getEventType()) {
                case "ORDER_PLACED":
                    emailVariables.put("orderId", extractOrderIdFromMessage(event.getMessage()));
                    emailVariables.put("totalAmount", extractAmountFromMessage(event.getMessage()));
                    break;
                case "ORDER_STATUS_UPDATED":
                    emailVariables.put("orderId", extractOrderIdFromMessage(event.getMessage()));
                    emailVariables.put("status", extractStatusFromMessage(event.getMessage()));
                    break;
                case "USER_LOGIN":
                    emailVariables.put("message", "You have successfully logged into your ShopVerse account.");
                    break;
                case "USER_REGISTER":
                    emailVariables.put("message", "Welcome to ShopVerse! Your account has been created successfully.");
                    break;
                case "PASSWORD_RESET":
                    emailVariables.put("resetToken", event.getResetToken());
                    emailVariables.put("resetLink", generateResetLink(event.getResetToken()));
                    emailVariables.put("expiryTime", calculateExpiryTime());
                    emailService.sendPasswordResetEmail(event.getUserEmail(), emailVariables);
                    log.info("PASSWORD RESET EMAIL SENT to {}", event.getUserEmail());
                    return; // Skip the general notification email for password reset
            }

            emailService.sendGeneralNotificationEmail(event.getUserEmail(), emailVariables);
            log.info("EMAIL NOTIFICATION SENT to {} for event type {}", event.getUserEmail(), event.getEventType());
        } catch (Exception e) {
            log.error("FAILED TO SEND EMAIL NOTIFICATION to {}", event.getUserEmail(), e);
        }
    }

    private String extractOrderIdFromMessage(String message) {
        return "ORD-" + System.currentTimeMillis();
    }

    private String extractAmountFromMessage(String message) {
        if (message.contains("₹")) {
            String[] parts = message.split("₹");
            if (parts.length > 1) {
                return parts[1].trim();
            }
        }
        return "0.00";
    }

    private String extractStatusFromMessage(String message) {
        if (message.contains("to ")) {
            String[] parts = message.split("to ");
            if (parts.length > 1) {
                return parts[1].trim();
            }
        }
        return "UNKNOWN";
    }

    private String generateResetLink(String resetToken) {
        // In production, this should be your frontend URL
        return productionUrl +
                "/reset-password?token="
                + resetToken;
    }

    private String calculateExpiryTime() {
        return java.time.LocalDateTime.now().plusHours(1).toString();
    }
}
