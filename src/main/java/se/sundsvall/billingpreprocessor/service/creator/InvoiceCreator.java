package se.sundsvall.billingpreprocessor.service.creator;

import java.io.IOException;

import se.sundsvall.billingpreprocessor.integration.db.model.BillingRecordEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.enums.Type;

public interface InvoiceCreator {
	/**
	 * Method for determining if creator can handle requested type
	 */
	boolean canHandle(Type type);

	/**
	 * Method for determining if creator can handle requested category
	 */
	boolean canHandle(String category);

	/**
	 * Method for creating a file header
	 * 
	 * @return bytearray representing the file header
	 * @throws IOException if byte array output stream can not be closed
	 */
	byte[] createFileHeader() throws IOException;

	/**
	 * Method for creating an invoice data section
	 * 
	 * @param billingRecord containing the billing record to produce a invoice data section for
	 * @return bytearray representing the invoice data section
	 * @throws IOException if byte array output stream can not be closed
	 */
	byte[] createInvoiceData(BillingRecordEntity billingRecord) throws IOException;
}
