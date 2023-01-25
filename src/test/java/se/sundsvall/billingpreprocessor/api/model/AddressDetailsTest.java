package se.sundsvall.billingpreprocessor.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

class AddressDetailsTest {

	@Test
	void testBean() {
		assertThat(AddressDetails.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var careOf = "careOf";
		final var city = "city";
		final var street = "street";
		final var postalCode = "postalCode";

		final var bean = AddressDetails.create()
			.withCareOf(careOf)
			.withCity(city)
			.withStreet(street)
			.withtPostalCode(postalCode);

		assertThat(bean.getCareOf()).isEqualTo(careOf);
		assertThat(bean.getCity()).isEqualTo(city);
		assertThat(bean.getStreet()).isEqualTo(street);
		assertThat(bean.getPostalCode()).isEqualTo(postalCode);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(new AddressDetails()).hasAllNullFieldsOrProperties();
		assertThat(AddressDetails.create()).hasAllNullFieldsOrProperties();
	}
}
