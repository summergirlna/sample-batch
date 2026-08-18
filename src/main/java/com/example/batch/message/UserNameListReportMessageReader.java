package com.example.batch.message;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserNameListReportMessageReader {

  private final RabbitTemplate rabbitTemplate;
  private final ReportProperties reportProperties;

  public Optional<UserNameListReportRequestMessage> receive() {
    UserNameListReportRequestMessage message =
        rabbitTemplate.receiveAndConvert(
            reportProperties.queueName(), new ParameterizedTypeReference<>() {});

    return Optional.ofNullable(message);
  }
}
