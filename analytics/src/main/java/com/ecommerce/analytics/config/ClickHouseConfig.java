package com.ecommerce.analytics.config;

import com.ecommerce.analytics.service.AnalyticsEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClickHouseConfig {

    private final AnalyticsEventService analyticsEventService;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeClickHouseTable() {
        log.info("Initializing ClickHouse analytics table...");
        analyticsEventService.createTableIfNotExists();
        log.info("ClickHouse initialization completed");
    }
}
