package se.sundsvall.billingpreprocessor.integration.sftp;


import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.Files.readString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.util.ResourceUtils.getFile;

@ActiveProfiles("junit")
@SpringBootTest
class SftpConfigurationTest {

	@Autowired
	private SftpConfiguration.UploadGateway gateway;

	private static final Logger LOGGER = LoggerFactory.getLogger(SftpConfigurationTest.class);
	private static final Path TEST_FILE;

	static {
		try {
			TEST_FILE = Files.createTempFile("TEST", ".txt");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Container
	private static final GenericContainer<?> SFTP_SERVER = new GenericContainer<>("atmoz/sftp:alpine-3.7")
		.withCopyFileToContainer(MountableFile.forClasspathResource("keys/ssh_host_rsa_key", 0600),
			"/etc/ssh/")
		.withCopyFileToContainer(MountableFile.forClasspathResource("keys/ssh_host_rsa_key.pub", 0600),
			"/etc/ssh/")
		.withExposedPorts(22)
		.withCommand("user:pass:1001::upload");

	@DynamicPropertySource
	static void registerProperties(DynamicPropertyRegistry registry) throws IOException {
		SFTP_SERVER.start();
		SFTP_SERVER.followOutput(new Slf4jLogConsumer(LOGGER));

		registry.add("integration.sftp.host", SFTP_SERVER::getHost);
		registry.add("integration.sftp.port", () -> SFTP_SERVER.getMappedPort(22));
		var key = readString(getFile("classpath:keys/ssh_host_rsa_key.pub").toPath());
		registry.add("integration.sftp.knownHosts", () -> String.format("[%s]:%s %s", SFTP_SERVER.getHost(), SFTP_SERVER.getMappedPort(22), key));
	}

	@AfterAll
	static void tearDown() throws IOException {
		SFTP_SERVER.stop();
		Files.delete(TEST_FILE);
	}


	@Test
	void testUpload() throws InterruptedException, IOException, JSchException, SftpException {

		var resource = new FileSystemResource(TEST_FILE);
		ChannelSftp channel = getSftpChannel();

		try {
			assertThat(channel.ls("/upload/")).noneMatch(item -> item.toString().contains(TEST_FILE.getFileName().toString()));

			gateway.sendToSftp(resource);

			assertThat(channel.ls("/upload/")).anyMatch(item -> item.toString().contains(TEST_FILE.getFileName().toString()));
		} finally {
			channel.disconnect();
		}
	}

	private ChannelSftp getSftpChannel() throws JSchException {
		JSch jsch = new JSch();
		Session jschSession = jsch.getSession("user", SFTP_SERVER.getHost(), SFTP_SERVER.getMappedPort(22));
		jschSession.setPassword("pass");
		jschSession.setConfig("StrictHostKeyChecking", "no");
		jschSession.connect();
		ChannelSftp channel = (ChannelSftp) jschSession.openChannel("sftp");
		channel.connect();
		return channel;
	}
}