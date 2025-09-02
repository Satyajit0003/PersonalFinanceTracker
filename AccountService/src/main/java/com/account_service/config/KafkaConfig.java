package com.account_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic newTopic() {
        return TopicBuilder.name("account-notifications").build();
    }

    @Bean
    public NewTopic accountDebitTopic() {
        return TopicBuilder.name("account-debit-success-event").build();
    }


    @Bean
    public NewTopic transactionTopic() {
        return TopicBuilder.name("transaction-complete-event").build();
    }

    @Bean
    public NewTopic goalTopic() {
        return TopicBuilder.name("money-reduce-complete-event").build();
    }
}
