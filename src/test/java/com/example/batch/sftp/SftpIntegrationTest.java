package com.example.batch.sftp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.sftp.server.SftpSubsystemFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SftpIntegrationTest {

  private static final String USERNAME = "test-user";
  private static final String PASSWORD = "test-password";

  private SshServer sshServer;

  @TempDir private Path tempDir;

  @AfterEach
  void tearDown() throws Exception {
    if (sshServer != null) {
      sshServer.stop();
    }
  }

  @Test
  void uploadToSftpServer() throws Exception {
    Path remoteRootDirectory = tempDir.resolve("remote-root");
    Files.createDirectories(remoteRootDirectory);

    startSftpServer(remoteRootDirectory);

    Path localFilePath = tempDir.resolve("test.txt");
    Files.writeString(localFilePath, "hello sftp");

    SftpProperties sftpProperties =
        new SftpProperties("localhost", sshServer.getPort(), USERNAME, PASSWORD, ".", null);
    SftpSessionFactory sftpSessionFactory =
        new SftpSessionFactory(sftpProperties, new PromiscuousVerifier());
    SftpClient sftpClient = new SftpClient(sftpProperties, sftpSessionFactory);

    sftpClient.upload(localFilePath);

    Path uploadedFilePath = remoteRootDirectory.resolve("test.txt");
    assertTrue(Files.exists(uploadedFilePath));
    assertEquals("hello sftp", Files.readString(uploadedFilePath));
  }

  private void startSftpServer(Path remoteRootDirectory) throws Exception {
    sshServer = SshServer.setUpDefaultServer();
    sshServer.setPort(0);
    sshServer.setKeyPairProvider(
        new SimpleGeneratorHostKeyProvider(tempDir.resolve("host-key.ser")));
    sshServer.setPasswordAuthenticator(
        (username, password, session) -> USERNAME.equals(username) && PASSWORD.equals(password));

    VirtualFileSystemFactory fileSystemFactory = new VirtualFileSystemFactory();
    fileSystemFactory.setUserHomeDir(USERNAME, remoteRootDirectory);
    sshServer.setFileSystemFactory(fileSystemFactory);

    sshServer.setSubsystemFactories(List.of(new SftpSubsystemFactory.Builder().build()));
    sshServer.start();
  }
}
