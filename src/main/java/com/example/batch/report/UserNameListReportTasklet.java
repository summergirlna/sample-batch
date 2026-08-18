package com.example.batch.report;

import com.example.batch.client.UserApiClient;
import com.example.batch.client.response.UserResponse;
import com.example.batch.message.UserNameListReportMessageReader;
import com.example.batch.message.UserNameListReportRequestMessage;
import com.example.batch.sftp.SftpClient;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserNameListReportTasklet implements Tasklet {

  private final UserApiClient userApiClient;
  private final UserNameListReportFactory userNameListReportFactory;
  private final UserNameListReportHtmlRenderer userNameListReportHtmlRenderer;
  private final UserNameListPdfGenerator userNameListPdfGenerator;
  private final UserNameListReportFileWriter userNameListReportFileWriter;
  private final SftpClient sftpClient;
  private final UserNameListReportMessageReader userNameListReportMessageReader;

  @Override
  public @Nullable RepeatStatus execute(
      @NonNull StepContribution contribution, @NonNull ChunkContext chunkContext) {
    Optional<UserNameListReportRequestMessage> requestMessage =
        userNameListReportMessageReader.receive();

    if (requestMessage.isEmpty()) {
      log.info("No report request message");
      return RepeatStatus.FINISHED;
    }

    List<String> ids = requestMessage.get().userIds();

    List<UserResponse> userResponses = userApiClient.listByIds(ids);
    UserNameListReport report = userNameListReportFactory.create(userResponses);
    String html = userNameListReportHtmlRenderer.render(report);
    byte[] pdf = userNameListPdfGenerator.generate(html);
    Path outputPath = userNameListReportFileWriter.write(pdf);

    sftpClient.upload(outputPath);

    log.info("users = {}", userResponses);
    log.info("pdf = {}", outputPath.toAbsolutePath());
    log.info("SFTP転送が完了しました。");

    return RepeatStatus.FINISHED;
  }
}
