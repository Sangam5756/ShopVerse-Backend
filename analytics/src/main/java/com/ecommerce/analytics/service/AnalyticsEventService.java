package com.ecommerce.analytics.service;

import com.ecommerce.analytics.dto.AnalyticsEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsEventService {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public void saveAnalyticsEvent(AnalyticsEvent event) {
        try {
            String sql = """
                INSERT INTO analytics_events (
                    event_type, service, user_email, entity_id, amount, 
                    timestamp, metadata
                ) VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
            
            String metadataJson = objectMapper.writeValueAsString(event.getMetadata());
            
            jdbcTemplate.update(sql,
                    event.getEventType(),
                    event.getService(),
                    event.getUserEmail(),
                    event.getEntityId(),
                    event.getAmount(),
                    event.getTimestamp(),
                    metadataJson
            );
            
            log.debug("Analytics event saved to ClickHouse: {}", event.getEventType());
            
        } catch (Exception e) {
            log.error("Failed to save analytics event to ClickHouse", e);
            throw new RuntimeException("Failed to save analytics event", e);
        }
    }

    public void createTableIfNotExists() {
        try {
            String sql = """
                CREATE TABLE IF NOT EXISTS analytics_events (
                    event_type String,
                    service String,
                    user_email String,
                    entity_id String,
                    amount Nullable(Float64),
                    timestamp DateTime,
                    metadata String,
                    created_at DateTime DEFAULT now()
                ) ENGINE = MergeTree()
                ORDER BY (timestamp, event_type, service)
                """;
            
            jdbcTemplate.execute(sql);
            log.info("Analytics events table created/verified successfully");
            
        } catch (Exception e) {
            log.error("Failed to create analytics_events table", e);
        }
    }
}
