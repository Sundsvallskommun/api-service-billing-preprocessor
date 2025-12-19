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
import java.time.LocalDate;
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
		@Index(name = "idx_billing_record_municipality_id", columnList = "municipalityId"),
		@Index(name = "idx_billing_record_status_municipalityId_transfer_date", columnList = "status, municipalityId,transferDate")
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

	@Column(name = "transfer_date")
	private LocalDate transferDate;

	public static BillingRecordEntity create() {
		return new BillingRecordEntity();
	}

	@PrePersist
	void onCreate() {
		created = now(ZoneId.systemDefault()).truncatedTo(MILLIS);
	}

	@PreUpdate
	protected void onUpdate() {
		modified = now(ZoneId.systemDefault()).truncatedTo(MILLIS);
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public BillingRecordEntity withId(final String id) {
		this.id = id;
		return this;
	}

	public String getMunicipalityId() {
		return municipalityId;
	}

	public void setMunicipalityId(final String municipalityId) {
		this.municipalityId = municipalityId;
	}

	public BillingRecordEntity withMunicipalityId(final String municipalityId) {
		this.municipalityId = municipalityId;
		return this;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(final String category) {
		this.category = category;
	}

	public BillingRecordEntity withCategory(final String category) {
		this.category = category;
		return this;
	}

	public Type getType() {
		return type;
	}

	public void setType(final Type type) {
		this.type = type;
	}

	public BillingRecordEntity withType(final Type type) {
		this.type = type;
		return this;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(final Status status) {
		this.status = status;
	}

	public BillingRecordEntity withStatus(final Status status) {
		this.status = status;
		return this;
	}

	public String getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(final String approvedBy) {
		this.approvedBy = approvedBy;
	}

	public BillingRecordEntity withApprovedBy(final String approvedBy) {
		this.approvedBy = approvedBy;
		return this;
	}

	public OffsetDateTime getApproved() {
		return approved;
	}

	public void setApproved(final OffsetDateTime approved) {
		this.approved = approved;
	}

	public BillingRecordEntity withApproved(final OffsetDateTime approved) {
		this.approved = approved;
		return this;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(final OffsetDateTime created) {
		this.created = created;
	}

	public BillingRecordEntity withCreated(final OffsetDateTime created) {
		this.created = created;
		return this;
	}

	public OffsetDateTime getModified() {
		return modified;
	}

	public void setModified(final OffsetDateTime modified) {
		this.modified = modified;
	}

	public BillingRecordEntity withModified(final OffsetDateTime modified) {
		this.modified = modified;
		return this;
	}

	public RecipientEntity getRecipient() {
		return recipient;
	}

	public void setRecipient(final RecipientEntity recipient) {
		this.recipient = recipient;
	}

	public BillingRecordEntity withRecipient(final RecipientEntity recipient) {
		this.recipient = recipient;
		return this;
	}

	public InvoiceEntity getInvoice() {
		return invoice;
	}

	public void setInvoice(final InvoiceEntity invoice) {
		this.invoice = invoice;
	}

	public BillingRecordEntity withInvoice(final InvoiceEntity invoice) {
		this.invoice = invoice;
		return this;
	}

	public Map<String, String> getExtraParameters() {
		return extraParameters;
	}

	public void setExtraParameters(final Map<String, String> extraParameters) {
		this.extraParameters = extraParameters;
	}

	public BillingRecordEntity withExtraParameters(final Map<String, String> extraParameters) {
		this.extraParameters = extraParameters;
		return this;
	}

	public LocalDate getTransferDate() {
		return transferDate;
	}

	public void setTransferDate(final LocalDate transferDate) {
		this.transferDate = transferDate;
	}

	public BillingRecordEntity withTransferDate(final LocalDate transferDate) {
		this.transferDate = transferDate;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(municipalityId, category, approved, approvedBy, created, id, invoice, recipient, modified, status, type, extraParameters, transferDate);
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
		final BillingRecordEntity other = (BillingRecordEntity) obj;
		return Objects.equals(municipalityId, other.municipalityId) && Objects.equals(category, other.category) && Objects.equals(approved, other.approved) && Objects.equals(approvedBy, other.approvedBy) && Objects.equals(created, other.created) && Objects
			.equals(id, other.id) && Objects.equals(
				invoice, other.invoice) && Objects.equals(recipient, other.recipient) && Objects.equals(modified, other.modified) && Objects.equals(status, other.status) && Objects.equals(type, other.type) && Objects.equals(extraParameters,
					other.extraParameters) && Objects.equals(transferDate, other.transferDate);
	}

	@Override
	public String toString() {
		return "BillingRecordEntity{" +
			"id='" + id + '\'' +
			", category='" + category + '\'' +
			", municipalityId='" + municipalityId + '\'' +
			", type=" + type +
			", status=" + status +
			", approvedBy='" + approvedBy + '\'' +
			", approved=" + approved +
			", created=" + created +
			", modified=" + modified +
			", recipient=" + recipient +
			", invoice=" + invoice +
			", extraParameters=" + extraParameters +
			", transferDate=" + transferDate +
			'}';
	}
}
