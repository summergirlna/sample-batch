package com.example.batch.report;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
@RequiredArgsConstructor
public class UserNameListReportHtmlRenderer {

  private static final String TEMPLATE_NAME = "user-name-list-report";

  private final TemplateEngine templateEngine;

  public String render(UserNameListReport report) {
    Context context = new Context();
    context.setVariable("title", report.title());
    context.setVariable("createdAt", report.createdAt());
    context.setVariable("rows", report.rows());

    return templateEngine.process(TEMPLATE_NAME, context);
  }
}
