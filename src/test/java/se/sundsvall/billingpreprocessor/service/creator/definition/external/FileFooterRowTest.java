package se.sundsvall.billingpreprocessor.service.creator.definition.external;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class FileFooterRowTest {

	@Test
	void testBean() {
		assertThat(FileFooterRow.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var totalAmount = BigDecimal.valueOf(200d);

		final var bean = FileFooterRow.create()
			.withTotalAmount(totalAmount);

		assertThat(bean.getTotalAmount()).isEqualTo(totalAmount);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(FileFooterRow.create()).hasAllNullFieldsOrProperties();
	}
}
