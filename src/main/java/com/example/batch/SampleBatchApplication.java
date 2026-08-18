package com.example.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SampleBatchApplication {

  public static void main(String[] args) {
    int exitCode;

    try (ConfigurableApplicationContext context =
        SpringApplication.run(SampleBatchApplication.class, args)) {
      exitCode = SpringApplication.exit(context);
    }

    System.exit(exitCode);
  }
}
