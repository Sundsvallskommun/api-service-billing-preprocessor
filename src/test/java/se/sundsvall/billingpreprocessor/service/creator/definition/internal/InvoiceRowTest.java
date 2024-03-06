package se.sundsvall.billingpreprocessor.service.creator.definition.internal;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Random;

import org.junit.jupiter.api.Test;

class InvoiceRowTest {

	@Test
	void testBean() {
		assertThat(InvoiceRow.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var costPerUnit = new Random().nextFloat();
		final var quantity = new Random().nextFloat();
		final var description = "description";
		final var totalAmount = new Random().nextFloat();

		final var bean = InvoiceRow.create()
			.withCostPerUnit(costPerUnit)
			.withDescription(description)
			.withQuantity(quantity)
			.withTotalAmount(totalAmount);

		assertThat(bean.getCostPerUnit()).isEqualTo(costPerUnit);
		assertThat(bean.getDescription()).isEqualTo(description);
		assertThat(bean.getQuantity()).isEqualTo(quantity);
		assertThat(bean.getTotalAmount()).isEqualTo(totalAmount);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(new InvoiceRow()).hasAllNullFieldsOrProperties();
		assertThat(InvoiceRow.create()).hasAllNullFieldsOrProperties();
	}
}
