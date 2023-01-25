package se.sundsvall.billingpreprocessor.api.model.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.billingpreprocessor.api.model.enums.Type.EXTERNAL;
import static se.sundsvall.billingpreprocessor.api.model.enums.Type.INTERNAL;

import org.junit.jupiter.api.Test;

class TypeTest {

	@Test
	void enums() {
		assertThat(Type.values()).containsExactlyInAnyOrder(EXTERNAL, INTERNAL);
	}

	@Test
	void enumValues() {
		assertThat(EXTERNAL).hasToString("EXTERNAL");
		assertThat(INTERNAL).hasToString("INTERNAL");
	}
}
