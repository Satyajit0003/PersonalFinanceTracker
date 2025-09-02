package com.transaction_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic limitTopic() {
        return TopicBuilder.name("limit-notifications").build();
    }

    @Bean
    public NewTopic transactionTopic() {
        return TopicBuilder.name("transaction-event").build();
    }

    @Bean
    public NewTopic goalTopic() {
        return TopicBuilder.name("add-money-event").build();
    }

    @Bean
    public NewTopic goal_Topic() {
        return TopicBuilder.name("goal-complete-event").build();
    }
}
