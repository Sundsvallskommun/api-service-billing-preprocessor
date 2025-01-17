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
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.DescriptionType.DETAILED;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.DescriptionType.STANDARD;
import static se.sundsvall.billingpreprocessor.service.util.CalculationUtil.calculateTotalInvoiceAmount;
import static se.sundsvall.billingpreprocessor.service.util.CalculationUtil.calculateTotalInvoiceRowAmount;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.commons.collections4.CollectionUtils;
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
import se.sundsvall.billingpreprocessor.integration.db.model.enums.Status;
import se.sundsvall.billingpreprocessor.integration.db.model.enums.Type;

public final class BillingRecordMapper {

	private BillingRecordMapper() {}

	/**
	 * Method for mapping a BillingRecord object to a BillingRecordEntity object
	 *
	 * @param  billingRecord  a billing record represented by the BillingRecord class
	 * @param  municipalityId municipality ID
	 * @return                a object of class BillingRecordEntity representing the incoming BillingRecord object
	 */
	public static BillingRecordEntity toBillingRecordEntity(final BillingRecord billingRecord, String municipalityId) {
		if (isNull(billingRecord)) {
			return null;
		}

		final var billingRecordEntity = BillingRecordEntity.create() // Create billing record entity
			.withCategory(billingRecord.getCategory())
			.withStatus(Status.valueOf(billingRecord.getStatus().toString()))
			.withType(Type.valueOf(billingRecord.getType().toString()))
			.withMunicipalityId(municipalityId);

		billingRecordEntity.setRecipient(toRecipientEntity(billingRecordEntity, billingRecord.getRecipient())); // Add recipient entity to billing record entity
		billingRecordEntity.setInvoice(toInvoiceEntity(billingRecordEntity, billingRecord.getInvoice())); // Add invoice entity to billing record entity
		billingRecordEntity.setExtraParameters(billingRecord.getExtraParameters()); // Add extra parameters to billing record entity

		if (Status.APPROVED == billingRecordEntity.getStatus()) {
			setApprovedBy(billingRecordEntity, billingRecord.getApprovedBy());
		}

		return billingRecordEntity;
	}

	/**
	 * Method for mapping a list of BillingRecordEntity objects to a BillingRecord objects
	 *
	 * @param  billingRecords a list of billing records represented by the BillingRecordEntity class
	 * @param  municipalityId municipality ID
	 * @return                a list of objects of class BillingRecord representing the incoming BillingRecordEntity objects
	 */
	public static List<BillingRecordEntity> toBillingRecordEntities(final List<BillingRecord> billingRecords, String municipalityId) {
		return ofNullable(billingRecords)
			.map(records -> records.stream()
				.map(r -> toBillingRecordEntity(r, municipalityId))
				.filter(Objects::nonNull)
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
		if (isNull(billingRecord)) {
			return billingRecordEntity;
		}

		// Update billing record entity with request data
		ofNullable(billingRecord.getCategory()).ifPresent(billingRecordEntity::setCategory);
		ofNullable(billingRecord.getStatus()).ifPresent(v -> billingRecordEntity.setStatus(Status.valueOf(v.toString())));
		ofNullable(billingRecord.getType()).ifPresent(v -> billingRecordEntity.setType(Type.valueOf(v.toString())));

		billingRecordEntity.setRecipient(toRecipientEntity(billingRecordEntity, billingRecord.getRecipient())); // Update recipient entity of billing record entity with new information
		billingRecordEntity.setInvoice(toInvoiceEntity(billingRecordEntity, billingRecord.getInvoice())); // Update invoice entity of billing record entity with new information
		billingRecordEntity.setExtraParameters(billingRecord.getExtraParameters()); // Update extra parameters of billing record entity with new information

		// Only set approved by and approved timestamp first time billing record receives approved status
		if ((Status.APPROVED == billingRecordEntity.getStatus()) && isNull(billingRecordEntity.getApproved())) {
			setApprovedBy(billingRecordEntity, billingRecord.getApprovedBy());
		}

		// Need to trigger modified date for billing record manually here as adding or modifying sub entities doesn't trigger
		// the @preUpdate annotation
		return billingRecordEntity.withModified(now(ZoneId.systemDefault()).truncatedTo(MILLIS));
	}

	private static void setApprovedBy(final BillingRecordEntity billingRecordEntity, String approvedBy) {
		billingRecordEntity
			.withApproved(now(ZoneId.systemDefault()).truncatedTo(MILLIS))
			.withApprovedBy(approvedBy);
	}

	private static InvoiceEntity toInvoiceEntity(final BillingRecordEntity billingRecordEntity, final Invoice invoice) {
		final var invoiceEntity = ofNullable(billingRecordEntity.getInvoice()).orElse(InvoiceEntity.create().withBillingRecord(billingRecordEntity));

		ofNullable(invoice).ifPresent(i -> invoiceEntity.withCustomerId(i.getCustomerId())
			.withCustomerReference(i.getCustomerReference())
			.withDescription(i.getDescription())
			.withDate(i.getDate())
			.withDueDate(i.getDueDate())
			.withInvoiceRows(toInvoiceRowEntities(invoiceEntity, i.getInvoiceRows()))
			.withOurReference(i.getOurReference())
			.withReferenceId(i.getReferenceId())
			.withTotalAmount(calculateTotalInvoiceAmount(invoiceEntity)));

		return invoiceEntity;
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

	private static List<AccountInformationEmbeddable> toAccountInformationEmbeddable(final AccountInformation accountInformation) {
		return ofNullable(accountInformation).map(a -> AccountInformationEmbeddable.create()
			.withAccuralKey(a.getAccuralKey())
			.withActivity(a.getActivity())
			.withArticle(a.getArticle())
			.withCostCenter(a.getCostCenter())
			.withCounterpart(a.getCounterpart())
			.withDepartment(a.getDepartment())
			.withProject(a.getProject())
			.withSubaccount(a.getSubaccount()))
			.map(List::of)
			.orElse(emptyList());
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
		return ofNullable(addressDetails).map(a -> AddressDetailsEmbeddable.create()
			.withCareOf(a.getCareOf())
			.withCity(a.getCity())
			.withPostalCode(a.getPostalCode())
			.withStreet(a.getStreet()))
			.orElse(null);
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
			.filter(Objects::nonNull)
			.toList();
	}

	/**
	 * Method for mapping a BillingRecordEntity object to a BillingRecord object
	 *
	 * @param  billingRecordEntity a billing record represented by the BillingRecordEntity class
	 * @return                     a object of class BillingRecord representing the incoming BillingRecordEntity object
	 */
	public static BillingRecord toBillingRecord(final BillingRecordEntity billingRecordEntity) {
		return ofNullable(billingRecordEntity).map(b -> BillingRecord.create()
			.withCategory(b.getCategory())
			.withApproved(b.getApproved())
			.withApprovedBy(b.getApprovedBy())
			.withCreated(b.getCreated())
			.withId(b.getId())
			.withInvoice(toInvoice(b.getInvoice()))
			.withRecipient(toRecipient(b.getRecipient()))
			.withModified(b.getModified())
			.withExtraParameters(b.getExtraParameters())
			.withStatus(se.sundsvall.billingpreprocessor.api.model.enums.Status.valueOf(b.getStatus().toString()))
			.withType(se.sundsvall.billingpreprocessor.api.model.enums.Type.valueOf(b.getType().toString())))
			.orElse(null);
	}

	private static Recipient toRecipient(RecipientEntity recipientEntity) {
		return ofNullable(recipientEntity).map(r -> Recipient.create()
			.withAddressDetails(toAddressDetails(r.getAddressDetails()))
			.withFirstName(r.getFirstName())
			.withLastName(r.getLastName())
			.withOrganizationName(r.getOrganizationName())
			.withPartyId(r.getPartyId())
			.withLegalId(r.getLegalId())
			.withUserId(r.getUserId()))
			.orElse(null);
	}

	private static AddressDetails toAddressDetails(AddressDetailsEmbeddable addressDetailsEmbeddable) {
		return ofNullable(addressDetailsEmbeddable).map(details -> AddressDetails.create()
			.withCareOf(addressDetailsEmbeddable.getCareOf())
			.withCity(addressDetailsEmbeddable.getCity())
			.withStreet(addressDetailsEmbeddable.getStreet())
			.withtPostalCode(addressDetailsEmbeddable.getPostalCode()))
			.orElse(null);
	}

	private static Invoice toInvoice(InvoiceEntity invoiceEntity) {
		return ofNullable(invoiceEntity).map(i -> Invoice.create()
			.withCustomerId(i.getCustomerId())
			.withCustomerReference(i.getCustomerReference())
			.withDescription(i.getDescription())
			.withDate(i.getDate())
			.withDueDate(i.getDueDate())
			.withInvoiceRows(toInvoiceRows(i.getInvoiceRows()))
			.withOurReference(i.getOurReference())
			.withReferenceId(i.getReferenceId())
			.withTotalAmount(i.getTotalAmount()))
			.orElse(null);
	}

	private static List<InvoiceRow> toInvoiceRows(List<InvoiceRowEntity> invoiceRowEntities) {
		return ofNullable(invoiceRowEntities).orElse(emptyList()).stream()
			.map(BillingRecordMapper::toInvoiceRow)
			.filter(Objects::nonNull)
			.toList();
	}

	private static InvoiceRow toInvoiceRow(InvoiceRowEntity invoiceRowEntity) {
		return ofNullable(invoiceRowEntity).map(i -> InvoiceRow.create()
			.withAccountInformation(toAccountInformation(i.getAccountInformation()))
			.withCostPerUnit(i.getCostPerUnit())
			.withDescriptions(toDescription(STANDARD, i.getDescriptions()))
			.withDetailedDescriptions(toDescription(DETAILED, i.getDescriptions()))
			.withQuantity(i.getQuantity())
			.withTotalAmount(i.getTotalAmount())
			.withVatCode(i.getVatCode()))
			.orElse(null);
	}

	private static AccountInformation toAccountInformation(List<AccountInformationEmbeddable> accountInformationEmbeddable) {
		return ofNullable(accountInformationEmbeddable)
			.filter(CollectionUtils::isNotEmpty)
			.map(List::getFirst)
			.map(a -> AccountInformation.create()
				.withAccuralKey(a.getAccuralKey())
				.withActivity(a.getActivity())
				.withArticle(a.getArticle())
				.withCostCenter(a.getCostCenter())
				.withCounterpart(a.getCounterpart())
				.withDepartment(a.getDepartment())
				.withProject(a.getProject())
				.withSubaccount(a.getSubaccount()))
			.orElse(null);
	}

	private static List<String> toDescription(DescriptionType type, List<DescriptionEntity> descriptionEntities) {
		return ofNullable(descriptionEntities).orElse(emptyList()).stream()
			.filter(entity -> entity.getType() == type)
			.map(DescriptionEntity::getText)
			.collect(collectingAndThen(toList(), list -> isEmpty(list) ? null : list));
	}
}
