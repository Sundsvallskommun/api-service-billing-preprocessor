package se.sundsvall.billingpreprocessor.api.model;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Objects;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import se.sundsvall.billingpreprocessor.api.model.enums.Status;
import se.sundsvall.billingpreprocessor.api.model.enums.Type;
import se.sundsvall.billingpreprocessor.api.validation.ValidAddressDetails;
import se.sundsvall.billingpreprocessor.api.validation.ValidApprovedBy;
import se.sundsvall.billingpreprocessor.api.validation.ValidInvoice;
import se.sundsvall.billingpreprocessor.api.validation.ValidInvoiceRows;
import se.sundsvall.billingpreprocessor.api.validation.ValidRecipient;

@Schema(description = "Billing record model")
@ValidApprovedBy
@ValidInvoice
@ValidInvoiceRows
@ValidRecipient
@ValidAddressDetails
public class BillingRecord {

	@Schema(description = "Unique id for the billing record", examples = "71258e7d-5285-46ce-b9b2-877f8cad8edd", accessMode = READ_ONLY)
	@Null
	private String id;

	@Schema(description = "Billing category", requiredMode = REQUIRED)
	@Pattern(regexp = "ACCESS_CARD|CUSTOMER_INVOICE|SALARY_AND_PENSION|ISYCASE|MEX_INVOICE", message = "must be one of ACCESS_CARD, CUSTOMER_INVOICE, SALARY_AND_PENSION, ISYCASE or MEX_INVOICE")
	@NotNull
	private String category;

	@Schema(implementation = Type.class, requiredMode = REQUIRED)
	@NotNull
	private Type type;

	@Schema(implementation = Status.class, requiredMode = REQUIRED)
	@NotNull
	private Status status;

	@Schema(description = "Information regarding the person that has approved the billing record", examples = "Big Bird")
	private String approvedBy;

	@Schema(description = "Timestamp when the billing record got approved status", examples = "2022-11-21T16:57:13.988+02:00", accessMode = READ_ONLY)
	@DateTimeFormat(iso = ISO.DATE_TIME)
	@Null
	private OffsetDateTime approved;

	@Schema(implementation = Recipient.class)
	@Valid
	private Recipient recipient;

	@Schema(implementation = Invoice.class, requiredMode = REQUIRED)
	@NotNull
	@Valid
	private Invoice invoice;

	@Schema(description = "Timestamp when the billing record was created", examples = "2022-10-31T14:30:00.001+02:00", accessMode = READ_ONLY)
	@DateTimeFormat(iso = ISO.DATE_TIME)
	@Null
	private OffsetDateTime created;

	@Schema(description = "Timestamp when the billing record was last modified", examples = "2022-11-14T08:57:42.358+02:00", accessMode = READ_ONLY)
	@DateTimeFormat(iso = ISO.DATE_TIME)
	@Null
	private OffsetDateTime modified;

	@Schema(description = "A map of extra parameters for custom values on the billing record", examples = "{\"caseId\":\"abc123\",\"uuid\":\"82a400cf-eb02-4a18-962d-fde55440868f\"}")
	private Map<String, String> extraParameters;

	@Schema(description = "The date when the billing record should be transferred to Raindance. If not specified, defaults to the next 15th of the month.", examples = "2023-12-15")
	@DateTimeFormat(iso = ISO.DATE)
	private LocalDate transferDate;

	public static BillingRecord create() {
		return new BillingRecord();
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public BillingRecord withId(final String id) {
		this.id = id;
		return this;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(final String category) {
		this.category = category;
	}

	public BillingRecord withCategory(final String category) {
		this.category = category;
		return this;
	}

	public Type getType() {
		return type;
	}

	public void setType(final Type type) {
		this.type = type;
	}

	public BillingRecord withType(final Type type) {
		this.type = type;
		return this;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(final Status status) {
		this.status = status;
	}

	public BillingRecord withStatus(final Status status) {
		this.status = status;
		return this;
	}

	public String getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(final String approvedBy) {
		this.approvedBy = approvedBy;
	}

	public BillingRecord withApprovedBy(final String approvedBy) {
		this.approvedBy = approvedBy;
		return this;
	}

	public OffsetDateTime getApproved() {
		return approved;
	}

	public void setApproved(final OffsetDateTime approved) {
		this.approved = approved;
	}

	public BillingRecord withApproved(final OffsetDateTime approved) {
		this.approved = approved;
		return this;
	}

	public Recipient getRecipient() {
		return recipient;
	}

	public void setRecipient(final Recipient recipient) {
		this.recipient = recipient;
	}

	public BillingRecord withRecipient(final Recipient recipient) {
		this.recipient = recipient;
		return this;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(final Invoice invoice) {
		this.invoice = invoice;
	}

	public BillingRecord withInvoice(final Invoice invoice) {
		this.invoice = invoice;
		return this;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(final OffsetDateTime created) {
		this.created = created;
	}

	public BillingRecord withCreated(final OffsetDateTime created) {
		this.created = created;
		return this;
	}

	public OffsetDateTime getModified() {
		return modified;
	}

	public void setModified(final OffsetDateTime modified) {
		this.modified = modified;
	}

	public BillingRecord withModified(final OffsetDateTime modified) {
		this.modified = modified;
		return this;
	}

	public Map<String, String> getExtraParameters() {
		return extraParameters;
	}

	public void setExtraParameters(final Map<String, String> extraParameters) {
		this.extraParameters = extraParameters;
	}

	public BillingRecord withExtraParameters(final Map<String, String> extraParameters) {
		this.extraParameters = extraParameters;
		return this;
	}

	public LocalDate getTransferDate() {
		return transferDate;
	}

	public void setTransferDate(final LocalDate transferDate) {
		this.transferDate = transferDate;
	}

	public BillingRecord withTransferDate(final LocalDate transferDate) {
		this.transferDate = transferDate;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(category, approved, approvedBy, created, id, invoice, recipient, modified, status, type, extraParameters, transferDate);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final BillingRecord other = (BillingRecord) obj;
		return Objects.equals(category, other.category) && Objects.equals(approved, other.approved) && Objects.equals(approvedBy, other.approvedBy) && Objects.equals(created, other.created) && Objects.equals(id, other.id) && Objects.equals(invoice,
			other.invoice) && Objects.equals(recipient, other.recipient) && Objects.equals(modified, other.modified) && Objects.equals(status, other.status) && Objects.equals(type, other.type) && Objects.equals(extraParameters, other.extraParameters)
			&& Objects.equals(transferDate, other.transferDate);
	}

	@Override
	public String toString() {
		return "BillingRecord[id=" + id
			+ ", category=" + category
			+ ", type=" + type
			+ ", status=" + status
			+ ", approvedBy=" + approvedBy
			+ ", approved=" + approved
			+ ", recipient=" + recipient
			+ ", invoice=" + invoice
			+ ", created=" + created
			+ ", modified=" + modified
			+ ", extraParameters" + extraParameters
			+ ", transferDate=" + transferDate + "]";
	}
}
