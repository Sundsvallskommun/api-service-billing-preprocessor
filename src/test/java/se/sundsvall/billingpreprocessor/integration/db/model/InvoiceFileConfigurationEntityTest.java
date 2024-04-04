package se.sundsvall.billingpreprocessor.integration.db.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

class InvoiceFileConfigurationEntityTest {

	@Test
	void testBean() {
		assertThat(InvoiceFileConfigurationEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builderMethods() {
		final var categoryTage = "categoryTag";
		final var creatorName = "creatorName";
		final var encoding = "encoding";
		final var fileNamePattern = "fileNamePattern";
		final var type = "type";

		final var result = InvoiceFileConfigurationEntity.create()
			.withCategoryTag(categoryTage)
			.withCreatorName(creatorName)
			.withEncoding(encoding)
			.withFileNamePattern(fileNamePattern)
			.withType(type);

		assertThat(result.getCategoryTag()).isEqualTo(categoryTage);
		assertThat(result.getCreatorName()).isEqualTo(creatorName);
		assertThat(result.getEncoding()).isEqualTo(encoding);
		assertThat(result.getFileNamePattern()).isEqualTo(fileNamePattern);
		assertThat(result.getType()).isEqualTo(type);
	}
}