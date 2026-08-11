package com.example.batch.sftp;

import lombok.RequiredArgsConstructor;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;

import java.io.IOException;

@RequiredArgsConstructor
public class SftpSession implements AutoCloseable {

    private final SSHClient sshClient;
    private final SFTPClient sftpClient;

    public void put(String localFilePath, String remoteFilePath) throws IOException {
        sftpClient.put(localFilePath, remoteFilePath);
    }

    @Override
    public void close() throws IOException {
        try {
            sftpClient.close();
        } finally {
            sshClient.close();
        }
    }
}
