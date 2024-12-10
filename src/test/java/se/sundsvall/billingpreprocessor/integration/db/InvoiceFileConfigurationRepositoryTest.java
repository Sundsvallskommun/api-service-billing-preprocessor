package se.sundsvall.billingpreprocessor.integration.db;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceFileConfigurationEntity;

@SpringBootTest
@ActiveProfiles("junit")
@Transactional
@Sql(scripts = {
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata-junit.sql"
})
class InvoiceFileConfigurationRepositoryTest {

	@Autowired
	private InvoiceFileConfigurationRepository repository;

	@Test
	void createSuccessful() {

		final var config = InvoiceFileConfigurationEntity.create()
			.withType("type")
			.withCategoryTag("categoryTag")
			.withFileNamePattern("fileNamePattern")
			.withCreatorName("creatorName")
			.withEncoding("encoding");

		final var result = repository.saveAndFlush(config);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isPositive();
		assertThat(result.getType()).isEqualTo("type");
		assertThat(result.getCategoryTag()).isEqualTo("categoryTag");
		assertThat(result.getFileNamePattern()).isEqualTo("fileNamePattern");
		assertThat(result.getCreatorName()).isEqualTo("creatorName");
		assertThat(result.getEncoding()).isEqualTo("encoding");
	}

	@ParameterizedTest
	@MethodSource("createFailsDueToNotNullConstraintArgumentProvider")
	void createFailsDueToNotNullConstraint(InvoiceFileConfigurationEntity config, String expectedErrorMessage) {
		final var exception = assertThrows(DataIntegrityViolationException.class, () -> repository.save(config));

		assertThat(exception.getMessage()).contains(expectedErrorMessage);
	}

	private static Stream<Arguments> createFailsDueToNotNullConstraintArgumentProvider() {
		return Stream.of(
			Arguments.of(InvoiceFileConfigurationEntity.create()
				.withCategoryTag("categoryTag")
				.withFileNamePattern("fileNamePattern")
				.withCreatorName("creatorName")
				.withEncoding("encoding"),
				"Column 'type' cannot be null"),
			Arguments.of(InvoiceFileConfigurationEntity.create()
				.withType("type")
				.withFileNamePattern("fileNamePattern")
				.withCreatorName("creatorName")
				.withEncoding("encoding"),
				"Column 'category_tag' cannot be null"),
			Arguments.of(InvoiceFileConfigurationEntity.create()
				.withType("type")
				.withCategoryTag("categoryTag")
				.withCreatorName("creatorName")
				.withEncoding("encoding"),
				"Column 'file_name_pattern' cannot be null"),
			Arguments.of(InvoiceFileConfigurationEntity.create()
				.withType("type")
				.withCategoryTag("categoryTag")
				.withFileNamePattern("fileNamePattern")
				.withEncoding("encoding"),
				"Column 'creator_name' cannot be null"),
			Arguments.of(InvoiceFileConfigurationEntity.create()
				.withType("type")
				.withCategoryTag("categoryTag")
				.withFileNamePattern("fileNamePattern")
				.withCreatorName("creatorName"),
				"Column 'encoding' cannot be null"));
	}

	@Test
	void createFailsDueToUniqueConstraint() {

		final var config1 = InvoiceFileConfigurationEntity.create()
			.withType("type")
			.withCategoryTag("categoryTag")
			.withFileNamePattern("fileNamePattern")
			.withCreatorName("creatorName")
			.withEncoding("encoding");

		final var config2 = InvoiceFileConfigurationEntity.create()
			.withType("type")
			.withCategoryTag("categoryTag")
			.withFileNamePattern("fileNamePattern")
			.withCreatorName("creatorName")
			.withEncoding("encoding");

		repository.save(config1);

		final var exception = assertThrows(DataIntegrityViolationException.class, () -> repository.save(config2));

		assertThat(exception.getMessage()).contains("Duplicate entry 'type-categoryTag' for key 'uq_type_category_tag");
	}

	@Test
	void findByTypeAndCategoryTag() {

		final var result = repository.findByTypeAndCategoryTag("type1", "category_tag1").get();

		assertThat(result).isNotNull();
		assertThat(result.getFileNamePattern()).isEqualTo("file_name_pattern1");
	}

	@Test
	void existsByTypeAndCategoryTag() {
		assertThat(repository.existsByTypeAndCategoryTag("type3", "category_tag3")).isTrue();
	}

	@Test
	void findByCreatorName() {
		final var result = repository.findByCreatorName("creator_name_3").get();

		assertThat(result).isNotNull();
		assertThat(result.getFileNamePattern()).isEqualTo("file_name_pattern3");
	}
}
