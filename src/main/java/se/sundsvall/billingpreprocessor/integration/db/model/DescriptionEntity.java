package se.sundsvall.billingpreprocessor.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Objects;
import se.sundsvall.billingpreprocessor.integration.db.model.enums.DescriptionType;

@Entity
@Table(name = "description")
public class DescriptionEntity implements Serializable {
	private static final long serialVersionUID = -6749290077380338561L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "invoice_row.id", nullable = false, foreignKey = @ForeignKey(name = "fk_invoice_row_id_description"))
	private InvoiceRowEntity invoiceRow;

	@Column(name = "type", columnDefinition = "ENUM('DETAILED', 'STANDARD')")
	private DescriptionType type;

	@Column(name = "text")
	private String text;

	public static DescriptionEntity create() {
		return new DescriptionEntity();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public DescriptionEntity withId(long id) {
		this.id = id;
		return this;
	}

	public InvoiceRowEntity getInvoiceRow() {
		return invoiceRow;
	}

	public void setInvoiceRow(InvoiceRowEntity invoiceRow) {
		this.invoiceRow = invoiceRow;
	}

	public DescriptionEntity withInvoiceRow(InvoiceRowEntity invoiceRow) {
		this.invoiceRow = invoiceRow;
		return this;
	}

	public DescriptionType getType() {
		return type;
	}

	public void setType(DescriptionType type) {
		this.type = type;
	}

	public DescriptionEntity withType(DescriptionType type) {
		this.type = type;
		return this;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public DescriptionEntity withText(String text) {
		this.text = text;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, invoiceRow, text, type);
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
		final DescriptionEntity other = (DescriptionEntity) obj;
		return (id == other.id) && Objects.equals(invoiceRow, other.invoiceRow) && Objects.equals(text, other.text) && (type == other.type);
	}

	@Override
	public String toString() {
		final var invoiceRowId = invoiceRow == null ? null : invoiceRow.getId();
		final StringBuilder builder = new StringBuilder();
		builder.append("DescriptionEntity [id=").append(id)
			.append(", invoiceRow=").append(invoiceRowId)
			.append(", type=").append(type)
			.append(", text=").append(text).append("]");
		return builder.toString();
	}
}
