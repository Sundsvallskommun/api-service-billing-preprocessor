package se.sundsvall.billingpreprocessor.integration.sftp;

import static java.nio.file.Files.readString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.util.ResourceUtils.getFile;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Vector;
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
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.MountableFile;

@ActiveProfiles("junit")
@SpringBootTest
class SftpConfigurationTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(SftpConfigurationTest.class);
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

	@Container
	private static final GenericContainer<?> SFTP_SERVER = new GenericContainer<>("atmoz/sftp:alpine-3.7")
		.withCopyFileToContainer(MountableFile.forClasspathResource("keys/ssh_host_rsa_key", 0600), "/etc/ssh/")
		.withCopyFileToContainer(MountableFile.forClasspathResource("keys/ssh_host_rsa_key.pub", 0600), "/etc/ssh/")
		.withExposedPorts(22)
		.withCommand("user:pass:1001::upload");

	@DynamicPropertySource
	static void registerProperties(final DynamicPropertyRegistry registry) throws IOException {
		SFTP_SERVER.start();
		SFTP_SERVER.followOutput(new Slf4jLogConsumer(LOGGER));

		final var key = readString(getFile("classpath:keys/ssh_host_rsa_key.pub").toPath());
		registry.add("integration.sftp.municipalityIds.2281.host", SFTP_SERVER::getHost);
		registry.add("integration.sftp.municipalityIds.2281.port", () -> SFTP_SERVER.getMappedPort(22));
		registry.add("integration.sftp.municipalityIds.2281.knownHosts", () -> String.format("[%s]:%s %s", SFTP_SERVER.getHost(), SFTP_SERVER.getMappedPort(22), key));
	}

	@AfterAll
	static void tearDown() throws IOException {
		SFTP_SERVER.stop();
		Files.delete(TEST_FILE);
	}

	@Test
	void testUpload() throws IOException, JSchException, SftpException {
		final var resource = new ByteArrayResource(Files.readAllBytes(TEST_FILE));
		final var channel = getSftpChannel();

		try {
			assertThat((Vector<?>) channel.ls(properties.getMap().get("2281").getRemoteDir())).noneMatch(item -> item.toString().contains(TEST_FILE.getFileName().toString()));

			sessionFactory.setThreadKey("2281");
			gateway.sendToSftp(resource, TEST_FILE.toFile().getName(), properties.getMap().get("2281").getRemoteDir());
			sessionFactory.clearThreadKey();

			assertThat((Vector<?>) channel.ls(properties.getMap().get("2281").getRemoteDir())).anyMatch(item -> item.toString().contains(TEST_FILE.getFileName().toString()));
		} finally {
			channel.disconnect();
		}
	}

	private ChannelSftp getSftpChannel() throws JSchException {
		final var jsch = new JSch();
		final Session jschSession = jsch.getSession(properties.getMap().get("2281").getUser(), properties.getMap().get("2281").getHost(), properties.getMap().get("2281").getPort());
		jschSession.setPassword(properties.getMap().get("2281").getPassword());
		jschSession.setConfig("StrictHostKeyChecking", "no");
		jschSession.connect();

		final var channel = (ChannelSftp) jschSession.openChannel("sftp");
		channel.connect();
		return channel;
	}
}
