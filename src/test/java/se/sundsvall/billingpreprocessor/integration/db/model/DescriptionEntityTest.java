package se.sundsvall.billingpreprocessor.integration.db.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToStringExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.DescriptionType.STANDARD;

import org.junit.jupiter.api.Test;

class DescriptionEntityTest {

	@Test
	void testBean() {
		assertThat(DescriptionEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToStringExcluding("invoiceRow")));
	}

	@Test
	void hasValidBuilderMethods() {
		final var id = 1234L;
		final var invoiceRow = InvoiceRowEntity.create();
		final var text = "text";
		final var type = STANDARD;

		final var entity = DescriptionEntity.create()
			.withId(id)
			.withInvoiceRow(invoiceRow)
			.withText(text)
			.withType(type);

		assertThat(entity).hasNoNullFieldsOrProperties();
		assertThat(entity.getId()).isEqualTo(id);
		assertThat(entity.getInvoiceRow()).isEqualTo(invoiceRow);
		assertThat(entity.getText()).isEqualTo(text);
		assertThat(entity.getType()).isEqualTo(type);
	}

	@Test
	void hasNoDirtOnCreatedBean() {
		assertThat(DescriptionEntity.create()).hasAllNullFieldsOrPropertiesExcept("id").hasFieldOrPropertyWithValue("id", 0L);
		assertThat(new DescriptionEntity()).hasAllNullFieldsOrPropertiesExcept("id").hasFieldOrPropertyWithValue("id", 0L);
	}
}
