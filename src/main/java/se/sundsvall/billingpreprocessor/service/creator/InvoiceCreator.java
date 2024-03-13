package se.sundsvall.billingpreprocessor.service.creator;

import java.io.IOException;
import java.util.List;

import se.sundsvall.billingpreprocessor.integration.db.model.BillingRecordEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.enums.Type;

public interface InvoiceCreator {
	byte[] createFileHeader() throws IOException;

	byte[] createInvoiceData(BillingRecordEntity billingRecord) throws IOException;

	boolean canHandle(Type type);

	List<String> handledCategories();
}
