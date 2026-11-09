package se.sundsvall.billingpreprocessor.integration.sftp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.stream.StreamSupport;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.keyverifier.AcceptAllServerKeyVerifier;
import org.apache.sshd.sftp.client.SftpClient;
import org.apache.sshd.sftp.client.SftpClientFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.integration.file.remote.session.DelegatingSessionFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("junit")
@SpringBootTest
class SftpConfigurationTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(SftpConfigurationTest.class);
	private static final Duration TIMEOUT = Duration.ofSeconds(10);
	private static final Path TEST_FILE;

	@Autowired
	private SftpConfiguration.UploadGateway gateway;

	@Autowired
	private SftpPropertiesConfig properties;

	@Autowired
	private DelegatingSessionFactory<?> sessionFactory;

	static {
		try {
			TEST_FILE = Files.createTempFile("TEST", ".txt");
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static final GenericContainer<?> SFTP_SERVER = new GenericContainer<>("atmoz/sftp:alpine")
		.withExposedPorts(22)
		.withCommand("user:pass:1001::upload");

	@DynamicPropertySource
	static void registerProperties(final DynamicPropertyRegistry registry) {
		SFTP_SERVER.start();
		SFTP_SERVER.followOutput(new Slf4jLogConsumer(LOGGER));

		registry.add("integration.sftp.municipalityIds.2281.host", SFTP_SERVER::getHost);
		registry.add("integration.sftp.municipalityIds.2281.port", () -> SFTP_SERVER.getMappedPort(22));
		registry.add("integration.sftp.municipalityIds.2281.allowUnknownKeys", () -> true);
	}

	@AfterAll
	static void tearDown() throws IOException {
		SFTP_SERVER.stop();
		Files.delete(TEST_FILE);
	}

	@Test
	void testUpload() throws IOException {
		final var resource = new ByteArrayResource(Files.readAllBytes(TEST_FILE));
		final var sftpProperties = properties.getMap().get("2281");
		final var fileName = TEST_FILE.getFileName().toString();

		assertThat(listRemoteDir(sftpProperties)).noneMatch(item -> item.contains(fileName));

		sessionFactory.setThreadKey("2281");
		gateway.sendToSftp(resource, TEST_FILE.toFile().getName(), sftpProperties.getRemoteDir());
		sessionFactory.clearThreadKey();

		assertThat(listRemoteDir(sftpProperties)).anyMatch(item -> item.contains(fileName));
	}

	private static List<String> listRemoteDir(final SftpProperties sftpProperties) throws IOException {
		try (final var client = SshClient.setUpDefaultClient()) {
			client.setServerKeyVerifier(AcceptAllServerKeyVerifier.INSTANCE);
			client.start();

			try (final var session = client.connect(sftpProperties.getUser(), sftpProperties.getHost(), sftpProperties.getPort()).verify(TIMEOUT).getSession()) {
				session.addPasswordIdentity(sftpProperties.getPassword());
				session.auth().verify(TIMEOUT);

				try (final var sftpClient = SftpClientFactory.instance().createSftpClient(session)) {
					return StreamSupport.stream(sftpClient.readDir(sftpProperties.getRemoteDir()).spliterator(), false)
						.map(SftpClient.DirEntry::getFilename)
						.toList();
				}
			}
		}
	}
}
