package se.sundsvall.billingpreprocessor.integration.db.model;

import static jakarta.persistence.GenerationType.IDENTITY;
import static java.time.OffsetDateTime.now;
import static java.time.ZoneId.systemDefault;
import static java.time.temporal.ChronoUnit.MILLIS;
import static org.hibernate.annotations.TimeZoneStorageType.NORMALIZE;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.InvoiceFileStatus.GENERATED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;
import org.hibernate.Length;
import org.hibernate.annotations.TimeZoneStorage;
import se.sundsvall.billingpreprocessor.integration.db.model.enums.InvoiceFileStatus;

@Entity
@Table(name = "invoice_file",
	indexes = {
		@Index(name = "idx_invoice_file_status", columnList = "status"),
		@Index(name = "idx_invoice_file_municipality_id", columnList = "municipalityId")
	},
	uniqueConstraints = @UniqueConstraint(name = "uq_file_name", columnNames = {
		"name"
	}))
public class InvoiceFileEntity implements Serializable {

	private static final long serialVersionUID = 912687910950201275L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id")
	private long id;

	@Column(name = "municipality_id", nullable = false)
	private String municipalityId;

	@Column(name = "name")
	private String name;

	@Column(name = "content", length = Length.LONG32)
	private String content;

	@Column(name = "encoding")
	private String encoding;

	@Column(name = "status")
	private InvoiceFileStatus status;

	@Column(name = "type")
	private String type;

	@Column(name = "created")
	@TimeZoneStorage(NORMALIZE)
	private OffsetDateTime created;

	@Column(name = "sent")
	@TimeZoneStorage(NORMALIZE)
	private OffsetDateTime sent;

	public static InvoiceFileEntity create() {
		return new InvoiceFileEntity();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public InvoiceFileEntity withId(long id) {
		this.id = id;
		return this;
	}

	public String getMunicipalityId() {
		return municipalityId;
	}

	public void setMunicipalityId(String municipalityId) {
		this.municipalityId = municipalityId;
	}

	public InvoiceFileEntity withMunicipalityId(String municipalityId) {
		this.municipalityId = municipalityId;
		return this;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public InvoiceFileEntity withName(String name) {
		this.name = name;
		return this;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public InvoiceFileEntity withContent(String content) {
		this.content = content;
		return this;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public InvoiceFileEntity withEncoding(String encoding) {
		this.encoding = encoding;
		return this;
	}

	public InvoiceFileStatus getStatus() {
		return status;
	}

	public void setStatus(InvoiceFileStatus status) {
		this.status = status;
	}

	public InvoiceFileEntity withStatus(InvoiceFileStatus status) {
		this.status = status;
		return this;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public InvoiceFileEntity withType(String type) {
		this.type = type;
		return this;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(OffsetDateTime created) {
		this.created = created;
	}

	public InvoiceFileEntity withCreated(OffsetDateTime created) {
		this.created = created;
		return this;
	}

	public OffsetDateTime getSent() {
		return sent;
	}

	public void setSent(OffsetDateTime sent) {
		this.sent = sent;
	}

	public InvoiceFileEntity withSent(OffsetDateTime sent) {
		this.sent = sent;
		return this;
	}

	@PrePersist
	void prePersist() {
		created = now(systemDefault()).truncatedTo(MILLIS);
		status = GENERATED;
	}

	@Override
	public int hashCode() {
		return Objects.hash(municipalityId, content, created, encoding, id, name, sent, status, type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof InvoiceFileEntity)) {
			return false;
		}
		InvoiceFileEntity other = (InvoiceFileEntity) obj;
		return Objects.equals(municipalityId, other.municipalityId) && Objects.equals(content, other.content) && Objects.equals(created, other.created) && Objects.equals(encoding, other.encoding) && id == other.id && Objects.equals(name, other.name)
			&& Objects.equals(sent, other.sent)
			&& status == other.status && Objects.equals(type, other.type);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("InvoiceFileEntity [id=").append(id).append(", municipalityId=").append(municipalityId).append(", name=").append(name).append(", content=").append(content).append(", encoding=").append(encoding).append(", status=").append(status)
			.append(", type=").append(type).append(
				", created=").append(created).append(", sent=").append(sent).append("]");
		return builder.toString();
	}
}
