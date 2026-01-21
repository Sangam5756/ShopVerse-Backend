package org.ecommerce.orderservice.producer;

import lombok.RequiredArgsConstructor;
import org.ecommerce.orderservice.dtos.NotificationEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class OrderEventPublisher {

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    public void orderPlaced(String userEmail, double totalAmount) {

        NotificationEvent event = NotificationEvent.builder()
                .userEmail(userEmail)
                .eventType("ORDER_PLACED")
                .message("Order placed successfully. Total: ₹" + totalAmount)
                .timestamp(Instant.now())
                .build();

        kafkaTemplate.send("notification-topic", userEmail, event);
    }

    public void orderStatusUpdated(String userEmail, String status) {

        NotificationEvent event = NotificationEvent.builder()
                .userEmail(userEmail)
                .eventType("ORDER_STATUS_UPDATED")
                .message("Order status updated to " + status)
                .timestamp(Instant.now())
                .build();

        kafkaTemplate.send("notification-topic", userEmail, event);
    }
}
