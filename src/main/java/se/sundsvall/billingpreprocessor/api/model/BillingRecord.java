package se.sundsvall.billingpreprocessor.api.model;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import java.time.OffsetDateTime;
import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import io.swagger.v3.oas.annotations.media.Schema;
import se.sundsvall.billingpreprocessor.api.model.enums.Status;
import se.sundsvall.billingpreprocessor.api.model.enums.Type;
import se.sundsvall.billingpreprocessor.api.validation.ValidAddressDetails;
import se.sundsvall.billingpreprocessor.api.validation.ValidCertifiedBy;
import se.sundsvall.billingpreprocessor.api.validation.ValidInvoice;
import se.sundsvall.billingpreprocessor.api.validation.ValidInvoiceRows;
import se.sundsvall.billingpreprocessor.api.validation.ValidIssuer;

@Schema(description = "Billing record model")
@ValidCertifiedBy
@ValidInvoice
@ValidInvoiceRows
@ValidIssuer
@ValidAddressDetails
public class BillingRecord {

	@Schema(description = "Unique id for the billing record", example = "71258e7d-5285-46ce-b9b2-877f8cad8edd", accessMode = READ_ONLY)
	@Null
	private String id;

	@Schema(description = "Billing category", requiredMode = REQUIRED)
	@Pattern(regexp = "ACCESS_CARD|SALARY_AND_PENSION", message = "must be one of ACCESS_CARD or SALARY_AND_PENSION")
	@NotNull
	private String category;

	@Schema(implementation = Type.class, requiredMode = REQUIRED)
	@NotNull
	private Type type;

	@Schema(implementation = Status.class, requiredMode = REQUIRED)
	@NotNull
	private Status status;

	@Schema(description = "Information regarding the person that has certificed the billing record", example = "Big Bird")
	private String certifiedBy;

	@Schema(description = "Timestamp when the billing record got certified status", example = "2022-11-21T16:57:13.988+02:00", accessMode = READ_ONLY)
	@DateTimeFormat(iso = ISO.DATE_TIME)
	@Null
	private OffsetDateTime certified;

	@Schema(implementation = Issuer.class)
	@Valid
	private Issuer issuer;

	@Schema(implementation = Invoice.class, requiredMode = REQUIRED)
	@NotNull
	@Valid
	private Invoice invoice;

	@Schema(description = "Timestamp when the billing record was created", example = "2022-10-31T14:30:00.001+02:00", accessMode = READ_ONLY)
	@DateTimeFormat(iso = ISO.DATE_TIME)
	@Null
	private OffsetDateTime created;

	@Schema(description = "Timestamp when the billing record was last modified", example = "2022-11-14T08:57:42.358+02:00", accessMode = READ_ONLY)
	@DateTimeFormat(iso = ISO.DATE_TIME)
	@Null
	private OffsetDateTime modified;

	public static BillingRecord create() {
		return new BillingRecord();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public BillingRecord withId(String id) {
		this.id = id;
		return this;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public BillingRecord withCategory(String category) {
		this.category = category;
		return this;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public BillingRecord withType(Type type) {
		this.type = type;
		return this;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public BillingRecord withStatus(Status status) {
		this.status = status;
		return this;
	}

	public String getCertifiedBy() {
		return certifiedBy;
	}

	public void setCertifiedBy(String certifiedBy) {
		this.certifiedBy = certifiedBy;
	}

	public BillingRecord withCertifiedBy(String certifiedBy) {
		this.certifiedBy = certifiedBy;
		return this;
	}

	public OffsetDateTime getCertified() {
		return certified;
	}

	public void setCertified(OffsetDateTime certified) {
		this.certified = certified;
	}

	public BillingRecord withCertified(OffsetDateTime certified) {
		this.certified = certified;
		return this;
	}

	public Issuer getIssuer() {
		return issuer;
	}

	public void setIssuer(Issuer issuer) {
		this.issuer = issuer;
	}

	public BillingRecord withIssuer(Issuer issuer) {
		this.issuer = issuer;
		return this;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public BillingRecord withInvoice(Invoice invoice) {
		this.invoice = invoice;
		return this;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(OffsetDateTime created) {
		this.created = created;
	}

	public BillingRecord withCreated(OffsetDateTime created) {
		this.created = created;
		return this;
	}

	public OffsetDateTime getModified() {
		return modified;
	}

	public void setModified(OffsetDateTime modified) {
		this.modified = modified;
	}

	public BillingRecord withModified(OffsetDateTime modified) {
		this.modified = modified;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(category, certified, certifiedBy, created, id, invoice, issuer, modified, status, type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		BillingRecord other = (BillingRecord) obj;
		return category == other.category && Objects.equals(certified, other.certified) && Objects.equals(certifiedBy, other.certifiedBy) && Objects.equals(created, other.created) && Objects.equals(id, other.id) && Objects.equals(invoice,
			other.invoice) && Objects.equals(issuer, other.issuer) && Objects.equals(modified, other.modified) && status == other.status && type == other.type;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BillingRecord[id=").append(id)
			.append(", category=").append(category)
			.append(", type=").append(type)
			.append(", status=").append(status)
			.append(", certifiedBy=").append(certifiedBy)
			.append(", certified=").append(certified)
			.append(", issuer=").append(issuer)
			.append(", invoice=").append(invoice)
			.append(", created=").append(created)
			.append(", modified=").append(modified).append("]");
		return builder.toString();
	}
}
