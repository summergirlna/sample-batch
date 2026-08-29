package com.example.batch.config;

import com.example.batch.report.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.EnableJdbcJobRepository;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
@EnableJdbcJobRepository
public class UserNameListReportJobConfig {

  @Bean
  public Job userNameListreportJob(JobRepository jobRepository, Step userNameListReportStep) {
    return new JobBuilder("userNameListReportJob", jobRepository)
        .start(userNameListReportStep)
        .build();
  }

  @Bean
  public Step userNameListReportStep(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      UserNameListReportTasklet userNameListReportTasklet) {
    return new StepBuilder("userNameListReportStep", jobRepository)
        .tasklet(userNameListReportTasklet, transactionManager)
        .build();
  }
}
