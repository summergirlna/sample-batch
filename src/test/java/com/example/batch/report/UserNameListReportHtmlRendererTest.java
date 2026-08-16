package com.example.batch.report;

import org.junit.jupiter.api.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserNameListReportHtmlRendererTest {

    @Test
    void render() {
        TemplateEngine templateEngine = createTemplateEngine();
        UserNameListReportHtmlRenderer userNameListReportHtmlRenderer =
                new UserNameListReportHtmlRenderer(templateEngine);

        List<UserNameListReportRow> rows = List.of(
                new UserNameListReportRow("user-1", "山田太郎"),
                new UserNameListReportRow("user-2", "佐藤花子")
        );
        UserNameListReport report = new UserNameListReport(
                "ユーザ名一覧",
                "2026-08-15 21:34:56",
                rows
        );

        String actual = userNameListReportHtmlRenderer.render(report);

        assertTrue(actual.contains("<title>ユーザ名一覧</title>"));
        assertTrue(actual.contains("<h1 class=\"report-title\">ユーザ名一覧</h1>"));
        assertTrue(actual.contains("<td>2026-08-15 21:34:56</td>"));
        assertTrue(actual.contains("<td class=\"no-column\">1</td>"));
        assertTrue(actual.contains("<td class=\"id-column\">user-1</td>"));
        assertTrue(actual.contains("<td class=\"name-column\">山田太郎</td>"));
        assertTrue(actual.contains("<td class=\"no-column\">2</td>"));
        assertTrue(actual.contains("<td class=\"id-column\">user-2</td>"));
        assertTrue(actual.contains("<td class=\"name-column\">佐藤花子</td>"));
        assertFalse(actual.contains("出力対象のユーザが存在しません。"));
    }

    private TemplateEngine createTemplateEngine() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("UTF-8");

        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        return templateEngine;
    }
}