package com.example.batch.config;

import com.example.batch.client.UserApiClient;
import com.example.batch.client.response.UserResponse;
import com.example.batch.report.UserNameListPdfGenerator;
import com.example.batch.report.UserNameListReport;
import com.example.batch.report.UserNameListReportFactory;
import com.example.batch.report.UserNameListReportHtmlRenderer;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@Configuration
public class UserNameListReportJobConfig {

    @Bean
    public Job userNameListreportJob(
            JobRepository jobRepository,
            Step helloWorldStep
    ) {
        return new JobBuilder("userNameListReportJob", jobRepository)
                .start(helloWorldStep)
                .build();
    }

    @Bean
    public Step userNameListReportStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            UserApiClient userApiClient,
            UserNameListReportFactory userNameListReportFactory,
            UserNameListReportHtmlRenderer userNameListReportHtmlRenderer,
            UserNameListPdfGenerator userNameListPdfGenerator
    ) {
        return new StepBuilder("userNameListReportStep", jobRepository)
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
                    UserNameListReport report = userNameListReportFactory.create(userResponses);
                    String html = userNameListReportHtmlRenderer.render(report);

                    Path outputPath = Path.of("work/output/user-name-list-report.pdf");
                    Path pdfPath = userNameListPdfGenerator.generate(html, outputPath);

                    System.out.println("users = " + userResponses);
                    System.out.println("pdf = " + pdfPath.toAbsolutePath());

                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
