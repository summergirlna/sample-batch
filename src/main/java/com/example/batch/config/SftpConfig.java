package com.example.batch.config;

import com.example.batch.sftp.SftpProperties;
import java.io.File;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import net.schmizz.sshj.transport.verification.HostKeyVerifier;
import net.schmizz.sshj.transport.verification.OpenSSHKnownHosts;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@RequiredArgsConstructor
public class SftpConfig {

  private final SftpProperties sftpProperties;

  @Bean
  @Profile("prod")
  public HostKeyVerifier hostKeyVerifier() throws IOException {
    return new OpenSSHKnownHosts(new File(sftpProperties.knownHostsPath()));
  }

  @Bean
  @Profile("test")
  public HostKeyVerifier promiscuousVerifier() {
    return new PromiscuousVerifier();
  }
}
