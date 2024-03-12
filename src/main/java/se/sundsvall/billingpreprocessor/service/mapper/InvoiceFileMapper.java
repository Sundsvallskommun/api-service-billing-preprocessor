package se.sundsvall.billingpreprocessor.service.mapper;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;

import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceFileEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.enums.InvoiceFileStatus;

public final class InvoiceFileMapper {

	private InvoiceFileMapper() {}

	public static InvoiceFileEntity toInvoiceFileEntity(String name, String type, byte[] bytes) {
		return InvoiceFileEntity.create()
			.withContent(new String(bytes, StandardCharsets.UTF_8))
			.withCreated(OffsetDateTime.now())
			.withName(name)
			.withStatus(InvoiceFileStatus.GENERATED)
			.withType(type);
	}
}
