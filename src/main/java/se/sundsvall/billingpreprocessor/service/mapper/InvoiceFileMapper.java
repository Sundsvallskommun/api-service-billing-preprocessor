package se.sundsvall.billingpreprocessor.service.mapper;

import static java.util.Optional.ofNullable;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.InvoiceFileStatus.GENERATED;

import java.nio.charset.StandardCharsets;

import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceFileEntity;

public final class InvoiceFileMapper {

	private InvoiceFileMapper() {}

	public static InvoiceFileEntity toInvoiceFileEntity(String name, String type, byte[] content) {
		final var entity = InvoiceFileEntity.create()
			.withStatus(GENERATED);

		ofNullable(name).ifPresent(entity::setName);
		ofNullable(type).ifPresent(entity::setType);
		ofNullable(content).ifPresent(b -> entity.setContent(new String(b, StandardCharsets.UTF_8)));

		return entity;
	}
}
