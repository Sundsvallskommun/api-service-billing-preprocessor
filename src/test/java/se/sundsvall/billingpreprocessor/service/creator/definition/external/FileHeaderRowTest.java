package se.sundsvall.billingpreprocessor.service.creator.definition.external;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static java.time.LocalDate.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.LocalDate;
import java.util.Random;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class FileHeaderRowTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> now().plusDays(new Random().nextInt()), LocalDate.class);
	}

	@Test
	void testBean() {
		assertThat(FileHeaderRow.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var createdDate = LocalDate.now();
		final var generatingSystem = "generatingSystem";
		final var invoiceType = "invoiceType";

		final var bean = FileHeaderRow.create()
			.withCreatedDate(createdDate)
			.withGeneratingSystem(generatingSystem)
			.withInvoiceType(invoiceType);

		assertThat(bean.getCreatedDate()).isEqualTo(createdDate);
		assertThat(bean.getGeneratingSystem()).isEqualTo(generatingSystem);
		assertThat(bean.getInvoiceType()).isEqualTo(invoiceType);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(new FileHeaderRow()).hasAllNullFieldsOrProperties();
		assertThat(FileHeaderRow.create()).hasAllNullFieldsOrProperties();
	}
}
