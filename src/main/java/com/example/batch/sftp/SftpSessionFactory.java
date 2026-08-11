package com.example.batch.sftp;

import lombok.RequiredArgsConstructor;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.HostKeyVerifier;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SftpSessionFactory {

    private final SftpProperties sftpProperties;
    private final HostKeyVerifier hostKeyVerifier;

    public SftpSession create() throws IOException {
        SSHClient sshClient = new SSHClient();

        try {
            sshClient.addHostKeyVerifier(hostKeyVerifier);
            sshClient.connect(sftpProperties.host(), sftpProperties.port());
            sshClient.authPassword(sftpProperties.username(), sftpProperties.password());

            SFTPClient sftpClient = sshClient.newSFTPClient();
            return new SftpSession(sshClient, sftpClient);

        } catch (IOException | RuntimeException e) {
            sshClient.close();
            throw e;
        }
    }
}
