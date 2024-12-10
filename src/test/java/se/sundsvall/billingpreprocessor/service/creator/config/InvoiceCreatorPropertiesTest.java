package se.sundsvall.billingpreprocessor.service.creator.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import se.sundsvall.billingpreprocessor.Application;

@SpringBootTest(classes = Application.class, webEnvironment = MOCK)
@ActiveProfiles("junit")
class InvoiceCreatorPropertiesTest {

	@Autowired
	private InvoiceCreatorProperties properties;

	@Test
	void externalStreamBuilder() {
		assertThat(properties).isNotNull().extracting(InvoiceCreatorProperties::recordTerminator).isEqualTo("\\n");
	}
}
