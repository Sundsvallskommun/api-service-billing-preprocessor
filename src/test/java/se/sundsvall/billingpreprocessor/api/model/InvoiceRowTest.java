package se.sundsvall.billingpreprocessor.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.math.BigDecimal;
import java.util.List;
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
		final var accountInformation = List.of(AccountInformation.create());
		final var costPerUnit = BigDecimal.valueOf(new Random().nextDouble());
		final var descriptions = List.of("description");
		final var detailedDescriptions = List.of("detailedDescription");
		final var quantity = BigDecimal.valueOf(2l);
		final var totalAmount = costPerUnit.multiply(quantity);
		final var vatCode = "vatCode";

		final var bean = InvoiceRow.create()
			.withAccountInformation(accountInformation)
			.withCostPerUnit(costPerUnit)
			.withDescriptions(descriptions)
			.withDetailedDescriptions(detailedDescriptions)
			.withQuantity(quantity)
			.withTotalAmount(totalAmount)
			.withVatCode(vatCode);

		assertThat(bean.getAccountInformation()).isEqualTo(accountInformation);
		assertThat(bean.getCostPerUnit()).isEqualTo(costPerUnit);
		assertThat(bean.getDescriptions()).isEqualTo(descriptions);
		assertThat(bean.getDetailedDescriptions()).isEqualTo(detailedDescriptions);
		assertThat(bean.getQuantity()).isEqualTo(quantity);
		assertThat(bean.getTotalAmount()).isEqualTo(totalAmount);
		assertThat(bean.getVatCode()).isEqualTo(vatCode);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(new InvoiceRow()).hasAllNullFieldsOrProperties();
		assertThat(InvoiceRow.create()).hasAllNullFieldsOrProperties();
	}
}
