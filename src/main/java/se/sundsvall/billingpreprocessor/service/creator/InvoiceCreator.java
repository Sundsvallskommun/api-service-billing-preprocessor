package se.sundsvall.billingpreprocessor.service.creator;

import java.io.IOException;

import se.sundsvall.billingpreprocessor.integration.db.model.BillingRecordEntity;

public interface InvoiceCreator {
	byte[] createFileHeader() throws IOException;

	byte[] createInvoiceData(BillingRecordEntity billingRecord) throws IOException;
}
