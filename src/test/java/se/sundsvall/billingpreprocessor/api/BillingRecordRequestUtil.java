package se.sundsvall.billingpreprocessor.api;

import se.sundsvall.billingpreprocessor.api.model.AccountInformation;
import se.sundsvall.billingpreprocessor.api.model.AddressDetails;
import se.sundsvall.billingpreprocessor.api.model.BillingRecord;
import se.sundsvall.billingpreprocessor.api.model.Invoice;
import se.sundsvall.billingpreprocessor.api.model.InvoiceRow;
import se.sundsvall.billingpreprocessor.api.model.Issuer;
import se.sundsvall.billingpreprocessor.api.model.enums.Type;

import java.util.List;

import static java.time.OffsetDateTime.now;
import static java.util.UUID.randomUUID;
import static se.sundsvall.billingpreprocessor.api.model.enums.Status.CERTIFIED;
import static se.sundsvall.billingpreprocessor.api.model.enums.Type.EXTERNAL;
import static se.sundsvall.billingpreprocessor.api.model.enums.Type.INTERNAL;

public class BillingRecordRequestUtil {

	private BillingRecordRequestUtil() {
	}

	public static BillingRecord createBillingRecordInstance(Type type, boolean validBillingRecord) {
		return BillingRecord.create()
				.withCategory(validBillingRecord ? "ACCESS_CARD" : "INVALID")
				.withCertified(validBillingRecord ? null : now())
				.withCertifiedBy("certifiedBy")
				.withCreated(validBillingRecord ? null : now())
				.withId(validBillingRecord ? null : randomUUID().toString())
				.withModified(validBillingRecord ? null : now())
				.withStatus(CERTIFIED)
				.withType(type);
	}

	public static Issuer createIssuerInstance(boolean validIssuer) {
		return Issuer.create()
				.withFirstName(validIssuer ? "firstName" : null)
				.withPartyId(randomUUID().toString())
				.withLastName("lastName")
				.withUserId("userId");
	}

	public static AddressDetails createAddressDetailsInstance(boolean validAddress) {
		return validAddress ? AddressDetails.create()
				.withCareOf("careOf")
				.withCity("city")
				.withStreet("street")
				.withtPostalCode("zipCode") : AddressDetails.create();
	}

	public static Invoice createInvoiceInstance(boolean validInvoice, Type type) {
		return Invoice.create()
				.withCustomerId("customerId")
				.withCustomerReference(type == EXTERNAL && !validInvoice ? null : "customerReference")
				.withDescription("description")
				.withOurReference(type == INTERNAL && !validInvoice ? null : "reference")
				.withReferenceId(type == INTERNAL && !validInvoice ? null : "referenceId")
				.withTotalAmount(validInvoice ? null : 123f);
	}

	public static InvoiceRow createInvoiceRowInstance(boolean validInvoiceRows, Type type) {
		return InvoiceRow.create()
				.withAccountInformation(createAccountInformationInstance(validInvoiceRows))
				.withCostPerUnit(123f)
				.withDescriptions(List.of(validInvoiceRows ? "description" : "a longer description than thirty characters"))
				.withDetailedDescriptions(validInvoiceRows && INTERNAL == type ? null : List.of("detailedDescription"))
				.withQuantity(1)
				.withTotalAmount(validInvoiceRows ? null : 123f)
				.withVatCode(validInvoiceRows ? INTERNAL == type ? null : "00" : INTERNAL == type ? "00" : null);
	}

	public static AccountInformation createAccountInformationInstance(boolean validAccountInformation) {
		return validAccountInformation ? AccountInformation.create()
				.withCounterpart("counterPart")
				.withDepartment("department")
				.withCostCenter("costCenter")
				.withSubaccount("subAccount") : AccountInformation.create();
	}
}
