package com.ecommerce.notification.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public TestController(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/send-notification")
    public String sendTestNotification(@RequestParam(defaultValue = "test@example.com") String email) {
        try {
            Map<String, Object> event = Map.of(
                "userEmail", email,
                "eventType", "TEST_EVENT",
                "message", "This is a test notification sent at " + Instant.now(),
                "timestamp", Instant.now().toString()
            );

            String jsonEvent = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("notification-topic", email, jsonEvent);
            
            return "✅ Test notification sent to Kafka topic 'notification-topic'";
        } catch (Exception e) {
            return "❌ Error sending test notification: " + e.getMessage();
        }
    }
}
