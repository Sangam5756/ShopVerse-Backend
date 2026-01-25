package org.ecommerce.orderservice.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.ecommerce.orderservice.dtos.AnalyticsEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OrderAnalyticsPublisher {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void orderPlaced(String userEmail, double totalAmount, Long orderId) {
        AnalyticsEvent event = AnalyticsEvent.builder()
                .eventType("ORDER_PLACED")
                .service("order-service")
                .userEmail(userEmail)
                .entityId(String.valueOf(orderId))
                .amount(totalAmount)
                .timestamp(LocalDateTime.now())
                .metadata(createOrderMetadata(orderId, totalAmount, "placed"))
                .build();

        publishAnalyticsEvent(event);
    }

    public void orderStatusUpdated(String userEmail, String status, Long orderId) {
        AnalyticsEvent event = AnalyticsEvent.builder()
                .eventType("ORDER_STATUS_UPDATED")
                .service("order-service")
                .userEmail(userEmail)
                .entityId(String.valueOf(orderId))
                .amount(null)
                .timestamp(LocalDateTime.now())
                .metadata(createOrderStatusMetadata(orderId, status))
                .build();

        publishAnalyticsEvent(event);
    }

    private Map<String, Object> createOrderMetadata(Long orderId, double totalAmount, String action) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("orderId", orderId);
        metadata.put("totalAmount", totalAmount);
        metadata.put("action", action);
        metadata.put("category", "order_management");
        metadata.put("currency", "INR");
        return metadata;
    }

    private Map<String, Object> createOrderStatusMetadata(Long orderId, String status) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("orderId", orderId);
        metadata.put("status", status);
        metadata.put("action", "status_updated");
        metadata.put("category", "order_management");
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
