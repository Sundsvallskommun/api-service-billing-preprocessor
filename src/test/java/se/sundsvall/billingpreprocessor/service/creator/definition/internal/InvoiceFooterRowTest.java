package se.sundsvall.billingpreprocessor.service.creator.definition.internal;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.math.BigDecimal;
import java.util.Random;
import org.junit.jupiter.api.Test;

class InvoiceFooterRowTest {

	@Test
	void testBean() {
		assertThat(InvoiceFooterRow.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var totalAmount = BigDecimal.valueOf(new Random().nextDouble());

		final var bean = InvoiceFooterRow.create()
			.withTotalAmount(totalAmount);

		assertThat(bean.getTotalAmount()).isEqualTo(totalAmount);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(new InvoiceFooterRow()).hasAllNullFieldsOrProperties();
		assertThat(InvoiceFooterRow.create()).hasAllNullFieldsOrProperties();
	}
}
