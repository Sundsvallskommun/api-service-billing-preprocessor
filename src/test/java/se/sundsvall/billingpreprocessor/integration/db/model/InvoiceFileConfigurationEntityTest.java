package se.sundsvall.billingpreprocessor.integration.db.model;

import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

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
		var type = "type";
		var categoryTage = "categoryTag";
		var fileNamePattern = "fileNamePattern";

		var result = InvoiceFileConfigurationEntity.create()
			.withType(type)
			.withCategoryTag(categoryTage)
			.withFileNamePattern(fileNamePattern);

		assertThat(result.getType()).isEqualTo(type);
		assertThat(result.getCategoryTag()).isEqualTo(categoryTage);
		assertThat(result.getFileNamePattern()).isEqualTo(fileNamePattern);
	}
}