package com.example.batch.message;

import com.example.batch.report.UserNameListReport;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserNameListReportMessageReader {

    private final RabbitTemplate rabbitTemplate;
    private final ReportProperties reportProperties;

    public Optional<UserNameListReportRequestMessage> receive() {
        UserNameListReportRequestMessage message = rabbitTemplate.receiveAndConvert(
                reportProperties.queueName(),
                new ParameterizedTypeReference<>() {
                }
        );

        return Optional.ofNullable(message);
    }
}
