package se.sundsvall.billingpreprocessor.service.creator.definition.external;

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
		final var customerReference = "customerReference";
		final var dueDate = LocalDate.now();
		final var legalId = "legalId";
		final var ourReference = "ourReference";

		final var bean = InvoiceHeaderRow.create()
			.withCustomerReference(customerReference)
			.withDueDate(dueDate)
			.withLegalId(legalId)
			.withOurReference(ourReference);

		assertThat(bean.getCustomerReference()).isEqualTo(customerReference);
		assertThat(bean.getDueDate()).isEqualTo(dueDate);
		assertThat(bean.getLegalId()).isEqualTo(legalId);
		assertThat(bean.getOurReference()).isEqualTo(ourReference);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(new InvoiceHeaderRow()).hasAllNullFieldsOrProperties();
		assertThat(InvoiceHeaderRow.create()).hasAllNullFieldsOrProperties();
	}
}
