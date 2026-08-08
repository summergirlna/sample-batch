package com.example.batch.sftp;

import lombok.RequiredArgsConstructor;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class SftpClient {

    private final SftpProperties sftpProperties;

    public void upload(Path localFilePath) {
        String remoteFilePath = sftpProperties.remoteDirectory() + "/" + localFilePath.getFileName();

        try(SSHClient sshClient = new SSHClient()) {
            sshClient.addHostKeyVerifier(new PromiscuousVerifier());
            sshClient.connect(sftpProperties.host(), sftpProperties.port());
            sshClient.authPassword(sftpProperties.username(), sftpProperties.password());

            try(SFTPClient sftpClient = sshClient.newSFTPClient()) {
                sftpClient.put(localFilePath.toString(), remoteFilePath);
            }

        } catch (IOException e) {
            throw new IllegalStateException(
                    "SFTP転送に失敗しました。localFilePath=%s, remoteFilePath=%s"
                            .formatted(localFilePath, remoteFilePath),
                    e
            );
        }

    }
}
