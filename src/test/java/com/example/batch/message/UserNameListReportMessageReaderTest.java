package com.example.batch.message;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.ParameterizedTypeReference;

@ExtendWith(MockitoExtension.class)
class UserNameListReportMessageReaderTest {

  @Mock private RabbitTemplate rabbitTemplate;

  @Test
  void receive() {
    ReportProperties reportProperties = new ReportProperties("report.queue");
    UserNameListReportMessageReader userNameListReportMessageReader =
        new UserNameListReportMessageReader(rabbitTemplate, reportProperties);

    UserNameListReportRequestMessage message =
        new UserNameListReportRequestMessage("request-1", List.of("user-1", "user-2"));

    when(rabbitTemplate.receiveAndConvert(
            eq("report.queue"),
            ArgumentMatchers.<ParameterizedTypeReference<UserNameListReportRequestMessage>>any()))
        .thenReturn(message);

    Optional<UserNameListReportRequestMessage> actual = userNameListReportMessageReader.receive();

    assertTrue(actual.isPresent());
    assertEquals("request-1", actual.get().requestId());
    assertEquals(List.of("user-1", "user-2"), actual.get().userIds());

    verify(rabbitTemplate)
        .receiveAndConvert(
            eq("report.queue"),
            ArgumentMatchers.<ParameterizedTypeReference<UserNameListReportRequestMessage>>any());
  }
}
