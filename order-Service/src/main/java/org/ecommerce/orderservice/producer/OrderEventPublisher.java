package org.ecommerce.orderservice.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.ecommerce.orderservice.dtos.NotificationEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class OrderEventPublisher {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final OrderAnalyticsPublisher analyticsPublisher;

    public void orderPlaced(String userEmail, double totalAmount, Long orderId) {

        NotificationEvent event = NotificationEvent.builder()
                .userEmail(userEmail)
                .eventType("ORDER_PLACED")
                .role("CUSTOMER")
                .message("Order placed successfully. Total: ₹" + totalAmount)
                .timestamp(Instant.now())
                .build();

        try {
            byte[] payload = objectMapper.writeValueAsBytes(event);
            kafkaTemplate.send("notification-topic", payload);
            
            // Also publish analytics event
            analyticsPublisher.orderPlaced(userEmail, totalAmount, orderId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish notification", e);
        }
    }

    public void orderStatusUpdated(String userEmail, String status, Long orderId) {

        NotificationEvent event = NotificationEvent.builder()
                .userEmail(userEmail)
                .eventType("ORDER_STATUS_UPDATED")
                .role("CUSTOMER")
                .message("Order status updated to " + status)
                .timestamp(Instant.now())
                .build();

        try {
            byte[] payload = objectMapper.writeValueAsBytes(event);
            kafkaTemplate.send("notification-topic", payload);
            
            // Also publish analytics event
            analyticsPublisher.orderStatusUpdated(userEmail, status, orderId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish notification", e);
        }
    }
}

