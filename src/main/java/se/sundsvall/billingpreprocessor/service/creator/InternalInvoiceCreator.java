package se.sundsvall.billingpreprocessor.service.creator;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static se.sundsvall.billingpreprocessor.Constants.EMPTY_ARRAY;
import static se.sundsvall.billingpreprocessor.service.creator.config.InvoiceCreatorConfig.INTERNAL_INVOICE_BUILDER;
import static se.sundsvall.billingpreprocessor.service.mapper.InternalInvoiceMapper.toFileHeader;
import static se.sundsvall.billingpreprocessor.service.mapper.InternalInvoiceMapper.toInvoiceAccountingRow;
import static se.sundsvall.billingpreprocessor.service.mapper.InternalInvoiceMapper.toInvoiceDescriptionRow;
import static se.sundsvall.billingpreprocessor.service.mapper.InternalInvoiceMapper.toInvoiceRowDescriptionRows;
import static se.sundsvall.billingpreprocessor.service.mapper.InternalInvoiceMapper.toInvoiceFooter;
import static se.sundsvall.billingpreprocessor.service.mapper.InternalInvoiceMapper.toInvoiceHeader;
import static se.sundsvall.billingpreprocessor.service.mapper.InternalInvoiceMapper.toInvoiceRow;
import static se.sundsvall.billingpreprocessor.service.util.ProblemUtil.createInternalServerErrorProblem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.beanio.BeanWriter;
import org.beanio.StreamFactory;
import org.beanio.builder.StreamBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import se.sundsvall.billingpreprocessor.integration.db.InvoiceFileConfigurationRepository;
import se.sundsvall.billingpreprocessor.integration.db.model.BillingRecordEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceFileConfigurationEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceRowEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.enums.Type;

@Component
public class InternalInvoiceCreator implements InvoiceCreator {
	private final InvoiceFileConfigurationRepository configurationRepository;
	private final StreamFactory factory;

	public InternalInvoiceCreator(@Qualifier(INTERNAL_INVOICE_BUILDER) StreamBuilder builder, InvoiceFileConfigurationRepository configurationRepository) {
		this.factory = StreamFactory.newInstance();
		this.factory.define(builder);
		this.configurationRepository = configurationRepository;
	}

	private InvoiceFileConfigurationEntity getConfiguration() {
		return configurationRepository.findByCreatorName(this.getClass().getSimpleName())
			.orElseThrow(createInternalServerErrorProblem(CONFIGURATION_NOT_PRESENT.formatted(this.getClass().getSimpleName())));
	}

	/**
	 * Method returning the type that the creator can handle
	 *
	 * @return the type that the creator can handle
	 */
	@Override
	public Type getProcessableType() {
		return Type.valueOf(getConfiguration().getType());
	}

	/**
	 * Method returning the category that the creator can handle
	 *
	 * @return the category that the creator can handle
	 */
	@Override
	public String getProcessableCategory() {
		return getConfiguration().getCategoryTag();
	}

	/**
	 * Method creates a file header according to the specification for internal invoices
	 *
	 * @return             bytearray representing the file header
	 * @throws IOException if byte array output stream can not be closed
	 */
	@Override
	public byte[] createFileHeader() throws IOException {
		final var encoding = Charset.forName(getConfiguration().getEncoding());
		try (var byteArrayOutputStream = new ByteArrayOutputStream();
			var invoiceWriter = factory.createWriter(INTERNAL_INVOICE_BUILDER, new OutputStreamWriter(byteArrayOutputStream, encoding))) {
			invoiceWriter.write(toFileHeader());
			invoiceWriter.flush();
			return byteArrayOutputStream.toByteArray();
		}
	}

	/**
	 * Method creates a invoice data section according to the specification for internal invoices
	 *
	 * @param  billingRecord containing the billing record to produce a invoice data section for
	 * @return               bytearray representing the invoice data section
	 * @throws IOException   if byte array output stream can not be closed
	 */
	@Override
	public byte[] createInvoiceData(BillingRecordEntity billingRecord) throws IOException {
		if (isNull(billingRecord)) {
			return EMPTY_ARRAY;
		}

		final var encoding = Charset.forName(getConfiguration().getEncoding());
		try (var byteArrayOutputStream = new ByteArrayOutputStream();
			var invoiceWriter = factory.createWriter(INTERNAL_INVOICE_BUILDER, new OutputStreamWriter(byteArrayOutputStream, encoding))) {
			processInvoice(invoiceWriter, billingRecord);
			invoiceWriter.flush();
			return byteArrayOutputStream.toByteArray();
		}
	}

	private void processInvoice(BeanWriter invoiceWriter, BillingRecordEntity billingRecord) {
		invoiceWriter.write(toInvoiceHeader(billingRecord));
		invoiceWriter.write(toInvoiceDescriptionRow(billingRecord));

		ofNullable(billingRecord.getInvoice())
			.orElseThrow(createInternalServerErrorProblem("Invoice is not present"))
			.getInvoiceRows()
			.forEach(row -> processInvoiceRow(invoiceWriter, row));

		invoiceWriter.write(toInvoiceFooter(billingRecord));
	}

	private void processInvoiceRow(BeanWriter invoiceWriter, InvoiceRowEntity invoiceRow) {
		invoiceWriter.write(toInvoiceRow(invoiceRow));
		toInvoiceRowDescriptionRows(invoiceRow).forEach(invoiceWriter::write);
		invoiceWriter.write(toInvoiceAccountingRow(invoiceRow));
	}
}
