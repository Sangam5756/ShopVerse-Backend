package org.ecommerce.product.producer;

import lombok.RequiredArgsConstructor;
import org.ecommerce.product.dto.NotificationEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class ProductEventPublisher {

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    public void productCreated(String adminEmail, String productName) {

        NotificationEvent event = NotificationEvent.builder()
                .userEmail(adminEmail)
                .eventType("PRODUCT_CREATED")
                .message("New product added: " + productName)
                .timestamp(Instant.now())
                .build();

        kafkaTemplate.send("notification-topic", adminEmail, event);
    }

    public void productUpdated(String adminEmail, String productName) {

        NotificationEvent event = NotificationEvent.builder()
                .userEmail(adminEmail)
                .eventType("PRODUCT_UPDATED")
                .message("Product updated: " + productName)
                .timestamp(Instant.now())
                .build();

        kafkaTemplate.send("notification-topic", adminEmail, event);
    }

    public void productDeleted(String adminEmail, String productName) {

        NotificationEvent event = NotificationEvent.builder()
                .userEmail(adminEmail)
                .eventType("PRODUCT_DELETED")
                .message("Product deleted: " + productName)
                .timestamp(Instant.now())
                .build();

        kafkaTemplate.send("notification-topic", adminEmail, event);
    }

    public void bulkProductCreated(String adminEmail, int count) {

        NotificationEvent event = NotificationEvent.builder()
                .userEmail(adminEmail)
                .eventType("PRODUCT_BULK_CREATED")
                .message(count + " products added successfully")
                .timestamp(Instant.now())
                .build();

        kafkaTemplate.send("notification-topic", adminEmail, event);
    }
}
