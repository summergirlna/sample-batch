package com.example.batch.message;

import java.util.List;

public record UserNameListReportRequestMessage(String requestId, List<String> userIds) {}
