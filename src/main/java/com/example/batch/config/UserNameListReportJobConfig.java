package com.example.batch.config;

import com.example.batch.client.UserApiClient;
import com.example.batch.client.response.UserResponse;
import com.example.batch.message.UserNameListReportMessageReader;
import com.example.batch.message.UserNameListReportRequestMessage;
import com.example.batch.report.UserNameListPdfGenerator;
import com.example.batch.report.UserNameListReport;
import com.example.batch.report.UserNameListReportFactory;
import com.example.batch.report.UserNameListReportHtmlRenderer;
import com.example.batch.sftp.SftpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Configuration
@Slf4j
public class UserNameListReportJobConfig {

    @Bean
    public Job userNameListreportJob(
            JobRepository jobRepository,
            Step userNameListReportStep
    ) {
        return new JobBuilder("userNameListReportJob", jobRepository)
                .start(userNameListReportStep)
                .build();
    }

    @Bean
    public Step userNameListReportStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            UserApiClient userApiClient,
            UserNameListReportFactory userNameListReportFactory,
            UserNameListReportHtmlRenderer userNameListReportHtmlRenderer,
            UserNameListPdfGenerator userNameListPdfGenerator,
            SftpClient sftpClient,
            UserNameListReportMessageReader userNameListReportMessageReader
    ) {
        return new StepBuilder("userNameListReportStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    Optional<UserNameListReportRequestMessage> requestMessage =
                            userNameListReportMessageReader.receive();

                    if (requestMessage.isEmpty()) {
                        System.out.println("No report request message");
                        return RepeatStatus.FINISHED;
                    }

                    List<String> ids = requestMessage.get().userIds();

                    List<UserResponse> userResponses = userApiClient.listByIds(ids);
                    UserNameListReport report = userNameListReportFactory.create(userResponses);
                    String html = userNameListReportHtmlRenderer.render(report);

                    Path outputPath = Path.of("work/output/user-name-list-report.pdf");
                    Files.createDirectories(outputPath.getParent());
                    Files.write(outputPath, userNameListPdfGenerator.generate(html));

                    sftpClient.upload(outputPath);

                    log.info("users = {}", userResponses);
                    log.info("pdf = {}", outputPath.toAbsolutePath());
                    log.info("SFTP転送が完了しました。");

                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
