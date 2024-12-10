package se.sundsvall.billingpreprocessor.service.creator.definition.internal;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static java.time.LocalDate.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.LocalDate;
import java.util.Random;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class InvoiceHeaderRowTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> now().plusDays(new Random().nextInt()), LocalDate.class);
	}

	@Test
	void testBean() {
		assertThat(InvoiceHeaderRow.class, allOf(
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
		final var date = LocalDate.now();
		final var dueDate = LocalDate.now().plusMonths(1);
		final var ourReference = "ourReference";

		final var bean = InvoiceHeaderRow.create()
			.withCustomerId(customerId)
			.withCustomerReference(customerReference)
			.withDate(date)
			.withDueDate(dueDate)
			.withOurReference(ourReference);

		assertThat(bean.getCustomerId()).isEqualTo(customerId);
		assertThat(bean.getCustomerReference()).isEqualTo(customerReference);
		assertThat(bean.getDate()).isEqualTo(date);
		assertThat(bean.getDueDate()).isEqualTo(dueDate);
		assertThat(bean.getOurReference()).isEqualTo(ourReference);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(new InvoiceHeaderRow()).hasAllNullFieldsOrProperties();
		assertThat(InvoiceHeaderRow.create()).hasAllNullFieldsOrProperties();
	}
}
