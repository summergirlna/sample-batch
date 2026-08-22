package com.example.batch.report;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.backendclient.user.UserApiClient;
import com.example.backendclient.user.response.UserResponse;
import com.example.batch.message.UserNameListReportMessageReader;
import com.example.batch.message.UserNameListReportRequestMessage;
import com.example.batch.sftp.SftpClient;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;

@ExtendWith(MockitoExtension.class)
class UserNameListReportTaskletTest {

  @Mock private UserApiClient userApiClient;

  @Mock private UserNameListReportFactory userNameListReportFactory;

  @Mock private UserNameListReportHtmlRenderer userNameListReportHtmlRenderer;

  @Mock private UserNameListPdfGenerator userNameListPdfGenerator;

  @Mock private UserNameListReportFileWriter userNameListReportFileWriter;

  @Mock private SftpClient sftpClient;

  @Mock private UserNameListReportMessageReader userNameListReportMessageReader;

  @Test
  void execute() {
    UserNameListReportTasklet userNameListReportTasklet =
        new UserNameListReportTasklet(
            userApiClient,
            userNameListReportFactory,
            userNameListReportHtmlRenderer,
            userNameListPdfGenerator,
            userNameListReportFileWriter,
            sftpClient,
            userNameListReportMessageReader);

    UserNameListReportRequestMessage requestMessage =
        new UserNameListReportRequestMessage("request-1", List.of("user-1", "user-2"));
    List<UserResponse> userResponses =
        List.of(new UserResponse("user-1", "山田太郎"), new UserResponse("user-2", "佐藤花子"));
    UserNameListReport report =
        new UserNameListReport(
            "ユーザ名一覧",
            "2026-08-15 21:34:56",
            List.of(
                new UserNameListReportRow("user-1", "山田太郎"),
                new UserNameListReportRow("user-2", "佐藤花子")));
    byte[] pdf = "%PDF".getBytes(StandardCharsets.UTF_8);
    Path outputPath = Path.of("work/output/user-name-list-report.pdf");

    when(userNameListReportMessageReader.receive()).thenReturn(Optional.of(requestMessage));
    when(userApiClient.listByIds(List.of("user-1", "user-2"))).thenReturn(userResponses);
    when(userNameListReportFactory.create(userResponses)).thenReturn(report);
    when(userNameListReportHtmlRenderer.render(report)).thenReturn("<html>report</html>");
    when(userNameListPdfGenerator.generate("<html>report</html>")).thenReturn(pdf);
    when(userNameListReportFileWriter.write(pdf)).thenReturn(outputPath);

    RepeatStatus actual = userNameListReportTasklet.execute(null, null);

    assertEquals(RepeatStatus.FINISHED, actual);

    verify(userNameListReportMessageReader).receive();
    verify(userApiClient).listByIds(List.of("user-1", "user-2"));
    verify(userNameListReportFactory).create(userResponses);
    verify(userNameListReportHtmlRenderer).render(report);
    verify(userNameListPdfGenerator).generate("<html>report</html>");
    verify(userNameListReportFileWriter).write(pdf);
    verify(sftpClient).upload(outputPath);
  }
}
