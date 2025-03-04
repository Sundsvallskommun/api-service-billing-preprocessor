package se.sundsvall.billingpreprocessor.service.mapper;

import static java.time.OffsetDateTime.now;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.DescriptionType.DETAILED;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.DescriptionType.STANDARD;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.Status.APPROVED;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.Type.EXTERNAL;

import java.math.BigDecimal;
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
import se.sundsvall.billingpreprocessor.service.creator.definition.external.InvoiceDescriptionRow;

class ExternalInvoiceMapperTest {

	// billingRecord constants
	private static final String ID = randomUUID().toString();
	private static final String CATEGORY = "category";
	private static final String APPROVED_BY = "approvedBy";
	private static final Status STATUS = APPROVED;
	private static final Type TYPE = EXTERNAL;
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
	private static final BigDecimal INVOICE_TOTAL_AMOUNT = BigDecimal.valueOf(2469d);

	// Invoice row constants
	private static final BigDecimal COST_PER_UNIT = BigDecimal.valueOf(123.45d);
	private static final String DETAILED_DESCRIPTION_1 = "detailed_description_1";
	private static final String DETAILED_DESCRIPTION_2 = "detailed_description_2";
	private static final BigDecimal QUANTITY = BigDecimal.valueOf(10d);
	private static final String VAT_CODE = "vatCode";

	// Account information constants
	private static final BigDecimal ACCOUNTING_AMOUNT = BigDecimal.valueOf(9843d);
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
		final var generatingSystem = "generatingSystem";
		final var invoiceType = "invoiceType";

		final var bean = ExternalInvoiceMapper.toFileHeader(generatingSystem, invoiceType);

		assertThat(bean.getCreatedDate()).isEqualTo(LocalDate.now());
		assertThat(bean.getGeneratingSystem()).isEqualTo(generatingSystem);
		assertThat(bean.getInvoiceType()).isEqualTo(invoiceType);
	}

	@ParameterizedTest
	@MethodSource("toFileHeaderWhenMissingVitalDataArgumentProvider")
	void toFileHeaderWhenMissingVitalData(final String generatingSystem, final String invoiceType, final String expectedMessage) {
		final var e = assertThrows(ThrowableProblem.class, () -> ExternalInvoiceMapper.toFileHeader(generatingSystem, invoiceType));

		assertThat(e.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR);
		assertThat(e.getMessage()).isEqualTo("Internal Server Error: %s".formatted(expectedMessage));
	}

	private static Stream<Arguments> toFileHeaderWhenMissingVitalDataArgumentProvider() {
		return Stream.of(
			Arguments.of(null, "value", "Generating system is not present"),
			Arguments.of("value", null, "Invoice type is not present"));
	}

	@Test
	void toCustomerForPrivateCustomer() {
		final var billingRecordEntity = createbillingRecordEntity();
		billingRecordEntity.getRecipient().setOrganizationName(null);

		final var bean = ExternalInvoiceMapper.toCustomer(LEGAL_ID, billingRecordEntity);

		assertThat(bean.getCareOf()).isEqualTo(CARE_OF);
		assertThat(bean.getCounterpart()).isEqualTo(COUNTERPART);
		assertThat(bean.getCustomerName()).isEqualTo(FIRST_NAME + " " + LAST_NAME);
		assertThat(bean.getLegalId()).isEqualTo(LEGAL_ID);
		assertThat(bean.getStreetAddress()).isEqualTo(STREET);
		assertThat(bean.getZipCodeAndCity()).isEqualTo(POSTAL_CODE + " " + CITY);
	}

	@Test
	void toCustomerForOrganization() {
		final var billingRecordEntity = createbillingRecordEntity();

		final var bean = ExternalInvoiceMapper.toCustomer(LEGAL_ID, billingRecordEntity);

		assertThat(bean.getCareOf()).isEqualTo(CARE_OF);
		assertThat(bean.getCounterpart()).isEqualTo(COUNTERPART);
		assertThat(bean.getCustomerName()).isEqualTo(ORGANIZATION_NAME);
		assertThat(bean.getLegalId()).isEqualTo(LEGAL_ID);
		assertThat(bean.getStreetAddress()).isEqualTo(STREET);
		assertThat(bean.getZipCodeAndCity()).isEqualTo(POSTAL_CODE + " " + CITY);
	}

	@Test
	void toCustomerWhenMissingCounterpartData() {
		final var billingRecordEntity = createbillingRecordEntity();
		billingRecordEntity.getInvoice().getInvoiceRows().stream()
			.map(InvoiceRowEntity::getAccountInformation)
			.flatMap(List::stream)
			.forEach(ac -> ac.setCounterpart(" "));

		final var e = assertThrows(ThrowableProblem.class, () -> ExternalInvoiceMapper.toCustomer(LEGAL_ID, billingRecordEntity));

		assertThat(e.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR);
		assertThat(e.getMessage()).isEqualTo("Internal Server Error: Recipient counterpart is not present");
	}

	@Test
	void toCustomerWhenMissingInvoiceData() {
		final var billingRecordEntity = createbillingRecordEntity().withInvoice(null);

		final var e = assertThrows(ThrowableProblem.class, () -> ExternalInvoiceMapper.toCustomer(LEGAL_ID, billingRecordEntity));

		assertThat(e.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR);
		assertThat(e.getMessage()).isEqualTo("Internal Server Error: Recipient counterpart is not present");
	}

	@ParameterizedTest
	@MethodSource("toCustomerWhenMissingVitalDataArgumentProvider")
	void toCustomerWhenMissingVitalData(final String legalId, final RecipientEntity recipientEntity, final String expectedMessage) {

		final var billingRecordEntity = BillingRecordEntity.create().withRecipient(recipientEntity);
		final var e = assertThrows(ThrowableProblem.class, () -> ExternalInvoiceMapper.toCustomer(legalId, billingRecordEntity));

		assertThat(e.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR);
		assertThat(e.getMessage()).isEqualTo("Internal Server Error: %s".formatted(expectedMessage));
	}

	private static Stream<Arguments> toCustomerWhenMissingVitalDataArgumentProvider() {
		return Stream.of(
			Arguments.of("value", null, "Recipient is not present"),
			Arguments.of(null, RecipientEntity.create(), "LegalId is not present"),
			Arguments.of("value", createRecipientEntity(null).withFirstName(null).withLastName(null).withOrganizationName(null), "Recipient name is not present"),
			Arguments.of("value", createRecipientEntity(null).withAddressDetails(createAddressDetailsEmbeddable().withStreet(null)), "Recipient street address is not present"),
			Arguments.of("value", createRecipientEntity(null).withAddressDetails(createAddressDetailsEmbeddable().withPostalCode(null)), "Recipient zip code or city is not present"),
			Arguments.of("value", createRecipientEntity(null).withAddressDetails(createAddressDetailsEmbeddable().withCity(null)), "Recipient zip code or city is not present"));
	}

	@Test
	void toInvoiceHeader() {
		final var bean = ExternalInvoiceMapper.toInvoiceHeader(LEGAL_ID, createbillingRecordEntity());

		assertThat(bean.getCustomerReference()).isEqualTo(CUSTOMER_REFERENCE);
		assertThat(bean.getDueDate()).isEqualTo(DUE_DATE);
		assertThat(bean.getLegalId()).isEqualTo(LEGAL_ID);
		assertThat(bean.getOurReference()).isEqualTo(OUR_REFERENCE);
	}

	@ParameterizedTest
	@MethodSource("toInvoiceHeaderWhenMissingVitalDataArgumentProvider")
	void toInvoiceHeaderWhenMissingVitalData(final String legalId, final InvoiceEntity invoiceEntity, final String expectedMessage) {

		final var billingRecordEntity = BillingRecordEntity.create().withInvoice(invoiceEntity);
		final var e = assertThrows(ThrowableProblem.class, () -> ExternalInvoiceMapper.toInvoiceHeader(legalId, billingRecordEntity));

		assertThat(e.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR);
		assertThat(e.getMessage()).isEqualTo("Internal Server Error: %s".formatted(expectedMessage));
	}

	private static Stream<Arguments> toInvoiceHeaderWhenMissingVitalDataArgumentProvider() {
		return Stream.of(
			Arguments.of("value", null, "Invoice is not present"),
			Arguments.of(null, createInvoiceEntity(null), "LegalId is not present"),
			Arguments.of("value", createInvoiceEntity(null).withCustomerReference(null), "Customer reference is not present"));
	}

	@Test
	void toInvoiceRow() {
		final var bean = ExternalInvoiceMapper.toInvoiceRow(LEGAL_ID, createInvoiceRowEntity(1, null));

		assertThat(bean.getCostPerUnit()).isEqualTo(COST_PER_UNIT);
		assertThat(bean.getLegalId()).isEqualTo(LEGAL_ID);
		assertThat(bean.getQuantity()).isEqualTo(QUANTITY);
		assertThat(bean.getText()).isEqualTo(DESCRIPTION);
		assertThat(bean.getTotalAmount()).isEqualTo(QUANTITY.multiply(COST_PER_UNIT));
		assertThat(bean.getVatCode()).isEqualTo(VAT_CODE);
	}

	@ParameterizedTest
	@MethodSource("toInvoiceRowWhenMissingVitalDataArgumentProvider")
	void toInvoiceRowWhenMissingVitalData(final String legalId, final InvoiceRowEntity invoiceRowEntity, final String expectedMessage) {

		final var e = assertThrows(ThrowableProblem.class, () -> ExternalInvoiceMapper.toInvoiceRow(legalId, invoiceRowEntity));

		assertThat(e.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR);
		assertThat(e.getMessage()).isEqualTo("Internal Server Error: %s".formatted(expectedMessage));
	}

	private static Stream<Arguments> toInvoiceRowWhenMissingVitalDataArgumentProvider() {
		return Stream.of(
			Arguments.of(null, createInvoiceRowEntity(1, null), "LegalId is not present"),
			Arguments.of("value", createInvoiceRowEntity(1, null).withDescriptions(null), "Description is not present"),
			Arguments.of("value", createInvoiceRowEntity(1, null).withDescriptions(List.of(DescriptionEntity.create().withType(DETAILED).withText("text"))), "Description is not present"),
			Arguments.of("value", createInvoiceRowEntity(1, null).withDescriptions(List.of(DescriptionEntity.create().withType(STANDARD).withText("    "))), "Description is not present"),
			Arguments.of("value", createInvoiceRowEntity(1, null).withVatCode(null), "Vat code is not present"));
	}

	@Test
	void toInvoiceDescriptionRows() {
		final var invoiceEntity = createInvoiceRowEntity(1, null);
		invoiceEntity.getDescriptions().add(DescriptionEntity.create().withType(DETAILED).withText(" ")); // Add empty detail to verify filtering of blank content

		final var list = ExternalInvoiceMapper.toInvoiceDescriptionRows(LEGAL_ID, invoiceEntity);

		assertThat(list).hasSize(2)
			.extracting(
				InvoiceDescriptionRow::getDescription,
				InvoiceDescriptionRow::getLegalId)
			.containsExactlyInAnyOrder(
				tuple(DETAILED_DESCRIPTION_1, LEGAL_ID),
				tuple(DETAILED_DESCRIPTION_2, LEGAL_ID));
	}

	@Test
	void toInvoiceAccountingRow() {
		final var result = ExternalInvoiceMapper.toInvoiceAccountingRows(createInvoiceRowEntity(1, null));

		assertThat(result).hasSize(1).satisfiesExactly(bean -> {
			assertThat(bean.getAccuralKey()).isEqualTo(ACCURAL_KEY);
			assertThat(bean.getActivity()).isEqualTo(ACTIVITY);
			assertThat(bean.getCostCenter()).isEqualTo(COST_CENTER);
			assertThat(bean.getCounterpart()).isEqualTo(COUNTERPART);
			assertThat(bean.getObject()).isEqualTo(ARTICLE);
			assertThat(bean.getOperation()).isEqualTo(DEPARTMENT);
			assertThat(bean.getProject()).isEqualTo(PROJECT);
			assertThat(bean.getSubAccount()).isEqualTo(SUBACCOUNT);
			assertThat(bean.getAmount()).isEqualTo(ACCOUNTING_AMOUNT);
		});
	}

	@ParameterizedTest
	@MethodSource("toInvoiceAccountingRowWhenMissingVitalDataArgumentProvider")
	void toInvoiceAccountingRowWhenMissingVitalData(final InvoiceRowEntity invoiceRowEntity, final String expectedMessage) {

		final var e = assertThrows(ThrowableProblem.class, () -> ExternalInvoiceMapper.toInvoiceAccountingRows(invoiceRowEntity));

		assertThat(e.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR);
		assertThat(e.getMessage()).isEqualTo("Internal Server Error: %s".formatted(expectedMessage));
	}

	private static Stream<Arguments> toInvoiceAccountingRowWhenMissingVitalDataArgumentProvider() {
		return Stream.of(
			Arguments.of(createInvoiceRowEntity(1, null).withAccountInformation(List.of(AccountInformationEmbeddable.create())), "Costcenter is not present"),
			Arguments.of(createInvoiceRowEntity(1, null).withAccountInformation(List.of(AccountInformationEmbeddable.create()
				.withCostCenter(COST_CENTER))), "Sub account is not present"),
			Arguments.of(createInvoiceRowEntity(1, null).withAccountInformation(List.of(AccountInformationEmbeddable.create()
				.withCostCenter(COST_CENTER)
				.withSubaccount(SUBACCOUNT))), "Operation is not present"),
			Arguments.of(createInvoiceRowEntity(1, null).withAccountInformation(List.of(AccountInformationEmbeddable.create()
				.withCostCenter(COST_CENTER)
				.withSubaccount(SUBACCOUNT)
				.withDepartment(DEPARTMENT))), "Counterpart is not present"),
			Arguments.of(createInvoiceRowEntity(1, null).withAccountInformation(List.of(AccountInformationEmbeddable.create()
				.withCostCenter(COST_CENTER)
				.withSubaccount(SUBACCOUNT)
				.withDepartment(DEPARTMENT)
				.withCounterpart(COUNTERPART))), "Accounting amount is not present"));
	}

	@Test
	void toInvoiceFooter() {
		final var bean = ExternalInvoiceMapper.toInvoiceFooter(createbillingRecordEntity());

		assertThat(bean.getTotalAmount()).isEqualTo(INVOICE_TOTAL_AMOUNT);
	}

	@Test
	void toInvoiceFooterWhenMissingVitalData() {
		final var billingRecordEntity = createbillingRecordEntity().withInvoice(null);
		final var e = assertThrows(ThrowableProblem.class, () -> ExternalInvoiceMapper.toInvoiceFooter(billingRecordEntity));

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

	private static InvoiceEntity createInvoiceEntity(final BillingRecordEntity billingRecordEntity) {
		final var invoiceEntity = InvoiceEntity.create()
			.withBillingRecord(billingRecordEntity)
			.withCustomerId(CUSTOMER_ID)
			.withCustomerReference(CUSTOMER_REFERENCE)
			.withDescription(DESCRIPTION)
			.withDate(DATE)
			.withDueDate(DUE_DATE)
			.withId(ID)
			.withOurReference(OUR_REFERENCE)
			.withTotalAmount(INVOICE_TOTAL_AMOUNT);

		return invoiceEntity.withInvoiceRows(List.of(createInvoiceRowEntity(1, invoiceEntity), createInvoiceRowEntity(2, invoiceEntity)));
	}

	private static InvoiceRowEntity createInvoiceRowEntity(final int id, final InvoiceEntity invoiceEntity) {
		final var invoiceRowEntity = InvoiceRowEntity.create()
			.withAccountInformation(List.of(createAccountInformationEmbeddable()))
			.withCostPerUnit(COST_PER_UNIT)
			.withId(id)
			.withInvoice(invoiceEntity)
			.withQuantity(QUANTITY)
			.withTotalAmount(COST_PER_UNIT.multiply(QUANTITY))
			.withVatCode(VAT_CODE);

		return invoiceRowEntity.withDescriptions(List.of(
			createDescriptionEntity(1, invoiceRowEntity, STANDARD, DESCRIPTION),
			createDescriptionEntity(2, invoiceRowEntity, DETAILED, DETAILED_DESCRIPTION_1),
			createDescriptionEntity(3, invoiceRowEntity, DETAILED, DETAILED_DESCRIPTION_2)));
	}

	private static DescriptionEntity createDescriptionEntity(final int id, final InvoiceRowEntity invoiceRowEntity, final DescriptionType type, final String text) {
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
			.withAmount(ACCOUNTING_AMOUNT)
			.withArticle(ARTICLE)
			.withCostCenter(COST_CENTER)
			.withCounterpart(COUNTERPART)
			.withDepartment(DEPARTMENT)
			.withProject(PROJECT)
			.withSubaccount(SUBACCOUNT);
	}

	private static RecipientEntity createRecipientEntity(final BillingRecordEntity billingRecordEntity) {
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
