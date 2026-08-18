package com.example.batch.report;

import static org.junit.jupiter.api.Assertions.*;

import com.example.batch.client.response.UserResponse;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.Test;

class UserNameListReportFactoryTest {

  @Test
  void create() {
    Clock clock = Clock.fixed(Instant.parse("2026-08-15T12:34:56Z"), ZoneId.of("Asia/Tokyo"));
    UserNameListReportFactory userNameListReportFactory = new UserNameListReportFactory(clock);

    List<UserResponse> userResponses =
        List.of(new UserResponse("user-1", "山田太郎"), new UserResponse("user-2", "佐藤花子"));

    UserNameListReport actual = userNameListReportFactory.create(userResponses);

    assertEquals("ユーザ名一覧", actual.title());
    assertEquals("2026-08-15 21:34:56", actual.createdAt());
    assertEquals(2, actual.rows().size());
    assertEquals("user-1", actual.rows().getFirst().id());
    assertEquals("山田太郎", actual.rows().getFirst().name());
    assertEquals("user-2", actual.rows().get(1).id());
    assertEquals("佐藤花子", actual.rows().get(1).name());
  }
}
