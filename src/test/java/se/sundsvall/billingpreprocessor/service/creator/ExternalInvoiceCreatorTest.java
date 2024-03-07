package se.sundsvall.billingpreprocessor.service.creator;

import static java.time.OffsetDateTime.now;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.DescriptionType.DETAILED;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.DescriptionType.STANDARD;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.Status.APPROVED;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.Type.EXTERNAL;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.zalando.problem.ThrowableProblem;

import se.sundsvall.billingpreprocessor.integration.db.model.AccountInformationEmbeddable;
import se.sundsvall.billingpreprocessor.integration.db.model.AddressDetailsEmbeddable;
import se.sundsvall.billingpreprocessor.integration.db.model.BillingRecordEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.DescriptionEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceRowEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.RecipientEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.enums.DescriptionType;
import se.sundsvall.billingpreprocessor.integration.db.model.enums.Status;
import se.sundsvall.billingpreprocessor.integration.db.model.enums.Type;

@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@ActiveProfiles("junit")
class ExternalInvoiceCreatorTest {
	// billingRecord constants
	private static final String ID = randomUUID().toString();
	private static final Status STATUS = APPROVED;
	private static final Type TYPE = EXTERNAL;
	private static final OffsetDateTime APPROVED_TIMESTAMP = now();
	private static final OffsetDateTime CREATED_TIMESTAMP = now().minusDays(2);
	private static final OffsetDateTime MODIFIED_TIMESTAMP = now().minusDays(1);

	// Invoice constants
	private static final String CUSTOMER_REFERENCE = "Snurre Sprätto";
	private static final String DESCRIPTION = "En fakturarad";
	private static final LocalDate DATE = LocalDate.of(2024, 3, 06);
	private static final LocalDate DUE_DATE = LocalDate.of(2024, 3, 30);
	private static final String OUR_REFERENCE = "Johnny Bråttom";
	private static final float INVOICE_TOTAL_AMOUNT = 1395f;

	// Invoice row constants
	private static final float COST_PER_UNIT = 1395f;
	private static final String DETAILED_DESCRIPTION = "En mer detaljerad fakturaradsbeskrivning";
	private static final int QUANTITY = 1;
	private static final String VAT_CODE = "25";

	// Account information constants
	private static final String ACCURAL_KEY = "5647";
	private static final String ACTIVITY = "5756";
	private static final String ARTICLE = "Elcykel";
	private static final String COST_CENTER = "15800100";
	private static final String COUNTERPART = "118";
	private static final String DEPARTMENT = "920360";
	private static final String PROJECT = "11041";
	private static final String SUBACCOUNT = "936300";

	// Recipient constants
	private static final String ORGANIZATION_NAME = "Testbolaget AB";
	private static final String LEGAL_ID = "3456789123";

	// Address details constants
	private static final String CARE_OF = "Jenny Långsam";
	private static final String CITY = "Sundsvall";
	private static final String STREET = "Testgatan 12";
	private static final String POSTAL_CODE = "85643";

	@Autowired
	private ExternalInvoiceCreator creator;

	@Test
	void createInvoiceHeader() throws Exception {
		final var result = creator.createFileHeader();
		final var expected = getResource("validation/expected_external_header_format.txt")
			.replace("yyMMdd", LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd")));

		assertThat(new String(result, StandardCharsets.UTF_8)).isEqualTo(expected);
	}

	@Test
	void createInvoiceDataFromNull() throws Exception {
		assertThat(creator.createInvoiceData(null)).isEmpty();
	}

	@Test
	void createInvoiceDataWhenInvoiceMissing() throws Exception {
		final var input = createbillingRecordEntity().withInvoice(null);
		final var e = assertThrows(ThrowableProblem.class, () -> creator.createInvoiceData(input));

		assertThat(e.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR);
		assertThat(e.getMessage()).isEqualTo("Internal Server Error: Recipient counterpart is not present");
	}

	@Test
	void createInvoiceDataFromEntity() throws Exception {
		final var result = creator.createInvoiceData(createbillingRecordEntity());
		final var expected = getResource("validation/expected_external_invoicedata_format.txt");

		assertThat(new String(result, StandardCharsets.UTF_8)).isEqualTo(expected);
	}

	private String getResource(final String fileName) throws IOException, URISyntaxException {
		return Files.readString(Paths.get(getClass().getClassLoader().getResource(fileName).toURI()), StandardCharsets.UTF_8);
	}

	private static BillingRecordEntity createbillingRecordEntity() {
		final var billingRecordEntity = BillingRecordEntity.create()
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
			.withAccountInformation(createAccountInformationEmbeddable())
			.withCostPerUnit(COST_PER_UNIT)
			.withId(id)
			.withInvoice(invoiceEntity)
			.withQuantity(QUANTITY)
			.withTotalAmount(COST_PER_UNIT * QUANTITY)
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

	private static AccountInformationEmbeddable createAccountInformationEmbeddable() {
		return AccountInformationEmbeddable.create()
			.withAccuralKey(ACCURAL_KEY)
			.withActivity(ACTIVITY)
			.withArticle(ARTICLE)
			.withCostCenter(COST_CENTER)
			.withCounterpart(COUNTERPART)
			.withDepartment(DEPARTMENT)
			.withProject(PROJECT)
			.withSubaccount(SUBACCOUNT);
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