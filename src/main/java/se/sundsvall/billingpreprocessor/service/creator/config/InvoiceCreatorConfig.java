package se.sundsvall.billingpreprocessor.service.creator.config;

import static org.apache.commons.text.StringEscapeUtils.unescapeJava;

import org.beanio.builder.FixedLengthParserBuilder;
import org.beanio.builder.StreamBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InvoiceCreatorConfig {
	public static final String INTERNAL_INVOICE_BUILDER = "internalInvoiceFileBuilder";
	public static final String EXTERNAL_INVOICE_BUILDER = "externalInvoiceFileBuilder";
	private static final String FIXED_LENGTH = "fixedlength";

	@Bean(INTERNAL_INVOICE_BUILDER)
	StreamBuilder internalInvoiceStreamBuilder(InvoiceCreatorProperties properties) {

		return new StreamBuilder(INTERNAL_INVOICE_BUILDER)
			.format(FIXED_LENGTH)
			.parser(new FixedLengthParserBuilder().recordTerminator(unescapeJava(properties.recordTerminator())))
			.addTypeHandler(InternalInvoiceFloatTypeHandler.NAME, new InternalInvoiceFloatTypeHandler())
			.addTypeHandler(InternalInvoiceIntegerTypeHandler.NAME, new InternalInvoiceIntegerTypeHandler())
			.addRecord(se.sundsvall.billingpreprocessor.service.creator.definition.internal.FileHeaderRow.class)
			.addRecord(se.sundsvall.billingpreprocessor.service.creator.definition.internal.InvoiceHeaderRow.class)
			.addRecord(se.sundsvall.billingpreprocessor.service.creator.definition.internal.InvoiceDescriptionRow.class)
			.addRecord(se.sundsvall.billingpreprocessor.service.creator.definition.internal.InvoiceRow.class)
			.addRecord(se.sundsvall.billingpreprocessor.service.creator.definition.internal.InvoiceRowDescriptionRow.class)
			.addRecord(se.sundsvall.billingpreprocessor.service.creator.definition.internal.InvoiceAccountingRow.class)
			.addRecord(se.sundsvall.billingpreprocessor.service.creator.definition.internal.InvoiceFooterRow.class);
	}

	@Bean(EXTERNAL_INVOICE_BUILDER)
	StreamBuilder externalInvoiceStreamBuilder(InvoiceCreatorProperties properties) {

		return new StreamBuilder(EXTERNAL_INVOICE_BUILDER)
			.format(FIXED_LENGTH)
			.parser(new FixedLengthParserBuilder().recordTerminator(unescapeJava(properties.recordTerminator())))
			.addTypeHandler(ExternalInvoiceFloatTypeHandler.NAME, new ExternalInvoiceFloatTypeHandler())
			.addRecord(se.sundsvall.billingpreprocessor.service.creator.definition.external.FileHeaderRow.class)
			.addRecord(se.sundsvall.billingpreprocessor.service.creator.definition.external.CustomerRow.class)
			.addRecord(se.sundsvall.billingpreprocessor.service.creator.definition.external.InvoiceHeaderRow.class)
			.addRecord(se.sundsvall.billingpreprocessor.service.creator.definition.external.InvoiceRow.class)
			.addRecord(se.sundsvall.billingpreprocessor.service.creator.definition.external.InvoiceDescriptionRow.class)
			.addRecord(se.sundsvall.billingpreprocessor.service.creator.definition.external.InvoiceAccountingRow.class)
			.addRecord(se.sundsvall.billingpreprocessor.service.creator.definition.external.InvoiceFooterRow.class);
	}
}
