package org.ecommerce.product.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.ecommerce.product.dto.AnalyticsEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ProductAnalyticsPublisher {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void productCreated(String adminEmail, String productName, Long productId) {
        AnalyticsEvent event = AnalyticsEvent.builder()
                .eventType("PRODUCT_CREATED")
                .service("product-service")
                .userEmail(adminEmail)
                .entityId(String.valueOf(productId))
                .amount(null)
                .timestamp(LocalDateTime.now())
                .metadata(createProductMetadata(productName, productId, "created"))
                .build();

        publishAnalyticsEvent(event);
    }

    public void productUpdated(String adminEmail, String productName, Long productId) {
        AnalyticsEvent event = AnalyticsEvent.builder()
                .eventType("PRODUCT_UPDATED")
                .service("product-service")
                .userEmail(adminEmail)
                .entityId(String.valueOf(productId))
                .amount(null)
                .timestamp(LocalDateTime.now())
                .metadata(createProductMetadata(productName, productId, "updated"))
                .build();

        publishAnalyticsEvent(event);
    }

    public void productDeleted(String adminEmail, String productName, Long productId) {
        AnalyticsEvent event = AnalyticsEvent.builder()
                .eventType("PRODUCT_DELETED")
                .service("product-service")
                .userEmail(adminEmail)
                .entityId(String.valueOf(productId))
                .amount(null)
                .timestamp(LocalDateTime.now())
                .metadata(createProductMetadata(productName, productId, "deleted"))
                .build();

        publishAnalyticsEvent(event);
    }

    public void bulkProductCreated(String adminEmail, int count) {
        AnalyticsEvent event = AnalyticsEvent.builder()
                .eventType("PRODUCT_BULK_CREATED")
                .service("product-service")
                .userEmail(adminEmail)
                .entityId(null)
                .amount(null)
                .timestamp(LocalDateTime.now())
                .metadata(createBulkProductMetadata(count))
                .build();

        publishAnalyticsEvent(event);
    }

    private Map<String, Object> createProductMetadata(String productName, Long productId, String action) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("productName", productName);
        metadata.put("productId", productId);
        metadata.put("action", action);
        metadata.put("category", "product_management");
        return metadata;
    }

    private Map<String, Object> createBulkProductMetadata(int count) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("productCount", count);
        metadata.put("action", "bulk_created");
        metadata.put("category", "product_management");
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
