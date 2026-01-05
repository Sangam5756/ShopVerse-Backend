package com.ecommerce.notification.consumer;

import com.ecommerce.notification.dto.NotificationEvent;
import com.ecommerce.notification.model.Notification;
import com.ecommerce.notification.repository.NotificationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationRepository repository;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = {
                    "user-events",
                    "order-events",
                    "payment-events",
                    "product-events"
            },
            groupId = "notification-group"
    )
    public void consume(byte[] payload) {

        try {
            NotificationEvent event =
                    objectMapper.readValue(payload, NotificationEvent.class);

            Notification notification = Notification.builder()
                    .userEmail(event.getUserEmail())
                    .role(event.getRole())
                    .eventType(event.getEventType())
                    .message(event.getMessage())
                    .timestamp(event.getTimestamp())
                    .read(false)
                    .build();

            repository.save(notification);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
