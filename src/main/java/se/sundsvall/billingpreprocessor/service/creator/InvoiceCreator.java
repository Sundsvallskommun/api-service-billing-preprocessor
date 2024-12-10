package se.sundsvall.billingpreprocessor.service.creator;

import java.io.IOException;
import se.sundsvall.billingpreprocessor.integration.db.model.BillingRecordEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.enums.Type;

public interface InvoiceCreator {
	static final String CONFIGURATION_NOT_PRESENT = "No configuration present for invoice creator with name %s";

	/**
	 * Method returning the type that the creator can handle
	 * 
	 * @return type that the creator can handle
	 */
	Type getProcessableType();

	/**
	 * Method returning the category that the creator can handle
	 * 
	 * @return the category that the creator can handle
	 */
	String getProcessableCategory();

	/**
	 * Method for creating a file header
	 * 
	 * @return             bytearray representing the file header
	 * @throws IOException if byte array output stream can not be closed
	 */
	byte[] createFileHeader() throws IOException;

	/**
	 * Method for creating an invoice data section
	 * 
	 * @param  billingRecord containing the billing record to produce a invoice data section for
	 * @return               bytearray representing the invoice data section
	 * @throws IOException   if byte array output stream can not be closed
	 */
	byte[] createInvoiceData(BillingRecordEntity billingRecord) throws IOException;
}
