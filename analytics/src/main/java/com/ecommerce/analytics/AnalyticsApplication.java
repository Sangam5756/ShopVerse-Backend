package com.ecommerce.analytics;

import com.ecommerce.analytics.service.AnalyticsEventService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.stereotype.Component;

@SpringBootApplication
@EnableDiscoveryClient
@EnableKafka
public class AnalyticsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnalyticsApplication.class, args);
    }

    @Component
    public static class DatabaseInitializer {
        private final AnalyticsEventService analyticsEventService;

        public DatabaseInitializer(AnalyticsEventService analyticsEventService) {
            this.analyticsEventService = analyticsEventService;
        }

        @EventListener(ApplicationReadyEvent.class)
        public void initializeDatabase() {
            analyticsEventService.createTableIfNotExists();
        }
    }
}
