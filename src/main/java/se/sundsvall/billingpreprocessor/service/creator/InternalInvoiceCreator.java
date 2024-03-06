package se.sundsvall.billingpreprocessor.service.creator;

import static java.util.Optional.ofNullable;
import static org.springframework.util.CollectionUtils.isEmpty;
import static se.sundsvall.billingpreprocessor.Constants.EMPTY_ARRAY;
import static se.sundsvall.billingpreprocessor.service.creator.config.InvoiceCreatorConfig.INTERNAL_INVOICE_BUILDER;
import static se.sundsvall.billingpreprocessor.service.mapper.InternalInvoiceMapper.toFileHeader;
import static se.sundsvall.billingpreprocessor.service.mapper.InternalInvoiceMapper.toInvoiceAccountingRow;
import static se.sundsvall.billingpreprocessor.service.mapper.InternalInvoiceMapper.toInvoiceDescriptionRow;
import static se.sundsvall.billingpreprocessor.service.mapper.InternalInvoiceMapper.toInvoiceFooter;
import static se.sundsvall.billingpreprocessor.service.mapper.InternalInvoiceMapper.toInvoiceHeader;
import static se.sundsvall.billingpreprocessor.service.mapper.InternalInvoiceMapper.toInvoiceRow;
import static se.sundsvall.billingpreprocessor.service.util.ProblemUtil.createProblem;

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

@Component
public class InternalInvoiceCreator {

	private final StreamFactory factory;

	public InternalInvoiceCreator(@Qualifier(INTERNAL_INVOICE_BUILDER) StreamBuilder builder) {
		this.factory = StreamFactory.newInstance();
		this.factory.define(builder);
	}

	public byte[] toBytes(List<BillingRecordEntity> billingRecords) throws IOException {
		if (isEmpty(billingRecords)) {
			return EMPTY_ARRAY;
		}

		try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			BeanWriter invoiceWriter = factory.createWriter(INTERNAL_INVOICE_BUILDER, new OutputStreamWriter(byteArrayOutputStream))) {

			invoiceWriter.write(toFileHeader());
			billingRecords.forEach(billingRecord -> {
				handleInvoice(invoiceWriter, billingRecord);
			});

			invoiceWriter.flush();
			return byteArrayOutputStream.toByteArray();
		}
	}

	private void handleInvoice(BeanWriter invoiceWriter, BillingRecordEntity billingRecord) {
		invoiceWriter.write(toInvoiceHeader(billingRecord));
		invoiceWriter.write(toInvoiceDescriptionRow(billingRecord));

		ofNullable(billingRecord.getInvoice())
			.orElseThrow(() -> createProblem("Invoice is not present"))
			.getInvoiceRows()
			.forEach(row -> handleInvoiceRow(invoiceWriter, row));

		invoiceWriter.write(toInvoiceFooter(billingRecord));
	}

	private void handleInvoiceRow(BeanWriter invoiceWriter, InvoiceRowEntity invoiceRow) {
		invoiceWriter.write(toInvoiceRow(invoiceRow));
		invoiceWriter.write(toInvoiceAccountingRow(invoiceRow));
	}
}
