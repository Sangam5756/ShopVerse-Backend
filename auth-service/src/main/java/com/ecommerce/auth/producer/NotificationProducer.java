package com.ecommerce.auth.producer;

import com.ecommerce.auth.dto.NotificationEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationProducer {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String TOPIC = "notification-topic";

    public void sendNotification(NotificationEvent event) {
        try {
            byte[] payload = objectMapper.writeValueAsBytes(event);
            kafkaTemplate.send(TOPIC, payload);
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish notification", e);
        }
    }
}