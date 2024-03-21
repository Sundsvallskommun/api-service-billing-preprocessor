package se.sundsvall.billingpreprocessor.integration.messaging.config;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import se.sundsvall.billingpreprocessor.Application;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
class ErrorMessagePropertiesTest {

	@Autowired
	private ErrorMessageProperties properties;

	@Test
	void testProperties() {
		assertThat(properties.bodyPrefixTemplate()).isEqualTo("bodyPrefixTemplate");
		assertThat(properties.bodySuffixTemplate()).isEqualTo("bodySuffixTemplate");
		assertThat(properties.htmlPrefixTemplate()).isEqualTo("htmlPrefixTemplate");
		assertThat(properties.htmlSuffixTemplate()).isEqualTo("htmlSuffixTemplate");
		assertThat(properties.listItemTemplate()).isEqualTo("listItemTemplate");
		assertThat(properties.listPrefixTemplate()).isEqualTo("listPrefixTemplate");
		assertThat(properties.listSuffixTemplate()).isEqualTo("listSuffixTemplate");
		assertThat(properties.subjectTemplate()).isEqualTo("subjectTemplate");
		assertThat(properties.sender()).isEqualTo("sender");
		assertThat(properties.recipients()).asList().containsExactlyInAnyOrder("recipient.1", "recipient.2");
	}
}