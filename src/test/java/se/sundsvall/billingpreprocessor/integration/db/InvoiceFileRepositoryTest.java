package se.sundsvall.billingpreprocessor.integration.db;

import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.InvoiceFileStatus.GENERATED;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.InvoiceFileStatus.SEND_FAILED;

import java.util.List;
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

	private static final String MUNICIPALITY_ID = "2281";

	@Autowired
	private InvoiceFileRepository repository;

	@Test
	void createSuccessful() {

		// Arrange
		final var content = "content";
		final var invoiceFileEntity = InvoiceFileEntity.create()
			.withContent(content)
			.withMunicipalityId(MUNICIPALITY_ID)
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
			.withMunicipalityId(MUNICIPALITY_ID)
			.withStatus(GENERATED);

		final var invoiceFileEntity2 = InvoiceFileEntity.create()
			.withName("file.txt")
			.withMunicipalityId(MUNICIPALITY_ID)
			.withStatus(GENERATED);

		// Save first entity.
		repository.save(invoiceFileEntity1);

		final var exception = assertThrows(DataIntegrityViolationException.class, () -> repository.save(invoiceFileEntity2));

		assertThat(exception.getMessage()).contains("Duplicate entry 'file.txt' for key 'uq_file_name'");
	}

	@Test
	void findByStatusAndMunicipalityId() {

		// Act
		final var result = repository.findByStatusAndMunicipalityId(GENERATED, MUNICIPALITY_ID);

		// Assert
		assertThat(result)
			.isNotNull()
			.hasSize(2)
			.extracting(InvoiceFileEntity::getName, InvoiceFileEntity::getStatus)
			.containsExactlyInAnyOrder(
				tuple("INVOICE_FILE_1.txt", GENERATED),
				tuple("INVOICE_FILE_3.txt", GENERATED));
	}

	@Test
	void findByStatusAndMunicipalityIdIn() {

		// Act
		final var result = repository.findByStatusInAndMunicipalityId(List.of(GENERATED, SEND_FAILED), MUNICIPALITY_ID);

		// Assert
		assertThat(result)
			.isNotNull()
			.hasSize(3)
			.extracting(InvoiceFileEntity::getName, InvoiceFileEntity::getStatus)
			.containsExactlyInAnyOrder(
				tuple("INVOICE_FILE_1.txt", GENERATED),
				tuple("INVOICE_FILE_3.txt", GENERATED),
				tuple("INVOICE_FILE_4.txt", SEND_FAILED));
	}

}
