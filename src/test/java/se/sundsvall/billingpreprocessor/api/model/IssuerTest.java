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

class IssuerTest {

	@Test
	void testBean() {
		assertThat(Issuer.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var addressDetails = AddressDetails.create();
		final var firstName = "firstName";
		final var lastName = "lastName";
		final var partyId = "partyId";
		final var organizationName = "organizationName";
		final var userId = "userId";

		final var bean = Issuer.create()
			.withAddressDetails(addressDetails)
			.withFirstName(firstName)
			.withLastName(lastName)
			.withOrganizationName(organizationName)
			.withPartyId(partyId)
			.withUserId(userId);

		assertThat(bean.getAddressDetails()).isEqualTo(addressDetails);
		assertThat(bean.getFirstName()).isEqualTo(firstName);
		assertThat(bean.getLastName()).isEqualTo(lastName);
		assertThat(bean.getOrganizationName()).isEqualTo(organizationName);
		assertThat(bean.getPartyId()).isEqualTo(partyId);
		assertThat(bean.getUserId()).isEqualTo(userId);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(new Issuer()).hasAllNullFieldsOrProperties();
		assertThat(Issuer.create()).hasAllNullFieldsOrProperties();
	}
}
