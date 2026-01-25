package org.ecommerce.paymentservice.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.ecommerce.paymentservice.dtos.NotificationEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class PaymentEventPublisher {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final PaymentAnalyticsPublisher analyticsPublisher;

    public void paymentSuccess(String userEmail, Long orderId) {

        NotificationEvent event = NotificationEvent.builder()
                .userEmail(userEmail)
                .eventType("PAYMENT_SUCCESS")
                .role("CUSTOMER")
                .message("Payment successful for Order #" + orderId)
                .timestamp(Instant.now())
                .build();

        try {
            byte[] payload = objectMapper.writeValueAsBytes(event);
            kafkaTemplate.send("notification-topic", payload);
            
            // Also publish analytics event (amount would need to be passed or retrieved)
            analyticsPublisher.paymentSuccess(userEmail, orderId, null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish notification", e);
        }
    }

    public void paymentFailed(String userEmail, Long orderId) {

        NotificationEvent event = NotificationEvent.builder()
                .userEmail(userEmail)
                .eventType("PAYMENT_FAILED")
                .role("CUSTOMER")
                .message("Payment failed for Order #" + orderId)
                .timestamp(Instant.now())
                .build();

        try {
            byte[] payload = objectMapper.writeValueAsBytes(event);
            kafkaTemplate.send("notification-topic", payload);
            
            // Also publish analytics event (amount would need to be passed or retrieved)
            analyticsPublisher.paymentFailed(userEmail, orderId, null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish notification", e);
        }
    }
}

