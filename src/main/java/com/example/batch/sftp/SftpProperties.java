package com.example.batch.sftp;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sftp")
public record SftpProperties(
        String host,
        int port,
        String username,
        String password,
        String remoteDirectory,
        String knownHostsPath
) {
}
