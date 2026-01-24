package com.ecommerce.notification.consumer;

import com.ecommerce.notification.event.NotificationEvent;
import com.ecommerce.notification.model.Notification;
import com.ecommerce.notification.repository.NotificationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationRepository repository;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "notification-topic",
            groupId = "notification-group"
    )
    public void consume(ConsumerRecord<String, String> record) {

        log.info("RAW EVENT RECEIVED: key={}, value={}", record.key(), record.value());

        try {
            NotificationEvent event =
                    objectMapper.readValue(record.value(), NotificationEvent.class);

            log.info("DESERIALIZED EVENT: {}", event);

            Notification notification = Notification.builder()
                    .eventType(event.getEventType())
                    .userEmail(event.getUserEmail())
                    .role(event.getRole())
                    .message(event.getMessage())
                    .timestamp(event.getTimestamp())
                    .read(false)
                    .build();

            repository.save(notification);

            log.info("SAVED TO MONGO for {}", event.getUserEmail());

        } catch (Exception e) {
            log.error("CONSUMER FAILED", e);
        }
    }

}
