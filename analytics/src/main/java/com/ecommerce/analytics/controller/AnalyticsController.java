package com.ecommerce.analytics.controller;

import com.ecommerce.analytics.service.AnalyticsQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsQueryService service;

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        return Map.of(
                "totalUsers", service.totalUsers(),
                "totalRevenue", service.totalRevenue(),
                "ordersToday", service.ordersToday()
        );
    }
}
