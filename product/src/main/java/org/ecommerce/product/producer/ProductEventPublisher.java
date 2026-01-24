package org.ecommerce.product.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.ecommerce.product.dto.NotificationEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class ProductEventPublisher {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final ProductAnalyticsPublisher analyticsPublisher;

    public void productCreated(String adminEmail, String productName) {

        NotificationEvent event = NotificationEvent.builder()
                .userEmail(adminEmail)
                .eventType("PRODUCT_CREATED")
                .role("ADMIN")
                .message("New product added: " + productName)
                .timestamp(Instant.now())
                .build();

        try {
            byte[] payload = objectMapper.writeValueAsBytes(event);
            kafkaTemplate.send("notification-topic", payload);
            
            // Also publish analytics event
            analyticsPublisher.productCreated(adminEmail, productName, null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish notification", e);
        }
    }

    public void productUpdated(String adminEmail, String productName) {

        NotificationEvent event = NotificationEvent.builder()
                .userEmail(adminEmail)
                .eventType("PRODUCT_UPDATED")
                .role("ADMIN")
                .message("Product updated: " + productName)
                .timestamp(Instant.now())
                .build();

        try {
            byte[] payload = objectMapper.writeValueAsBytes(event);
            kafkaTemplate.send("notification-topic", payload);
            
            // Also publish analytics event
            analyticsPublisher.productUpdated(adminEmail, productName, null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish notification", e);
        }
    }

    public void productDeleted(String adminEmail, String productName) {

        NotificationEvent event = NotificationEvent.builder()
                .userEmail(adminEmail)
                .eventType("PRODUCT_DELETED")
                .role("ADMIN")
                .message("Product deleted: " + productName)
                .timestamp(Instant.now())
                .build();

        try {
            byte[] payload = objectMapper.writeValueAsBytes(event);
            kafkaTemplate.send("notification-topic", payload);
            
            // Also publish analytics event
            analyticsPublisher.productDeleted(adminEmail, productName, null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish notification", e);
        }
    }

    public void bulkProductCreated(String adminEmail, int count) {

        NotificationEvent event = NotificationEvent.builder()
                .userEmail(adminEmail)
                .eventType("PRODUCT_BULK_CREATED")
                .role("ADMIN")
                .message(count + " products added successfully")
                .timestamp(Instant.now())
                .build();

        try {
            byte[] payload = objectMapper.writeValueAsBytes(event);
            kafkaTemplate.send("notification-topic", payload);
            
            // Also publish analytics event
            analyticsPublisher.bulkProductCreated(adminEmail, count);
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish notification", e);
        }
    }
}
