package com.runhub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class RunHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(RunHubApplication.class, args);
    }
}
