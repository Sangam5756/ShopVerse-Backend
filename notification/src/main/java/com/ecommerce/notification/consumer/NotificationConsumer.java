package com.ecommerce.notification.consumer;

import com.ecommerce.notification.dto.NotificationEvent;
import com.ecommerce.notification.model.Notification;
import com.ecommerce.notification.repository.NotificationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationRepository repository;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "notification-topic",
            groupId = "notification-group"
    )
    public void consume(byte[] payload) {

        try {
            NotificationEvent event =
                    objectMapper.readValue(payload, NotificationEvent.class);

            Notification notification = Notification.builder()
                    .userEmail(event.getUserEmail())
                    .eventType(event.getEventType())
                    .message(event.getMessage())
                    .timestamp(
                            event.getTimestamp()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDateTime()
                    )
                    .read(false)
                    .build();

            repository.save(notification);

        } catch (Exception e) {
            // 🔥 In production → send to DLQ
            e.printStackTrace();
        }
    }
}

