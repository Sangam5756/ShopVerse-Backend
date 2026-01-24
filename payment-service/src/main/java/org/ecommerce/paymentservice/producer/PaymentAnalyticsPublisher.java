package org.ecommerce.paymentservice.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.ecommerce.paymentservice.dtos.AnalyticsEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PaymentAnalyticsPublisher {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void paymentSuccess(String userEmail, Long orderId, Double amount) {
        AnalyticsEvent event = AnalyticsEvent.builder()
                .eventType("PAYMENT_SUCCESS")
                .service("payment-service")
                .userEmail(userEmail)
                .entityId(String.valueOf(orderId))
                .amount(amount)
                .timestamp(LocalDateTime.now())
                .metadata(createPaymentMetadata(orderId, amount, "success"))
                .build();

        publishAnalyticsEvent(event);
    }

    public void paymentFailed(String userEmail, Long orderId, Double amount) {
        AnalyticsEvent event = AnalyticsEvent.builder()
                .eventType("PAYMENT_FAILED")
                .service("payment-service")
                .userEmail(userEmail)
                .entityId(String.valueOf(orderId))
                .amount(amount)
                .timestamp(LocalDateTime.now())
                .metadata(createPaymentMetadata(orderId, amount, "failed"))
                .build();

        publishAnalyticsEvent(event);
    }

    private Map<String, Object> createPaymentMetadata(Long orderId, Double amount, String status) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("orderId", orderId);
        metadata.put("amount", amount);
        metadata.put("status", status);
        metadata.put("category", "payment_processing");
        metadata.put("currency", "INR");
        return metadata;
    }

    private void publishAnalyticsEvent(AnalyticsEvent event) {
        try {
            byte[] payload = objectMapper.writeValueAsBytes(event);
            kafkaTemplate.send("analytics-topic", payload);
        } catch (Exception e) {
            // Log error but don't throw to avoid disrupting main business flow
            System.err.println("Failed to publish analytics event: " + e.getMessage());
        }
    }
}
