package com.example.batch.report;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class UserNameListPdfGeneratorTest {

  @Test
  void generate() {
    UserNameListPdfGenerator userNameListPdfGenerator = new UserNameListPdfGenerator();

    String html =
        """
                <!DOCTYPE html>
                <html lang="ja">
                <head>
                    <meta charset="UTF-8"></meta>
                    <style>
                        body {
                            font-family: "Noto Sans JP", sans-serif;
                        }
                    </style>
                </head>
                <body>
                    <h1>ユーザ名一覧</h1>
                    <table>
                        <tr>
                            <th>ID</th>
                            <th>名前</th>
                        </tr>
                        <tr>
                            <td>user-1</td>
                            <td>山田太郎</td>
                        </tr>
                    </table>
                </body>
                </html>
                """;

    byte[] actual = userNameListPdfGenerator.generate(html);

    assertTrue(actual.length > 0);
    assertPdfData(actual);
  }

  private void assertPdfData(byte[] actual) {
    String header = new String(actual, 0, 4, StandardCharsets.US_ASCII);

    assertEquals("%PDF", header);
  }
}
