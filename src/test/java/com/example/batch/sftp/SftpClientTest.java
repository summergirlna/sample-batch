package com.example.batch.sftp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SftpClientTest {

  @TempDir private Path tempDir;

  @Mock private SftpSessionFactory sftpSessionFactory;

  @Mock private SftpSession sftpSession;

  @Test
  void upload() throws IOException {
    SftpProperties sftpProperties =
        new SftpProperties("localhost", 22, "user", "password", "/upload", null);
    SftpClient sftpClient = new SftpClient(sftpProperties, sftpSessionFactory);

    Path localFilePath = tempDir.resolve("test.txt");
    Files.writeString(localFilePath, "hello");

    when(sftpSessionFactory.create()).thenReturn(sftpSession);

    sftpClient.upload(localFilePath);

    verify(sftpSession).put(localFilePath.toString(), "/upload/test.txt");
    verify(sftpSession).close();
  }

  @Test
  void uploadThrowsIllegalStateExceptionWhenSftpPutFails() throws IOException {
    SftpProperties sftpProperties =
        new SftpProperties("localhost", 22, "user", "password", "/upload", null);
    SftpClient sftpClient = new SftpClient(sftpProperties, sftpSessionFactory);

    Path localFilePath = tempDir.resolve("test.txt");
    Files.writeString(localFilePath, "hello");

    when(sftpSessionFactory.create()).thenReturn(sftpSession);
    doThrow(new IOException("upload failed"))
        .when(sftpSession)
        .put(localFilePath.toString(), "/upload/test.txt");

    assertThrows(IllegalStateException.class, () -> sftpClient.upload(localFilePath));

    verify(sftpSession).close();
  }
}
