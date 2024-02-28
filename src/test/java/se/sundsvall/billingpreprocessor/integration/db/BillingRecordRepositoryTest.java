package se.sundsvall.billingpreprocessor.integration.db;

import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.stream.Collectors.toCollection;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.Assertions.within;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.DescriptionType.DETAILED;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.DescriptionType.STANDARD;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.Status.APPROVED;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.Status.NEW;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.Status.REJECTED;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.Type.EXTERNAL;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.turkraft.springfilter.converter.FilterSpecificationConverter;

import se.sundsvall.billingpreprocessor.integration.db.model.AccountInformationEmbeddable;
import se.sundsvall.billingpreprocessor.integration.db.model.AddressDetailsEmbeddable;
import se.sundsvall.billingpreprocessor.integration.db.model.BillingRecordEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.DescriptionEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceRowEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.RecipientEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.enums.Status;
import se.sundsvall.billingpreprocessor.integration.db.model.enums.Type;

/**
 * billingRecord repository tests
 *
 * @see /src/test/resources/db/testdata-junit.sql for data setup.
 */
@SpringBootTest
@ActiveProfiles("it")
@Transactional
@Sql(scripts = {
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata-junit.sql"
})
class BillingRecordRepositoryTest {
	private static final String ACCURAL_KEY = "accuralKey";
	private static final String ACTIVITY = "activity";
	private static final String ARTICLE = "article";
	private static final String COST_CENTER = "costCenter";
	private static final String COUNTER_PART = "counterPart";
	private static final String DEPARTMENT = "department";
	private static final String PROJECT = "project";
	private static final String SUBACCOUNT = "subaccount";
	private static final String STANDARD_TEXT = "standardText";
	private static final String DETAILED_TEXT = "detailedText";
	private static final Float COST_PER_UNIT = 13.37f;
	private static final Integer QUANTITY = 10;
	private static final Float TOTAL_ROW_AMOUNT = COST_PER_UNIT * QUANTITY;
	private static final String VAT_CODE = "06";
	private static final String CUSTOMER_ID = "customerId";
	private static final String CUSTOMER_REFERENCE = "customerReference";
	private static final String DESCRIPTION = "description";
	private static final LocalDate DATE = LocalDate.now();
	private static final LocalDate DUE_DATE = LocalDate.now().plusWeeks(1);
	private static final String OUR_REFERENCE = "ourReference";
	private static final String REFERENCE_ID = "referenceId";
	private static final Float TOTAL_AMOUNT = COST_PER_UNIT * TOTAL_ROW_AMOUNT;
	private static final String CARE_OF = "careOf";
	private static final String CITY = "city";
	private static final String STREET = "street";
	private static final String POSTAL_CODE = "postalCode";
	private static final String FIRST_NAME = "firstName";
	private static final String LAST_NAME = "lastName";
	private static final String ORGANIZATION_NAME = "organizationName";
	private static final String PARTY_ID = "partyId";
	private static final String LEGAL_ID = "legalId";
	private static final String USER_ID = "userId";
	private static final String CATEGORY = "category";
	private static final OffsetDateTime APPROVED_TIMESTAMP = now().plusDays(3);
	private static final String APPROVED_BY = "approvedBy";
	private static final Status STATUS = APPROVED;
	private static final Type TYPE = EXTERNAL;

	@Autowired
	private BillingRecordRepository repository;

	@Autowired
	private FilterSpecificationConverter filterSpecificationConverter;

	@Test
	void create() {
		final var billingRecord = createbillingRecord(); // Create billingRecord entity
		billingRecord.withInvoice(createInvoice(billingRecord)).withRecipient(createRecipient(billingRecord)); // Connect Recipient and invoice to it
		billingRecord.getInvoice().withInvoiceRows(createInvoiceRows(billingRecord.getInvoice())); // Connect invoice rows to connected invoice
		billingRecord.getInvoice().getInvoiceRows().forEach(row -> row.withDescriptions(createDescriptions(row))); // Connect descriptions to connected invoice rows
		repository.save(billingRecord); // Save entity

		// Verify billingRecord data
		verifybillingRecord(billingRecord);

		// Verify invoice data
		verifyInvoice(billingRecord);

		// Verify invoice row data
		verifyInvoiceRow(billingRecord);

		// Verify Recipient data
		verifyRecipient(billingRecord);
	}

	private static void verifybillingRecord(final BillingRecordEntity billingRecord) {
		assertThat(billingRecord).isNotNull();
		assertThat(billingRecord.getCategory()).isEqualTo(CATEGORY);
		assertThat(billingRecord.getApproved()).isEqualTo(APPROVED_TIMESTAMP);
		assertThat(billingRecord.getApprovedBy()).isEqualTo(APPROVED_BY);
		assertThat(billingRecord.getCreated()).isCloseTo(now(), within(2, SECONDS));
		assertThat(billingRecord.getId()).isNotNull();
		assertThat(billingRecord.getInvoice()).isNotNull();
		assertThat(billingRecord.getRecipient()).isNotNull();
		assertThat(billingRecord.getModified()).isNull();
		assertThat(billingRecord.getStatus()).isEqualTo(STATUS);
		assertThat(billingRecord.getType()).isEqualTo(TYPE);
	}

	private static void verifyInvoice(final BillingRecordEntity billingRecord) {
		final var invoice = billingRecord.getInvoice();

		assertThat(invoice.getBillingRecord()).isEqualTo(billingRecord);
		assertThat(invoice.getCustomerId()).isEqualTo(CUSTOMER_ID);
		assertThat(invoice.getCustomerReference()).isEqualTo(CUSTOMER_REFERENCE);
		assertThat(invoice.getDescription()).isEqualTo(DESCRIPTION);
		assertThat(invoice.getDate()).isEqualTo(DATE);
		assertThat(invoice.getDueDate()).isEqualTo(DUE_DATE);
		assertThat(invoice.getId()).isEqualTo(billingRecord.getId());
		assertThat(invoice.getInvoiceRows()).hasSize(1);
		assertThat(invoice.getOurReference()).isEqualTo(OUR_REFERENCE);
		assertThat(invoice.getReferenceId()).isEqualTo(REFERENCE_ID);
		assertThat(invoice.getTotalAmount()).isEqualTo(TOTAL_AMOUNT);
	}

	private static void verifyInvoiceRow(final BillingRecordEntity billingRecord) {
		final var invoiceRow = billingRecord.getInvoice().getInvoiceRows().get(0);

		assertThat(invoiceRow.getAccountInformation()).isNotNull();
		assertThat(invoiceRow.getAccountInformation().getAccuralKey()).isEqualTo(ACCURAL_KEY);
		assertThat(invoiceRow.getAccountInformation().getActivity()).isEqualTo(ACTIVITY);
		assertThat(invoiceRow.getAccountInformation().getArticle()).isEqualTo(ARTICLE);
		assertThat(invoiceRow.getAccountInformation().getCostCenter()).isEqualTo(COST_CENTER);
		assertThat(invoiceRow.getAccountInformation().getCounterpart()).isEqualTo(COUNTER_PART);
		assertThat(invoiceRow.getAccountInformation().getDepartment()).isEqualTo(DEPARTMENT);
		assertThat(invoiceRow.getAccountInformation().getProject()).isEqualTo(PROJECT);
		assertThat(invoiceRow.getAccountInformation().getSubaccount()).isEqualTo(SUBACCOUNT);
		assertThat(invoiceRow.getCostPerUnit()).isEqualTo(COST_PER_UNIT);

		// Verify invoice row description data
		assertThat(invoiceRow.getDescriptions()).hasSize(2);
		assertThat(invoiceRow.getDescriptions())
			.extracting(DescriptionEntity::getText, DescriptionEntity::getType, DescriptionEntity::getInvoiceRow)
			.containsExactlyInAnyOrder(
				tuple(STANDARD_TEXT, STANDARD, invoiceRow),
				tuple(DETAILED_TEXT, DETAILED, invoiceRow));
		assertThat(invoiceRow.getInvoice()).isEqualTo(billingRecord.getInvoice());
		assertThat(invoiceRow.getQuantity()).isEqualTo(QUANTITY);
		assertThat(invoiceRow.getTotalAmount()).isEqualTo(TOTAL_ROW_AMOUNT);
		assertThat(invoiceRow.getVatCode()).isEqualTo(VAT_CODE);
	}

	private static void verifyRecipient(final BillingRecordEntity billingRecord) {
		final var Recipient = billingRecord.getRecipient();
		assertThat(Recipient.getAddressDetails()).isNotNull();
		assertThat(Recipient.getAddressDetails().getCareOf()).isEqualTo(CARE_OF);
		assertThat(Recipient.getAddressDetails().getCity()).isEqualTo(CITY);
		assertThat(Recipient.getAddressDetails().getStreet()).isEqualTo(STREET);
		assertThat(Recipient.getAddressDetails().getPostalCode()).isEqualTo(POSTAL_CODE);
		assertThat(Recipient.getBillingRecord()).isEqualTo(billingRecord);
		assertThat(Recipient.getFirstName()).isEqualTo(FIRST_NAME);
		assertThat(Recipient.getId()).isEqualTo(billingRecord.getId());
		assertThat(Recipient.getLastName()).isEqualTo(LAST_NAME);
		assertThat(Recipient.getOrganizationName()).isEqualTo(ORGANIZATION_NAME);
		assertThat(Recipient.getPartyId()).isEqualTo(PARTY_ID);
		assertThat(Recipient.getLegalId()).isEqualTo(LEGAL_ID);
		assertThat(Recipient.getUserId()).isEqualTo(USER_ID);
	}

	@Test
	void read() {
		final var id = "83e4d599-5b4d-431c-8ebc-81192e9401ee";
		final var entity = repository.getReferenceById(id);

		assertThat(entity).isNotNull().extracting(BillingRecordEntity::getId).isEqualTo(id);
		assertThat(entity).extracting(BillingRecordEntity::getStatus).isEqualTo(NEW);
		assertThat(entity).extracting(BillingRecordEntity::getType).isEqualTo(EXTERNAL);
	}

	@Test
	void findWithSpecification() {
		final Specification<BillingRecordEntity> specification = filterSpecificationConverter.convert("(category : 'ACCESS_CARD' and status : 'APPROVED')");
		final Pageable pageable = PageRequest.of(0, 20);

		final var matches = repository.findAll(specification, pageable);

		assertThat(matches).isNotNull();
		assertThat(matches.getTotalElements()).isEqualTo(1);
		assertThat(matches.getNumberOfElements()).isEqualTo(1);
		assertThat(matches.getTotalPages()).isEqualTo(1);
		assertThat(matches)
			.extracting(BillingRecordEntity::getId, BillingRecordEntity::getCategory, BillingRecordEntity::getStatus).containsExactly(
				tuple("1310ee8b-ecf9-4fe1-ab9d-f19153b19d06", "ACCESS_CARD", APPROVED));
	}

	@Test
	void findWithEmptySpecification() {
		final Specification<BillingRecordEntity> specification = Specification.where(null);
		final Pageable pageable = PageRequest.of(0, 20);

		final var matches = repository.findAll(specification, pageable);

		assertThat(matches).isNotNull();
		assertThat(matches.getTotalElements()).isEqualTo(3);
		assertThat(matches.getNumberOfElements()).isEqualTo(3);
		assertThat(matches.getTotalPages()).isEqualTo(1);
		assertThat(matches)
			.extracting(BillingRecordEntity::getId, BillingRecordEntity::getCategory, BillingRecordEntity::getStatus).containsExactlyInAnyOrder(
				tuple("71258e7d-5285-46ce-b9b2-877f8cad8edd", "ACCESS_CARD", NEW),
				tuple("1310ee8b-ecf9-4fe1-ab9d-f19153b19d06", "ACCESS_CARD", APPROVED),
				tuple("83e4d599-5b4d-431c-8ebc-81192e9401ee", "SALARY_AND_PENSION", NEW));
	}

	@Test
	void findWithPagingAndSorting() {
		final Specification<BillingRecordEntity> specification = Specification.where(null);
		final Pageable pageable = PageRequest.of(0, 1).withSort(Sort.by(Direction.ASC, "created"));

		final var matches = repository.findAll(specification, pageable);

		assertThat(matches).isNotNull();
		assertThat(matches.getTotalElements()).isEqualTo(3);
		assertThat(matches.getNumber()).isZero();
		assertThat(matches.getNumberOfElements()).isEqualTo(1);
		assertThat(matches.getTotalPages()).isEqualTo(3);
		assertThat(matches)
			.extracting(BillingRecordEntity::getId, BillingRecordEntity::getCategory, BillingRecordEntity::getStatus).containsExactly(
				tuple("71258e7d-5285-46ce-b9b2-877f8cad8edd", "ACCESS_CARD", NEW));
	}

	@Test
	void update() {
		final var entity = repository.getReferenceById("83e4d599-5b4d-431c-8ebc-81192e9401ee");
		final var status = REJECTED;

		assertThat(entity.getStatus()).isEqualTo(NEW);
		final var updatedEntity = repository.saveAndFlush(entity.withStatus(status));
		assertThat(updatedEntity.getStatus()).isEqualTo(REJECTED);
		assertThat(updatedEntity.getModified()).isCloseTo(now(), within(2, SECONDS));
	}

	@Test
	void delete() {
		final var id = "1310ee8b-ecf9-4fe1-ab9d-f19153b19d06";

		assertThat(repository.existsById(id)).isTrue();
		repository.deleteById(id);
		assertThat(repository.existsById(id)).isFalse();
	}

	private static AccountInformationEmbeddable createAccountInformation() {
		return AccountInformationEmbeddable.create()
			.withAccuralKey(ACCURAL_KEY)
			.withActivity(ACTIVITY)
			.withArticle(ARTICLE)
			.withCostCenter(COST_CENTER)
			.withCounterpart(COUNTER_PART)
			.withDepartment(DEPARTMENT)
			.withProject(PROJECT)
			.withSubaccount(SUBACCOUNT);
	}

	private static List<DescriptionEntity> createDescriptions(final InvoiceRowEntity invoiceRow) {
		return List.of(
			DescriptionEntity.create()
				.withInvoiceRow(invoiceRow)
				.withText(STANDARD_TEXT)
				.withType(STANDARD),
			DescriptionEntity.create()
				.withInvoiceRow(invoiceRow)
				.withText(DETAILED_TEXT)
				.withType(DETAILED))
			.stream().collect(toCollection(ArrayList::new));
	}

	private static List<InvoiceRowEntity> createInvoiceRows(final InvoiceEntity invoice) {
		return List.of(InvoiceRowEntity.create()
			.withAccountInformation(createAccountInformation())
			.withCostPerUnit(COST_PER_UNIT)
			.withInvoice(invoice)
			.withQuantity(QUANTITY)
			.withTotalAmount(TOTAL_ROW_AMOUNT)
			.withVatCode(VAT_CODE))
			.stream().collect(toCollection(ArrayList::new));
	}

	private static InvoiceEntity createInvoice(final BillingRecordEntity billingRecord) {
		return InvoiceEntity.create()
			.withBillingRecord(billingRecord)
			.withCustomerId(CUSTOMER_ID)
			.withCustomerReference(CUSTOMER_REFERENCE)
			.withDescription(DESCRIPTION)
			.withDate(DATE)
			.withDueDate(DUE_DATE)
			.withOurReference(OUR_REFERENCE)
			.withReferenceId(REFERENCE_ID)
			.withTotalAmount(TOTAL_AMOUNT);
	}

	private static AddressDetailsEmbeddable createAddressDetails() {
		return AddressDetailsEmbeddable.create()
			.withCareOf(CARE_OF)
			.withCity(CITY)
			.withStreet(STREET)
			.withPostalCode(POSTAL_CODE);
	}

	private static RecipientEntity createRecipient(final BillingRecordEntity billingRecord) {
		return RecipientEntity.create()
			.withAddressDetails(createAddressDetails())
			.withBillingRecord(billingRecord)
			.withFirstName(FIRST_NAME)
			.withLastName(LAST_NAME)
			.withOrganizationName(ORGANIZATION_NAME)
			.withPartyId(PARTY_ID)
			.withLegalId(LEGAL_ID)
			.withUserId(USER_ID);
	}

	private static BillingRecordEntity createbillingRecord() {
		return BillingRecordEntity.create()
			.withCategory(CATEGORY)
			.withApproved(APPROVED_TIMESTAMP)
			.withApprovedBy(APPROVED_BY)
			.withStatus(STATUS)
			.withType(TYPE);
	}
}
