package se.sundsvall.billingpreprocessor;

public final class Constants {
	private Constants() {}

	public static final byte[] EMPTY_ARRAY = new byte[0];

	public static final String ERROR_ACCOUNT_INFORMATION_NOT_PRESENT = "Account information is not present";
	public static final String ERROR_COSTCENTER_NOT_PRESENT = "Costcenter is not present";
	public static final String ERROR_COUNTERPART_NOT_PRESENT = "Counterpart is not present";
	public static final String ERROR_CUSTOMER_ID_NOT_PRESENT = "Customer id is not present";
	public static final String ERROR_CUSTOMER_REFERENCE_NOT_PRESENT = "Customer reference is not present";
	public static final String ERROR_DEPARTMENT_NOT_PRESENT = "Department is not present";
	public static final String ERROR_DESCRIPTION_NOT_PRESENT = "Description is not present";
	public static final String ERROR_GENERATING_SYSTEM_NOT_PRESENT = "Generating system is not present";
	public static final String ERROR_INVOICE_NOT_PRESENT = "Invoice is not present";
	public static final String ERROR_INVOICE_TYPE_NOT_PRESENT = "Invoice type is not present";
	public static final String ERROR_LEGALID_NOT_PRESENT = "LegalId is not present";
	public static final String ERROR_OPERATION_NOT_PRESENT = "Operation is not present";
	public static final String ERROR_OUR_REFERENCE_NOT_PRESENT = "Our reference is not present";
	public static final String ERROR_RECIPIENT_COUNTERPART_NOT_PRESENT = "Recipient counterpart is not present";
	public static final String ERROR_RECIPIENT_NAME_NOT_PRESENT = "Recipient name is not present";
	public static final String ERROR_RECIPIENT_NOT_PRESENT = "Recipient is not present";
	public static final String ERROR_RECIPIENT_STREET_ADDRESS_NOT_PRESENT = "Recipient street address is not present";
	public static final String ERROR_RECIPIENT_ZIPCODE_OR_CITY_NOT_PRESENT = "Recipient zip code or city is not present";
	public static final String ERROR_SUBACCOUNT_NOT_PRESENT = "Sub account is not present";
	public static final String ERROR_TOTAL_AMOUNT_NOT_PRESENT = "Total amount is not present";
	public static final String ERROR_VAT_CODE_NOT_PRESENT = "Vat code is not present";

	public static final String ERROR_NO_INVOICE_FILE_CONFIGURATION_FOUND = "No invoice file configuration found by type: '%s' and categoryTag: '%s'";
	public static final String ERROR_INVOICE_FILE_NAME_GENERATION_FAILURE = "Could not generate filename from template: '%s'";

	public static final String EXTERNAL_INVOICE_TYPE = "Extern debitering";
	public static final String GENERATING_SYSTEM = "BillingPreProcessor";
}
