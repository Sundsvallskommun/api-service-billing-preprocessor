package se.sundsvall.billingpreprocessor.integration.db;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
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
			.withFileNamePattern("fileNamePattern");

		final var result = repository.saveAndFlush(config);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isPositive();
		assertThat(result.getType()).isEqualTo("type");
		assertThat(result.getCategoryTag()).isEqualTo("categoryTag");
		assertThat(result.getFileNamePattern()).isEqualTo("fileNamePattern");
	}

	@Test
	void createFailsDueToUniqueConstraint() {

		final var config1 = InvoiceFileConfigurationEntity.create()
			.withType("type")
			.withCategoryTag("categoryTag")
			.withFileNamePattern("fileNamePattern1");

		final var config2 = InvoiceFileConfigurationEntity.create()
			.withType("type")
			.withCategoryTag("categoryTag")
			.withFileNamePattern("fileNamePattern2");

		repository.save(config1);

		final var exception = assertThrows(DataIntegrityViolationException.class, () -> repository.save(config2));

		assertThat(exception.getMessage()).contains("Duplicate entry 'type-categoryTag' for key 'uq_type_category_tag");
	}

	@Test
	void findByTypeAndCategoryTag() {
		System.out.println(repository.findAll());

		final var result = repository.findByTypeAndCategoryTag("type1", "category_tag1").get();

		assertThat(result).isNotNull();
		assertThat(result.getFileNamePattern()).isEqualTo("file_name_pattern1");
	}

	@Test
	void existsByTypeAndCategoryTag() {
		assertThat(repository.existsByTypeAndCategoryTag("type3", "category_tag3")).isTrue();
	}
}
