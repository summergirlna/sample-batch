package com.example.batch.sftp;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;

@RequiredArgsConstructor
public class SftpSession implements AutoCloseable {

  private final SSHClient sshClient;
  private final SFTPClient sftpClient;

  public void put(String localFilePath, String remoteFilePath) throws IOException {
    sftpClient.put(localFilePath, remoteFilePath);
  }

  @Override
  public void close() throws IOException {
    try (SSHClient ignoredSshClient = sshClient;
        SFTPClient ignoredSftpClient = sftpClient) {
      // Resources are closed automatically in reverse order:
      // SFTPClient first, then SSHClient.
    }
  }
}
