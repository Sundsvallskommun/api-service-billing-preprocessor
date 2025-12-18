package se.sundsvall.billingpreprocessor.service.creator;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.DescriptionType.STANDARD;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.Type.INTERNAL;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.zalando.problem.ThrowableProblem;
import se.sundsvall.billingpreprocessor.integration.db.InvoiceFileConfigurationRepository;
import se.sundsvall.billingpreprocessor.integration.db.model.BillingRecordEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.DescriptionEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceFileConfigurationEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceRowEntity;

@SpringBootTest(webEnvironment = MOCK)
@ActiveProfiles("junit")
class InternalMexInvoiceCreatorTest {

	@MockitoBean
	private InvoiceFileConfigurationRepository invoiceFileConfigurationRepositoryMock;

	@Autowired
	private InternalMexInvoiceCreator creator;

	@BeforeEach
	void setup() {
		final var config = InvoiceFileConfigurationEntity.create()
			.withCategoryTag("MEX_INVOICE")
			.withType(INTERNAL.name())
			.withEncoding(StandardCharsets.ISO_8859_1.name());

		when(invoiceFileConfigurationRepositoryMock.findByCreatorName("InternalMexInvoiceCreator"))
			.thenReturn(Optional.of(config));
	}

	@Test
	void testInternalMexInvoiceCreator_extendsInternalInvoiceCreator() {
		assertThat(InternalMexInvoiceCreator.class).isAssignableTo(InternalInvoiceCreator.class);
	}

	@Test
	void getProcessableCategory() {
		assertThat(creator.getProcessableCategory()).isEqualTo("MEX_INVOICE");
		verify(invoiceFileConfigurationRepositoryMock).findByCreatorName("InternalMexInvoiceCreator");
	}

	@Test
	void getProcessableType() {
		assertThat(creator.getProcessableType()).isEqualTo(INTERNAL);
		verify(invoiceFileConfigurationRepositoryMock).findByCreatorName("InternalMexInvoiceCreator");
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

	@Test
	void createInvoiceData() throws Exception {
		final var billingRecord = createBillingRecord(BigDecimal.valueOf(100));
		billingRecord.getInvoice()
			.withCustomerId("123")
			.withCustomerReference("ref")
			.withOurReference("ourRef")
			.withDate(LocalDate.of(2024, 1, 1))
			.withDueDate(LocalDate.of(2024, 1, 31))
			.withDescription("description");

		billingRecord.getInvoice().getInvoiceRows().getFirst()
			.withDescriptions(List.of(DescriptionEntity.create()
				.withType(STANDARD)
				.withText("rowDescription")));

		final var result = creator.createInvoiceData(billingRecord);
		final var content = new String(result, StandardCharsets.ISO_8859_1);

		assertThat(content).contains("123").contains("description").doesNotContain("\nT ");
	}

	@Test
	void createInvoiceDataWhenInvoiceMissing() {
		final var input = createBillingRecord(BigDecimal.valueOf(100)).withInvoice(null);
		final var e = assertThrows(ThrowableProblem.class, () -> creator.createInvoiceData(input));

		assertThat(e.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR);
		assertThat(e.getMessage()).isEqualTo("Internal Server Error: Invoice is not present");
	}

	private BillingRecordEntity createBillingRecord(final BigDecimal... amounts) {
		final List<InvoiceRowEntity> invoiceRows = Arrays.stream(amounts)
			.map(amount -> InvoiceRowEntity.create().withTotalAmount(amount))
			.toList();

		return BillingRecordEntity.create()
			.withInvoice(InvoiceEntity.create()
				.withInvoiceRows(invoiceRows));
	}
}
