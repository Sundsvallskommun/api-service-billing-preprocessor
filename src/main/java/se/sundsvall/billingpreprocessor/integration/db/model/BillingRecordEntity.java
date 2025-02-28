package se.sundsvall.billingpreprocessor.integration.db.model;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.FetchType.LAZY;
import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.MILLIS;
import static org.hibernate.annotations.TimeZoneStorageType.NORMALIZE;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Objects;
import org.hibernate.annotations.TimeZoneStorage;
import org.hibernate.annotations.UuidGenerator;
import se.sundsvall.billingpreprocessor.integration.db.model.enums.Status;
import se.sundsvall.billingpreprocessor.integration.db.model.enums.Type;

@Entity
@Table(name = "billing_record",
	indexes = {
		@Index(name = "idx_billing_record_category_status", columnList = "category, status"),
		@Index(name = "idx_billing_record_municipality_id", columnList = "municipalityId")
	})
public class BillingRecordEntity implements Serializable {
	private static final long serialVersionUID = -1199591346011106014L;

	@Id
	@UuidGenerator
	@Column(name = "id")
	private String id;

	@Column(name = "category", nullable = false)
	private String category;

	@Column(name = "municipality_id", nullable = false)
	private String municipalityId;

	@Column(name = "type", nullable = false)
	private Type type;

	@Column(name = "status", nullable = false)
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
	private RecipientEntity recipient;

	@OneToOne(mappedBy = "billingRecord", cascade = ALL, fetch = LAZY, optional = false)
	private InvoiceEntity invoice;

	// Notice the "backticks" to avoid reserved word conflicts.
	@ElementCollection(fetch = EAGER)
	@CollectionTable(
		indexes = {
			@Index(name = "idx_extra_parameter_key", columnList = "`key`")
		},
		name = "extra_parameter",
		joinColumns = @JoinColumn(
			name = "billing_record_id",
			referencedColumnName = "id",
			foreignKey = @ForeignKey(name = "fk_billing_record_id_extra_parameter")))
	@MapKeyColumn(name = "`key`")
	@Column(name = "`value`")
	private Map<String, String> extraParameters;

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

	public String getMunicipalityId() {
		return municipalityId;
	}

	public void setMunicipalityId(String municipalityId) {
		this.municipalityId = municipalityId;
	}

	public BillingRecordEntity withMunicipalityId(String municipalityId) {
		this.municipalityId = municipalityId;
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

	public RecipientEntity getRecipient() {
		return recipient;
	}

	public void setRecipient(RecipientEntity recipient) {
		this.recipient = recipient;
	}

	public BillingRecordEntity withRecipient(RecipientEntity recipient) {
		this.recipient = recipient;
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

	public Map<String, String> getExtraParameters() {
		return extraParameters;
	}

	public void setExtraParameters(Map<String, String> extraParameters) {
		this.extraParameters = extraParameters;
	}

	public BillingRecordEntity withExtraParameters(Map<String, String> extraParameters) {
		this.extraParameters = extraParameters;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(municipalityId, category, approved, approvedBy, created, id, invoice, recipient, modified, status, type, extraParameters);
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
		return Objects.equals(municipalityId, other.municipalityId) && Objects.equals(category, other.category) && Objects.equals(approved, other.approved) && Objects.equals(approvedBy, other.approvedBy) && Objects.equals(created, other.created) && Objects
			.equals(id, other.id) && Objects.equals(
				invoice, other.invoice) && Objects.equals(recipient, other.recipient) && Objects.equals(modified, other.modified) && Objects.equals(status, other.status) && Objects.equals(type, other.type) && Objects.equals(extraParameters,
					other.extraParameters);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("BillingRecordEntity [id=").append(id)
			.append(", municipalityId=").append(municipalityId)
			.append(", category=").append(category)
			.append(", type=").append(type)
			.append(", status=").append(status)
			.append(", approvedBy=").append(approvedBy)
			.append(", approved=").append(approved)
			.append(", created=").append(created)
			.append(", modified=").append(modified)
			.append(", recipient=").append(recipient)
			.append(", invoice=").append(invoice)
			.append(", extraParameters").append(extraParameters).append("]");
		return builder.toString();
	}
}
