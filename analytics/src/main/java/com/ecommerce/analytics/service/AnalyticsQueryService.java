package com.ecommerce.analytics.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalyticsQueryService {

    private final JdbcTemplate jdbcTemplate;

    public long totalUsers() {
        return jdbcTemplate.queryForObject("""
            SELECT count()
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
}
