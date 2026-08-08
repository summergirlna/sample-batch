package com.example.batch.config;

import com.example.batch.client.UserApiClient;
import com.example.batch.client.response.UserResponse;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;
import java.util.List;

@Configuration
public class UserNameListReportJobConfig {

    @Bean
    public Job helloWorldJob(
            JobRepository jobRepository,
            Step helloWorldStep
    ) {
        return new JobBuilder("helloWorldJob", jobRepository)
                .start(helloWorldStep)
                .build();
    }

    @Bean
    public Step helloWorldStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            UserApiClient userApiClient
    ) {
        return new StepBuilder("helloWorldStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    String idsParameter = chunkContext
                            .getStepContext()
                            .getJobParameters()
                            .get("ids")
                            .toString();

                    List<String> ids = Arrays.stream(idsParameter.split("_"))
                            .map(String::trim)
                            .filter(id -> !id.isEmpty())
                            .toList();

                    List<UserResponse> userResponses = userApiClient.listByIds(ids);

                    System.out.println("Hello, Spring Batch!");
                    System.out.println("users = " + userResponses);

                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
