package se.sundsvall.billingpreprocessor.service.creator;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;
import static se.sundsvall.billingpreprocessor.integration.db.model.AddressDetailsEmbeddable.create;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.DescriptionType.STANDARD;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.Type.EXTERNAL;

import java.io.IOException;
import java.math.BigDecimal;
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
import se.sundsvall.billingpreprocessor.integration.db.model.AccountInformationEmbeddable;
import se.sundsvall.billingpreprocessor.integration.db.model.BillingRecordEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.DescriptionEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceFileConfigurationEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceRowEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.RecipientEntity;

@SpringBootTest(webEnvironment = MOCK)
@ActiveProfiles("junit")
class ExternalMexInvoiceCreatorTest {

	@MockitoBean
	private InvoiceFileConfigurationRepository invoiceFileConfigurationRepositoryMock;

	@MockitoBean
	private LegalIdProvider legalIdProviderMock;

	@Autowired
	private ExternalMexInvoiceCreator creator;

	@BeforeEach
	void setup() {
		final var config = InvoiceFileConfigurationEntity.create()
			.withCategoryTag("MEX_INVOICE")
			.withType(EXTERNAL.name())
			.withEncoding(ISO_8859_1.name());

		when(invoiceFileConfigurationRepositoryMock.findByCreatorName("ExternalMexInvoiceCreator"))
			.thenReturn(Optional.of(config));
	}

	@Test
	void testExternalMexInvoiceCreator_extendsExternalInvoiceCreator() {
		assertThat(ExternalMexInvoiceCreator.class).isAssignableTo(ExternalInvoiceCreator.class);
	}

	@Test
	void getProcessableCategory() {
		assertThat(creator.getProcessableCategory()).isEqualTo("MEX_INVOICE");
		verify(invoiceFileConfigurationRepositoryMock).findByCreatorName("ExternalMexInvoiceCreator");
	}

	@Test
	void getProcessableType() {
		assertThat(creator.getProcessableType()).isEqualTo(EXTERNAL);
		verify(invoiceFileConfigurationRepositoryMock).findByCreatorName("ExternalMexInvoiceCreator");
	}

	@Test
	void testCreateFileFooter_withSingleBillingRecord() throws IOException {
		final var billingRecords = List.of(createBillingRecord(BigDecimal.valueOf(50)));

		final var result = creator.createFileFooter(billingRecords);

		assertThat(new String(result)).isEqualTo("T+00000000005000\n");
	}

	@Test
	void testCreateFileFooter_withMultipleBillingRecords() throws IOException {
		final var billingRecords = List.of(
			createBillingRecord(BigDecimal.valueOf(100)),
			createBillingRecord(BigDecimal.valueOf(200), BigDecimal.valueOf(300)));

		final var result = creator.createFileFooter(billingRecords);

		assertThat(new String(result)).isEqualTo("T+00000000060000\n");
	}

	@Test
	void testCreateFileFooter_withEmptyList() throws IOException {
		assertThat(creator.createFileFooter(emptyList())).isEmpty();
	}

	@Test
	void createInvoiceData() throws Exception {
		final var legalId = "197001011234";
		when(legalIdProviderMock.translateToLegalId(any(), any())).thenReturn(legalId);

		final var billingRecord = createBillingRecord(BigDecimal.valueOf(100));
		billingRecord.getInvoice()
			.withCustomerReference("customerReference")
			.withTotalAmount(BigDecimal.valueOf(100));

		billingRecord.getInvoice().getInvoiceRows().getFirst()
			.withVatCode("00")
			.withDescriptions(List.of(DescriptionEntity.create()
				.withType(STANDARD)
				.withText("text")))
			.withAccountInformation(List.of(AccountInformationEmbeddable.create()
				.withCostCenter("costCenter")
				.withSubaccount("subaccount")
				.withDepartment("operation")
				.withAmount(BigDecimal.valueOf(100))
				.withCounterpart("counter")));

		billingRecord.withRecipient(RecipientEntity.create()
			.withFirstName("firstName")
			.withLastName("lastName")
			.withAddressDetails(create()
				.withStreet("street")
				.withPostalCode("zipCode")
				.withCity("city"))
			.withPartyId("partyId"));

		final var result = creator.createInvoiceData(billingRecord);
		final var content = new String(result, ISO_8859_1);

		assertThat(content).contains("S7001011234").contains("H7001011234").doesNotContain("\nT");
	}

	@Test
	void createInvoiceDataWhenInvoiceMissing() {
		final var input = createBillingRecord(BigDecimal.valueOf(100)).withInvoice(null);
		input.withRecipient(RecipientEntity.create()
			.withFirstName("firstName")
			.withLastName("lastName")
			.withAddressDetails(create()
				.withStreet("street")
				.withPostalCode("postalCode")
				.withCity("city"))
			.withLegalId("197001011234"));

		input.getRecipient().getAddressDetails().setCareOf("careOf");
		input.withMunicipalityId("municipalityId");

		final var e = assertThrows(ThrowableProblem.class, () -> creator.createInvoiceData(input));

		assertThat(e.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR);
		assertThat(e.getMessage()).contains("Internal Server Error");
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
