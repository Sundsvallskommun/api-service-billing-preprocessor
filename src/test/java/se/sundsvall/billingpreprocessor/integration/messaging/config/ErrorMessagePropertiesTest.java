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
		assertThat(properties.creationErrorMailTemplate().bodyPrefix()).isEqualTo("creationErrorBodyPrefix");
		assertThat(properties.creationErrorMailTemplate().bodySuffix()).isEqualTo("creationErrorBodySuffix");
		assertThat(properties.creationErrorMailTemplate().htmlPrefix()).isEqualTo("creationErrorHtmlPrefix");
		assertThat(properties.creationErrorMailTemplate().htmlSuffix()).isEqualTo("creationErrorHtmlSuffix");
		assertThat(properties.creationErrorMailTemplate().listItem()).isEqualTo("creationErrorListItem");
		assertThat(properties.creationErrorMailTemplate().listPrefix()).isEqualTo("creationErrorListPrefix");
		assertThat(properties.creationErrorMailTemplate().listSuffix()).isEqualTo("creationErrorListSuffix");
		assertThat(properties.creationErrorMailTemplate().subject()).isEqualTo("creationErrorSubject");

		assertThat(properties.transferErrorMailTemplate().bodyPrefix()).isEqualTo("transferErrorBodyPrefix");
		assertThat(properties.transferErrorMailTemplate().bodySuffix()).isEqualTo("transferErrorBodySuffix");
		assertThat(properties.transferErrorMailTemplate().htmlPrefix()).isEqualTo("transferErrorHtmlPrefix");
		assertThat(properties.transferErrorMailTemplate().htmlSuffix()).isEqualTo("transferErrorHtmlSuffix");
		assertThat(properties.transferErrorMailTemplate().listItem()).isEqualTo("transferErrorListItem");
		assertThat(properties.transferErrorMailTemplate().listPrefix()).isEqualTo("transferErrorListPrefix");
		assertThat(properties.transferErrorMailTemplate().listSuffix()).isEqualTo("transferErrorListSuffix");
		assertThat(properties.transferErrorMailTemplate().subject()).isEqualTo("transferErrorSubject");

		assertThat(properties.sender()).isEqualTo("sender");
		assertThat(properties.recipients().getFirst()).isEqualTo("recipient.1");
		assertThat(properties.recipients().getLast()).isEqualTo("recipient.2");

	}
}
