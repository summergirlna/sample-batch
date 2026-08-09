package com.example.batch.message;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "report")
public record ReportProperties(
        String queueName
) {
}
