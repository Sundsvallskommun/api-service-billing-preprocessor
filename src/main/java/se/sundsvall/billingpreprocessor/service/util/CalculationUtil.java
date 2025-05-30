package se.sundsvall.billingpreprocessor.service.util;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.ObjectUtils.allNotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import se.sundsvall.billingpreprocessor.api.model.InvoiceRow;
import se.sundsvall.billingpreprocessor.integration.db.model.BillingRecordEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceRowEntity;

public final class CalculationUtil {

	private CalculationUtil() {}

	public static BigDecimal calculateTotalInvoiceRowAmount(final InvoiceRow invoiceRow) {
		return ofNullable(invoiceRow)
			.filter(row -> allNotNull(row.getCostPerUnit(), row.getQuantity()))
			.map(row -> row.getCostPerUnit().multiply(row.getQuantity()))
			.orElse(null);
	}

	public static BigDecimal calculateTotalInvoiceAmount(final InvoiceEntity entity) {
		if (isNull(entity)) {
			return BigDecimal.ZERO;
		}

		return ofNullable(entity.getInvoiceRows()).orElse(emptyList()).stream()
			.filter(row -> nonNull(row.getTotalAmount()))
			.map(InvoiceRowEntity::getTotalAmount)
			.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public static BigDecimal calculateTotalAmount(final List<BillingRecordEntity> billingRecords) {
		if (isNull(billingRecords)) {
			return BigDecimal.ZERO;
		}

		return billingRecords.stream()
			.map(BillingRecordEntity::getInvoice)
			.filter(Objects::nonNull)
			.map(InvoiceEntity::getInvoiceRows)
			.filter(Objects::nonNull)
			.flatMap(List::stream)
			.map(InvoiceRowEntity::getTotalAmount)
			.filter(Objects::nonNull)
			.reduce(BigDecimal::add)
			.orElse(BigDecimal.ZERO);
	}
}
