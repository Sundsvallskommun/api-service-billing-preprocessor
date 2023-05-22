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

class RecipientTest {

	@Test
	void testBean() {
		assertThat(Recipient.class, allOf(
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
		final var organizationNumber = "organizationNumber";
		final var organizationName = "organizationName";
		final var userId = "userId";

		final var bean = Recipient.create()
			.withAddressDetails(addressDetails)
			.withFirstName(firstName)
			.withLastName(lastName)
			.withOrganizationName(organizationName)
			.withPartyId(partyId)
			.withOrganizationNumber(organizationNumber)
			.withUserId(userId);

		assertThat(bean.getAddressDetails()).isEqualTo(addressDetails);
		assertThat(bean.getFirstName()).isEqualTo(firstName);
		assertThat(bean.getLastName()).isEqualTo(lastName);
		assertThat(bean.getOrganizationName()).isEqualTo(organizationName);
		assertThat(bean.getPartyId()).isEqualTo(partyId);
		assertThat(bean.getOrganizationNumber()).isEqualTo(organizationNumber);
		assertThat(bean.getUserId()).isEqualTo(userId);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(new Recipient()).hasAllNullFieldsOrProperties();
		assertThat(Recipient.create()).hasAllNullFieldsOrProperties();
	}
}
