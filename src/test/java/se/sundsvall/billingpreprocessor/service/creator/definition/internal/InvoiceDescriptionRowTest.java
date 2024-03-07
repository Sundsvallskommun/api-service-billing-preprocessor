package se.sundsvall.billingpreprocessor.service.creator.definition.internal;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

class InvoiceDescriptionRowTest {

	@Test
	void testBean() {
		assertThat(InvoiceDescriptionRow.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var description = "description";

		final var bean = InvoiceDescriptionRow.create()
			.withDescription(description);

		assertThat(bean.getDescription()).isEqualTo(description);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(new InvoiceDescriptionRow()).hasAllNullFieldsOrProperties();
		assertThat(InvoiceDescriptionRow.create()).hasAllNullFieldsOrProperties();
	}
}
