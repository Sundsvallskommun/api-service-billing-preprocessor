package se.sundsvall.billingpreprocessor.service.util;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.billingpreprocessor.service.util.CalculationUtil.calculateTotalInvoiceAmount;
import static se.sundsvall.billingpreprocessor.service.util.CalculationUtil.calculateTotalInvoiceRowAmount;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.billingpreprocessor.api.model.InvoiceRow;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceRowEntity;

class CalculationUtilTest {
	private static final float COST_PER_UNIT = 123f;
	private static final float QUANTITY = 45f;
	private static final int ROW_AMOUNT = 10;

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
		assertThat(calculateTotalInvoiceAmount(InvoiceEntity.create().withInvoiceRows(createRows(false)))).isEqualTo(COST_PER_UNIT * QUANTITY * ROW_AMOUNT);
	}

	@Test
	void testCalculateTotalInvoiceAmountWithInvoiceWithRowListContainingNullSum() {
		assertThat(calculateTotalInvoiceAmount(InvoiceEntity.create().withInvoiceRows(createRows(true)))).isEqualTo(COST_PER_UNIT * QUANTITY * ROW_AMOUNT / 2);
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
		assertThat(calculateTotalInvoiceRowAmount(InvoiceRow.create().withQuantity(QUANTITY))).isNull();
	}

	@Test
	void testCalculateTotalInvoiceRowAmountWithEmptyQuantity() {
		assertThat(calculateTotalInvoiceRowAmount(InvoiceRow.create().withCostPerUnit(COST_PER_UNIT))).isNull();
	}

	@Test
	void testCalculateTotalInvoiceRowAmount() {
		assertThat(calculateTotalInvoiceRowAmount(InvoiceRow.create().withCostPerUnit(COST_PER_UNIT).withQuantity(QUANTITY))).isEqualTo(COST_PER_UNIT * QUANTITY);
	}

	private static List<InvoiceRowEntity> createRows(boolean noTotalAmount) {
		final var rows = new ArrayList<InvoiceRowEntity>();

		for (int i = 0; i < ROW_AMOUNT; i++) {
			rows.add(InvoiceRowEntity.create().withTotalAmount(noTotalAmount && i % 2 == 0 ? null : COST_PER_UNIT * QUANTITY));
		}

		return rows;
	}
}
