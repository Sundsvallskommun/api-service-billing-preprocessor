package se.sundsvall.billingpreprocessor.service.creator.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import org.beanio.builder.StreamBuilder;
import org.beanio.internal.config.StreamConfig;
import org.beanio.stream.fixedlength.FixedLengthRecordParserFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = InvoiceCreatorConfig.class)
class InvoiceCreatorConfigTest {

	@Autowired
	@Qualifier(InvoiceCreatorConfig.EXTERNAL_INVOICE_BUILDER)
	private StreamBuilder externalStreamBuilder;

	@Autowired
	@Qualifier(InvoiceCreatorConfig.INTERNAL_INVOICE_BUILDER)
	private StreamBuilder internalStreamBuilder;

	@Test
	void externalStreamBuilder() {
		assertThat(externalStreamBuilder).isNotNull().isNotSameAs(internalStreamBuilder);

		assertThat(externalStreamBuilder).extracting("config").isInstanceOfSatisfying(StreamConfig.class, streamConfig -> {
			assertThat(streamConfig.getName()).isEqualTo("externalInvoiceFileBuilder");
			assertThat(streamConfig.getFormat()).isEqualTo("fixedlength");
			assertThat(streamConfig.getParserFactory().getInstance()).isInstanceOf(FixedLengthRecordParserFactory.class);
			assertThat(streamConfig.getHandlerList()).hasSize(1).satisfiesExactly(config -> {
				assertThat(config.getName()).isEqualTo("externalInvoiceFloatTypeHandler");
				assertThat(config.getInstance()).isInstanceOf(ExternalInvoiceFloatTypeHandler.class);
			});
			assertThat(streamConfig.getChildren()).extracting("name", "type").containsExactlyInAnyOrder(
				tuple("fileHeaderRow", "se.sundsvall.billingpreprocessor.service.creator.definition.external.FileHeaderRow"),
				tuple("customerRow", "se.sundsvall.billingpreprocessor.service.creator.definition.external.CustomerRow"),
				tuple("invoiceHeaderRow", "se.sundsvall.billingpreprocessor.service.creator.definition.external.InvoiceHeaderRow"),
				tuple("invoiceRow", "se.sundsvall.billingpreprocessor.service.creator.definition.external.InvoiceRow"),
				tuple("invoiceDescriptionRow", "se.sundsvall.billingpreprocessor.service.creator.definition.external.InvoiceDescriptionRow"),
				tuple("invoiceAccountingRow", "se.sundsvall.billingpreprocessor.service.creator.definition.external.InvoiceAccountingRow"),
				tuple("invoiceFooterRow", "se.sundsvall.billingpreprocessor.service.creator.definition.external.InvoiceFooterRow"));
		});
	}

	@Test
	void internalStreamBuilder() {
		assertThat(internalStreamBuilder).isNotNull().isNotSameAs(externalStreamBuilder);

		assertThat(internalStreamBuilder).extracting("config").isInstanceOfSatisfying(StreamConfig.class, streamConfig -> {
			assertThat(streamConfig.getName()).isEqualTo("internalInvoiceFileBuilder");
			assertThat(streamConfig.getFormat()).isEqualTo("fixedlength");
			assertThat(streamConfig.getParserFactory().getInstance()).isInstanceOf(FixedLengthRecordParserFactory.class);
			assertThat(streamConfig.getHandlerList()).hasSize(2).satisfiesExactlyInAnyOrder(config -> {
				assertThat(config.getName()).isEqualTo("internalInvoiceFloatTypeHandler");
				assertThat(config.getInstance()).isInstanceOf(InternalInvoiceFloatTypeHandler.class);
			}, config -> {
				assertThat(config.getName()).isEqualTo("internalInvoiceIntegerTypeHandler");
				assertThat(config.getInstance()).isInstanceOf(InternalInvoiceIntegerTypeHandler.class);
			});
			assertThat(streamConfig.getChildren()).extracting("name", "type").containsExactlyInAnyOrder(
				tuple("fileHeaderRow", "se.sundsvall.billingpreprocessor.service.creator.definition.internal.FileHeaderRow"),
				tuple("invoiceHeaderRow", "se.sundsvall.billingpreprocessor.service.creator.definition.internal.InvoiceHeaderRow"),
				tuple("invoiceRow", "se.sundsvall.billingpreprocessor.service.creator.definition.internal.InvoiceRow"),
				tuple("invoiceDescriptionRow", "se.sundsvall.billingpreprocessor.service.creator.definition.internal.InvoiceDescriptionRow"),
				tuple("invoiceAccountingRow", "se.sundsvall.billingpreprocessor.service.creator.definition.internal.InvoiceAccountingRow"),
				tuple("invoiceFooterRow", "se.sundsvall.billingpreprocessor.service.creator.definition.internal.InvoiceFooterRow"));
		});
	}
}
