package se.sundsvall.billingpreprocessor.service.creator;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static se.sundsvall.billingpreprocessor.Constants.EMPTY_ARRAY;
import static se.sundsvall.billingpreprocessor.Constants.EXTERNAL_INVOICE_TYPE;
import static se.sundsvall.billingpreprocessor.Constants.GENERATING_SYSTEM;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.Type.EXTERNAL;
import static se.sundsvall.billingpreprocessor.service.creator.config.InvoiceCreatorConfig.EXTERNAL_INVOICE_BUILDER;
import static se.sundsvall.billingpreprocessor.service.mapper.ExternalInvoiceMapper.toCustomer;
import static se.sundsvall.billingpreprocessor.service.mapper.ExternalInvoiceMapper.toFileHeader;
import static se.sundsvall.billingpreprocessor.service.mapper.ExternalInvoiceMapper.toInvoiceAccountingRow;
import static se.sundsvall.billingpreprocessor.service.mapper.ExternalInvoiceMapper.toInvoiceDescriptionRows;
import static se.sundsvall.billingpreprocessor.service.mapper.ExternalInvoiceMapper.toInvoiceFooter;
import static se.sundsvall.billingpreprocessor.service.mapper.ExternalInvoiceMapper.toInvoiceHeader;
import static se.sundsvall.billingpreprocessor.service.mapper.ExternalInvoiceMapper.toInvoiceRow;
import static se.sundsvall.billingpreprocessor.service.util.ProblemUtil.createInternalServerErrorProblem;
import static se.sundsvall.billingpreprocessor.service.util.StringUtil.formatLegalId;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import org.beanio.BeanWriter;
import org.beanio.StreamFactory;
import org.beanio.builder.StreamBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import se.sundsvall.billingpreprocessor.integration.db.model.BillingRecordEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceRowEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.enums.Type;

@Component
public class ExternalInvoiceCreator implements InvoiceCreator {
	private static final List<String> VALID_CAGTEGORIES = List.of("ISYCASE");

	private final StreamFactory factory;
	private final LegalIdProvider legalIdProvider;

	public ExternalInvoiceCreator(@Qualifier(EXTERNAL_INVOICE_BUILDER) StreamBuilder builder, LegalIdProvider legalIdProvider) {
		this.factory = StreamFactory.newInstance();
		this.factory.define(builder);
		this.legalIdProvider = legalIdProvider;
	}

	/**
	 * Method returning the types that the creator can handle
	 * 
	 * @return list of types that the creator can handle
	 */
	public List<Type> getProcessableTypes() {
		return List.of(EXTERNAL);
	}

	/**
	 * Method returning the categories that the creator can handle
	 * 
	 * @return list of categories that the creator can handle
	 */
	public List<String> getProcessableCategories() {
		return VALID_CAGTEGORIES;
	}

	/**
	 * Method creates a file header according to the specification for external invoices
	 * 
	 * @return bytearray representing the file header
	 * @throws IOException if byte array output stream can not be closed
	 */
	@Override
	public byte[] createFileHeader() throws IOException {
		try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			BeanWriter invoiceWriter = factory.createWriter(EXTERNAL_INVOICE_BUILDER, new OutputStreamWriter(byteArrayOutputStream))) {
			invoiceWriter.write(toFileHeader(GENERATING_SYSTEM, EXTERNAL_INVOICE_TYPE));
			invoiceWriter.flush();
			return byteArrayOutputStream.toByteArray();
		}
	}

	/**
	 * Method creates a invoice data section according to the specification for external invoices
	 * 
	 * @param billingRecord containing the billing record to produce a invoice data section for
	 * @return bytearray representing the invoice data section
	 * @throws IOException if byte array output stream can not be closed
	 */
	@Override
	public byte[] createInvoiceData(BillingRecordEntity billingRecord) throws IOException {
		if (isNull(billingRecord)) {
			return EMPTY_ARRAY;
		}

		try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			BeanWriter invoiceWriter = factory.createWriter(EXTERNAL_INVOICE_BUILDER, new OutputStreamWriter(byteArrayOutputStream))) {
			processInvoice(invoiceWriter, billingRecord);
			invoiceWriter.flush();
			return byteArrayOutputStream.toByteArray();
		}
	}

	private void processInvoice(BeanWriter invoiceWriter, BillingRecordEntity billingRecord) {
		final var recipientLegalId = extractLegalId(billingRecord);

		invoiceWriter.write(toCustomer(recipientLegalId, billingRecord));
		invoiceWriter.write(toInvoiceHeader(recipientLegalId, billingRecord));

		ofNullable(billingRecord.getInvoice())
			.orElseThrow(createInternalServerErrorProblem("Invoice is not present"))
			.getInvoiceRows()
			.forEach(row -> processInvoiceRow(invoiceWriter, recipientLegalId, row));

		invoiceWriter.write(toInvoiceFooter(billingRecord));
	}

	private void processInvoiceRow(BeanWriter invoiceWriter, String recipientLegalId, InvoiceRowEntity invoiceRow) {
		invoiceWriter.write(toInvoiceRow(recipientLegalId, invoiceRow));
		toInvoiceDescriptionRows(recipientLegalId, invoiceRow).forEach(invoiceWriter::write);
		invoiceWriter.write(toInvoiceAccountingRow(invoiceRow));
	}

	private String extractLegalId(BillingRecordEntity billingRecord) {
		final var legalId = ofNullable(billingRecord.getRecipient().getLegalId())
			.orElseGet(() -> legalIdProvider.translateToLegalId(billingRecord.getRecipient().getPartyId()));

		return formatLegalId(legalId);
	}
}
