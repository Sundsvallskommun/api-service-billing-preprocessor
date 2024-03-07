package se.sundsvall.billingpreprocessor.integration.db;

import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.InvoiceFileStatus.GENERATED;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceFileEntity;

/**
 * Invoice file repository tests.
 *
 * @see /src/test/resources/db/testdata-junit.sql for data setup.
 */
@SpringBootTest
@ActiveProfiles("junit")
@Transactional
@Sql(scripts = {
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata-junit.sql"
})
class InvoiceFileRepositoryTest {

	@Autowired
	private InvoiceFileRepository repository;

	@Test
	void createSuccessful() {

		// Arrange
		final var content = "content";
		final var invoiceFileEntity = InvoiceFileEntity.create()
			.withContent(content)
			.withName("file.txt");

		// Act
		final var result = repository.save(invoiceFileEntity);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getId()).isPositive();
		assertThat(result.getContent()).isEqualTo(content);
		assertThat(result.getCreated()).isCloseTo(now(), within(2, SECONDS));
		assertThat(result.getSent()).isNull();
		assertThat(result.getStatus()).isEqualTo(GENERATED);
	}

	@Test
	void createFailsDueToUniqueConstraintViolation() {

		// Arrange
		final var invoiceFileEntity1 = InvoiceFileEntity.create()
			.withName("file.txt")
			.withStatus(GENERATED);

		final var invoiceFileEntity2 = InvoiceFileEntity.create()
			.withName("file.txt")
			.withStatus(GENERATED);

		// Save first entity.
		repository.save(invoiceFileEntity1);

		final var exception = assertThrows(DataIntegrityViolationException.class, () -> repository.save(invoiceFileEntity2));

		assertThat(exception.getMessage()).contains("Duplicate entry 'file.txt' for key 'uq_file_name'");
	}

	@Test
	void findByStatus() {

		final var parameter = GENERATED;

		// Act
		final var result = repository.findByStatus(parameter);

		// Assert
		assertThat(result)
			.isNotNull()
			.hasSize(2)
			.extracting(InvoiceFileEntity::getName, InvoiceFileEntity::getStatus)
			.containsExactly(
				tuple("INVOICE_FILE_1.txt", GENERATED),
				tuple("INVOICE_FILE_3.txt", GENERATED));
	}
}
