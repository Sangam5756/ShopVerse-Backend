package com.ecommerce.analytics.consumer;

import com.ecommerce.analytics.dto.AnalyticsEvent;
import com.ecommerce.analytics.service.AnalyticsEventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AnalyticsEventConsumer {

    private final AnalyticsEventService analyticsEventService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "analytics-topic", groupId = "analytics-group")
    public void consumeAnalyticsEvent(ConsumerRecord<String, String> record) {
        try {
            log.info("🔥 ANALYTICS EVENT RECEIVED: topic={}, partition={}, offset={}, key={}", 
                    record.topic(), record.partition(), record.offset(), record.key());
            log.info("📦 RAW ANALYTICS EVENT VALUE: {}", record.value());
            
            if (record.value() == null || record.value().trim().isEmpty()) {
                log.warn("⚠️ Received empty analytics event");
                return;
            }
            
            // Parse the analytics event directly (no conversion needed)
            AnalyticsEvent analyticsEvent = objectMapper.readValue(record.value(), AnalyticsEvent.class);
            log.info("🗺️ PARSED ANALYTICS EVENT: {}", analyticsEvent);
            
            // Save to ClickHouse
            analyticsEventService.saveAnalyticsEvent(analyticsEvent);
            
            log.info("✅ ANALYTICS EVENT SAVED: eventType={}, service={}, userEmail={}", 
                    analyticsEvent.getEventType(), 
                    analyticsEvent.getService(), 
                    analyticsEvent.getUserEmail());
                    
        } catch (Exception e) {
            log.error("❌ Failed to process analytics event: {}", e.getMessage());
            log.error("❌ Raw record: key={}, value={}", record.key(), record.value());
            e.printStackTrace();
        }
    }

    // Keep the notification consumer for backward compatibility
    @KafkaListener(topics = "notification-topic", groupId = "analytics-group")
    public void consumeNotificationEvent(ConsumerRecord<String, String> record) {
        try {
            log.info("🔥 NOTIFICATION EVENT RECEIVED: topic={}, partition={}, offset={}, key={}", 
                    record.topic(), record.partition(), record.offset(), record.key());
            log.info("📦 RAW NOTIFICATION EVENT VALUE: {}", record.value());
            
            if (record.value() == null || record.value().trim().isEmpty()) {
                log.warn("⚠️ Received empty notification event");
                return;
            }
            
            // Parse the notification event
            Map<String, Object> notificationEvent = objectMapper.readValue(record.value(), Map.class);
            log.info("🗺️ PARSED NOTIFICATION EVENT: {}", notificationEvent);
            
            // Convert to AnalyticsEvent
            AnalyticsEvent analyticsEvent = convertToAnalyticsEvent(notificationEvent);
            log.info("🔄 CONVERTED TO ANALYTICS EVENT: {}", analyticsEvent);
            
            // Save to ClickHouse
            analyticsEventService.saveAnalyticsEvent(analyticsEvent);
            
            log.info("✅ NOTIFICATION EVENT CONVERTED AND SAVED: eventType={}, service={}, userEmail={}", 
                    analyticsEvent.getEventType(), 
                    analyticsEvent.getService(), 
                    analyticsEvent.getUserEmail());
                    
        } catch (Exception e) {
            log.error("❌ Failed to process notification event: {}", e.getMessage());
            log.error("❌ Raw record: key={}, value={}", record.key(), record.value());
            e.printStackTrace();
        }
    }

    private AnalyticsEvent convertToAnalyticsEvent(Map<String, Object> notificationEvent) {
        String eventType = (String) notificationEvent.get("eventType");
        String userEmail = (String) notificationEvent.get("userEmail");
        String message = (String) notificationEvent.get("message");
        String role = (String) notificationEvent.get("role");
        
        // Determine service based on event type
        String service = determineService(eventType);
        
        // Extract entity ID and amount from message
        String entityId = extractEntityId(message);
        Double amount = extractAmount(message);
        
        // Create metadata
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("message", message);
        metadata.put("role", role);
        if (notificationEvent.containsKey("timestamp")) {
            metadata.put("originalTimestamp", notificationEvent.get("timestamp"));
        }
        
        // Convert timestamp
        LocalDateTime timestamp = LocalDateTime.now();
        if (notificationEvent.containsKey("timestamp")) {
            Object ts = notificationEvent.get("timestamp");
            if (ts instanceof String) {
                try {
                    timestamp = LocalDateTime.parse(ts.toString());
                } catch (Exception e) {
                    // If parsing fails, use current time
                    timestamp = LocalDateTime.now();
                }
            } else if (ts instanceof Instant) {
                timestamp = LocalDateTime.ofInstant((Instant) ts, ZoneId.systemDefault());
            }
        }
        
        return AnalyticsEvent.builder()
                .eventType(eventType)
                .service(service)
                .userEmail(userEmail)
                .entityId(entityId)
                .amount(amount)
                .timestamp(timestamp)
                .metadata(metadata)
                .build();
    }

    private String determineService(String eventType) {
        return switch (eventType) {
            case "USER_LOGIN", "USER_REGISTER" -> "auth-service";
            case "PRODUCT_CREATED", "PRODUCT_UPDATED", "PRODUCT_DELETED", "PRODUCT_BULK_CREATED" -> "product-service";
            case "ORDER_PLACED", "ORDER_STATUS_UPDATED" -> "order-service";
            case "PAYMENT_SUCCESS", "PAYMENT_FAILED" -> "payment-service";
            default -> "unknown-service";
        };
    }

    private String extractEntityId(String message) {
        if (message == null) return null;
        
        // Extract order ID from payment/order messages
        if (message.contains("Order #")) {
            String[] parts = message.split("Order #");
            if (parts.length > 1) {
                String orderId = parts[1].split(" ")[0];
                return orderId.replaceAll("[^0-9]", "");
            }
        }
        
        // Extract product name from product messages
        if (message.toLowerCase().contains("product")) {
            if (message.contains(":")) {
                String[] parts = message.split(":");
                if (parts.length > 1) {
                    return parts[1].trim();
                }
            }
        }
        
        return null;
    }

    private Double extractAmount(String message) {
        if (message == null) return null;
        
        // Extract amount from order/payment messages
        if (message.contains("₹") || message.toLowerCase().contains("total")) {
            String[] parts = message.split("₹");
            if (parts.length > 1) {
                String amountStr = parts[1].split(" ")[0];
                try {
                    return Double.parseDouble(amountStr.replaceAll("[^0-9.]", ""));
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }
        
        return null;
    }
}
