package se.sundsvall.billingpreprocessor.integration.db.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToStringExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static java.time.LocalDate.now;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class InvoiceEntityTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> now().plusDays(new Random().nextInt()), LocalDate.class);
	}

	@Test
	void testBean() {
		assertThat(InvoiceEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToStringExcluding("billingRecord")));
	}

	@Test
	void hasValidBuilderMethods() {
		final var billingRecord = BillingRecordEntity.create();
		final var customerId = "customerId";
		final var customerReference = "customerReference";
		final var description = "description";
		final var date = now().minusWeeks(1);
		final var dueDate = now();
		final var id = "id";
		final var invoiceRow = InvoiceRowEntity.create();
		final var ourReference = "ourReference";
		final var referenceId = "referenceId";
		final var totalAmount = 123456f;

		final var entity = InvoiceEntity.create()
			.withBillingRecord(billingRecord)
			.withCustomerId(customerId)
			.withCustomerReference(customerReference)
			.withDescription(description)
			.withDate(date)
			.withDueDate(dueDate)
			.withId(id)
			.withInvoiceRows(List.of(invoiceRow))
			.withOurReference(ourReference)
			.withReferenceId(referenceId)
			.withTotalAmount(totalAmount);

		assertThat(entity).hasNoNullFieldsOrProperties();
		assertThat(entity.getBillingRecord()).isEqualTo(billingRecord);
		assertThat(entity.getCustomerId()).isEqualTo(customerId);
		assertThat(entity.getCustomerReference()).isEqualTo(customerReference);
		assertThat(entity.getDescription()).isEqualTo(description);
		assertThat(entity.getDate()).isEqualTo(date);
		assertThat(entity.getDueDate()).isEqualTo(dueDate);
		assertThat(entity.getId()).isEqualTo(id);
		assertThat(entity.getInvoiceRows()).containsExactly(invoiceRow);
		assertThat(entity.getOurReference()).isEqualTo(ourReference);
		assertThat(entity.getReferenceId()).isEqualTo(referenceId);
		assertThat(entity.getTotalAmount()).isEqualTo(totalAmount);
	}

	@Test
	void listInstanceIsUntouched() {
		final var entity = InvoiceEntity.create()
			.withInvoiceRows(List.of(InvoiceRowEntity.create()));

		final var list = entity.getInvoiceRows();

		entity.withInvoiceRows(emptyList());
		assertThat(entity.getInvoiceRows()).isSameAs(list);

		entity.setInvoiceRows(List.of(InvoiceRowEntity.create()));
		assertThat(entity.getInvoiceRows()).isSameAs(list);
	}

	@Test
	void hasNoDirtOnCreatedBean() {
		assertThat(InvoiceEntity.create()).hasAllNullFieldsOrProperties();
		assertThat(new InvoiceEntity()).hasAllNullFieldsOrProperties();
	}
}
