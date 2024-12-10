package se.sundsvall.billingpreprocessor.integration.sftp;

import java.io.ByteArrayInputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.sshd.sftp.client.SftpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.DelegatingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.outbound.SftpMessageHandler;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.handler.annotation.Header;

@Configuration
public class SftpConfiguration {

	@Bean
	public DelegatingSessionFactory<SftpClient.DirEntry> sftpSessionFactory(SftpPropertiesConfig config) {
		Map<Object, SessionFactory<SftpClient.DirEntry>> factories = new LinkedHashMap<>();
		for (Map.Entry<String, SftpProperties> properties : config.getMap().entrySet()) {
			DefaultSftpSessionFactory factory = getSftpSessionFactory(properties);
			factories.put(properties.getKey(), new CachingSessionFactory<>(factory));
		}
		// Set first factory as default
		return new DelegatingSessionFactory<>(factories, factories.values().iterator().next());
	}

	private DefaultSftpSessionFactory getSftpSessionFactory(Map.Entry<String, SftpProperties> properties) {
		DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory(true);
		factory.setHost(properties.getValue().getHost());
		factory.setPort(properties.getValue().getPort());
		factory.setUser(properties.getValue().getUser());
		factory.setPassword(properties.getValue().getPassword());

		if (properties.getValue().getKnownHosts() != null) {
			factory.setKnownHostsResource(new InputStreamResource(new ByteArrayInputStream(properties.getValue().getKnownHosts().getBytes())));
		}
		factory.setAllowUnknownKeys(properties.getValue().isAllowUnknownKeys());
		return factory;
	}

	@Bean
	@ServiceActivator(inputChannel = "toSftpChannel")
	public MessageHandler handler(SessionFactory<SftpClient.DirEntry> factory) {
		SftpMessageHandler handler = new SftpMessageHandler(factory);
		handler.setRemoteDirectoryExpression(new SpelExpressionParser().parseExpression("headers['remoteDirectory']"));

		return handler;
	}

	@MessagingGateway
	public interface UploadGateway {

		@Gateway(requestChannel = "toSftpChannel")
		void sendToSftp(Resource file, @Header(FileHeaders.FILENAME) String filename, @Header("remoteDirectory") String remoteDirectory);

	}
}
