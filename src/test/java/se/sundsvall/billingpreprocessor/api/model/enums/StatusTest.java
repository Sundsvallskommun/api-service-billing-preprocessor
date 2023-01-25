package se.sundsvall.billingpreprocessor.api.model.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.billingpreprocessor.api.model.enums.Status.CERTIFIED;
import static se.sundsvall.billingpreprocessor.api.model.enums.Status.INVOICED;
import static se.sundsvall.billingpreprocessor.api.model.enums.Status.NEW;
import static se.sundsvall.billingpreprocessor.api.model.enums.Status.REJECTED;

import org.junit.jupiter.api.Test;

class StatusTest {

	@Test
	void enums() {
		assertThat(Status.values()).containsExactlyInAnyOrder(CERTIFIED, INVOICED, NEW, REJECTED);
	}

	@Test
	void enumValues() {
		assertThat(CERTIFIED).hasToString("CERTIFIED");
		assertThat(INVOICED).hasToString("INVOICED");
		assertThat(NEW).hasToString("NEW");
		assertThat(REJECTED).hasToString("REJECTED");
	}
}
