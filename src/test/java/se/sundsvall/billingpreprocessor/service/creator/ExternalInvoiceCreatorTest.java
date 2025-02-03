package se.sundsvall.billingpreprocessor.service.creator;

import static java.time.OffsetDateTime.now;
import static java.util.UUID.randomUUID;
import static org.apache.commons.text.StringEscapeUtils.unescapeJava;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.DescriptionType.DETAILED;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.DescriptionType.STANDARD;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.Status.APPROVED;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.Type.EXTERNAL;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.zalando.problem.ThrowableProblem;
import se.sundsvall.billingpreprocessor.integration.db.InvoiceFileConfigurationRepository;
import se.sundsvall.billingpreprocessor.integration.db.model.AccountInformationEmbeddable;
import se.sundsvall.billingpreprocessor.integration.db.model.AddressDetailsEmbeddable;
import se.sundsvall.billingpreprocessor.integration.db.model.BillingRecordEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.DescriptionEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceFileConfigurationEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceRowEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.RecipientEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.enums.DescriptionType;
import se.sundsvall.billingpreprocessor.integration.db.model.enums.Status;
import se.sundsvall.billingpreprocessor.integration.db.model.enums.Type;
import se.sundsvall.billingpreprocessor.service.creator.config.InvoiceCreatorProperties;

@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@ActiveProfiles("junit")
class ExternalInvoiceCreatorTest {

	// billingRecord constants
	private static final String ID = randomUUID().toString();
	private static final String MUNICIPALITY_ID = "municipalityId";
	private static final Status STATUS = APPROVED;
	private static final Type TYPE = EXTERNAL;
	private static final OffsetDateTime APPROVED_TIMESTAMP = now();
	private static final OffsetDateTime CREATED_TIMESTAMP = now().minusDays(2);
	private static final OffsetDateTime MODIFIED_TIMESTAMP = now().minusDays(1);

	// Invoice constants
	private static final String CUSTOMER_REFERENCE = "Snurre Sprätto";
	private static final String DESCRIPTION = "En fakturarad";
	private static final LocalDate DATE = LocalDate.of(2024, 3, 6);
	private static final LocalDate DUE_DATE = LocalDate.of(2024, 3, 30);
	private static final String OUR_REFERENCE = "Johnny Bråttom";
	private static final BigDecimal INVOICE_TOTAL_AMOUNT = BigDecimal.valueOf(1395d);

	// Invoice row constants
	private static final BigDecimal COST_PER_UNIT = BigDecimal.valueOf(1395d);
	private static final String DETAILED_DESCRIPTION = "En mer detaljerad fakturaradsbeskrivning";
	private static final BigDecimal QUANTITY = BigDecimal.ONE;
	private static final String VAT_CODE = "25";

	// Account information constants
	private static final BigDecimal ACCOUNTING_AMOUNT_1 = BigDecimal.valueOf(895d);
	private static final String ACCURAL_KEY_1 = "5647";
	private static final String ACTIVITY_1 = "5756";
	private static final String ARTICLE_1 = "Interim";
	private static final String COST_CENTER_1 = "15800100";
	private static final String COUNTERPART_1 = "118";
	private static final String DEPARTMENT_1 = "920360";
	private static final String PROJECT_1 = "11041";
	private static final String SUBACCOUNT_1 = "936300";

	private static final BigDecimal ACCOUNTING_AMOUNT_2 = BigDecimal.valueOf(500d);
	private static final String ACCURAL_KEY_2 = "6658";
	private static final String ACTIVITY_2 = "3313";
	private static final String ARTICLE_2 = "Övrigt";
	private static final String COST_CENTER_2 = "40050303";
	private static final String COUNTERPART_2 = "228";
	private static final String DEPARTMENT_2 = "902911";
	private static final String PROJECT_2 = "35073";
	private static final String SUBACCOUNT_2 = "553229";

	// Recipient constants
	private static final String ORGANIZATION_NAME = "Testbolaget AB";
	private static final String PARTY_ID = "5cd1d415-9d7f-4e12-a1ec-011c1d0e37ad";
	private static final String LEGAL_ID = "3456789123";

	// Address details constants
	private static final String CARE_OF = "Jenny Långsam";
	private static final String CITY = "Sundsvall";
	private static final String STREET = "Testgatan 12";
	private static final String POSTAL_CODE = "85643";

	@MockitoBean
	private LegalIdProvider legalIdProviderMock;

	@MockitoBean
	private InvoiceFileConfigurationRepository invoiceFileConfigurationRepositoryMock;

	@Autowired
	@Qualifier(value = "externalInvoiceCreator")
	private ExternalInvoiceCreator creator;

	@Autowired
	private InvoiceCreatorProperties properties;

	@Test
	void validateImplementation() {
		assertThat(creator).isInstanceOf(InvoiceCreator.class);
	}

	@Test
	void getProcessableCategory() {
		final var category = "category";
		final var config = InvoiceFileConfigurationEntity.create().withCategoryTag(category);

		when(invoiceFileConfigurationRepositoryMock.findByCreatorName("ExternalInvoiceCreator")).thenReturn(Optional.of(config));

		assertThat(creator.getProcessableCategory()).isEqualTo(category);
		verify(invoiceFileConfigurationRepositoryMock).findByCreatorName("ExternalInvoiceCreator");
	}

	@Test
	void getProcessableType() {
		final var type = EXTERNAL;
		final var config = InvoiceFileConfigurationEntity.create().withType(type.toString());

		when(invoiceFileConfigurationRepositoryMock.findByCreatorName("ExternalInvoiceCreator")).thenReturn(Optional.of(config));

		assertThat(creator.getProcessableType()).isEqualTo(type);
		verify(invoiceFileConfigurationRepositoryMock).findByCreatorName("ExternalInvoiceCreator");
	}

	@Test
	void getProcessablesWhenNoConfigFound() {
		final var e1 = assertThrows(ThrowableProblem.class, () -> creator.getProcessableCategory());
		final var e2 = assertThrows(ThrowableProblem.class, () -> creator.getProcessableType());

		assertThat(List.of(e1, e2)).allSatisfy(e -> {
			assertThat(e.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR);
			assertThat(e.getMessage()).isEqualTo("Internal Server Error: No configuration present for invoice creator with name ExternalInvoiceCreator");
		});
	}

	@Test
	void createFileHeader() throws Exception {
		final var config = InvoiceFileConfigurationEntity.create().withEncoding(StandardCharsets.ISO_8859_1.name());
		when(invoiceFileConfigurationRepositoryMock.findByCreatorName("ExternalInvoiceCreator")).thenReturn(Optional.of(config));

		final var result = creator.createFileHeader();
		final var expected = getResource("validation/external_header_expected_format.txt")
			.replace("yyMMdd", LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd")));

		assertThat(new String(result, StandardCharsets.ISO_8859_1)).isEqualTo(expected);
	}

	@Test
	void createInvoiceDataFromNull() throws Exception {
		assertThat(creator.createInvoiceData(null)).isEmpty();
	}

	@Test
	void createInvoiceDataWhenInvoiceMissing() {
		final var config = InvoiceFileConfigurationEntity.create().withEncoding(StandardCharsets.ISO_8859_1.name());
		when(invoiceFileConfigurationRepositoryMock.findByCreatorName("ExternalInvoiceCreator")).thenReturn(Optional.of(config));

		final var input = createbillingRecordEntity().withInvoice(null);
		final var e = assertThrows(ThrowableProblem.class, () -> creator.createInvoiceData(input));

		assertThat(e.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR);
		assertThat(e.getMessage()).isEqualTo("Internal Server Error: Recipient counterpart is not present");
	}

	@Test
	void createInvoiceDataFromEntityWithLegalId() throws Exception {
		final var config = InvoiceFileConfigurationEntity.create().withEncoding(StandardCharsets.ISO_8859_1.name());
		when(invoiceFileConfigurationRepositoryMock.findByCreatorName("ExternalInvoiceCreator")).thenReturn(Optional.of(config));

		final var result = creator.createInvoiceData(createbillingRecordEntity());
		final var expected = getResource("validation/external_invoicedata_expected_format.txt");

		assertThat(new String(result, StandardCharsets.ISO_8859_1)).isEqualTo(expected);
		verify(legalIdProviderMock, never()).translateToLegalId(any(), any());
	}

	@Test
	void createInvoiceDataFromEntityWithoutLegalId() throws Exception {
		final var config = InvoiceFileConfigurationEntity.create().withEncoding(StandardCharsets.ISO_8859_1.name());
		when(invoiceFileConfigurationRepositoryMock.findByCreatorName("ExternalInvoiceCreator")).thenReturn(Optional.of(config));

		final var input = createbillingRecordEntity();
		input.getRecipient().withLegalId(null).withPartyId(PARTY_ID);

		when(legalIdProviderMock.translateToLegalId(MUNICIPALITY_ID, PARTY_ID)).thenReturn(LEGAL_ID);

		final var result = creator.createInvoiceData(input);
		final var expected = getResource("validation/external_invoicedata_expected_format.txt");

		assertThat(new String(result, StandardCharsets.ISO_8859_1)).isEqualTo(expected);
		verify(legalIdProviderMock).translateToLegalId(MUNICIPALITY_ID, PARTY_ID);
	}

	private String getResource(final String fileName) throws IOException, URISyntaxException {
		return Files.readString(Paths.get(getClass().getClassLoader().getResource(fileName).toURI()), StandardCharsets.UTF_8)
			.replaceAll(System.lineSeparator(), unescapeJava(properties.recordTerminator()));
	}

	private static BillingRecordEntity createbillingRecordEntity() {
		final var billingRecordEntity = BillingRecordEntity.create()
			.withMunicipalityId(MUNICIPALITY_ID)
			.withApproved(APPROVED_TIMESTAMP)
			.withCreated(CREATED_TIMESTAMP)
			.withId(ID)
			.withModified(MODIFIED_TIMESTAMP)
			.withStatus(STATUS)
			.withType(TYPE);

		billingRecordEntity
			.withInvoice(createInvoiceEntity(billingRecordEntity))
			.withRecipient(createRecipientEntity(billingRecordEntity));

		return billingRecordEntity;
	}

	private static InvoiceEntity createInvoiceEntity(BillingRecordEntity billingRecordEntity) {
		final var invoiceEntity = InvoiceEntity.create()
			.withBillingRecord(billingRecordEntity)
			.withCustomerReference(CUSTOMER_REFERENCE)
			.withDescription(DESCRIPTION)
			.withDate(DATE)
			.withDueDate(DUE_DATE)
			.withId(ID)
			.withOurReference(OUR_REFERENCE)
			.withTotalAmount(INVOICE_TOTAL_AMOUNT);

		return invoiceEntity.withInvoiceRows(List.of(createInvoiceRowEntity(1, invoiceEntity)));
	}

	private static InvoiceRowEntity createInvoiceRowEntity(int id, InvoiceEntity invoiceEntity) {
		final var invoiceRowEntity = InvoiceRowEntity.create()
			.withAccountInformation(createAccountInformationEmbeddables())
			.withCostPerUnit(COST_PER_UNIT)
			.withId(id)
			.withInvoice(invoiceEntity)
			.withQuantity(QUANTITY)
			.withTotalAmount(COST_PER_UNIT.multiply(QUANTITY))
			.withVatCode(VAT_CODE);

		return invoiceRowEntity.withDescriptions(List.of(
			createDescriptionEntity(1, invoiceRowEntity, STANDARD, DESCRIPTION),
			createDescriptionEntity(2, invoiceRowEntity, DETAILED, DETAILED_DESCRIPTION)));
	}

	private static DescriptionEntity createDescriptionEntity(int id, InvoiceRowEntity invoiceRowEntity, DescriptionType type, String text) {
		return DescriptionEntity.create()
			.withId(id)
			.withInvoiceRow(invoiceRowEntity)
			.withText(text)
			.withType(type);
	}

	private static List<AccountInformationEmbeddable> createAccountInformationEmbeddables() {
		return List.of(
			AccountInformationEmbeddable.create()
				.withAccuralKey(ACCURAL_KEY_1)
				.withActivity(ACTIVITY_1)
				.withAmount(ACCOUNTING_AMOUNT_1)
				.withArticle(ARTICLE_1)
				.withCostCenter(COST_CENTER_1)
				.withCounterpart(COUNTERPART_1)
				.withDepartment(DEPARTMENT_1)
				.withProject(PROJECT_1)
				.withSubaccount(SUBACCOUNT_1),
			AccountInformationEmbeddable.create()
				.withAccuralKey(ACCURAL_KEY_2)
				.withActivity(ACTIVITY_2)
				.withAmount(ACCOUNTING_AMOUNT_2)
				.withArticle(ARTICLE_2)
				.withCostCenter(COST_CENTER_2)
				.withCounterpart(COUNTERPART_2)
				.withDepartment(DEPARTMENT_2)
				.withProject(PROJECT_2)
				.withSubaccount(SUBACCOUNT_2));
	}

	private static RecipientEntity createRecipientEntity(BillingRecordEntity billingRecordEntity) {
		return RecipientEntity.create()
			.withAddressDetails(createAddressDetailsEmbeddable())
			.withBillingRecord(billingRecordEntity)
			.withId(ID)
			.withOrganizationName(ORGANIZATION_NAME)
			.withLegalId(LEGAL_ID);
	}

	private static AddressDetailsEmbeddable createAddressDetailsEmbeddable() {
		return AddressDetailsEmbeddable.create()
			.withCareOf(CARE_OF)
			.withCity(CITY)
			.withPostalCode(POSTAL_CODE)
			.withStreet(STREET);
	}
}
