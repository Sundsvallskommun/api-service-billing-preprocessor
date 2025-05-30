package se.sundsvall.billingpreprocessor.service.creator;

import static java.util.Optional.ofNullable;
import static se.sundsvall.billingpreprocessor.Constants.EMPTY_ARRAY;
import static se.sundsvall.billingpreprocessor.service.creator.config.InvoiceCreatorConfig.INTERNAL_INVOICE_BUILDER;
import static se.sundsvall.billingpreprocessor.service.mapper.InternalInvoiceMapper.toFileFooter;
import static se.sundsvall.billingpreprocessor.service.mapper.InternalInvoiceMapper.toInvoiceDescriptionRow;
import static se.sundsvall.billingpreprocessor.service.mapper.InternalInvoiceMapper.toInvoiceHeader;
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

@Component
public class InternalSalaryAndPensionInvoiceCreator extends InternalInvoiceCreator {

	public InternalSalaryAndPensionInvoiceCreator(@Qualifier(INTERNAL_INVOICE_BUILDER) StreamBuilder builder, InvoiceFileConfigurationRepository configurationRepository) {
		super(builder, configurationRepository);
	}

	/**
	 * Creates a file footer according to the specification for internal salary and pension invoices
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
			var invoiceWriter = getFactory().createWriter(INTERNAL_INVOICE_BUILDER, new OutputStreamWriter(byteArrayOutputStream, encoding))) {
			invoiceWriter.write(toFileFooter(billingRecords));
			invoiceWriter.flush();
			return byteArrayOutputStream.toByteArray();
		}
	}

	@Override
	void processInvoice(BeanWriter invoiceWriter, BillingRecordEntity billingRecord) {
		invoiceWriter.write(toInvoiceHeader(billingRecord));
		invoiceWriter.write(toInvoiceDescriptionRow(billingRecord));

		ofNullable(billingRecord.getInvoice())
			.orElseThrow(createInternalServerErrorProblem("Invoice is not present"))
			.getInvoiceRows()
			.forEach(row -> processInvoiceRow(invoiceWriter, row));
	}
}
