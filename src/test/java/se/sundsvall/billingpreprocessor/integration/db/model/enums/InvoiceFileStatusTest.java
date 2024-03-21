package se.sundsvall.billingpreprocessor.integration.db.model.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.InvoiceFileStatus.GENERATED;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.InvoiceFileStatus.SEND_FAILED;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.InvoiceFileStatus.SEND_SUCCESSFUL;

import org.junit.jupiter.api.Test;

class InvoiceFileStatusTest {

	@Test
	void enums() {
		assertThat(InvoiceFileStatus.values()).containsExactlyInAnyOrder(GENERATED, SEND_FAILED, SEND_SUCCESSFUL);
	}

	@Test
	void enumValues() {
		assertThat(GENERATED).hasToString("GENERATED");
		assertThat(SEND_FAILED).hasToString("SEND_FAILED");
		assertThat(SEND_SUCCESSFUL).hasToString("SEND_SUCCESSFUL");
	}
}
