package se.sundsvall.billingpreprocessor.service.creator.definition.external;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

class CustomerRowTest {

	@Test
	void testBean() {
		assertThat(CustomerRow.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var careOf = "careOf";
		final var counterpart = "counterpart";
		final var customerName = "customerName";
		final var legalId = "legalId";
		final var streetAddress = "streetAddress";
		final var zipCodeAndCity = "zipCodeAndCity";

		final var bean = CustomerRow.create()
			.withCareOf(careOf)
			.withCounterpart(counterpart)
			.withCustomerName(customerName)
			.withLegalId(legalId)
			.withStreetAddress(streetAddress)
			.withZipCodeAndCity(zipCodeAndCity);

		assertThat(bean.getCareOf()).isEqualTo(careOf);
		assertThat(bean.getCounterpart()).isEqualTo(counterpart);
		assertThat(bean.getCustomerName()).isEqualTo(customerName);
		assertThat(bean.getLegalId()).isEqualTo(legalId);
		assertThat(bean.getStreetAddress()).isEqualTo(streetAddress);
		assertThat(bean.getZipCodeAndCity()).isEqualTo(zipCodeAndCity);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(new CustomerRow()).hasAllNullFieldsOrProperties();
		assertThat(CustomerRow.create()).hasAllNullFieldsOrProperties();
	}
}
