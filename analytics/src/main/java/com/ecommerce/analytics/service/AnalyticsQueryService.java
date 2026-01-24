package com.ecommerce.analytics.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsQueryService {

    private final JdbcTemplate jdbcTemplate;

    // Basic Metrics
    public long totalUsers() {
        return jdbcTemplate.queryForObject("""
            SELECT count(DISTINCT user_email)
            FROM analytics_events
            WHERE event_type = 'USER_REGISTER'
        """, Long.class);
    }

    public double totalRevenue() {
        Double result = jdbcTemplate.queryForObject("""
            SELECT sum(amount)
            FROM analytics_events
            WHERE event_type = 'PAYMENT_SUCCESS'
        """, Double.class);

        return result == null ? 0.0 : result;
    }

    public long ordersToday() {
        return jdbcTemplate.queryForObject("""
            SELECT count()
            FROM analytics_events
            WHERE event_type = 'ORDER_PLACED'
              AND toDate(timestamp) = today()
        """, Long.class);
    }

    // User Activity Analytics
    public long activeUsersToday() {
        return jdbcTemplate.queryForObject("""
            SELECT count(DISTINCT user_email)
            FROM analytics_events
            WHERE toDate(timestamp) = today()
        """, Long.class);
    }

    public long totalLogins() {
        return jdbcTemplate.queryForObject("""
            SELECT count()
            FROM analytics_events
            WHERE event_type = 'USER_LOGIN'
        """, Long.class);
    }

    public long loginsToday() {
        return jdbcTemplate.queryForObject("""
            SELECT count()
            FROM analytics_events
            WHERE event_type = 'USER_LOGIN'
              AND toDate(timestamp) = today()
        """, Long.class);
    }

    // Product Analytics
    public long totalProducts() {
        return jdbcTemplate.queryForObject("""
            SELECT count()
            FROM analytics_events
            WHERE event_type = 'PRODUCT_CREATED'
        """, Long.class);
    }

    public long productsCreatedToday() {
        return jdbcTemplate.queryForObject("""
            SELECT count()
            FROM analytics_events
            WHERE event_type = 'PRODUCT_CREATED'
              AND toDate(timestamp) = today()
        """, Long.class);
    }

    public List<Map<String, Object>> topProducts() {
        return jdbcTemplate.queryForList("""
            SELECT entity_id as productName, count() as mentions
            FROM analytics_events
            WHERE event_type IN ('PRODUCT_CREATED', 'PRODUCT_UPDATED', 'PRODUCT_DELETED')
              AND entity_id IS NOT NULL
            GROUP BY entity_id
            ORDER BY mentions DESC
            LIMIT 10
        """);
    }

    // Order Analytics
    public long totalOrders() {
        return jdbcTemplate.queryForObject("""
            SELECT count()
            FROM analytics_events
            WHERE event_type = 'ORDER_PLACED'
        """, Long.class);
    }

    public double averageOrderValue() {
        Double result = jdbcTemplate.queryForObject("""
            SELECT avg(amount)
            FROM analytics_events
            WHERE event_type = 'ORDER_PLACED'
              AND amount IS NOT NULL
        """, Double.class);

        return result == null ? 0.0 : result;
    }

    public List<Map<String, Object>> ordersByStatus() {
        return jdbcTemplate.queryForList("""
            SELECT 
                extract(metadata, 'message') as status,
                count() as count
            FROM analytics_events
            WHERE event_type = 'ORDER_STATUS_UPDATED'
            GROUP BY extract(metadata, 'message')
            ORDER BY count DESC
        """);
    }

    // Payment Analytics
    public long successfulPayments() {
        return jdbcTemplate.queryForObject("""
            SELECT count()
            FROM analytics_events
            WHERE event_type = 'PAYMENT_SUCCESS'
        """, Long.class);
    }

    public long failedPayments() {
        return jdbcTemplate.queryForObject("""
            SELECT count()
            FROM analytics_events
            WHERE event_type = 'PAYMENT_FAILED'
        """, Long.class);
    }

    public double paymentSuccessRate() {
        long successful = successfulPayments();
        long failed = failedPayments();
        long total = successful + failed;
        
        return total == 0 ? 0.0 : (double) successful / total * 100;
    }

    // Time-based Analytics
    public List<Map<String, Object>> hourlyActivity() {
        return jdbcTemplate.queryForList("""
            SELECT 
                toHour(timestamp) as hour,
                count() as events
            FROM analytics_events
            WHERE toDate(timestamp) = today()
            GROUP BY toHour(timestamp)
            ORDER BY hour
        """);
    }

    public List<Map<String, Object>> dailyActivity(int days) {
        return jdbcTemplate.queryForList("""
            SELECT 
                toDate(timestamp) as date,
                count() as events,
                count(DISTINCT user_email) as activeUsers
            FROM analytics_events
            WHERE timestamp >= now() - INTERVAL ? DAY
            GROUP BY toDate(timestamp)
            ORDER BY date
        """, days);
    }

    // Service Analytics
    public List<Map<String, Object>> eventsByService() {
        return jdbcTemplate.queryForList("""
            SELECT 
                service,
                count() as totalEvents,
                count(DISTINCT user_email) as uniqueUsers
            FROM analytics_events
            GROUP BY service
            ORDER BY totalEvents DESC
        """);
    }

    public List<Map<String, Object>> eventTypeDistribution() {
        return jdbcTemplate.queryForList("""
            SELECT 
                event_type,
                count() as count,
                count(DISTINCT user_email) as uniqueUsers
            FROM analytics_events
            GROUP BY event_type
            ORDER BY count DESC
        """);
    }

    // User Analytics
    public List<Map<String, Object>> topUsersByActivity() {
        return jdbcTemplate.queryForList("""
            SELECT 
                user_email,
                count() as totalEvents,
                count(DISTINCT event_type) as uniqueEventTypes
            FROM analytics_events
            WHERE user_email IS NOT NULL
            GROUP BY user_email
            ORDER BY totalEvents DESC
            LIMIT 10
        """);
    }

    public Map<String, Object> getUserActivity(String userEmail) {
        return jdbcTemplate.queryForMap("""
            SELECT 
                user_email,
                count() as totalEvents,
                min(timestamp) as firstActivity,
                max(timestamp) as lastActivity,
                count(DISTINCT event_type) as uniqueEventTypes,
                count(DISTINCT toDate(timestamp)) as activeDays
            FROM analytics_events
            WHERE user_email = ?
            GROUP BY user_email
        """, userEmail);
    }

    // Revenue Analytics
    public List<Map<String, Object>> revenueByDay(int days) {
        return jdbcTemplate.queryForList("""
            SELECT 
                toDate(timestamp) as date,
                sum(amount) as dailyRevenue,
                count() as transactions
            FROM analytics_events
            WHERE event_type = 'PAYMENT_SUCCESS'
              AND amount IS NOT NULL
              AND timestamp >= now() - INTERVAL ? DAY
            GROUP BY toDate(timestamp)
            ORDER BY date
        """, days);
    }

    public List<Map<String, Object>> revenueByMonth() {
        return jdbcTemplate.queryForList("""
            SELECT 
                toYearMonth(timestamp) as month,
                sum(amount) as monthlyRevenue,
                count() as transactions
            FROM analytics_events
            WHERE event_type = 'PAYMENT_SUCCESS'
              AND amount IS NOT NULL
            GROUP BY toYearMonth(timestamp)
            ORDER BY month
        """);
    }
}
