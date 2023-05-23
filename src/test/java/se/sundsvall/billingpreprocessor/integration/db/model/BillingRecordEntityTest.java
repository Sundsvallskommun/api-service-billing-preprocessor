package se.sundsvall.billingpreprocessor.integration.db.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static java.time.OffsetDateTime.now;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;
import static se.sundsvall.billingpreprocessor.api.model.enums.Status.REJECTED;
import static se.sundsvall.billingpreprocessor.api.model.enums.Type.INTERNAL;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class BillingRecordEntityTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> now().plusDays(nextInt()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		assertThat(BillingRecordEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void hasValidBuilderMethods() {
		final var category = "category";
		final var approved = now().minusWeeks(2);
		final var approvedBy = "approvedBy";
		final var created = now().minusWeeks(3);
		final var id = "id";
		final var invoice = InvoiceEntity.create();
		final var Recipient = RecipientEntity.create();
		final var modified = now().minusWeeks(1);
		final var status = REJECTED;
		final var type = INTERNAL;

		final var entity = BillingRecordEntity.create()
			.withCategory(category)
			.withApproved(approved)
			.withApprovedBy(approvedBy)
			.withCreated(created)
			.withId(id)
			.withInvoice(invoice)
			.withRecipient(Recipient)
			.withModified(modified)
			.withStatus(status)
			.withType(type);

		assertThat(entity).hasNoNullFieldsOrProperties();
		assertThat(entity.getCategory()).isEqualTo(category);
		assertThat(entity.getApproved()).isEqualTo(approved);
		assertThat(entity.getApprovedBy()).isEqualTo(approvedBy);
		assertThat(entity.getCreated()).isEqualTo(created);
		assertThat(entity.getId()).isEqualTo(id);
		assertThat(entity.getInvoice()).isEqualTo(invoice);
		assertThat(entity.getRecipient()).isEqualTo(Recipient);
		assertThat(entity.getModified()).isEqualTo(modified);
		assertThat(entity.getStatus()).isEqualTo(status);
		assertThat(entity.getType()).isEqualTo(type);
	}

	@Test
	void hasNoDirtOnCreatedBean() {
		assertThat(BillingRecordEntity.create()).hasAllNullFieldsOrProperties();
		assertThat(new BillingRecordEntity()).hasAllNullFieldsOrProperties();
	}
}
