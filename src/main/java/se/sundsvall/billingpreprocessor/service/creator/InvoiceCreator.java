package se.sundsvall.billingpreprocessor.service.creator;

import java.io.IOException;
import java.util.List;

import se.sundsvall.billingpreprocessor.integration.db.model.BillingRecordEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.enums.Type;

public interface InvoiceCreator {

	/**
	 * Method returning the types that the creator can handle
	 * 
	 * @return list of types that the creator can handle
	 */
	List<Type> getProcessableTypes();

	/**
	 * Method returning the categories that the creator can handle
	 * 
	 * @return list of categories that the creator can handle
	 */
	List<String> getProcessableCategories();

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
