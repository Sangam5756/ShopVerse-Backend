package com.ecommerce.recommendation.consumer;

import com.ecommerce.recommendation.dto.UserEvent;
import com.ecommerce.recommendation.model.UserInteraction;
import com.ecommerce.recommendation.repository.UserInteractionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class RecommendationConsumer {

    private final UserInteractionRepository interactionRepo;
    private final ObjectMapper objectMapper;

    private static final Set<String> ALLOWED_EVENTS =
            Set.of("VIEW_PRODUCT", "ADD_TO_CART", "ORDER_PLACED");

    @KafkaListener(
            topics = {
                    "user-events",
                    "order-events",
                    "product-events"
            },
            groupId = "recommendation-group"
    )
    public void consume(byte[] payload) {

        try {
            UserEvent event =
                    objectMapper.readValue(payload, UserEvent.class);

            if (!ALLOWED_EVENTS.contains(event.getEventType())) {
                return;
            }

            interactionRepo.save(
                    UserInteraction.builder()
                            .userEmail(event.getUserEmail())
                            .productId(event.getEntityId())
                            .eventType(event.getEventType())
                            .timestamp(event.getTimestamp())
                            .build()
            );

        } catch (Exception e) {
            log.error("Recommendation consume failed", e);
        }
    }
}
