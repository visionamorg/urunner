package com.runhub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AsyncConfig {
    // Spring Boot auto-configures the async task executor from
    // spring.task.execution.pool.* properties in application.yml
}
