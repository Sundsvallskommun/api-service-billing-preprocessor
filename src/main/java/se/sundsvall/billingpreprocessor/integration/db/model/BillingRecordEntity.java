package se.sundsvall.billingpreprocessor.integration.db.model;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.MILLIS;
import static org.hibernate.annotations.TimeZoneStorageType.NORMALIZE;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Objects;

import org.hibernate.annotations.TimeZoneStorage;
import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import se.sundsvall.billingpreprocessor.api.model.enums.Status;
import se.sundsvall.billingpreprocessor.api.model.enums.Type;

@Entity
@Table(name = "billing_record",
	indexes = {
		@Index(name = "idx_billing_record_category_status", columnList = "category, status")
	})
public class BillingRecordEntity implements Serializable {
	private static final long serialVersionUID = -1199591346011106014L;

	@Id
	@UuidGenerator
	@Column(name = "id")
	private String id;

	@Column(name = "category", nullable = false)
	private String category;

	@Column(name = "type", nullable = false)
	@Enumerated(STRING)
	private Type type;

	@Column(name = "status", nullable = false)
	@Enumerated(STRING)
	private Status status;

	@Column(name = "approved_by")
	private String approvedBy;

	@Column(name = "approved")
	@TimeZoneStorage(NORMALIZE)
	private OffsetDateTime approved;

	@Column(name = "created")
	@TimeZoneStorage(NORMALIZE)
	private OffsetDateTime created;

	@Column(name = "modified")
	@TimeZoneStorage(NORMALIZE)
	private OffsetDateTime modified;

	@OneToOne(mappedBy = "billingRecord", cascade = ALL, fetch = LAZY, orphanRemoval = true)
	private IssuerEntity issuer;

	@OneToOne(mappedBy = "billingRecord", cascade = ALL, fetch = LAZY, optional = false)
	private InvoiceEntity invoice;

	@PrePersist
	void onCreate() {
		created = now(ZoneId.systemDefault()).truncatedTo(MILLIS);
	}

	@PreUpdate
	protected void onUpdate() {
		modified = now(ZoneId.systemDefault()).truncatedTo(MILLIS);
	}

	public static BillingRecordEntity create() {
		return new BillingRecordEntity();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public BillingRecordEntity withId(String id) {
		this.id = id;
		return this;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public BillingRecordEntity withCategory(String category) {
		this.category = category;
		return this;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public BillingRecordEntity withType(Type type) {
		this.type = type;
		return this;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public BillingRecordEntity withStatus(Status status) {
		this.status = status;
		return this;
	}

	public String getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(String approvedBy) {
		this.approvedBy = approvedBy;
	}

	public BillingRecordEntity withApprovedBy(String approvedBy) {
		this.approvedBy = approvedBy;
		return this;
	}

	public OffsetDateTime getApproved() {
		return approved;
	}

	public void setApproved(OffsetDateTime approved) {
		this.approved = approved;
	}

	public BillingRecordEntity withApproved(OffsetDateTime approved) {
		this.approved = approved;
		return this;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(OffsetDateTime created) {
		this.created = created;
	}

	public BillingRecordEntity withCreated(OffsetDateTime created) {
		this.created = created;
		return this;
	}

	public OffsetDateTime getModified() {
		return modified;
	}

	public void setModified(OffsetDateTime modified) {
		this.modified = modified;
	}

	public BillingRecordEntity withModified(OffsetDateTime modified) {
		this.modified = modified;
		return this;
	}

	public IssuerEntity getIssuer() {
		return issuer;
	}

	public void setIssuer(IssuerEntity issuer) {
		this.issuer = issuer;
	}

	public BillingRecordEntity withIssuer(IssuerEntity issuer) {
		this.issuer = issuer;
		return this;
	}

	public InvoiceEntity getInvoice() {
		return invoice;
	}

	public void setInvoice(InvoiceEntity invoice) {
		this.invoice = invoice;
	}

	public BillingRecordEntity withInvoice(InvoiceEntity invoice) {
		this.invoice = invoice;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(category, approved, approvedBy, created, id, invoice, issuer, modified, status, type);
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
		final BillingRecordEntity other = (BillingRecordEntity) obj;
		return Objects.equals(category, other.category) && Objects.equals(approved, other.approved) && Objects.equals(approvedBy, other.approvedBy) && Objects.equals(created, other.created) && Objects.equals(id, other.id) && Objects.equals(
			invoice, other.invoice) && Objects.equals(issuer, other.issuer) && Objects.equals(modified, other.modified) && Objects.equals(status, other.status) && Objects.equals(type, other.type);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("BillingRecordEntity [id=").append(id)
			.append(", category=").append(category)
			.append(", type=").append(type)
			.append(", status=").append(status)
			.append(", approvedBy=").append(approvedBy)
			.append(", approved=").append(approved)
			.append(", created=").append(created)
			.append(", modified=").append(modified)
			.append(", issuer=").append(issuer)
			.append(", invoice=").append(invoice).append("]");
		return builder.toString();
	}
}
