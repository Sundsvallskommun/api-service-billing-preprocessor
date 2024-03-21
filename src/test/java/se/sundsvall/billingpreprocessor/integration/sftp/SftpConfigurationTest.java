package se.sundsvall.billingpreprocessor.integration.sftp;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.FileSystemUtils;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
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

	private static final Path SFTP_DIR;
	private static final Path TEST_FILE;

	static {
		try {
			SFTP_DIR = Files.createTempDirectory("SFTP_DIR");
			TEST_FILE = Files.createTempFile("TEST", ".txt");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Container
	private static final GenericContainer<?> SFTP_SERVER = new GenericContainer<>("atmoz/sftp:alpine-3.7")
		.withFileSystemBind(
			SFTP_DIR.toAbsolutePath().toString(),
			"/home/user/upload/",
			BindMode.READ_WRITE
		)
		.withCopyFileToContainer(MountableFile.forClasspathResource("keys/ssh_host_rsa_key", 0600),
			"/etc/ssh/")
		.withCopyFileToContainer(MountableFile.forClasspathResource("keys/ssh_host_rsa_key.pub", 0600),
			"/etc/ssh/")
		.withExposedPorts(22)
		.withCommand("user:pass:1001::upload");

	@DynamicPropertySource
	static void registerProperties(DynamicPropertyRegistry registry) throws IOException {
		SFTP_SERVER.start();
		registry.add("integration.sftp.port", () -> SFTP_SERVER.getMappedPort(22));
		var key = readString(getFile("classpath:keys/ssh_host_rsa_key.pub").toPath());
		registry.add("integration.sftp.knownHosts", () -> String.format("[127.0.0.1]:%s %s", SFTP_SERVER.getMappedPort(22), key));
	}

	@AfterAll
	static void tearDown() throws IOException {
		SFTP_SERVER.stop();
		FileSystemUtils.deleteRecursively(SFTP_DIR);
		Files.delete(TEST_FILE);
	}


	@Test
	void testUpload() {
		var resource = new FileSystemResource(TEST_FILE);

		assertThat(SFTP_DIR).isEmptyDirectory();

		gateway.sendToSftp(resource);

		assertThat(SFTP_DIR).isDirectoryContaining(path -> path.getFileName().toString().equals(resource.getFilename()));
	}
}