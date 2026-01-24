package com.ecommerce.analytics.controller;

import com.ecommerce.analytics.service.AnalyticsQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsQueryService service;

    // Dashboard Overview
    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        return Map.of(
                "totalUsers", service.totalUsers(),
                "totalRevenue", service.totalRevenue(),
                "ordersToday", service.ordersToday(),
                "activeUsersToday", service.activeUsersToday(),
                "totalOrders", service.totalOrders(),
                "totalProducts", service.totalProducts(),
                "paymentSuccessRate", service.paymentSuccessRate()
        );
    }

    // User Analytics
    @GetMapping("/users/total")
    public Map<String, Object> userStats() {
        return Map.of(
                "totalUsers", service.totalUsers(),
                "activeUsersToday", service.activeUsersToday(),
                "totalLogins", service.totalLogins(),
                "loginsToday", service.loginsToday()
        );
    }

    @GetMapping("/users/activity")
    public List<Map<String, Object>> topUsersByActivity() {
        return service.topUsersByActivity();
    }

    @GetMapping("/users/{email}/activity")
    public Map<String, Object> getUserActivity(@PathVariable String email) {
        return service.getUserActivity(email);
    }

    // Product Analytics
    @GetMapping("/products/stats")
    public Map<String, Object> productStats() {
        return Map.of(
                "totalProducts", service.totalProducts(),
                "productsCreatedToday", service.productsCreatedToday()
        );
    }

    @GetMapping("/products/top")
    public List<Map<String, Object>> topProducts() {
        return service.topProducts();
    }

    // Order Analytics
    @GetMapping("/orders/stats")
    public Map<String, Object> orderStats() {
        return Map.of(
                "totalOrders", service.totalOrders(),
                "ordersToday", service.ordersToday(),
                "averageOrderValue", service.averageOrderValue()
        );
    }

    @GetMapping("/orders/status")
    public List<Map<String, Object>> ordersByStatus() {
        return service.ordersByStatus();
    }

    // Payment Analytics
    @GetMapping("/payments/stats")
    public Map<String, Object> paymentStats() {
        return Map.of(
                "totalRevenue", service.totalRevenue(),
                "successfulPayments", service.successfulPayments(),
                "failedPayments", service.failedPayments(),
                "paymentSuccessRate", service.paymentSuccessRate()
        );
    }

    // Revenue Analytics
    @GetMapping("/revenue/daily/{days}")
    public List<Map<String, Object>> revenueByDay(@PathVariable int days) {
        return service.revenueByDay(days);
    }

    @GetMapping("/revenue/monthly")
    public List<Map<String, Object>> revenueByMonth() {
        return service.revenueByMonth();
    }

    // Time-based Analytics
    @GetMapping("/activity/hourly")
    public List<Map<String, Object>> hourlyActivity() {
        return service.hourlyActivity();
    }

    @GetMapping("/activity/daily/{days}")
    public List<Map<String, Object>> dailyActivity(@PathVariable int days) {
        return service.dailyActivity(days);
    }

    // Service Analytics
    @GetMapping("/services/events")
    public List<Map<String, Object>> eventsByService() {
        return service.eventsByService();
    }

    @GetMapping("/events/types")
    public List<Map<String, Object>> eventTypeDistribution() {
        return service.eventTypeDistribution();
    }

    // Custom Date Range Analytics
    @GetMapping("/activity/range")
    public Map<String, Object> activityByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        int days = (int) java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        return Map.of(
                "dateRange", Map.of("start", startDate, "end", endDate),
                "dailyActivity", service.dailyActivity(days),
                "revenueByDay", service.revenueByDay(days)
        );
    }

    // Real-time Stats (for dashboards)
    @GetMapping("/realtime")
    public Map<String, Object> realTimeStats() {
        return Map.of(
                "activeUsersToday", service.activeUsersToday(),
                "ordersToday", service.ordersToday(),
                "loginsToday", service.loginsToday(),
                "productsCreatedToday", service.productsCreatedToday(),
                "totalRevenue", service.totalRevenue(),
                "paymentSuccessRate", service.paymentSuccessRate()
        );
    }
}
