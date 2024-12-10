package se.sundsvall.billingpreprocessor.service.creator;

import static se.sundsvall.billingpreprocessor.service.creator.config.InvoiceCreatorConfig.EXTERNAL_INVOICE_BUILDER;

import org.beanio.builder.StreamBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import se.sundsvall.billingpreprocessor.integration.db.InvoiceFileConfigurationRepository;

@Component
public class ExternalCustomerInvoiceCreator extends ExternalInvoiceCreator {

	public ExternalCustomerInvoiceCreator(@Qualifier(EXTERNAL_INVOICE_BUILDER) StreamBuilder builder, LegalIdProvider legalIdProvider, InvoiceFileConfigurationRepository configurationRepository) {
		super(builder, legalIdProvider, configurationRepository);
	}
}
