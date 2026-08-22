package com.example.batch.report;

import com.example.backendclient.user.response.UserResponse;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserNameListReportFactory {

  private static final String TITLE = "ユーザ名一覧";
  private static final DateTimeFormatter CREATED_AT_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  private final Clock clock;

  public UserNameListReport create(List<UserResponse> userResponses) {
    List<UserNameListReportRow> rows =
        userResponses.stream()
            .map(userResponse -> new UserNameListReportRow(userResponse.id(), userResponse.name()))
            .toList();

    return new UserNameListReport(
        TITLE, LocalDateTime.now(clock).format(CREATED_AT_FORMATTER), rows);
  }
}
