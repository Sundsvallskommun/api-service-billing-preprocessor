package se.sundsvall.billingpreprocessor.integration.db.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToStringExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;

import java.util.List;
import org.junit.jupiter.api.Test;

class InvoiceRowEntityTest {

	@Test
	void testBean() {
		assertThat(InvoiceRowEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToStringExcluding("invoice")));
	}

	@Test
	void hasValidBuilderMethods() {
		final var accountInormation = AccountInformationEmbeddable.create();
		final var costPerUnit = 13.37f;
		final var description = DescriptionEntity.create();
		final var id = 1234L;
		final var invoice = InvoiceEntity.create();
		final var quantity = 13.0f;
		final var totalAmount = costPerUnit * quantity;
		final var vatCode = "vatCode";

		final var entity = InvoiceRowEntity.create()
			.withAccountInformation(accountInormation)
			.withCostPerUnit(costPerUnit)
			.withDescriptions(List.of(description))
			.withId(id)
			.withInvoice(invoice)
			.withQuantity(quantity)
			.withTotalAmount(totalAmount)
			.withVatCode(vatCode);

		assertThat(entity).hasNoNullFieldsOrProperties();
		assertThat(entity.getAccountInformation()).isEqualTo(accountInormation);
		assertThat(entity.getCostPerUnit()).isEqualTo(costPerUnit);
		assertThat(entity.getDescriptions()).containsExactly(description);
		assertThat(entity.getId()).isEqualTo(id);
		assertThat(entity.getInvoice()).isEqualTo(invoice);
		assertThat(entity.getQuantity()).isEqualTo(quantity);
		assertThat(entity.getTotalAmount()).isEqualTo(totalAmount);
		assertThat(entity.getVatCode()).isEqualTo(vatCode);
	}

	@Test
	void listInstanceIsUntouched() {
		final var entity = InvoiceRowEntity.create()
			.withDescriptions(List.of(DescriptionEntity.create()));

		final var list = entity.getDescriptions();

		entity.withDescriptions(emptyList());
		assertThat(entity.getDescriptions()).isSameAs(list);

		entity.setDescriptions(List.of(DescriptionEntity.create()));
		assertThat(entity.getDescriptions()).isSameAs(list);
	}

	@Test
	void hasNoDirtOnCreatedBean() {
		assertThat(InvoiceRowEntity.create()).hasAllNullFieldsOrPropertiesExcept("id").hasFieldOrPropertyWithValue("id", 0L);
		assertThat(new InvoiceRowEntity()).hasAllNullFieldsOrPropertiesExcept("id").hasFieldOrPropertyWithValue("id", 0L);
	}
}
