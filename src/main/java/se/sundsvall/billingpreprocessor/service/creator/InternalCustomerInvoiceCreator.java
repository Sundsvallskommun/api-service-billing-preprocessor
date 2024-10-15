package se.sundsvall.billingpreprocessor.service.creator;

import static se.sundsvall.billingpreprocessor.service.creator.config.InvoiceCreatorConfig.INTERNAL_INVOICE_BUILDER;

import org.beanio.builder.StreamBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import se.sundsvall.billingpreprocessor.integration.db.InvoiceFileConfigurationRepository;

@Component
public class InternalCustomerInvoiceCreator extends InternalInvoiceCreator {
	public InternalCustomerInvoiceCreator(@Qualifier(INTERNAL_INVOICE_BUILDER) StreamBuilder builder, InvoiceFileConfigurationRepository configurationRepository) {
		super(builder, configurationRepository);
	}
}
