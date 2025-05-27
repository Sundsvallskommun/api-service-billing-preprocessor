package se.sundsvall.billingpreprocessor.service.creator;

import static java.util.Optional.ofNullable;
import static se.sundsvall.billingpreprocessor.Constants.EMPTY_ARRAY;
import static se.sundsvall.billingpreprocessor.service.creator.config.InvoiceCreatorConfig.EXTERNAL_INVOICE_BUILDER;
import static se.sundsvall.billingpreprocessor.service.mapper.ExternalInvoiceMapper.toCustomer;
import static se.sundsvall.billingpreprocessor.service.mapper.ExternalInvoiceMapper.toFileFooter;
import static se.sundsvall.billingpreprocessor.service.util.ProblemUtil.createInternalServerErrorProblem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.List;
import org.beanio.BeanWriter;
import org.beanio.builder.StreamBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import se.sundsvall.billingpreprocessor.integration.db.InvoiceFileConfigurationRepository;
import se.sundsvall.billingpreprocessor.integration.db.model.BillingRecordEntity;
import se.sundsvall.billingpreprocessor.service.mapper.ExternalInvoiceMapper;

@Component
public class ExternalSalaryAndPensionInvoiceCreator extends ExternalInvoiceCreator {

	public ExternalSalaryAndPensionInvoiceCreator(@Qualifier(EXTERNAL_INVOICE_BUILDER) StreamBuilder builder, LegalIdProvider legalIdProvider, InvoiceFileConfigurationRepository configurationRepository) {
		super(builder, legalIdProvider, configurationRepository);
	}

	/**
	 * Creates a file footer according to the specification for external salary and pension invoices
	 *
	 * @param  billingRecords containing the billing record to produce a file footer section for
	 * @return                byte array representing the file footer
	 * @throws IOException    if the byte array output stream cannot be closed
	 */
	@Override
	public byte[] createFileFooter(List<BillingRecordEntity> billingRecords) throws IOException {
		if (billingRecords.isEmpty()) {
			return EMPTY_ARRAY;
		}

		final var encoding = Charset.forName(getConfiguration().getEncoding());
		try (var byteArrayOutputStream = new ByteArrayOutputStream();
			var invoiceWriter = factory.createWriter(EXTERNAL_INVOICE_BUILDER, new OutputStreamWriter(byteArrayOutputStream, encoding))) {
			invoiceWriter.write(toFileFooter(billingRecords));
			invoiceWriter.flush();
			return byteArrayOutputStream.toByteArray();
		}
	}

	@Override
	protected void processInvoice(BeanWriter invoiceWriter, BillingRecordEntity billingRecord) {
		final var recipientLegalId = extractLegalId(billingRecord);

		invoiceWriter.write(toCustomer(recipientLegalId, billingRecord));
		invoiceWriter.write(ExternalInvoiceMapper.toInvoiceHeader(recipientLegalId, billingRecord));

		ofNullable(billingRecord.getInvoice())
			.orElseThrow(createInternalServerErrorProblem("Invoice is not present"))
			.getInvoiceRows()
			.forEach(row -> processInvoiceRow(invoiceWriter, recipientLegalId, row));
	}
}
