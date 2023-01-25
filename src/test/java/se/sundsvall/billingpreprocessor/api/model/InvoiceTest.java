package se.sundsvall.billingpreprocessor.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static java.time.LocalDate.now;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class InvoiceTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> now().plusDays(nextInt()), LocalDate.class);
	}

	@Test
	void testBean() {
		assertThat(Invoice.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var customerId = "customerId";
		final var customerReference = "customerReference";
		final var description = "description";
		final var date = LocalDate.now().minusWeeks(1);
		final var dueDate = LocalDate.now();
		final var invoiceRows = List.of(InvoiceRow.create());
		final var ourReference = "ourReference";
		final var referenceId = "referenceId";
		final var totalAmount = 12345.67f;

		final var bean = Invoice.create()
			.withCustomerId(customerId)
			.withCustomerReference(customerReference)
			.withDescription(description)
			.withDate(date)
			.withDueDate(dueDate)
			.withInvoiceRows(invoiceRows)
			.withOurReference(ourReference)
			.withReferenceId(referenceId)
			.withTotalAmount(totalAmount);

		assertThat(bean.getCustomerId()).isEqualTo(customerId);
		assertThat(bean.getCustomerReference()).isEqualTo(customerReference);
		assertThat(bean.getDescription()).isEqualTo(description);
		assertThat(bean.getDate()).isEqualTo(date);
		assertThat(bean.getDueDate()).isEqualTo(dueDate);
		assertThat(bean.getInvoiceRows()).isEqualTo(invoiceRows);
		assertThat(bean.getOurReference()).isEqualTo(ourReference);
		assertThat(bean.getReferenceId()).isEqualTo(referenceId);
		assertThat(bean.getTotalAmount()).isEqualTo(totalAmount);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(new Invoice()).hasAllNullFieldsOrProperties();
		assertThat(Invoice.create()).hasAllNullFieldsOrProperties();
	}
}
