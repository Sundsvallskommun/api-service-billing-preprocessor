package se.sundsvall.billingpreprocessor.service.util;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.billingpreprocessor.service.util.CalculationUtil.calculateTotalInvoiceAmount;
import static se.sundsvall.billingpreprocessor.service.util.CalculationUtil.calculateTotalInvoiceRowAmount;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.sundsvall.billingpreprocessor.api.model.InvoiceRow;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceRowEntity;

class CalculationUtilTest {
	private BigDecimal costPerUnit;
	private BigDecimal quantity;
	private BigDecimal rowAmount;

	@BeforeEach
	void initialize() {
		costPerUnit = BigDecimal.valueOf(new Random().nextDouble());
		quantity = BigDecimal.valueOf(new Random().nextInt(1, 100));
		rowAmount = BigDecimal.valueOf(new Random().nextInt(1, 100));
	}

	@Test
	void testCalculateTotalInvoiceAmountWithNull() {
		assertThat(calculateTotalInvoiceAmount(null)).isZero();
	}

	@Test
	void testCalculateTotalInvoiceAmountWithEmptyInvoice() {
		assertThat(calculateTotalInvoiceAmount(InvoiceEntity.create())).isZero();
	}

	@Test
	void testCalculateTotalInvoiceAmountWithInvoiceWithEmptyInvoiceRowList() {
		assertThat(calculateTotalInvoiceAmount(InvoiceEntity.create().withInvoiceRows(emptyList()))).isZero();
	}

	@Test
	void testCalculateTotalInvoiceAmountWithInvoiceWithRowList() {
		assertThat(calculateTotalInvoiceAmount(InvoiceEntity.create().withInvoiceRows(createRows(false)))).isEqualTo(costPerUnit.multiply(quantity).multiply(rowAmount));
	}

	@Test
	void testCalculateTotalInvoiceAmountWithInvoiceWithRowListContainingNullSum() {
		final var rows = createRows(true);
		final var totalAmount = rows.stream()
			.filter(row -> row.getTotalAmount() != null)
			.map(InvoiceRowEntity::getTotalAmount)
			.reduce(BigDecimal.ZERO, BigDecimal::add);

		assertThat(calculateTotalInvoiceAmount(InvoiceEntity.create().withInvoiceRows(createRows(true)))).isEqualTo(totalAmount);
	}

	@Test
	void testCalculateTotalInvoiceRowAmountWithNull() {
		assertThat(calculateTotalInvoiceRowAmount(null)).isNull();
	}

	@Test
	void testCalculateTotalInvoiceRowAmountWithEmptyRow() {
		assertThat(calculateTotalInvoiceRowAmount(InvoiceRow.create())).isNull();
	}

	@Test
	void testCalculateTotalInvoiceRowAmountWithEmptyAmount() {
		assertThat(calculateTotalInvoiceRowAmount(InvoiceRow.create().withQuantity(quantity))).isNull();
	}

	@Test
	void testCalculateTotalInvoiceRowAmountWithEmptyQuantity() {
		assertThat(calculateTotalInvoiceRowAmount(InvoiceRow.create().withCostPerUnit(costPerUnit))).isNull();
	}

	@Test
	void testCalculateTotalInvoiceRowAmount() {
		assertThat(calculateTotalInvoiceRowAmount(InvoiceRow.create().withCostPerUnit(costPerUnit).withQuantity(quantity))).isEqualTo(costPerUnit.multiply(quantity));
	}

	private List<InvoiceRowEntity> createRows(boolean noTotalAmount) {
		final var rows = new ArrayList<InvoiceRowEntity>();

		for (var i = 0; i < rowAmount.intValue(); i++) {
			rows.add(InvoiceRowEntity.create().withTotalAmount(noTotalAmount && i % 2 == 0 ? null : costPerUnit.multiply(quantity)));
		}

		return rows;
	}
}
