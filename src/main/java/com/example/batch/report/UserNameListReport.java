package com.example.batch.report;

import java.util.List;

public record UserNameListReport(
    String title, String createdAt, List<UserNameListReportRow> rows) {}
