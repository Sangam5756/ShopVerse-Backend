package com.ecommerce.analytics.flink;

import com.ecommerce.analytics.dto.AnalyticsEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class AnalyticsFlinkJob {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env =
                StreamExecutionEnvironment.getExecutionEnvironment();

        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers("localhost:9092")
                .setTopics("analytics-events")
                .setGroupId("flink-analytics")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        ObjectMapper mapper = new ObjectMapper();

        env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source")
                .map(json -> mapper.readValue(json, AnalyticsEvent.class))
                .addSink(new ClickHouseSink());

        env.execute("Analytics Flink Pipeline");
    }
}
