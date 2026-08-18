package com.example.batch.sftp;

import java.io.IOException;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SftpClient {

  private final SftpProperties sftpProperties;
  private final SftpSessionFactory sftpSessionFactory;

  public void upload(Path localFilePath) {
    String remoteFilePath = sftpProperties.remoteDirectory() + "/" + localFilePath.getFileName();

    try (SftpSession sftpSession = sftpSessionFactory.create()) {
      sftpSession.put(localFilePath.toString(), remoteFilePath);
    } catch (IOException e) {
      throw new IllegalStateException(
          "SFTP転送に失敗しました。localFilePath=%s, remoteFilePath=%s"
              .formatted(localFilePath, remoteFilePath),
          e);
    }
  }
}
