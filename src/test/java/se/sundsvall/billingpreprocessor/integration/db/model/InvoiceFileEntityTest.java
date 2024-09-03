package se.sundsvall.billingpreprocessor.integration.db.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static java.time.OffsetDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.InvoiceFileStatus.SEND_SUCCESSFUL;

import java.time.OffsetDateTime;
import java.util.Random;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class InvoiceFileEntityTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> now().plusDays(new Random().nextInt()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		assertThat(InvoiceFileEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void hasValidBuilderMethods() {

		final var content = "content";
		final var created = OffsetDateTime.now();
		final var encoding = "encoding";
		final var id = 1;
		final var name = "name";
		final var sent = OffsetDateTime.now();
		final var status = SEND_SUCCESSFUL;
		final var type = "type";
		final var municipalityId = "municipalityId";

		final var entity = InvoiceFileEntity.create()
			.withContent(content)
			.withCreated(created)
			.withEncoding(encoding)
			.withId(id)
			.withName(name)
			.withSent(sent)
			.withStatus(status)
			.withType(type)
			.withMunicipalityId(municipalityId);

		assertThat(entity).hasNoNullFieldsOrProperties();
		assertThat(entity.getContent()).isEqualTo(content);
		assertThat(entity.getCreated()).isEqualTo(created);
		assertThat(entity.getEncoding()).isEqualTo(encoding);
		assertThat(entity.getId()).isEqualTo(id);
		assertThat(entity.getName()).isEqualTo(name);
		assertThat(entity.getSent()).isEqualTo(sent);
		assertThat(entity.getStatus()).isEqualTo(status);
		assertThat(entity.getType()).isEqualTo(type);
		assertThat(entity.getMunicipalityId()).isEqualTo(municipalityId);
	}

	@Test
	void hasNoDirtOnCreatedBean() {
		assertThat(InvoiceFileEntity.create()).hasAllNullFieldsOrPropertiesExcept("id");
		assertThat(new InvoiceFileEntity()).hasAllNullFieldsOrPropertiesExcept("id");
	}
}
