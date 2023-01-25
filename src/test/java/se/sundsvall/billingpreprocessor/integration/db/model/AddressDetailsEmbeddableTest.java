package se.sundsvall.billingpreprocessor.integration.db.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;

import org.junit.jupiter.api.Test;

class AddressDetailsEmbeddableTest {

	@Test
	void testBean() {
		assertThat(AddressDetailsEmbeddable.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void hasValidBuilderMethods() {
		final var careOf = "careOf";
		final var city = "city";
		final var street = "street";
		final var postalCode = "postalCode";

		final var embeddable = AddressDetailsEmbeddable.create()
			.withCareOf(careOf)
			.withCity(city)
			.withStreet(street)
			.withPostalCode(postalCode);

		assertThat(embeddable).hasNoNullFieldsOrProperties();
		assertThat(embeddable.getCareOf()).isEqualTo(careOf);
		assertThat(embeddable.getCity()).isEqualTo(city);
		assertThat(embeddable.getStreet()).isEqualTo(street);
		assertThat(embeddable.getPostalCode()).isEqualTo(postalCode);
	}

	@Test
	void hasNoDirtOnCreatedBean() {
		assertThat(AddressDetailsEmbeddable.create()).hasAllNullFieldsOrProperties();
		assertThat(new AddressDetailsEmbeddable()).hasAllNullFieldsOrProperties();
	}
}
