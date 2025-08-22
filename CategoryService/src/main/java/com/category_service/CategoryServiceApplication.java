package com.category_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBootApplication
@EnableDiscoveryClient
public class CategoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CategoryServiceApplication.class, args);
	}

    @Bean
    public PlatformTransactionManager add(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }
}
