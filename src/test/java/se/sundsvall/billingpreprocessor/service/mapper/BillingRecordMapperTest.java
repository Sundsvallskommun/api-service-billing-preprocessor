package se.sundsvall.billingpreprocessor.service.mapper;

import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.UUID.randomUUID;
import static org.apache.commons.lang3.ObjectUtils.allNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.DescriptionType.DETAILED;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.DescriptionType.STANDARD;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import se.sundsvall.billingpreprocessor.api.model.AccountInformation;
import se.sundsvall.billingpreprocessor.api.model.AddressDetails;
import se.sundsvall.billingpreprocessor.api.model.BillingRecord;
import se.sundsvall.billingpreprocessor.api.model.Invoice;
import se.sundsvall.billingpreprocessor.api.model.InvoiceRow;
import se.sundsvall.billingpreprocessor.api.model.Recipient;
import se.sundsvall.billingpreprocessor.api.model.enums.Status;
import se.sundsvall.billingpreprocessor.integration.db.model.AccountInformationEmbeddable;
import se.sundsvall.billingpreprocessor.integration.db.model.AddressDetailsEmbeddable;
import se.sundsvall.billingpreprocessor.integration.db.model.BillingRecordEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.DescriptionEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceRowEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.RecipientEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.enums.DescriptionType;

class BillingRecordMapperTest {

	private static final String MUNICIPALITY_ID = "municipalityId";

	// billingRecord constants
	private static final String ID = randomUUID().toString();
	private static final String CATEGORY = "category";
	private static final String APPROVED_BY = "approvedBy";
	private static final se.sundsvall.billingpreprocessor.integration.db.model.enums.Status STATUS = se.sundsvall.billingpreprocessor.integration.db.model.enums.Status.APPROVED;
	private static final se.sundsvall.billingpreprocessor.integration.db.model.enums.Type TYPE = se.sundsvall.billingpreprocessor.integration.db.model.enums.Type.EXTERNAL;
	private static final OffsetDateTime APPROVED_TIMESTAMP = now();
	private static final OffsetDateTime CREATED_TIMESTAMP = now().minusDays(2);
	private static final OffsetDateTime MODIFIED_TIMESTAMP = now().minusDays(1);
	private static final Map<String, String> EXTRA_PARAMETERS = Map.of("key", "value", "key2", "value2");

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
	private static final String DESCRIPTION_1 = "description_1";
	private static final String DESCRIPTION_2 = "description_2";
	private static final List<String> DESCRIPTIONS = List.of(DESCRIPTION_1, DESCRIPTION_2);
	private static final String DETAILED_DESCRIPTION_1 = "detailed_description_1";
	private static final String DETAILED_DESCRIPTION_2 = "detailed_description_2";
	private static final List<String> DETAILED_DESCRIPTIONS = List.of(DETAILED_DESCRIPTION_1, DETAILED_DESCRIPTION_2);
	private static final float QUANTITY = 10f;
	private static final String VAT_CODE = "vatCode";

	// Account information constants
	private static final String ACCURAL_KEY = "accuralKey";
	private static final String ACTIVITY = "activity";
	private static final Float ACCOUNTING_AMOUNT = 3359.89f;
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
	void tobillingRecordEntityForFullInstance() {
		final var billingRecord = createbillingRecord();
		final var billingRecordEntity = BillingRecordMapper.toBillingRecordEntity(billingRecord, MUNICIPALITY_ID);

		// Assert billing record entity values
		assertThat(billingRecordEntity.getCategory()).isEqualTo(CATEGORY);
		assertThat(billingRecordEntity.getApproved()).isCloseTo(now(), within(2, SECONDS));
		assertThat(billingRecordEntity.getApprovedBy()).isEqualTo(APPROVED_BY);
		assertThat(billingRecordEntity.getStatus()).isEqualTo(STATUS);
		assertThat(billingRecordEntity.getType()).isEqualTo(TYPE);
		assertThat(billingRecordEntity.getMunicipalityId()).isEqualTo(MUNICIPALITY_ID);
		assertThat(billingRecordEntity.getExtraParameters()).isEqualTo(EXTRA_PARAMETERS);

		assertThat(billingRecordEntity)
			.extracting(
				BillingRecordEntity::getCreated,
				BillingRecordEntity::getId,
				BillingRecordEntity::getModified)
			.containsOnlyNulls();

		// Assert Recipient entity values
		assertThat(billingRecordEntity.getRecipient()).isNotNull()
			.extracting(
				RecipientEntity::getBillingRecord,
				RecipientEntity::getFirstName,
				RecipientEntity::getId,
				RecipientEntity::getLastName,
				RecipientEntity::getOrganizationName,
				RecipientEntity::getPartyId,
				RecipientEntity::getLegalId,
				RecipientEntity::getUserId)
			.containsExactly(
				billingRecordEntity,
				FIRST_NAME,
				null,
				LAST_NAME,
				ORGANIZATION_NAME,
				PARTY_ID,
				LEGAL_ID,
				USER_ID);

		// Assert address details embeddable values
		assertThat(billingRecordEntity.getRecipient().getAddressDetails()).isNotNull()
			.extracting(
				AddressDetailsEmbeddable::getCareOf,
				AddressDetailsEmbeddable::getCity,
				AddressDetailsEmbeddable::getPostalCode,
				AddressDetailsEmbeddable::getStreet)
			.containsExactly(
				CARE_OF,
				CITY,
				POSTAL_CODE,
				STREET);

		// Assert invoice entity values
		assertThat(billingRecordEntity.getInvoice()).isNotNull()
			.extracting(
				InvoiceEntity::getBillingRecord,
				InvoiceEntity::getCustomerId,
				InvoiceEntity::getCustomerReference,
				InvoiceEntity::getDescription,
				InvoiceEntity::getDate,
				InvoiceEntity::getDueDate,
				InvoiceEntity::getId,
				InvoiceEntity::getOurReference,
				InvoiceEntity::getReferenceId,
				InvoiceEntity::getTotalAmount)
			.containsExactly(
				billingRecordEntity,
				CUSTOMER_ID,
				CUSTOMER_REFERENCE,
				DESCRIPTION,
				DATE,
				DUE_DATE,
				null,
				OUR_REFERENCE,
				REFERENCE_ID,
				INVOICE_TOTAL_AMOUNT);

		// Assert invoice row entity values
		assertThat(billingRecordEntity.getInvoice().getInvoiceRows()).isNotNull()
			.extracting(
				InvoiceRowEntity::getCostPerUnit,
				InvoiceRowEntity::getId,
				InvoiceRowEntity::getQuantity,
				InvoiceRowEntity::getTotalAmount,
				InvoiceRowEntity::getVatCode)
			.containsExactly(
				tuple(COST_PER_UNIT, 0L, QUANTITY, COST_PER_UNIT * QUANTITY, VAT_CODE),
				tuple(COST_PER_UNIT, 0L, QUANTITY, COST_PER_UNIT * QUANTITY, VAT_CODE));

		assertThat(billingRecordEntity.getInvoice().getInvoiceRows())
			.extracting(InvoiceRowEntity::getInvoice).isNotNull().allMatch(invoice -> invoice == billingRecordEntity.getInvoice());

		assertThat(billingRecordEntity.getInvoice().getInvoiceRows()).hasSize(2).satisfiesExactlyInAnyOrder(invoiceRow -> {
			// Assert invoice row account information embeddable values
			assertThat(invoiceRow.getAccountInformation()).hasSize(1)
				.extracting(
					AccountInformationEmbeddable::getAccuralKey,
					AccountInformationEmbeddable::getActivity,
					AccountInformationEmbeddable::getAmount,
					AccountInformationEmbeddable::getArticle,
					AccountInformationEmbeddable::getCostCenter,
					AccountInformationEmbeddable::getCounterpart,
					AccountInformationEmbeddable::getDepartment,
					AccountInformationEmbeddable::getProject,
					AccountInformationEmbeddable::getSubaccount)
				.containsExactly(
					tuple(
						ACCURAL_KEY,
						ACTIVITY,
						ACCOUNTING_AMOUNT,
						ARTICLE,
						COST_CENTER,
						COUNTERPART,
						DEPARTMENT,
						PROJECT,
						SUBACCOUNT));

		}, invoiceRow -> {
			assertThat(invoiceRow.getAccountInformation()).isEmpty();
		}).allSatisfy(invoiceRow -> {
			// Assert invoice row description entity values
			assertThat(invoiceRow.getDescriptions()).isNotEmpty()
				.extracting(
					DescriptionEntity::getId,
					DescriptionEntity::getInvoiceRow,
					DescriptionEntity::getText,
					DescriptionEntity::getType)
				.containsExactly(
					tuple(0L, invoiceRow, DESCRIPTION_1, STANDARD),
					tuple(0L, invoiceRow, DESCRIPTION_2, STANDARD),
					tuple(0L, invoiceRow, DETAILED_DESCRIPTION_1, DETAILED),
					tuple(0L, invoiceRow, DETAILED_DESCRIPTION_2, DETAILED));
		});
	}

	@Test
	void tobillingRecordEntityForNull() {
		assertThat(BillingRecordMapper.toBillingRecordEntity(null, MUNICIPALITY_ID)).isNull();
	}

	@Test
	void tobillingRecordEntityWithNoRecipient() {
		final var billingRecord = createbillingRecord().withRecipient(null);
		final var billingRecordEntity = BillingRecordMapper.toBillingRecordEntity(billingRecord, MUNICIPALITY_ID);

		assertThat(billingRecordEntity.getRecipient()).isNull();
	}

	@ParameterizedTest
	@EnumSource(value = Status.class, names = "APPROVED", mode = EXCLUDE)
	void verifyNoApprovedDateOrApprovedBy(Status status) {
		final var billingRecord = createbillingRecord().withStatus(status);
		final var billingRecordEntity = BillingRecordMapper.toBillingRecordEntity(billingRecord, MUNICIPALITY_ID);

		assertThat(billingRecordEntity.getApproved()).isNull();
		assertThat(billingRecordEntity.getApprovedBy()).isNull();
	}

	@Test
	void toBillingRecordEntitiesForFullInstance() {
		final var records = new ArrayList<BillingRecord>();
		records.add(createbillingRecord());
		records.add(null);

		final var billingRecordEntities = BillingRecordMapper.toBillingRecordEntities(records, MUNICIPALITY_ID);

		assertThat(billingRecordEntities).isNotNull().hasSize(1);
		final var billingRecordEntity = billingRecordEntities.get(0);

		// Assert billing record entity values
		assertThat(billingRecordEntity.getCategory()).isEqualTo(CATEGORY);
		assertThat(billingRecordEntity.getApproved()).isCloseTo(now(), within(2, SECONDS));
		assertThat(billingRecordEntity.getApprovedBy()).isEqualTo(APPROVED_BY);
		assertThat(billingRecordEntity.getStatus()).isEqualTo(STATUS);
		assertThat(billingRecordEntity.getType()).isEqualTo(TYPE);
		assertThat(billingRecordEntity.getMunicipalityId()).isEqualTo(MUNICIPALITY_ID);

		assertThat(billingRecordEntity)
			.extracting(
				BillingRecordEntity::getCreated,
				BillingRecordEntity::getId,
				BillingRecordEntity::getModified)
			.containsOnlyNulls();

		// Assert recipient entity values
		assertThat(billingRecordEntity.getRecipient()).isNotNull()
			.extracting(
				RecipientEntity::getBillingRecord,
				RecipientEntity::getFirstName,
				RecipientEntity::getId,
				RecipientEntity::getLastName,
				RecipientEntity::getOrganizationName,
				RecipientEntity::getPartyId,
				RecipientEntity::getLegalId,
				RecipientEntity::getUserId)
			.containsExactly(
				billingRecordEntity,
				FIRST_NAME,
				null,
				LAST_NAME,
				ORGANIZATION_NAME,
				PARTY_ID,
				LEGAL_ID,
				USER_ID);

		// Assert address details embeddable values
		assertThat(billingRecordEntity.getRecipient().getAddressDetails()).isNotNull()
			.extracting(
				AddressDetailsEmbeddable::getCareOf,
				AddressDetailsEmbeddable::getCity,
				AddressDetailsEmbeddable::getPostalCode,
				AddressDetailsEmbeddable::getStreet)
			.containsExactly(
				CARE_OF,
				CITY,
				POSTAL_CODE,
				STREET);

		// Assert invoice entity values
		assertThat(billingRecordEntity.getInvoice()).isNotNull()
			.extracting(
				InvoiceEntity::getBillingRecord,
				InvoiceEntity::getCustomerId,
				InvoiceEntity::getCustomerReference,
				InvoiceEntity::getDescription,
				InvoiceEntity::getDate,
				InvoiceEntity::getDueDate,
				InvoiceEntity::getId,
				InvoiceEntity::getOurReference,
				InvoiceEntity::getReferenceId,
				InvoiceEntity::getTotalAmount)
			.containsExactly(
				billingRecordEntity,
				CUSTOMER_ID,
				CUSTOMER_REFERENCE,
				DESCRIPTION,
				DATE,
				DUE_DATE,
				null,
				OUR_REFERENCE,
				REFERENCE_ID,
				INVOICE_TOTAL_AMOUNT);

		// Assert invoice row entity values
		assertThat(billingRecordEntity.getInvoice().getInvoiceRows()).isNotNull()
			.extracting(
				InvoiceRowEntity::getCostPerUnit,
				InvoiceRowEntity::getId,
				InvoiceRowEntity::getQuantity,
				InvoiceRowEntity::getTotalAmount,
				InvoiceRowEntity::getVatCode)
			.containsExactly(
				tuple(COST_PER_UNIT, 0L, QUANTITY, COST_PER_UNIT * QUANTITY, VAT_CODE),
				tuple(COST_PER_UNIT, 0L, QUANTITY, COST_PER_UNIT * QUANTITY, VAT_CODE));

		assertThat(billingRecordEntity.getInvoice().getInvoiceRows())
			.extracting(InvoiceRowEntity::getInvoice).isNotNull().allMatch(invoice -> invoice == billingRecordEntity.getInvoice());

		assertThat(billingRecordEntity.getInvoice().getInvoiceRows()).hasSize(2).satisfiesExactlyInAnyOrder(invoiceRow -> {
			// Assert invoice row account information embeddable values
			assertThat(invoiceRow.getAccountInformation()).hasSize(1)
				.extracting(
					AccountInformationEmbeddable::getAccuralKey,
					AccountInformationEmbeddable::getActivity,
					AccountInformationEmbeddable::getAmount,
					AccountInformationEmbeddable::getArticle,
					AccountInformationEmbeddable::getCostCenter,
					AccountInformationEmbeddable::getCounterpart,
					AccountInformationEmbeddable::getDepartment,
					AccountInformationEmbeddable::getProject,
					AccountInformationEmbeddable::getSubaccount)
				.containsExactly(
					tuple(
						ACCURAL_KEY,
						ACTIVITY,
						ACCOUNTING_AMOUNT,
						ARTICLE,
						COST_CENTER,
						COUNTERPART,
						DEPARTMENT,
						PROJECT,
						SUBACCOUNT));

		}, invoiceRow -> {
			assertThat(invoiceRow.getAccountInformation()).isEmpty();
		}).allSatisfy(invoiceRow -> {
			// Assert invoice row description entity values
			assertThat(invoiceRow.getDescriptions()).isNotEmpty()
				.extracting(
					DescriptionEntity::getId,
					DescriptionEntity::getInvoiceRow,
					DescriptionEntity::getText,
					DescriptionEntity::getType)
				.containsExactly(
					tuple(0L, invoiceRow, DESCRIPTION_1, STANDARD),
					tuple(0L, invoiceRow, DESCRIPTION_2, STANDARD),
					tuple(0L, invoiceRow, DETAILED_DESCRIPTION_1, DETAILED),
					tuple(0L, invoiceRow, DETAILED_DESCRIPTION_2, DETAILED));
		});
	}

	@Test
	void tobillingRecordEntitiesWithNoRecipient() {
		final var billingRecord = createbillingRecord().withRecipient(null);
		final var billingRecordEntity = BillingRecordMapper.toBillingRecordEntities(List.of(billingRecord), MUNICIPALITY_ID);

		assertThat(billingRecordEntity.getFirst().getRecipient()).isNull();
	}

	@Test
	void tobillingRecordEntitiesWithNoInvoice() {
		final var billingRecord = createbillingRecord().withInvoice(null);
		final var billingRecordEntity = BillingRecordMapper.toBillingRecordEntities(List.of(billingRecord), MUNICIPALITY_ID);

		assertThat(billingRecordEntity.getFirst().getInvoice()).isNotNull().hasAllNullFieldsOrPropertiesExcept("billingRecord");
	}

	@ParameterizedTest
	@EnumSource(value = Status.class, names = "APPROVED", mode = EXCLUDE)
	void updatebillingRecordEntityFromOtherStatusToApprovedStatus(Status status) {
		final var billingEntity = BillingRecordMapper.toBillingRecordEntity(createbillingRecord().withStatus(status).withApprovedBy(null), MUNICIPALITY_ID);
		final var billingRecord = createbillingRecord();
		final var updatedEntity = BillingRecordMapper.updateEntity(billingEntity, billingRecord);

		assertThat(updatedEntity.getApproved()).isCloseTo(now(), within(2, SECONDS));
		assertThat(updatedEntity.getApprovedBy()).isEqualTo(APPROVED_BY);
		assertThat(updatedEntity.getModified()).isCloseTo(now(), within(2, SECONDS));
	}

	@ParameterizedTest
	@ValueSource(strings = "RANDOM_APPROVED_BY")
	@NullSource
	void updatebillingRecordEntityWhenStatusApproved(String approvedBy) {
		final var billingEntity = BillingRecordMapper.toBillingRecordEntity(createbillingRecord(), MUNICIPALITY_ID);
		final var approvedTimestamp = billingEntity.getApproved();
		final var updatedEntity = BillingRecordMapper.updateEntity(billingEntity, createbillingRecord().withApprovedBy(approvedBy));

		assertThat(updatedEntity.getApproved()).isEqualTo(approvedTimestamp);
		assertThat(updatedEntity.getApprovedBy()).isEqualTo(APPROVED_BY);
		assertThat(updatedEntity.getModified()).isCloseTo(now(), within(2, SECONDS));
	}

	@ParameterizedTest
	@EnumSource(value = Status.class, names = "APPROVED", mode = EXCLUDE)
	void updatebillingRecordEntityFromStatusApprovedToOtherStatus(Status status) {
		final var billingEntity = BillingRecordMapper.toBillingRecordEntity(createbillingRecord(), MUNICIPALITY_ID);
		final var approvedTimestamp = billingEntity.getApproved();
		final var updatedEntity = BillingRecordMapper.updateEntity(billingEntity, createbillingRecord());

		assertThat(updatedEntity.getApproved()).isEqualTo(approvedTimestamp);
		assertThat(updatedEntity.getModified()).isCloseTo(now(), within(2, SECONDS));
	}

	@Test
	void tobillingRecordEntityWithNoAccountInformation() {
		final var billingRecord = createbillingRecord();
		billingRecord.getInvoice().getInvoiceRows().forEach(row -> row.setAccountInformation(null));
		final var billingRecordEntity = BillingRecordMapper.toBillingRecordEntity(billingRecord, MUNICIPALITY_ID);

		billingRecordEntity.getInvoice().getInvoiceRows().forEach(row -> assertThat(row.getAccountInformation()).isEmpty());
	}

	@Test
	void tobillingRecordForFullEntity() {
		final var billingRecordEntity = createbillingRecordEntity();
		final var billingRecord = BillingRecordMapper.toBillingRecord(billingRecordEntity);

		// Assert billing record values
		assertThat(billingRecord.getCategory()).isEqualTo(CATEGORY);
		assertThat(billingRecord.getApproved()).isEqualTo(APPROVED_TIMESTAMP);
		assertThat(billingRecord.getApprovedBy()).isEqualTo(APPROVED_BY);
		assertThat(billingRecord.getCreated()).isEqualTo(CREATED_TIMESTAMP);
		assertThat(billingRecord.getId()).isEqualTo(ID);
		assertThat(billingRecord.getModified()).isEqualTo(MODIFIED_TIMESTAMP);
		assertThat(billingRecord.getStatus()).isEqualTo(se.sundsvall.billingpreprocessor.api.model.enums.Status.valueOf(STATUS.toString()));
		assertThat(billingRecord.getType()).isEqualTo(se.sundsvall.billingpreprocessor.api.model.enums.Type.valueOf(TYPE.toString()));

		// Assert Recipient values
		assertThat(billingRecord.getRecipient()).isNotNull()
			.extracting(
				Recipient::getFirstName,
				Recipient::getLastName,
				Recipient::getOrganizationName,
				Recipient::getPartyId,
				Recipient::getLegalId,
				Recipient::getUserId)
			.containsExactly(
				FIRST_NAME,
				LAST_NAME,
				ORGANIZATION_NAME,
				PARTY_ID,
				LEGAL_ID,
				USER_ID);

		// Assert address details values
		assertThat(billingRecord.getRecipient().getAddressDetails()).isNotNull()
			.extracting(
				AddressDetails::getCareOf,
				AddressDetails::getCity,
				AddressDetails::getPostalCode,
				AddressDetails::getStreet)
			.containsExactly(
				CARE_OF,
				CITY,
				POSTAL_CODE,
				STREET);

		// Assert invoice values
		assertThat(billingRecord.getInvoice()).isNotNull()
			.extracting(
				Invoice::getCustomerId,
				Invoice::getCustomerReference,
				Invoice::getDescription,
				Invoice::getDate,
				Invoice::getDueDate,
				Invoice::getOurReference,
				Invoice::getReferenceId,
				Invoice::getTotalAmount)
			.containsExactly(
				CUSTOMER_ID,
				CUSTOMER_REFERENCE,
				DESCRIPTION,
				DATE,
				DUE_DATE,
				OUR_REFERENCE,
				REFERENCE_ID,
				INVOICE_TOTAL_AMOUNT);

		// Assert invoice row values
		assertThat(billingRecord.getInvoice().getInvoiceRows()).isNotNull()
			.extracting(
				InvoiceRow::getCostPerUnit,
				InvoiceRow::getQuantity,
				InvoiceRow::getTotalAmount,
				InvoiceRow::getVatCode)
			.containsExactly(
				tuple(COST_PER_UNIT, QUANTITY, COST_PER_UNIT * QUANTITY, VAT_CODE),
				tuple(COST_PER_UNIT, QUANTITY, COST_PER_UNIT * QUANTITY, VAT_CODE));

		billingRecord.getInvoice().getInvoiceRows().forEach(invoiceRow -> {
			// Assert invoice row account information values
			assertThat(invoiceRow.getAccountInformation()).isNotNull()
				.extracting(
					AccountInformation::getAccuralKey,
					AccountInformation::getActivity,
					AccountInformation::getArticle,
					AccountInformation::getCostCenter,
					AccountInformation::getCounterpart,
					AccountInformation::getDepartment,
					AccountInformation::getProject,
					AccountInformation::getSubaccount)
				.containsExactly(tuple(
					ACCURAL_KEY,
					ACTIVITY,
					ARTICLE,
					COST_CENTER,
					COUNTERPART,
					DEPARTMENT,
					PROJECT,
					SUBACCOUNT));

			// Assert invoice row description values
			assertThat(invoiceRow.getDescriptions()).containsExactly(DESCRIPTION_1);
			assertThat(invoiceRow.getDetailedDescriptions()).containsExactly(DETAILED_DESCRIPTION_1);
		});
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

	@Test
	void tobillingRecordForNull() {
		assertThat(BillingRecordMapper.toBillingRecord(null)).isNull();
	}

	@Test
	void tobillingRecordWhenNoDescriptions() {
		final var billingRecordEntity = createbillingRecordEntity();
		billingRecordEntity.getInvoice().getInvoiceRows().forEach(row -> row.setDescriptions(null));
		final var billingRecord = BillingRecordMapper.toBillingRecord(billingRecordEntity);

		assertThat(billingRecord.getInvoice().getInvoiceRows()).isNotEmpty().allMatch(row -> allNull(row.getDescriptions(), row.getDetailedDescriptions()));
	}

	@Test
	void tobillingRecordWhenNoRecipient() {
		final var billingRecordEntity = createbillingRecordEntity().withRecipient(null);
		final var billingRecord = BillingRecordMapper.toBillingRecord(billingRecordEntity);

		assertThat(billingRecord.getRecipient()).isNull();
	}

	@Test
	void tobillingRecordWhenNoAddress() {
		final var billingRecordEntity = createbillingRecordEntity();
		billingRecordEntity.getRecipient().setAddressDetails(null);
		final var billingRecord = BillingRecordMapper.toBillingRecord(billingRecordEntity);

		assertThat(billingRecord.getRecipient().getAddressDetails()).isNull();
	}

	@Test
	void tobillingRecordWhenNoInvoice() {
		final var billingRecordEntity = createbillingRecordEntity().withInvoice(null);
		final var billingRecord = BillingRecordMapper.toBillingRecord(billingRecordEntity);

		assertThat(billingRecord.getInvoice()).isNull();
	}

	@Test
	void tobillingRecordsForNull() {
		final var billingRecords = BillingRecordMapper.toBillingRecords(null);

		assertThat(billingRecords).isEmpty();
	}

	@Test
	void tobillingRecords() {
		final var entities = new ArrayList<BillingRecordEntity>();
		entities.addAll(List.of(createbillingRecordEntity(), createbillingRecordEntity(), createbillingRecordEntity()));
		entities.add(null);
		final var billingRecords = BillingRecordMapper.toBillingRecords(entities);

		assertThat(billingRecords).hasSize(3);
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
			.withAccountInformation(List.of(createAccountInformationEmbeddable()))
			.withCostPerUnit(COST_PER_UNIT)
			.withId(id)
			.withInvoice(invoiceEntity)
			.withQuantity(QUANTITY)
			.withTotalAmount(COST_PER_UNIT * QUANTITY)
			.withVatCode(VAT_CODE);

		return invoiceRowEntity.withDescriptions(List.of(createDescription(1, invoiceRowEntity, STANDARD, DESCRIPTION_1), createDescription(2, invoiceRowEntity, DETAILED, DETAILED_DESCRIPTION_1)));
	}

	private static DescriptionEntity createDescription(int id, InvoiceRowEntity invoiceRowEntity, DescriptionType type, String text) {
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

	private static BillingRecord createbillingRecord() {
		return BillingRecord.create()
			.withCategory(CATEGORY)
			.withApprovedBy(APPROVED_BY)
			.withInvoice(createInvoice())
			.withRecipient(createRecipient())
			.withStatus(se.sundsvall.billingpreprocessor.api.model.enums.Status.valueOf(STATUS.toString()))
			.withType(se.sundsvall.billingpreprocessor.api.model.enums.Type.valueOf(TYPE.toString()))
			.withExtraParameters(EXTRA_PARAMETERS);
	}

	private static Invoice createInvoice() {
		return Invoice.create()
			.withCustomerId(CUSTOMER_ID)
			.withCustomerReference(CUSTOMER_REFERENCE)
			.withDescription(DESCRIPTION)
			.withDate(DATE)
			.withDueDate(DUE_DATE)
			.withInvoiceRows(List.of(createInvoiceRow(false), createInvoiceRow(true)))
			.withOurReference(OUR_REFERENCE)
			.withReferenceId(REFERENCE_ID);
	}

	private static InvoiceRow createInvoiceRow(boolean withAccountInformation) {
		return InvoiceRow.create()
			.withAccountInformation(withAccountInformation ? List.of(createAccountInformation()) : null)
			.withCostPerUnit(COST_PER_UNIT)
			.withDescriptions(DESCRIPTIONS)
			.withDetailedDescriptions(DETAILED_DESCRIPTIONS)
			.withQuantity(QUANTITY)
			.withVatCode(VAT_CODE);
	}

	private static AccountInformation createAccountInformation() {
		return AccountInformation.create()
			.withAccuralKey(ACCURAL_KEY)
			.withActivity(ACTIVITY)
			.withArticle(ARTICLE)
			.withCostCenter(COST_CENTER)
			.withCounterpart(COUNTERPART)
			.withDepartment(DEPARTMENT)
			.withProject(PROJECT)
			.withSubaccount(SUBACCOUNT)
			.withAmount(ACCOUNTING_AMOUNT);
	}

	private static Recipient createRecipient() {
		return Recipient.create()
			.withAddressDetails(createAddressDetails())
			.withFirstName(FIRST_NAME)
			.withLastName(LAST_NAME)
			.withOrganizationName(ORGANIZATION_NAME)
			.withPartyId(PARTY_ID)
			.withLegalId(LEGAL_ID)
			.withUserId(USER_ID);
	}

	private static AddressDetails createAddressDetails() {
		return AddressDetails.create()
			.withCareOf(CARE_OF)
			.withCity(CITY)
			.withStreet(STREET)
			.withtPostalCode(POSTAL_CODE);
	}
}
