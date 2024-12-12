package se.sundsvall.billingpreprocessor.service.mapper;

import static java.time.OffsetDateTime.now;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.DescriptionType.DETAILED;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.DescriptionType.STANDARD;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.Status.APPROVED;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.Type.INTERNAL;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
import se.sundsvall.billingpreprocessor.service.creator.definition.internal.FileHeaderRow;

class InternalInvoiceMapperTest {

	// billingRecord constants
	private static final String ID = randomUUID().toString();
	private static final String CATEGORY = "category";
	private static final String APPROVED_BY = "approvedBy";
	private static final Status STATUS = APPROVED;
	private static final Type TYPE = INTERNAL;
	private static final OffsetDateTime APPROVED_TIMESTAMP = now();
	private static final OffsetDateTime CREATED_TIMESTAMP = now().minusDays(2);
	private static final OffsetDateTime MODIFIED_TIMESTAMP = now().minusDays(1);

	// Invoice constants
	private static final String CUSTOMER_ID = "customerId";
	private static final String CUSTOMER_REFERENCE = "customerReference";
	private static final String DESCRIPTION = "description";
	private static final LocalDate DATE = LocalDate.now().plusDays(15);
	private static final LocalDate DUE_DATE = LocalDate.now().plusDays(30);
	private static final String OUR_REFERENCE = "ourReference";
	private static final String REFERENCE_ID = "referenceId";
	private static final float INVOICE_TOTAL_AMOUNT = 2469f;

	// Invoice row constants
	private static final float COST_PER_UNIT = 123.45f;
	private static final String DETAILED_DESCRIPTION_1 = "detailed_description_1";
	private static final String DETAILED_DESCRIPTION_2 = "detailed_description_2";
	private static final float QUANTITY = 10f;
	private static final String VAT_CODE = "vatCode";

	// Account information constants
	private static final String ACCURAL_KEY = "accuralKey";
	private static final String ACTIVITY = "activity";
	private static final String ARTICLE = "article";
	private static final String COST_CENTER = "costCenter";
	private static final String COUNTERPART = "counterpart";
	private static final String DEPARTMENT = "department";
	private static final String PROJECT = "project";
	private static final String SUBACCOUNT = "subaccount";

	// Recipient constants
	private static final String FIRST_NAME = "firstName";
	private static final String LAST_NAME = "lastName";
	private static final String ORGANIZATION_NAME = "organizationName";
	private static final String PARTY_ID = "partyId";
	private static final String LEGAL_ID = "legalId";
	private static final String USER_ID = "userId";

	// Address details constants
	private static final String CARE_OF = "careOf";
	private static final String CITY = "city";
	private static final String STREET = "STREET";
	private static final String POSTAL_CODE = "postalCode";

	@Test
	void toFileHeader() {
		assertThat(InternalInvoiceMapper.toFileHeader()).isNotNull().isInstanceOf(FileHeaderRow.class);
	}

	@Test
	void toInvoiceHeader() {
		final var bean = InternalInvoiceMapper.toInvoiceHeader(createbillingRecordEntity());

		assertThat(bean.getCustomerId()).isEqualTo(CUSTOMER_ID);
		assertThat(bean.getCustomerReference()).isEqualTo(CUSTOMER_REFERENCE);
		assertThat(bean.getDate()).isEqualTo(DATE);
		assertThat(bean.getDueDate()).isEqualTo(DUE_DATE);
		assertThat(bean.getOurReference()).isEqualTo(OUR_REFERENCE);
	}

	@ParameterizedTest
	@MethodSource("toInvoiceHeaderWhenMissingVitalDataArgumentProvider")
	void toInvoiceHeaderWhenMissingVitalData(InvoiceEntity invoiceEntity, String expectedMessage) {

		final var billingRecordEntity = BillingRecordEntity.create().withInvoice(invoiceEntity);
		final var e = assertThrows(ThrowableProblem.class, () -> InternalInvoiceMapper.toInvoiceHeader(billingRecordEntity));

		assertThat(e.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR);
		assertThat(e.getMessage()).isEqualTo("Internal Server Error: %s".formatted(expectedMessage));
	}

	private static Stream<Arguments> toInvoiceHeaderWhenMissingVitalDataArgumentProvider() {
		return Stream.of(
			Arguments.of(null, "Invoice is not present"),
			Arguments.of(createInvoiceEntity(null).withCustomerId(null), "Customer id is not present"),
			Arguments.of(createInvoiceEntity(null).withCustomerReference(null), "Customer reference is not present"),
			Arguments.of(createInvoiceEntity(null).withOurReference(null), "Our reference is not present"));
	}

	@Test
	void toInvoiceRow() {
		final var bean = InternalInvoiceMapper.toInvoiceRow(createInvoiceRowEntity(1, null));

		assertThat(bean.getCostPerUnit()).isEqualTo(COST_PER_UNIT);
		assertThat(bean.getDescription()).isEqualTo(DESCRIPTION);
		assertThat(bean.getQuantity()).isEqualTo(QUANTITY);
		assertThat(bean.getTotalAmount()).isEqualTo(QUANTITY * COST_PER_UNIT);
	}

	@ParameterizedTest
	@MethodSource("toInvoiceRowWhenMissingVitalDataArgumentProvider")
	void toInvoiceRowWhenMissingVitalData(InvoiceRowEntity invoiceRowEntity, String expectedMessage) {

		final var e = assertThrows(ThrowableProblem.class, () -> InternalInvoiceMapper.toInvoiceRow(invoiceRowEntity));

		assertThat(e.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR);
		assertThat(e.getMessage()).isEqualTo("Internal Server Error: %s".formatted(expectedMessage));
	}

	private static Stream<Arguments> toInvoiceRowWhenMissingVitalDataArgumentProvider() {
		return Stream.of(
			Arguments.of(null, "Description is not present"),
			Arguments.of(createInvoiceRowEntity(1, null).withDescriptions(null), "Description is not present"),
			Arguments.of(createInvoiceRowEntity(1, null).withDescriptions(List.of(DescriptionEntity.create().withType(DETAILED).withText("text"))), "Description is not present"),
			Arguments.of(createInvoiceRowEntity(1, null).withDescriptions(List.of(DescriptionEntity.create().withType(STANDARD).withText("    "))), "Description is not present"),
			Arguments.of(createInvoiceRowEntity(1, null).withTotalAmount(null), "Total amount is not present"));
	}

	@Test
	void toInvoiceDescriptionRow() {
		final var bean = InternalInvoiceMapper.toInvoiceDescriptionRow(createbillingRecordEntity());

		assertThat(bean.getDescription()).isEqualTo(DESCRIPTION);
	}

	@Test
	void toInvoiceAccountingRow() {
		final var bean = InternalInvoiceMapper.toInvoiceAccountingRow(createInvoiceRowEntity(1, null));

		assertThat(bean.getAccuralKey()).isEqualTo(ACCURAL_KEY);
		assertThat(bean.getActivity()).isEqualTo(ACTIVITY);
		assertThat(bean.getCostCenter()).isEqualTo(COST_CENTER);
		assertThat(bean.getCounterpart()).isEqualTo(COUNTERPART);
		assertThat(bean.getDepartment()).isEqualTo(DEPARTMENT);
		assertThat(bean.getObject()).isEqualTo(ARTICLE);
		assertThat(bean.getProject()).isEqualTo(PROJECT);
		assertThat(bean.getSubAccount()).isEqualTo(SUBACCOUNT);
		assertThat(bean.getTotalAmount()).isEqualTo(COST_PER_UNIT * QUANTITY);
	}

	@ParameterizedTest
	@MethodSource("toInvoiceAccountingRowWhenMissingVitalDataArgumentProvider")
	void toInvoiceAccountingRowWhenMissingVitalData(InvoiceRowEntity invoiceRowEntity, String expectedMessage) {

		final var e = assertThrows(ThrowableProblem.class, () -> InternalInvoiceMapper.toInvoiceAccountingRow(invoiceRowEntity));

		assertThat(e.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR);
		assertThat(e.getMessage()).isEqualTo("Internal Server Error: %s".formatted(expectedMessage));
	}

	private static Stream<Arguments> toInvoiceAccountingRowWhenMissingVitalDataArgumentProvider() {
		return Stream.of(
			Arguments.of(createInvoiceRowEntity(1, null).withAccountInformation(null), "Account information is not present"),
			Arguments.of(createInvoiceRowEntity(1, null).withAccountInformation(AccountInformationEmbeddable.create()), "Costcenter is not present"),
			Arguments.of(createInvoiceRowEntity(1, null).withAccountInformation(AccountInformationEmbeddable.create()
				.withCostCenter(COST_CENTER)), "Sub account is not present"),
			Arguments.of(createInvoiceRowEntity(1, null).withAccountInformation(AccountInformationEmbeddable.create()
				.withCostCenter(COST_CENTER)
				.withSubaccount(SUBACCOUNT)), "Department is not present"),
			Arguments.of(createInvoiceRowEntity(1, null).withAccountInformation(AccountInformationEmbeddable.create()
				.withCostCenter(COST_CENTER)
				.withSubaccount(SUBACCOUNT)
				.withDepartment(DEPARTMENT)), "Counterpart is not present"));
	}

	@Test
	void toInvoiceFooter() {
		final var bean = InternalInvoiceMapper.toInvoiceFooter(createbillingRecordEntity());

		assertThat(bean.getTotalAmount()).isEqualTo(INVOICE_TOTAL_AMOUNT);
	}

	@Test
	void toInvoiceFooterWhenMissingVitalData() {
		final var billingRecord = createbillingRecordEntity().withInvoice(null);
		final var e = assertThrows(ThrowableProblem.class, () -> InternalInvoiceMapper.toInvoiceFooter(billingRecord));

		assertThat(e.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR);
		assertThat(e.getMessage()).isEqualTo("Internal Server Error: Invoice is not present");
	}

	private static BillingRecordEntity createbillingRecordEntity() {
		final var billingRecordEntity = BillingRecordEntity.create()
			.withCategory(CATEGORY)
			.withApproved(APPROVED_TIMESTAMP)
			.withApprovedBy(APPROVED_BY)
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
			.withCustomerId(CUSTOMER_ID)
			.withCustomerReference(CUSTOMER_REFERENCE)
			.withDescription(DESCRIPTION)
			.withDate(DATE)
			.withDueDate(DUE_DATE)
			.withId(ID)
			.withOurReference(OUR_REFERENCE)
			.withReferenceId(REFERENCE_ID)
			.withTotalAmount(INVOICE_TOTAL_AMOUNT);

		return invoiceEntity.withInvoiceRows(List.of(createInvoiceRowEntity(1, invoiceEntity), createInvoiceRowEntity(2, invoiceEntity)));
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
			createDescriptionEntity(2, invoiceRowEntity, DETAILED, DETAILED_DESCRIPTION_1),
			createDescriptionEntity(3, invoiceRowEntity, DETAILED, DETAILED_DESCRIPTION_2)));
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
			.withFirstName(FIRST_NAME)
			.withId(ID)
			.withLastName(LAST_NAME)
			.withOrganizationName(ORGANIZATION_NAME)
			.withPartyId(PARTY_ID)
			.withLegalId(LEGAL_ID)
			.withUserId(USER_ID);
	}

	private static AddressDetailsEmbeddable createAddressDetailsEmbeddable() {
		return AddressDetailsEmbeddable.create()
			.withCareOf(CARE_OF)
			.withCity(CITY)
			.withPostalCode(POSTAL_CODE)
			.withStreet(STREET);
	}
}
