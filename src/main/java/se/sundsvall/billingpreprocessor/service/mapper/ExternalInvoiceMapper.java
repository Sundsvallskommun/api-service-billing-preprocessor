package se.sundsvall.billingpreprocessor.service.mapper;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.ObjectUtils.allNotNull;
import static se.sundsvall.billingpreprocessor.Constants.ERROR_ACCOUNT_INFORMATION_AMOUNT_NOT_PRESENT;
import static se.sundsvall.billingpreprocessor.Constants.ERROR_COSTCENTER_NOT_PRESENT;
import static se.sundsvall.billingpreprocessor.Constants.ERROR_COUNTERPART_NOT_PRESENT;
import static se.sundsvall.billingpreprocessor.Constants.ERROR_CUSTOMER_REFERENCE_NOT_PRESENT;
import static se.sundsvall.billingpreprocessor.Constants.ERROR_DESCRIPTION_NOT_PRESENT;
import static se.sundsvall.billingpreprocessor.Constants.ERROR_GENERATING_SYSTEM_NOT_PRESENT;
import static se.sundsvall.billingpreprocessor.Constants.ERROR_INVOICE_NOT_PRESENT;
import static se.sundsvall.billingpreprocessor.Constants.ERROR_INVOICE_TYPE_NOT_PRESENT;
import static se.sundsvall.billingpreprocessor.Constants.ERROR_LEGALID_NOT_PRESENT;
import static se.sundsvall.billingpreprocessor.Constants.ERROR_OPERATION_NOT_PRESENT;
import static se.sundsvall.billingpreprocessor.Constants.ERROR_RECIPIENT_COUNTERPART_NOT_PRESENT;
import static se.sundsvall.billingpreprocessor.Constants.ERROR_RECIPIENT_NAME_NOT_PRESENT;
import static se.sundsvall.billingpreprocessor.Constants.ERROR_RECIPIENT_NOT_PRESENT;
import static se.sundsvall.billingpreprocessor.Constants.ERROR_RECIPIENT_STREET_ADDRESS_NOT_PRESENT;
import static se.sundsvall.billingpreprocessor.Constants.ERROR_RECIPIENT_ZIPCODE_OR_CITY_NOT_PRESENT;
import static se.sundsvall.billingpreprocessor.Constants.ERROR_SUBACCOUNT_NOT_PRESENT;
import static se.sundsvall.billingpreprocessor.Constants.ERROR_VAT_CODE_NOT_PRESENT;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.DescriptionType.DETAILED;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.DescriptionType.STANDARD;
import static se.sundsvall.billingpreprocessor.service.util.ProblemUtil.createInternalServerErrorProblem;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.zalando.problem.ThrowableProblem;
import se.sundsvall.billingpreprocessor.integration.db.model.AccountInformationEmbeddable;
import se.sundsvall.billingpreprocessor.integration.db.model.AddressDetailsEmbeddable;
import se.sundsvall.billingpreprocessor.integration.db.model.BillingRecordEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.DescriptionEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceRowEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.RecipientEntity;
import se.sundsvall.billingpreprocessor.service.creator.definition.external.CustomerRow;
import se.sundsvall.billingpreprocessor.service.creator.definition.external.FileFooterRow;
import se.sundsvall.billingpreprocessor.service.creator.definition.external.FileHeaderRow;
import se.sundsvall.billingpreprocessor.service.creator.definition.external.InvoiceAccountingRow;
import se.sundsvall.billingpreprocessor.service.creator.definition.external.InvoiceDescriptionRow;
import se.sundsvall.billingpreprocessor.service.creator.definition.external.InvoiceFooterRow;
import se.sundsvall.billingpreprocessor.service.creator.definition.external.InvoiceHeaderRow;
import se.sundsvall.billingpreprocessor.service.creator.definition.external.InvoiceRow;

public final class ExternalInvoiceMapper {

	private ExternalInvoiceMapper() {}

	/**
	 * method for creating file header row for external invoice files
	 *
	 * @param  generatingSystem the generating system for the file
	 * @param  invoiceType      type of invoice
	 * @return                  FileHeaderRow for external invoice files
	 * @throws ThrowableProblem if any mandatory data is missing
	 */
	public static FileHeaderRow toFileHeader(String generatingSystem, String invoiceType) {
		return FileHeaderRow.create()
			.withGeneratingSystem(ofNullable(generatingSystem).orElseThrow(createInternalServerErrorProblem(ERROR_GENERATING_SYSTEM_NOT_PRESENT)))
			.withCreatedDate(LocalDate.now())
			.withInvoiceType(ofNullable(invoiceType).orElseThrow(createInternalServerErrorProblem(ERROR_INVOICE_TYPE_NOT_PRESENT)));
	}

	/**
	 * Method for creating a file footer row for external invoice files
	 *
	 * @param  billingRecords list of billing records present in the file
	 * @return                FileFooterRow for external invoice files
	 */
	public static FileFooterRow toFileFooter(final List<BillingRecordEntity> billingRecords) {
		return FileFooterRow.create()
			.withTotalAmount(calculateTotalAmount(billingRecords));
	}

	/**
	 * Method for mapping recipient data to a customer row for an external invoice file
	 *
	 * @param  legalId             legal id of invoice recipient
	 * @param  billingRecordEntity entity representing the billingRecordEntity
	 * @return                     CustomerRow for external invoice files representing provided data
	 * @throws ThrowableProblem    if any mandatory data is missing
	 */
	public static CustomerRow toCustomer(String legalId, BillingRecordEntity billingRecordEntity) {
		final var recipientEntity = ofNullable(billingRecordEntity.getRecipient()).orElseThrow(createInternalServerErrorProblem(ERROR_RECIPIENT_NOT_PRESENT));

		return CustomerRow.create()
			.withLegalId(ofNullable(legalId).orElseThrow(createInternalServerErrorProblem(ERROR_LEGALID_NOT_PRESENT)))
			.withCustomerName(ofNullable(extractRecipientName(recipientEntity)).orElseThrow(createInternalServerErrorProblem(ERROR_RECIPIENT_NAME_NOT_PRESENT)))
			.withCareOf(recipientEntity.getAddressDetails().getCareOf())
			.withStreetAddress(ofNullable(recipientEntity.getAddressDetails().getStreet()).orElseThrow(createInternalServerErrorProblem(ERROR_RECIPIENT_STREET_ADDRESS_NOT_PRESENT)))
			.withZipCodeAndCity(ofNullable(extractZipCodeAndCity(recipientEntity.getAddressDetails())).orElseThrow(createInternalServerErrorProblem(ERROR_RECIPIENT_ZIPCODE_OR_CITY_NOT_PRESENT)))
			.withCounterpart(ofNullable(extractCounterpart(billingRecordEntity.getInvoice())).orElseThrow(createInternalServerErrorProblem(ERROR_RECIPIENT_COUNTERPART_NOT_PRESENT)));
	}

	/**
	 * Method for mapping invoice data to an invoice header row for an external invoice file
	 *
	 * @param  legalId             legal id of invoice recipient
	 * @param  billingRecordEntity entity representing the billingRecordEntity
	 * @return                     InvoiceHeaderRow for external invoice files representing provided data
	 * @throws ThrowableProblem    if any mandatory data is missing
	 */
	public static InvoiceHeaderRow toInvoiceHeader(String legalId, BillingRecordEntity billingRecordEntity) {
		final var invoiceEntity = ofNullable(billingRecordEntity.getInvoice()).orElseThrow(createInternalServerErrorProblem(ERROR_INVOICE_NOT_PRESENT));

		return InvoiceHeaderRow.create()
			.withLegalId(ofNullable(legalId).orElseThrow(createInternalServerErrorProblem(ERROR_LEGALID_NOT_PRESENT)))
			.withDueDate(invoiceEntity.getDueDate())
			.withCustomerReference(ofNullable(invoiceEntity.getCustomerReference()).orElseThrow(createInternalServerErrorProblem(ERROR_CUSTOMER_REFERENCE_NOT_PRESENT)))
			.withOurReference(invoiceEntity.getOurReference());
	}

	/**
	 * Method for mapping invoice row data to an invoice row for an external invoice file
	 *
	 * @param  legalId          legal id of invoice recipient
	 * @param  invoiceRowEntity entity representing the invoiceRowEntity
	 * @return                  InvoiceRow for external invoice files representing provided data
	 * @throws ThrowableProblem if any mandatory data is missing
	 */
	public static InvoiceRow toInvoiceRow(String legalId, InvoiceRowEntity invoiceRowEntity) {
		return InvoiceRow.create()
			.withLegalId(ofNullable(legalId).orElseThrow(createInternalServerErrorProblem(ERROR_LEGALID_NOT_PRESENT)))
			.withCostPerUnit(invoiceRowEntity.getCostPerUnit())
			.withText(ofNullable(extractDescription(invoiceRowEntity)).orElseThrow(createInternalServerErrorProblem(ERROR_DESCRIPTION_NOT_PRESENT)))
			.withQuantity(invoiceRowEntity.getQuantity())
			.withTotalAmount(invoiceRowEntity.getTotalAmount())
			.withVatCode(ofNullable(invoiceRowEntity.getVatCode()).orElseThrow(createInternalServerErrorProblem(ERROR_VAT_CODE_NOT_PRESENT)));
	}

	/**
	 * Method for mapping invoice row data to an invoice description row for an external invoice file
	 *
	 * @param  legalId          legal id of invoice recipient
	 * @param  invoiceRowEntity entity representing the invoiceRowEntity
	 * @return                  A list of InvoiceDescriptionRows representing provided data
	 */
	public static List<InvoiceDescriptionRow> toInvoiceDescriptionRows(String legalId, InvoiceRowEntity invoiceRowEntity) {
		return ofNullable(invoiceRowEntity.getDescriptions()).orElse(emptyList()).stream()
			.filter(description -> description.getType() == DETAILED)
			.map(DescriptionEntity::getText)
			.filter(StringUtils::isNotBlank)
			.map(text -> toInvoiceDescriptionRow(legalId, text))
			.toList();
	}

	/**
	 * Method for mapping accounting information on invoice row data to a list of invoice accounting rows for an external
	 * invoice file
	 *
	 * @param  invoiceRowEntity entity representing the invoiceRowEntity
	 * @return                  A list of invoiceAccountingRow for external invoice files representing provided data
	 * @throws ThrowableProblem if any mandatory data is missing
	 */
	public static List<InvoiceAccountingRow> toInvoiceAccountingRows(InvoiceRowEntity invoiceRowEntity) {
		return ofNullable(invoiceRowEntity.getAccountInformation()).orElse(emptyList()).stream()
			.map(ai -> InvoiceAccountingRow.create()
				.withCostCenter(ofNullable(ai.getCostCenter()).orElseThrow(createInternalServerErrorProblem(ERROR_COSTCENTER_NOT_PRESENT)))
				.withSubAccount(ofNullable(ai.getSubaccount()).orElseThrow(createInternalServerErrorProblem(ERROR_SUBACCOUNT_NOT_PRESENT)))
				.withOperation(ofNullable(ai.getDepartment()).orElseThrow(createInternalServerErrorProblem(ERROR_OPERATION_NOT_PRESENT)))
				.withActivity(ai.getActivity())
				.withProject(ai.getProject())
				.withObject(ai.getArticle())
				.withCounterpart(ofNullable(ai.getCounterpart()).orElseThrow(createInternalServerErrorProblem(ERROR_COUNTERPART_NOT_PRESENT)))
				.withAmount(ofNullable(ai.getAmount()).orElseThrow(createInternalServerErrorProblem(ERROR_ACCOUNT_INFORMATION_AMOUNT_NOT_PRESENT)))
				.withAccuralKey(ai.getAccuralKey()))
			.toList();
	}

	/**
	 * Method for mapping invoice data to an invoice footer row for an external invoice file
	 *
	 * @param  billingRecordEntity entity representing the billingRecordEntity
	 * @return                     InvoiceFooterRow for external invoice files representing provided data
	 * @throws ThrowableProblem    if any mandatory data is missing
	 */
	public static InvoiceFooterRow toInvoiceFooter(BillingRecordEntity billingRecordEntity) {
		final var invoiceEntity = ofNullable(billingRecordEntity.getInvoice()).orElseThrow(createInternalServerErrorProblem(ERROR_INVOICE_NOT_PRESENT));

		return InvoiceFooterRow.create()
			.withTotalAmount(invoiceEntity.getTotalAmount());
	}

	private static InvoiceDescriptionRow toInvoiceDescriptionRow(String legalId, String text) {
		return InvoiceDescriptionRow.create()
			.withLegalId(legalId)
			.withDescription(text);
	}

	private static String extractDescription(InvoiceRowEntity invoiceRowEntity) {
		return ofNullable(invoiceRowEntity.getDescriptions()).orElse(emptyList()).stream()
			.filter(description -> description.getType() == STANDARD)
			.map(DescriptionEntity::getText)
			.filter(StringUtils::isNotBlank)
			.findFirst()
			.orElse(null);
	}

	private static String extractRecipientName(RecipientEntity entity) {
		return ofNullable(entity.getOrganizationName())
			.orElseGet(() -> {
				if (allNotNull(entity.getFirstName(), entity.getLastName())) {
					return entity.getFirstName() + " " + entity.getLastName();
				}
				return null;
			});
	}

	private static String extractZipCodeAndCity(AddressDetailsEmbeddable addressDetailsEmbeddable) {
		if (allNotNull(addressDetailsEmbeddable, addressDetailsEmbeddable.getPostalCode(), addressDetailsEmbeddable.getCity())) {
			return addressDetailsEmbeddable.getPostalCode() + " " + addressDetailsEmbeddable.getCity();
		}
		return null;
	}

	private static String extractCounterpart(InvoiceEntity invoiceEntity) {
		return ofNullable(invoiceEntity)
			.map(InvoiceEntity::getInvoiceRows)
			.orElse(emptyList())
			.stream()
			.map(InvoiceRowEntity::getAccountInformation)
			.filter(CollectionUtils::isNotEmpty)
			.map(List::getFirst)
			.map(AccountInformationEmbeddable::getCounterpart)
			.filter(StringUtils::isNotBlank)
			.findFirst()
			.orElse(null);
	}

	private static BigDecimal calculateTotalAmount(final List<BillingRecordEntity> billingRecords) {
		return billingRecords.stream()
			.map(BillingRecordEntity::getInvoice)
			.map(InvoiceEntity::getInvoiceRows)
			.flatMap(List::stream)
			.map(InvoiceRowEntity::getTotalAmount)
			.reduce(BigDecimal::add)
			.orElse(BigDecimal.ZERO);
	}
}
