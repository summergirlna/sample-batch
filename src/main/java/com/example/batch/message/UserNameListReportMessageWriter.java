package com.example.batch.message;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserNameListReportMessageWriter {

  private final RabbitTemplate rabbitTemplate;
  private final ReportProperties reportProperties;

  public void send(UserNameListReportRequestMessage message) {
    rabbitTemplate.convertAndSend(reportProperties.queueName(), message);
  }
}
