package se.sundsvall.billingpreprocessor.service.util;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.ObjectUtils.allNotNull;

import se.sundsvall.billingpreprocessor.api.model.InvoiceRow;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceRowEntity;

public final class CalculationUtil {

	private CalculationUtil() {}

	public static Float calculateTotalInvoiceRowAmount(final InvoiceRow invoiceRow) {
		return ofNullable(invoiceRow)
			.filter(row -> allNotNull(row.getCostPerUnit(), row.getQuantity()))
			.map(row -> row.getCostPerUnit() * row.getQuantity())
			.orElse(null);
	}

	public static Float calculateTotalInvoiceAmount(final InvoiceEntity entity) {
		if (isNull(entity)) {
			return 0f;
		}

		return ((Double) ofNullable(entity.getInvoiceRows()).orElse(emptyList()).stream()
				.filter(row -> nonNull(row.getTotalAmount()))
				.mapToDouble(InvoiceRowEntity::getTotalAmount)
				.sum())
			.floatValue();
	}
}
