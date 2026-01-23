package com.ecommerce.notification.consumer;

import com.ecommerce.notification.model.Notification;
import com.ecommerce.notification.repository.NotificationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationRepository repository;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "notification-topic",
            groupId = "notification-group"
    )
    public void consume(String jsonEvent) {
        System.out.println("🔥 Received notification event from Kafka topic 'notification-topic'");
        System.out.println("📦 Raw JSON event: " + jsonEvent);
        
        try {
            // Parse JSON string to Map to extract fields dynamically
            Map<String, Object> eventMap = objectMapper.readValue(jsonEvent, Map.class);
            System.out.println("🗺️ Parsed JSON to Map: " + eventMap);
            
            String userEmail = (String) eventMap.get("userEmail");
            String eventType = (String) eventMap.get("eventType");
            String message = (String) eventMap.get("message");
            Instant timestamp = eventMap.get("timestamp") != null ? 
                Instant.parse(eventMap.get("timestamp").toString()) : Instant.now();
            
            System.out.println("📧 User Email: " + userEmail);
            System.out.println("📝 Event Type: " + eventType);
            System.out.println("💬 Message: " + message);
            System.out.println("⏰ Timestamp: " + timestamp);

            if (userEmail == null || eventType == null || message == null) {
                System.err.println("❌ Missing required fields - userEmail: " + userEmail + ", eventType: " + eventType + ", message: " + message);
                return;
            }

            Notification notification = Notification.builder()
                    .userEmail(userEmail)
                    .eventType(eventType)
                    .message(message)
                    .timestamp(timestamp.atZone(ZoneId.systemDefault()).toLocalDateTime())
                    .read(false)
                    .build();
            
            System.out.println("💾 Saving notification to MongoDB: " + notification);
            Notification savedNotification = repository.save(notification);
            System.out.println("✅ Successfully saved notification with ID: " + savedNotification.getId());

        } catch (Exception e) {
            System.err.println("❌ Error processing notification event: " + e.getMessage());
            System.err.println("❌ Raw JSON: " + jsonEvent);
            e.printStackTrace();
            // 🔥 In production → send to DLQ
        }
    }
}


