package com.ecommerce.auth.producer;

import com.ecommerce.auth.dto.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AuthEventPublisher {

    private final NotificationProducer notificationProducer;
    private final AuthAnalyticsPublisher analyticsPublisher;

    public void userLoggedIn(String email) {

        NotificationEvent event = NotificationEvent.builder()
                .eventType("USER_LOGIN")
                .userEmail(email)
                .role("CUSTOMER")
                .timestamp(LocalDateTime.now())
                .build();

        notificationProducer.sendNotification(event);
        
        // Also publish analytics event
        analyticsPublisher.userLoggedIn(email);
    }

    public void userRegistered(String email) {

        NotificationEvent event = NotificationEvent.builder()
                .eventType("USER_REGISTER")
                .userEmail(email)
                .role("CUSTOMER")
                .message("Account created successfully")
                .timestamp(LocalDateTime.now())
                .build();

        notificationProducer.sendNotification(event);
        
        // Also publish analytics event
        analyticsPublisher.userRegistered(email);
    }

    public void passwordResetRequested(String email, String resetToken) {

        NotificationEvent event = NotificationEvent.builder()
                .eventType("PASSWORD_RESET")
                .userEmail(email)
                .role("CUSTOMER")
                .message("Password reset requested")
                .timestamp(LocalDateTime.now())
                .resetToken(resetToken)
                .build();

        notificationProducer.sendNotification(event);
    }
}
