package se.sundsvall.billingpreprocessor.service.creator;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import se.sundsvall.billingpreprocessor.integration.db.InvoiceFileConfigurationRepository;
import se.sundsvall.billingpreprocessor.integration.db.model.BillingRecordEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceFileConfigurationEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceRowEntity;

@SpringBootTest(webEnvironment = MOCK)
@ActiveProfiles("junit")
class InternalSalaryAndPensionInvoiceCreatorTest {

	@MockitoBean
	private InvoiceFileConfigurationRepository invoiceFileConfigurationRepositoryMock;

	@Autowired
	private InternalSalaryAndPensionInvoiceCreator creator;

	@BeforeEach
	void setup() {
		final var config = InvoiceFileConfigurationEntity.create()
			.withEncoding(StandardCharsets.ISO_8859_1.name());

		when(invoiceFileConfigurationRepositoryMock.findByCreatorName("InternalSalaryAndPensionInvoiceCreator"))
			.thenReturn(Optional.of(config));
	}

	@Test
	void testInternalSalaryAndPensionInvoiceCreator_extendsInternalInvoiceCreator() {
		assertThat(InternalSalaryAndPensionInvoiceCreator.class).isAssignableTo(InternalInvoiceCreator.class);
	}

	@Test
	void testCreateFileFooter_withSingleBillingRecord() throws IOException {
		final var billingRecords = List.of(createBillingRecord(BigDecimal.valueOf(50)));

		final var result = creator.createFileFooter(billingRecords);

		assertThat(new String(result)).isEqualTo("T 50.00          \n");
	}

	@Test
	void testCreateFileFooter_withMultipleBillingRecords() throws IOException {
		final var billingRecords = List.of(
			createBillingRecord(BigDecimal.valueOf(100)),
			createBillingRecord(BigDecimal.valueOf(200), BigDecimal.valueOf(300)));

		final var result = creator.createFileFooter(billingRecords);

		assertThat(new String(result)).isEqualTo("T 600.00         \n");
	}

	@Test
	void testCreateFileFooter_withEmptyList() throws IOException {
		assertThat(creator.createFileFooter(emptyList())).isEmpty();
	}

	private BillingRecordEntity createBillingRecord(BigDecimal... amounts) {
		List<InvoiceRowEntity> invoiceRows = Arrays.stream(amounts)
			.map(amount -> InvoiceRowEntity.create().withTotalAmount(amount))
			.toList();

		return BillingRecordEntity.create()
			.withInvoice(InvoiceEntity.create()
				.withInvoiceRows(invoiceRows));
	}
}
