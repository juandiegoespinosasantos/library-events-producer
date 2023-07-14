package com.learnkafka.events.producer.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * @author juandiegoespinosasantos@gmail.com
 * @version Jul 13, 2023
 * @since 17
 */
@Configuration
public class AutoCreateConfig {

    @Value("${spring.kafka.topic}")
    public String topic;

    // Not for production!
    @Bean
    public NewTopic libraryEvents() {
        return TopicBuilder
                .name(topic)
                .partitions(3)
                .replicas(3)
                .build();
    }
}