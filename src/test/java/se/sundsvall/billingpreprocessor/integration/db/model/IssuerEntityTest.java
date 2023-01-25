package se.sundsvall.billingpreprocessor.integration.db.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToStringExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;

import org.junit.jupiter.api.Test;

class IssuerEntityTest {

	@Test
	void testBean() {
		assertThat(IssuerEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToStringExcluding("billingRecord")));
	}

	@Test
	void hasValidBuilderMethods() {
		final var addressDetails = AddressDetailsEmbeddable.create();
		final var billingRecord = BillingRecordEntity.create();
		final var firstName = "firstName";
		final var id = "id";
		final var lastName = "lastName";
		final var organizationName = "organizationName";
		final var partyId = "partyId";
		final var userId = "userId";

		final var entity = IssuerEntity.create()
			.withAddressDetails(addressDetails)
			.withBillingRecord(billingRecord)
			.withFirstName(firstName)
			.withId(id)
			.withLastName(lastName)
			.withOrganizationName(organizationName)
			.withPartyId(partyId)
			.withUserId(userId);

		assertThat(entity).hasNoNullFieldsOrProperties();
		assertThat(entity.getAddressDetails()).isEqualTo(addressDetails);
		assertThat(entity.getBillingRecord()).isEqualTo(billingRecord);
		assertThat(entity.getFirstName()).isEqualTo(firstName);
		assertThat(entity.getId()).isEqualTo(id);
		assertThat(entity.getLastName()).isEqualTo(lastName);
		assertThat(entity.getOrganizationName()).isEqualTo(organizationName);
		assertThat(entity.getPartyId()).isEqualTo(partyId);
		assertThat(entity.getUserId()).isEqualTo(userId);
	}

	@Test
	void hasNoDirtOnCreatedBean() {
		assertThat(IssuerEntity.create()).hasAllNullFieldsOrProperties();
		assertThat(new IssuerEntity()).hasAllNullFieldsOrProperties();
	}
}
