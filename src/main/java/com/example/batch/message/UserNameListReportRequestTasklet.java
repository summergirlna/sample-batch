package com.example.batch.message;

import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserNameListReportRequestTasklet implements Tasklet {

  private static final String REQUEST_ID_PARAMETER_NAME = "request.id";
  private static final String USER_IDS_PARAMETER_NAME = "user.ids";

  private final UserNameListReportMessageWriter userNameListReportMessageWriter;

  @Override
  public @Nullable RepeatStatus execute(
      @NonNull StepContribution contribution, @NonNull ChunkContext chunkContext) throws Exception {
    String requestId = getRequiredJobParameter(chunkContext, REQUEST_ID_PARAMETER_NAME);
    String userIdsText = getRequiredJobParameter(chunkContext, USER_IDS_PARAMETER_NAME);

    List<String> userIds =
        Arrays.stream(userIdsText.split(","))
            .map(String::trim)
            .filter(StringUtils::hasText)
            .toList();

    if (userIds.isEmpty()) {
      throw new IllegalArgumentException("user.ids must contain at least one user id.");
    }

    UserNameListReportRequestMessage message =
        new UserNameListReportRequestMessage(requestId, userIds);

    userNameListReportMessageWriter.send(message);

    log.info(
        "Sent user name list report request message. requestId={}, userIds={}", requestId, userIds);

    return RepeatStatus.FINISHED;
  }

  private String getRequiredJobParameter(ChunkContext chunkContext, String name) {
    Object value = chunkContext.getStepContext().getJobParameters().get(name);

    if (value == null || !StringUtils.hasText(value.toString())) {
      throw new IllegalArgumentException(name + "is required.");
    }

    return value.toString();
  }
}
