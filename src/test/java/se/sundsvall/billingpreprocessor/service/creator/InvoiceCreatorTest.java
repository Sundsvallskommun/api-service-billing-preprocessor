package se.sundsvall.billingpreprocessor.service.creator;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.billingpreprocessor.Constants.EMPTY_ARRAY;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.billingpreprocessor.integration.db.model.BillingRecordEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.enums.Type;

class InvoiceCreatorTest {

	private static class TestInvoiceCreator implements InvoiceCreator {

		@Override
		public Type getProcessableType() {
			return Type.INTERNAL;
		}

		@Override
		public String getProcessableCategory() {
			return "TEST";
		}

		@Override
		public byte[] createInvoiceData(BillingRecordEntity billingRecord) throws IOException {
			return EMPTY_ARRAY;
		}
	}

	@Test
	void defaultCreateFileHeader() throws IOException {
		final var creator = new TestInvoiceCreator();
		final var result = creator.createFileHeader();
		assertThat(result).isEqualTo(EMPTY_ARRAY);
	}

	@Test
	void defaultCreateFileFooter() throws IOException {
		final var creator = new TestInvoiceCreator();
		final var result = creator.createFileFooter(List.of());
		assertThat(result).isEqualTo(EMPTY_ARRAY);
	}
}
