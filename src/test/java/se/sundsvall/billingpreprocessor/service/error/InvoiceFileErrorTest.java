package se.sundsvall.billingpreprocessor.service.error;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEqualsExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCodeExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToStringExcluding;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

class InvoiceFileErrorTest {

	@Test
	void testBean() {
		assertThat(InvoiceFileError.class, allOf(
			hasValidBeanConstructor(),
			hasValidBeanHashCodeExcluding("commonError"),
			hasValidBeanEqualsExcluding("commonError"),
			hasValidBeanToStringExcluding("commonError")));
	}

	@Test
	void testBuilderMethods() {
		final var entityId = "entityId";
		final var message = "message";

		final var bean = InvoiceFileError.create()
			.withEntityId(entityId)
			.withMessage(message);

		assertThat(bean.getEntityId()).isEqualTo(entityId);
		assertThat(bean.getMessage()).isEqualTo(message);
		assertThat(bean.isCommonError()).isFalse();
	}

	@Test
	void testBuilderMethodForCommonError() {
		final var message = "message";

		final var bean = InvoiceFileError.create(message);

		assertThat(bean.getEntityId()).isNull();
		assertThat(bean.getMessage()).isEqualTo(message);
		assertThat(bean.isCommonError()).isTrue();
	}

	@Test
	void testBuilderMethodForEntitySpecificError() {
		final var entityId = "entityId";
		final var message = "message";

		final var bean = InvoiceFileError.create(entityId, message);

		assertThat(bean.getEntityId()).isEqualTo(entityId);
		assertThat(bean.getMessage()).isEqualTo(message);
		assertThat(bean.isCommonError()).isFalse();
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(InvoiceFileError.create()).hasAllNullFieldsOrProperties();
	}

}
