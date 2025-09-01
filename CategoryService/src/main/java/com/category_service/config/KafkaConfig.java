package com.category_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic categoryLimitUpdateTopic() {
        return TopicBuilder.name("category-update-success-event").build();
    }

    @Bean
    public NewTopic categoryLimitUpdateFailTopic() {
        return TopicBuilder.name("category-update-fail-event").build();
    }
}
