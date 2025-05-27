package se.sundsvall.billingpreprocessor.service.creator;

import static se.sundsvall.billingpreprocessor.Constants.EMPTY_ARRAY;

import java.io.IOException;
import java.util.List;
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
	 * @return             byte array representing the file header
	 * @throws IOException if the byte array output stream cannot be closed
	 */
	default byte[] createFileHeader() throws IOException {
		return EMPTY_ARRAY;
	}

	/**
	 * Method for creating a file footer
	 *
	 * @param  billingRecords containing the billing record to produce a file footer section for
	 * @return                byte array representing the file footer
	 * @throws IOException    if the byte array output stream cannot be closed
	 */
	default byte[] createFileFooter(List<BillingRecordEntity> billingRecords) throws IOException {
		return EMPTY_ARRAY;
	}

	/**
	 * Method for creating an invoice data section
	 * 
	 * @param  billingRecord containing the billing record to produce a invoice data section for
	 * @return               bytearray representing the invoice data section
	 * @throws IOException   if byte array output stream can not be closed
	 */
	byte[] createInvoiceData(BillingRecordEntity billingRecord) throws IOException;
}
