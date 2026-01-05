package com.ecommerce.analytics.flink;

import com.ecommerce.analytics.dto.AnalyticsEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class ClickHouseSink extends RichSinkFunction<AnalyticsEvent> {

    private transient Connection connection;
    private transient PreparedStatement stmt;
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void open(Configuration parameters) throws Exception {
        connection = DriverManager.getConnection(
                "jdbc:clickhouse://localhost:8123/default"
        );

        stmt = connection.prepareStatement("""
            INSERT INTO analytics_events
            (event_type, service, user_email, entity_id, amount, timestamp, metadata)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """);
    }

    @Override
    public void invoke(AnalyticsEvent event, Context context) throws Exception {

        stmt.setString(1, event.getEventType());
        stmt.setString(2, event.getService());
        stmt.setString(3, event.getUserEmail());
        stmt.setString(4, event.getEntityId());
        stmt.setObject(5, event.getAmount());
        stmt.setObject(6, event.getTimestamp());
        stmt.setString(7,
                event.getMetadata() == null
                        ? "{}"
                        : mapper.writeValueAsString(event.getMetadata())
        );

        stmt.executeUpdate();
    }

    @Override
    public void close() throws Exception {
        stmt.close();
        connection.close();
    }
}
