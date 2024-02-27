package se.sundsvall.billingpreprocessor.service.mapper;

import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.ObjectUtils.isEmpty;
import static se.sundsvall.billingpreprocessor.api.model.enums.Status.APPROVED;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.DescriptionType.DETAILED;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.DescriptionType.STANDARD;
import static se.sundsvall.billingpreprocessor.service.util.CalculationUtil.calculateTotalInvoiceAmount;
import static se.sundsvall.billingpreprocessor.service.util.CalculationUtil.calculateTotalInvoiceRowAmount;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import se.sundsvall.billingpreprocessor.api.model.AccountInformation;
import se.sundsvall.billingpreprocessor.api.model.AddressDetails;
import se.sundsvall.billingpreprocessor.api.model.BillingRecord;
import se.sundsvall.billingpreprocessor.api.model.Invoice;
import se.sundsvall.billingpreprocessor.api.model.InvoiceRow;
import se.sundsvall.billingpreprocessor.api.model.Recipient;
import se.sundsvall.billingpreprocessor.integration.db.model.AccountInformationEmbeddable;
import se.sundsvall.billingpreprocessor.integration.db.model.AddressDetailsEmbeddable;
import se.sundsvall.billingpreprocessor.integration.db.model.BillingRecordEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.DescriptionEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceRowEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.RecipientEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.enums.DescriptionType;

public final class BillingRecordMapper {

	private BillingRecordMapper() {}

	/**
	 * Method for mapping a BillingRecord object to a BillingRecordEntity object
	 *
	 * @param  billingRecord a billing record represented by the BillingRecord class
	 * @return               a object of class BillingRecordEntity representing the incoming BillingRecord object
	 */
	public static BillingRecordEntity toBillingRecordEntity(final BillingRecord billingRecord) {
		final var billingRecordEntity = BillingRecordEntity.create() // Create billing record entity
			.withCategory(billingRecord.getCategory())
			.withStatus(billingRecord.getStatus())
			.withType(billingRecord.getType());

		billingRecordEntity.setRecipient(toRecipientEntity(billingRecordEntity, billingRecord.getRecipient())); // Add recipient entity to billing record entity
		billingRecordEntity.setInvoice(toInvoiceEntity(billingRecordEntity, billingRecord.getInvoice())); // Add invoice entity to billing record entity

		if (APPROVED == billingRecordEntity.getStatus()) {
			setApprovedBy(billingRecordEntity, billingRecord.getApprovedBy());
		}

		return billingRecordEntity;
	}

	/**
	 * Method for mapping a list of BillingRecordEntity objects to a BillingRecord objects
	 *
	 * @param  billingRecords a list of billing records represented by the BillingRecordEntity class
	 * @return                a list of objects of class BillingRecord representing the incoming BillingRecordEntity objects
	 */
	public static List<BillingRecordEntity> toBillingRecordEntities(final List<BillingRecord> billingRecords) {
		return ofNullable(billingRecords)
			.map(records -> records.stream()
				.map(BillingRecordMapper::toBillingRecordEntity)
				.toList())
			.orElse(emptyList());
	}

	/**
	 * Method for updating an existing BillingRecordEntity object with information from a BillingRecord object
	 *
	 * @param  billingRecordEntity the entity object that will be updated with new information
	 * @param  billingRecord       the request object that holds the information to update the entity with
	 * @return                     the updated billingRecordEntity object
	 */
	public static BillingRecordEntity updateEntity(final BillingRecordEntity billingRecordEntity, final BillingRecord billingRecord) {
		billingRecordEntity // Update billing record entity with request data
			.withCategory(billingRecord.getCategory())
			.withStatus(billingRecord.getStatus())
			.withType(billingRecord.getType());

		billingRecordEntity.setRecipient(toRecipientEntity(billingRecordEntity, billingRecord.getRecipient())); // Update recipient entity of billing record entity with new information
		billingRecordEntity.setInvoice(toInvoiceEntity(billingRecordEntity, billingRecord.getInvoice())); // Update invoice entity of billing record entity with new information

		// Only set approved by and approved timestamp first time billing record receives approved status
		if ((APPROVED == billingRecordEntity.getStatus()) && isNull(billingRecordEntity.getApproved())) {
			setApprovedBy(billingRecordEntity, billingRecord.getApprovedBy());
		}

		// Need to trigger modified date for billing record manually here as adding or modifying sub entities doesn't trigger
		// the @preUpdate annotation
		return billingRecordEntity.withModified(now(ZoneId.systemDefault()).truncatedTo(MILLIS));
	}

	private static void setApprovedBy(final BillingRecordEntity billingRecordEntity, String approvedBy) {
		billingRecordEntity
			.withApproved(now(ZoneId.systemDefault()))
			.withApprovedBy(approvedBy);
	}

	private static InvoiceEntity toInvoiceEntity(final BillingRecordEntity billingRecordEntity, final Invoice invoice) {
		final var invoiceEntity = ofNullable(billingRecordEntity.getInvoice()).orElse(InvoiceEntity.create().withBillingRecord(billingRecordEntity));

		return invoiceEntity.withCustomerId(invoice.getCustomerId())
			.withCustomerReference(invoice.getCustomerReference())
			.withDescription(invoice.getDescription())
			.withDate(invoice.getDate())
			.withDueDate(invoice.getDueDate())
			.withInvoiceRows(toInvoiceRowEntities(invoiceEntity, invoice.getInvoiceRows()))
			.withOurReference(invoice.getOurReference())
			.withReferenceId(invoice.getReferenceId())
			.withTotalAmount(calculateTotalInvoiceAmount(invoiceEntity));
	}

	private static List<InvoiceRowEntity> toInvoiceRowEntities(final InvoiceEntity invoiceEntity, final List<InvoiceRow> invoiceRows) {
		return ofNullable(invoiceRows).orElse(emptyList()).stream()
			.map(invoiceRow -> toInvoiceRowEntity(invoiceEntity, invoiceRow))
			.collect(toCollection(ArrayList::new));
	}

	private static InvoiceRowEntity toInvoiceRowEntity(final InvoiceEntity invoiceEntity, final InvoiceRow invoiceRow) {
		final var invoiceRowEntity = InvoiceRowEntity.create().withInvoice(invoiceEntity);

		return invoiceRowEntity.withAccountInformation(toAccountInformationEmbeddable(invoiceRow.getAccountInformation()))
			.withCostPerUnit(invoiceRow.getCostPerUnit())
			.withQuantity(invoiceRow.getQuantity())
			.withDescriptions(toDescriptionEntities(invoiceRowEntity, invoiceRow.getDescriptions(), invoiceRow.getDetailedDescriptions()))
			.withTotalAmount(calculateTotalInvoiceRowAmount(invoiceRow))
			.withVatCode(invoiceRow.getVatCode());
	}

	private static List<DescriptionEntity> toDescriptionEntities(final InvoiceRowEntity invoiceRowEntity, final List<String> descriptions, final List<String> detailedDescriptions) {
		final var descriptionEntities = toDescriptionEntities(invoiceRowEntity, STANDARD, descriptions);
		descriptionEntities.addAll(toDescriptionEntities(invoiceRowEntity, DETAILED, detailedDescriptions));

		return descriptionEntities;
	}

	private static List<DescriptionEntity> toDescriptionEntities(final InvoiceRowEntity invoiceRowEntity, DescriptionType type, final List<String> descriptions) {
		return ofNullable(descriptions).orElse(emptyList()).stream()
			.map(text -> DescriptionEntity.create().withInvoiceRow(invoiceRowEntity).withText(text).withType(type))
			.collect(toCollection(ArrayList::new));
	}

	private static AccountInformationEmbeddable toAccountInformationEmbeddable(final AccountInformation accountInformation) {
		return isNull(accountInformation) ? AccountInformationEmbeddable.create()
			: AccountInformationEmbeddable.create()
				.withAccuralKey(accountInformation.getAccuralKey())
				.withActivity(accountInformation.getActivity())
				.withArticle(accountInformation.getArticle())
				.withCostCenter(accountInformation.getCostCenter())
				.withCounterpart(accountInformation.getCounterpart())
				.withDepartment(accountInformation.getDepartment())
				.withProject(accountInformation.getProject())
				.withSubaccount(accountInformation.getSubaccount());
	}

	private static RecipientEntity toRecipientEntity(final BillingRecordEntity billingRecord, final Recipient recipient) {
		if (isNull(recipient)) {
			return null;
		}

		return ofNullable(billingRecord.getRecipient()).orElse(RecipientEntity.create().withBillingRecord(billingRecord))
			.withAddressDetails(toAddressDetailsEmbeddable(recipient.getAddressDetails()))
			.withFirstName(recipient.getFirstName())
			.withLastName(recipient.getLastName())
			.withOrganizationName(recipient.getOrganizationName())
			.withPartyId(recipient.getPartyId())
			.withLegalId(recipient.getLegalId())
			.withUserId(recipient.getUserId());
	}

	private static AddressDetailsEmbeddable toAddressDetailsEmbeddable(final AddressDetails addressDetails) {
		return AddressDetailsEmbeddable.create()
			.withCareOf(addressDetails.getCareOf())
			.withCity(addressDetails.getCity())
			.withPostalCode(addressDetails.getPostalCode())
			.withStreet(addressDetails.getStreet());
	}

	/**
	 * Method for mapping a list of BillingRecordEntity objects to a list of BillingRecord objects
	 *
	 * @param  billingRecordEntities a list of billing records represented by the BillingRecordEntity class
	 * @return                       a list of objects of class BillingRecord representing the incoming list of
	 *                               BillingRecordEntity objects
	 */
	public static List<BillingRecord> toBillingRecords(final List<BillingRecordEntity> billingRecordEntities) {
		return ofNullable(billingRecordEntities).orElse(emptyList())
			.stream()
			.map(BillingRecordMapper::toBillingRecord)
			.toList();
	}

	/**
	 * Method for mapping a BillingRecordEntity object to a BillingRecord object
	 *
	 * @param  billingRecordEntity a billing record represented by the BillingRecordEntity class
	 * @return                     a object of class BillingRecord representing the incoming BillingRecordEntity object
	 */
	public static BillingRecord toBillingRecord(final BillingRecordEntity billingRecordEntity) {
		return BillingRecord.create()
			.withCategory(billingRecordEntity.getCategory())
			.withApproved(billingRecordEntity.getApproved())
			.withApprovedBy(billingRecordEntity.getApprovedBy())
			.withCreated(billingRecordEntity.getCreated())
			.withId(billingRecordEntity.getId())
			.withInvoice(toInvoice(billingRecordEntity.getInvoice()))
			.withRecipient(toRecipient(billingRecordEntity.getRecipient()))
			.withModified(billingRecordEntity.getModified())
			.withStatus(billingRecordEntity.getStatus())
			.withType(billingRecordEntity.getType());
	}

	private static Recipient toRecipient(RecipientEntity recipientEntity) {
		if (isNull(recipientEntity)) {
			return null;
		}

		return Recipient.create()
			.withAddressDetails(toAddressDetails(recipientEntity.getAddressDetails()))
			.withFirstName(recipientEntity.getFirstName())
			.withLastName(recipientEntity.getLastName())
			.withOrganizationName(recipientEntity.getOrganizationName())
			.withPartyId(recipientEntity.getPartyId())
			.withLegalId(recipientEntity.getLegalId())
			.withUserId(recipientEntity.getUserId());
	}

	private static AddressDetails toAddressDetails(AddressDetailsEmbeddable addressDetailsEmbeddable) {
		return AddressDetails.create()
			.withCareOf(addressDetailsEmbeddable.getCareOf())
			.withCity(addressDetailsEmbeddable.getCity())
			.withStreet(addressDetailsEmbeddable.getStreet())
			.withtPostalCode(addressDetailsEmbeddable.getPostalCode());
	}

	private static Invoice toInvoice(InvoiceEntity invoiceEntity) {
		return Invoice.create()
			.withCustomerId(invoiceEntity.getCustomerId())
			.withCustomerReference(invoiceEntity.getCustomerReference())
			.withDescription(invoiceEntity.getDescription())
			.withDate(invoiceEntity.getDate())
			.withDueDate(invoiceEntity.getDueDate())
			.withInvoiceRows(toInvoiceRows(invoiceEntity.getInvoiceRows()))
			.withOurReference(invoiceEntity.getOurReference())
			.withReferenceId(invoiceEntity.getReferenceId())
			.withTotalAmount(invoiceEntity.getTotalAmount());
	}

	private static List<InvoiceRow> toInvoiceRows(List<InvoiceRowEntity> invoiceRowEntities) {
		return ofNullable(invoiceRowEntities).orElse(emptyList()).stream()
			.map(BillingRecordMapper::toInvoiceRow)
			.toList();
	}

	private static InvoiceRow toInvoiceRow(InvoiceRowEntity invoiceRowEntity) {
		return InvoiceRow.create()
			.withAccountInformation(toAccountInformation(invoiceRowEntity.getAccountInformation()))
			.withCostPerUnit(invoiceRowEntity.getCostPerUnit())
			.withDescriptions(toDescription(STANDARD, invoiceRowEntity.getDescriptions()))
			.withDetailedDescriptions(toDescription(DETAILED, invoiceRowEntity.getDescriptions()))
			.withQuantity(invoiceRowEntity.getQuantity())
			.withTotalAmount(invoiceRowEntity.getTotalAmount())
			.withVatCode(invoiceRowEntity.getVatCode());
	}

	private static AccountInformation toAccountInformation(AccountInformationEmbeddable accountInformationEmbeddable) {
		return AccountInformation.create()
			.withAccuralKey(accountInformationEmbeddable.getAccuralKey())
			.withActivity(accountInformationEmbeddable.getActivity())
			.withArticle(accountInformationEmbeddable.getArticle())
			.withCostCenter(accountInformationEmbeddable.getCostCenter())
			.withCounterpart(accountInformationEmbeddable.getCounterpart())
			.withDepartment(accountInformationEmbeddable.getDepartment())
			.withProject(accountInformationEmbeddable.getProject())
			.withSubaccount(accountInformationEmbeddable.getSubaccount());
	}

	private static List<String> toDescription(DescriptionType type, List<DescriptionEntity> descriptionEntities) {
		return ofNullable(descriptionEntities).orElse(emptyList()).stream()
			.filter(entity -> entity.getType() == type)
			.map(DescriptionEntity::getText)
			.collect(collectingAndThen(toList(), list -> isEmpty(list) ? null : list));
	}
}
