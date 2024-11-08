package se.sundsvall.billingpreprocessor.service.mapper;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static se.sundsvall.billingpreprocessor.Constants.ERROR_ACCOUNT_INFORMATION_NOT_PRESENT;
import static se.sundsvall.billingpreprocessor.Constants.ERROR_COSTCENTER_NOT_PRESENT;
import static se.sundsvall.billingpreprocessor.Constants.ERROR_COUNTERPART_NOT_PRESENT;
import static se.sundsvall.billingpreprocessor.Constants.ERROR_CUSTOMER_ID_NOT_PRESENT;
import static se.sundsvall.billingpreprocessor.Constants.ERROR_CUSTOMER_REFERENCE_NOT_PRESENT;
import static se.sundsvall.billingpreprocessor.Constants.ERROR_DEPARTMENT_NOT_PRESENT;
import static se.sundsvall.billingpreprocessor.Constants.ERROR_DESCRIPTION_NOT_PRESENT;
import static se.sundsvall.billingpreprocessor.Constants.ERROR_INVOICE_NOT_PRESENT;
import static se.sundsvall.billingpreprocessor.Constants.ERROR_OUR_REFERENCE_NOT_PRESENT;
import static se.sundsvall.billingpreprocessor.Constants.ERROR_SUBACCOUNT_NOT_PRESENT;
import static se.sundsvall.billingpreprocessor.Constants.ERROR_TOTAL_AMOUNT_NOT_PRESENT;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.DescriptionType.DETAILED;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.DescriptionType.STANDARD;
import static se.sundsvall.billingpreprocessor.service.util.ProblemUtil.createInternalServerErrorProblem;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.zalando.problem.ThrowableProblem;

import se.sundsvall.billingpreprocessor.integration.db.model.BillingRecordEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.DescriptionEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceRowEntity;
import se.sundsvall.billingpreprocessor.service.creator.definition.internal.FileHeaderRow;
import se.sundsvall.billingpreprocessor.service.creator.definition.internal.InvoiceAccountingRow;
import se.sundsvall.billingpreprocessor.service.creator.definition.internal.InvoiceDescriptionRow;
import se.sundsvall.billingpreprocessor.service.creator.definition.internal.InvoiceFooterRow;
import se.sundsvall.billingpreprocessor.service.creator.definition.internal.InvoiceHeaderRow;
import se.sundsvall.billingpreprocessor.service.creator.definition.internal.InvoiceRow;
import se.sundsvall.billingpreprocessor.service.creator.definition.internal.InvoiceRowDescriptionRow;

public class InternalInvoiceMapper {
	private InternalInvoiceMapper() {}

	/**
	 * method for creating file header row for internal invoice files
	 *
	 * @return FileHeaderRow for internal invoice files
	 */
	public static FileHeaderRow toFileHeader() {
		return FileHeaderRow.create();
	}

	/**
	 * Method for mapping invoice data to a invoice header row for an internal invoice file
	 *
	 * @param  billingRecordEntity entity representing the billingRecordEntity
	 * @return                     InvoiceHeaderRow for internal invoice files representing provided data
	 * @throws ThrowableProblem    if any mandatory data is missing
	 */
	public static InvoiceHeaderRow toInvoiceHeader(BillingRecordEntity billingRecordEntity) {
		final var invoiceEntity = ofNullable(billingRecordEntity.getInvoice()).orElseThrow(createInternalServerErrorProblem(ERROR_INVOICE_NOT_PRESENT));

		return InvoiceHeaderRow.create()
			.withCustomerId(ofNullable(invoiceEntity.getCustomerId()).orElseThrow(createInternalServerErrorProblem(ERROR_CUSTOMER_ID_NOT_PRESENT)))
			.withDate(invoiceEntity.getDate())
			.withDueDate(invoiceEntity.getDueDate())
			.withCustomerReference(ofNullable(invoiceEntity.getCustomerReference()).orElseThrow(createInternalServerErrorProblem(ERROR_CUSTOMER_REFERENCE_NOT_PRESENT)))
			.withOurReference(ofNullable(invoiceEntity.getOurReference()).orElseThrow(createInternalServerErrorProblem(ERROR_OUR_REFERENCE_NOT_PRESENT)));
	}

	/**
	 * Method for mapping invoice data to a invoice description row for an internal invoice file
	 *
	 * @param  billingRecordEntity entity representing the billingRecordEntity
	 * @return                     InvoiceDescriptionRow for internal invoice files representing provided data
	 * @throws ThrowableProblem    if any mandatory data is missing
	 */
	public static InvoiceDescriptionRow toInvoiceDescriptionRow(BillingRecordEntity billingRecordEntity) {
		final var invoiceEntity = ofNullable(billingRecordEntity.getInvoice()).orElseThrow(createInternalServerErrorProblem(ERROR_INVOICE_NOT_PRESENT));

		return InvoiceDescriptionRow.create()
			.withDescription(invoiceEntity.getDescription());
	}

	/**
	 * Method for mapping invoice row data to an invoice description row for an internal invoice file. Description rows
	 * in entity with DescriptionType DETAILED will be transformed to a InvoiceRowDescriptionRow
	 *
	 * @param  invoiceRowEntity entity representing the invoiceRowEntity
	 * @return                  A list of InvoiceRowDescriptionRow representing provided data
	 */
	public static List<InvoiceRowDescriptionRow> toInvoiceRowDescriptionRows(InvoiceRowEntity invoiceRowEntity) {
		return ofNullable(invoiceRowEntity.getDescriptions()).orElse(emptyList()).stream()
			.filter(description -> description.getType() == DETAILED)
			.map(DescriptionEntity::getText)
			.filter(StringUtils::isNotBlank)
			.map(InternalInvoiceMapper::toInvoiceRowDescriptionRow)
			.toList();
	}

	private static InvoiceRowDescriptionRow toInvoiceRowDescriptionRow(String text) {
		return InvoiceRowDescriptionRow.create()
			.withDescription(text);
	}

	/**
	 * Method for mapping invoice row data to a invoice row for an internal invoice file
	 *
	 * @param  invoiceRowEntity entity representing the invoiceRowEntity
	 * @return                  InvoiceRow for internal invoice files representing provided data
	 * @throws ThrowableProblem if any mandatory data is missing
	 */
	public static InvoiceRow toInvoiceRow(InvoiceRowEntity invoiceRowEntity) {
		return InvoiceRow.create()
			.withDescription(ofNullable(extractDescription(invoiceRowEntity)).orElseThrow(createInternalServerErrorProblem(ERROR_DESCRIPTION_NOT_PRESENT)))
			.withCostPerUnit(invoiceRowEntity.getCostPerUnit())
			.withQuantity(invoiceRowEntity.getQuantity())
			.withTotalAmount(ofNullable(invoiceRowEntity.getTotalAmount()).orElseThrow(createInternalServerErrorProblem(ERROR_TOTAL_AMOUNT_NOT_PRESENT)));
	}

	/**
	 * Method for mapping invoice accounting row data to a invoice row for an internal invoice file
	 *
	 * @param  invoiceRowEntity entity representing the invoiceRowEntity
	 * @return                  InvoiceAccountingRow for internal invoice files representing provided data
	 * @throws ThrowableProblem if any mandatory data is missing
	 */
	public static InvoiceAccountingRow toInvoiceAccountingRow(InvoiceRowEntity invoiceRowEntity) {
		final var accountInformationEmbeddable = ofNullable(invoiceRowEntity.getAccountInformation()).orElseThrow(createInternalServerErrorProblem(ERROR_ACCOUNT_INFORMATION_NOT_PRESENT));

		return InvoiceAccountingRow.create()
			.withCostCenter(ofNullable(accountInformationEmbeddable.getCostCenter()).orElseThrow(createInternalServerErrorProblem(ERROR_COSTCENTER_NOT_PRESENT)))
			.withSubAccount(ofNullable(accountInformationEmbeddable.getSubaccount()).orElseThrow(createInternalServerErrorProblem(ERROR_SUBACCOUNT_NOT_PRESENT)))
			.withDepartment(ofNullable(accountInformationEmbeddable.getDepartment()).orElseThrow(createInternalServerErrorProblem(ERROR_DEPARTMENT_NOT_PRESENT)))
			.withActivity(accountInformationEmbeddable.getActivity())
			.withProject(accountInformationEmbeddable.getProject())
			.withObject(accountInformationEmbeddable.getArticle())
			.withCounterpart(ofNullable(accountInformationEmbeddable.getCounterpart()).orElseThrow(createInternalServerErrorProblem(ERROR_COUNTERPART_NOT_PRESENT)))
			.withTotalAmount(invoiceRowEntity.getTotalAmount())
			.withAccuralKey(accountInformationEmbeddable.getAccuralKey());
	}

	/**
	 *
	 * @param  invoiceEntity
	 * @return
	 */
	public static InvoiceFooterRow toInvoiceFooter(BillingRecordEntity billingRecordEntity) {
		final var invoiceEntity = ofNullable(billingRecordEntity.getInvoice()).orElseThrow(createInternalServerErrorProblem(ERROR_INVOICE_NOT_PRESENT));

		return InvoiceFooterRow.create()
			.withTotalAmount(invoiceEntity.getTotalAmount());
	}

	private static String extractDescription(InvoiceRowEntity invoiceRowEntity) {
		return ofNullable(invoiceRowEntity.getDescriptions()).orElse(emptyList()).stream()
			.filter(description -> description.getType() == STANDARD)
			.map(DescriptionEntity::getText)
			.filter(StringUtils::isNoneBlank)
			.findFirst()
			.orElse(null);
	}
}
