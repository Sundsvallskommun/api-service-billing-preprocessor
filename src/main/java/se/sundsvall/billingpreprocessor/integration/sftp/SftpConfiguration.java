package se.sundsvall.billingpreprocessor.integration.sftp;

import org.apache.sshd.sftp.client.SftpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.InputStreamResource;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.outbound.SftpMessageHandler;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.messaging.MessageHandler;
import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;

@Configuration
public class SftpConfiguration {

	@Bean
	public SessionFactory<SftpClient.DirEntry> sftpSessionFactory(SftpProperties properties) {
		DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory(true);
		factory.setHost(properties.host());
		factory.setPort(properties.port());
		factory.setUser(properties.user());
		factory.setPassword(properties.password());
		if(properties.knownHosts() != null) {
			factory.setKnownHostsResource(new InputStreamResource(new ByteArrayInputStream(properties.knownHosts().getBytes())));
		}
		factory.setAllowUnknownKeys(properties.allowUnknownKeys());
		return new CachingSessionFactory<>(factory);
	}

	@Bean
	@ServiceActivator(inputChannel = "toSftpChannel")
	public MessageHandler handler(SessionFactory<SftpClient.DirEntry> factory, SftpProperties properties) {
		SftpMessageHandler handler = new SftpMessageHandler(factory);
		handler.setRemoteDirectoryExpression(new LiteralExpression(properties.remoteDir()));
		handler.setFileNameGenerator(message -> {
			if (message.getPayload() instanceof Resource) {
				return ((Resource) message.getPayload()).getFilename();
			} else {
				throw new IllegalArgumentException("Resource expected as payload.");
			}
		});
		return handler;
	}

	@MessagingGateway
	public interface UploadGateway {

		@Gateway(requestChannel = "toSftpChannel")
		void sendToSftp(Resource file);

	}
}
