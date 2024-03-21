package se.sundsvall.billingpreprocessor.integration.db.model.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.DescriptionType.DETAILED;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.DescriptionType.STANDARD;

import org.junit.jupiter.api.Test;

class DescriptionTypeTest {

	@Test
	void enums() {
		assertThat(DescriptionType.values()).containsExactlyInAnyOrder(DETAILED, STANDARD);
	}

	@Test
	void enumValues() {
		assertThat(DETAILED).hasToString("DETAILED");
		assertThat(STANDARD).hasToString("STANDARD");
	}
}
