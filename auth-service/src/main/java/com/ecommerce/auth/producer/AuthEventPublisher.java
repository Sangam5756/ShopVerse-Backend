package com.ecommerce.auth.producer;

import com.ecommerce.auth.dto.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class AuthEventPublisher {

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    public void userLoggedIn(String email) {
        NotificationEvent event = NotificationEvent.builder()
                .userEmail(email)
                .eventType("USER_LOGIN")
                .message("User logged in successfully")
                .timestamp(Instant.now())
                .build();

        kafkaTemplate.send("notification-topic", email, event);
    }

    public void userRegistered(String email) {
        NotificationEvent event = NotificationEvent.builder()
                .userEmail(email)
                .eventType("USER_REGISTER")
                .message("Account created successfully")
                .timestamp(Instant.now())
                .build();

        kafkaTemplate.send("notification-topic", email, event);
    }
}

