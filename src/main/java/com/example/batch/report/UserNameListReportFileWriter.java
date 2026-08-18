package com.example.batch.report;

import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.stereotype.Component;

@Component
public class UserNameListReportFileWriter {

  private static final Path OUTPUT_PATH = Path.of("work/output/user-name-list-report.pdf");

  public Path write(byte[] pdf) {
    try {
      Files.createDirectories(OUTPUT_PATH.getParent());
      Files.write(OUTPUT_PATH, pdf);

      return OUTPUT_PATH;

    } catch (Exception e) {
      throw new IllegalStateException("ユーザ名一覧PDFファイルの保存に失敗しました。outputPath=" + OUTPUT_PATH, e);
    }
  }
}
