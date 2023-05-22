package se.sundsvall.billingpreprocessor.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static java.time.OffsetDateTime.now;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static se.sundsvall.billingpreprocessor.api.model.enums.Status.REJECTED;
import static se.sundsvall.billingpreprocessor.api.model.enums.Type.INTERNAL;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class BillingRecordTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> now().plusDays(nextInt()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		assertThat(BillingRecord.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var category = "category";
		final var approved = now();
		final var approvedBy = "approvedBy";
		final var created = now().minusDays(14);
		final var id = "id";
		final var invoice = Invoice.create();
		final var recipient = Recipient.create();
		final var modified = now().minusDays(7);
		final var status = REJECTED;
		final var type = INTERNAL;

		final var bean = BillingRecord.create()
			.withCategory(category)
			.withApproved(approved)
			.withApprovedBy(approvedBy)
			.withCreated(created)
			.withId(id)
			.withInvoice(invoice)
			.withRecipient(recipient)
			.withModified(modified)
			.withStatus(status)
			.withType(type);

		assertThat(bean.getCategory()).isEqualTo(category);
		assertThat(bean.getApproved()).isEqualTo(approved);
		assertThat(bean.getApprovedBy()).isEqualTo(approvedBy);
		assertThat(bean.getCreated()).isEqualTo(created);
		assertThat(bean.getId()).isEqualTo(id);
		assertThat(bean.getInvoice()).isEqualTo(invoice);
		assertThat(bean.getRecipient()).isEqualTo(recipient);
		assertThat(bean.getModified()).isEqualTo(modified);
		assertThat(bean.getStatus()).isEqualTo(status);
		assertThat(bean.getType()).isEqualTo(type);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(new BillingRecord()).hasAllNullFieldsOrProperties();
		assertThat(BillingRecord.create()).hasAllNullFieldsOrProperties();
	}
}
