package org.ecommerce.paymentservice.producer;

import lombok.RequiredArgsConstructor;
import org.ecommerce.paymentservice.dtos.NotificationEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class PaymentEventPublisher {

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    public void paymentSuccess(String userEmail, Long orderId) {

        NotificationEvent event = NotificationEvent.builder()
                .userEmail(userEmail)
                .eventType("PAYMENT_SUCCESS")
                .message("Payment successful for Order #" + orderId).timestamp(Instant.now()).build();

        kafkaTemplate.send("notification-topic", userEmail, event);
    }

    public void paymentFailed(String userEmail, Long orderId) {

        NotificationEvent event = NotificationEvent.builder()
                .userEmail(userEmail)
                .eventType("PAYMENT_FAILED")
                .message("Payment failed for Order #" + orderId).timestamp(Instant.now()).build();

        kafkaTemplate.send("notification-topic", userEmail, event);
    }
}

