package se.sundsvall.billingpreprocessor.integration.sftp;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("integration.sftp")
public record SftpProperties(String host, int port, String user, String password, String remoteDir, boolean allowUnknownKeys, String knownHosts) {

}
