package se.sundsvall.billingpreprocessor.service.creator;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEqualsExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCodeExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToStringExcluding;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

class CreationErrorTest {

	@Test
	void testBean() {
		assertThat(CreationError.class, allOf(
			hasValidBeanConstructor(),
			hasValidBeanHashCodeExcluding("commonError"),
			hasValidBeanEqualsExcluding("commonError"),
			hasValidBeanToStringExcluding("commonError")));
	}

	@Test
	void testBuilderMethods() {
		final var entityId = "entityId";
		final var message = "message";

		final var bean = CreationError.create()
			.withEntityId(entityId)
			.withMessage(message);

		assertThat(bean.getEntityId()).isEqualTo(entityId);
		assertThat(bean.getMessage()).isEqualTo(message);
		assertThat(bean.isCommonError()).isFalse();
	}

	@Test
	void testBuilderMethodForCommonError() {
		final var message = "message";

		final var bean = CreationError.create(message);

		assertThat(bean.getEntityId()).isNull();
		assertThat(bean.getMessage()).isEqualTo(message);
		assertThat(bean.isCommonError()).isTrue();
	}

	@Test
	void testBuilderMethodForEntitySpecificError() {
		final var entityId = "entityId";
		final var message = "message";

		final var bean = CreationError.create(entityId, message);

		assertThat(bean.getEntityId()).isEqualTo(entityId);
		assertThat(bean.getMessage()).isEqualTo(message);
		assertThat(bean.isCommonError()).isFalse();
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(CreationError.create()).hasAllNullFieldsOrProperties();
	}

}
