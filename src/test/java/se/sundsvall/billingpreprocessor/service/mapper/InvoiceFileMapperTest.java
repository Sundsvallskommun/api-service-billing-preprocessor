package se.sundsvall.billingpreprocessor.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.InvoiceFileStatus.GENERATED;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class InvoiceFileMapperTest {

	@Test
	void toInvoiceFileEntity() {
		final var name = "name";
		final var type = "type";
		final var content = "content";
		final var charset = StandardCharsets.UTF_8;
		final var municipalityId = "municipalityId";

		final var entity = InvoiceFileMapper.toInvoiceFileEntity(name, type, content.getBytes(), charset, municipalityId);

		assertThat(entity).hasAllNullFieldsOrPropertiesExcept("id", "content", "name", "status", "type", "encoding", "municipalityId");
		assertThat(entity.getId()).isZero();
		assertThat(entity.getContent()).isEqualTo(content);
		assertThat(entity.getEncoding()).isEqualTo(charset.name());
		assertThat(entity.getName()).isEqualTo(name);
		assertThat(entity.getStatus()).isEqualTo(GENERATED);
		assertThat(entity.getType()).isEqualTo(type);
		assertThat(entity.getMunicipalityId()).isEqualTo(municipalityId);
	}

	@Test
	void toInvoiceFileEntityFromNull() {

		final var entity = InvoiceFileMapper.toInvoiceFileEntity(null, null, null, null, "municipalityId");

		assertThat(entity).hasAllNullFieldsOrPropertiesExcept("id", "status", "municipalityId");
		assertThat(entity.getId()).isZero();
		assertThat(entity.getStatus()).isEqualTo(GENERATED);
		assertThat(entity.getMunicipalityId()).isEqualTo("municipalityId");
	}
}
