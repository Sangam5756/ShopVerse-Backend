package com.ecommerce.auth.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import com.ecommerce.auth.dto.AnalyticsEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuthAnalyticsPublisher {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void userLoggedIn(String email) {
        AnalyticsEvent event = AnalyticsEvent.builder()
                .eventType("USER_LOGIN")
                .service("auth-service")
                .userEmail(email)
                .entityId(null)
                .amount(null)
                .timestamp(LocalDateTime.now())
                .metadata(createAuthMetadata("login", email))
                .build();

        publishAnalyticsEvent(event);
    }

    public void userRegistered(String email) {
        AnalyticsEvent event = AnalyticsEvent.builder()
                .eventType("USER_REGISTER")
                .service("auth-service")
                .userEmail(email)
                .entityId(null)
                .amount(null)
                .timestamp(LocalDateTime.now())
                .metadata(createAuthMetadata("register", email))
                .build();

        publishAnalyticsEvent(event);
    }

    private Map<String, Object> createAuthMetadata(String action, String email) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("action", action);
        metadata.put("email", email);
        metadata.put("category", "user_authentication");
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
