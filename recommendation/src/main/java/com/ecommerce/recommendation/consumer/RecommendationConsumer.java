package com.ecommerce.recommendation.consumer;

import com.ecommerce.recommendation.dto.UserEvent;
import com.ecommerce.recommendation.model.UserInteraction;
import com.ecommerce.recommendation.repository.UserInteractionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class RecommendationConsumer {

    private final UserInteractionRepository interactionRepo;
    private final ObjectMapper objectMapper;

    private static final Set<String> ALLOWED_EVENTS =
            Set.of("USER_LOGIN", "USER_REGISTER", "PRODUCT_CREATED", "PRODUCT_UPDATED", 
                   "ORDER_PLACED", "ORDER_STATUS_UPDATED", "PAYMENT_SUCCESS", "PAYMENT_FAILED");

    @KafkaListener(
            topics = "notification-topic",
            groupId = "recommendation-group"
    )
    public void consume(ConsumerRecord<String, String> record) {
        try {
            log.info("RAW EVENT RECEIVED: key={}, value={}", record.key(), record.value());

            // Parse the notification event
            Map<String, Object> notificationEvent = objectMapper.readValue(record.value(), Map.class);
            
            String eventType = (String) notificationEvent.get("eventType");
            String userEmail = (String) notificationEvent.get("userEmail");
            String message = (String) notificationEvent.get("message");
            
            log.info("PARSED EVENT: eventType={}, userEmail={}, message={}", eventType, userEmail, message);

            // Only process relevant events for recommendations
            if (!ALLOWED_EVENTS.contains(eventType)) {
                log.info("Event type {} not allowed for recommendations, skipping", eventType);
                return;
            }

            // Extract entity ID from message if available
            String entityId = extractEntityId(message, eventType);
            
            // Create user interaction
            UserInteraction interaction = UserInteraction.builder()
                    .userEmail(userEmail)
                    .productId(entityId)
                    .eventType(eventType)
                    .timestamp(LocalDateTime.now())
                    .build();

            // Save to MongoDB
            interactionRepo.save(interaction);
            
            log.info("SAVED USER INTERACTION: userEmail={}, eventType={}, productId={}", 
                    userEmail, eventType, entityId);

        } catch (Exception e) {
            log.error("RECOMMENDATION CONSUMER FAILED", e);
        }
    }

    private String extractEntityId(String message, String eventType) {
        if (message == null) return null;
        
        // Extract product ID from product-related messages
        if (eventType.startsWith("PRODUCT_") && message.contains(":")) {
            String[] parts = message.split(":");
            if (parts.length > 1) {
                return parts[1].trim();
            }
        }
        
        // Extract order ID from order/payment messages
        if ((eventType.startsWith("ORDER_") || eventType.startsWith("PAYMENT_")) && message.contains("Order #")) {
            String[] parts = message.split("Order #");
            if (parts.length > 1) {
                String orderId = parts[1].split(" ")[0];
                return orderId.replaceAll("[^0-9]", "");
            }
        }
        
        return null;
    }
}
