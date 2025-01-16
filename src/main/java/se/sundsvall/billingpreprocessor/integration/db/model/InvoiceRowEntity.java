package se.sundsvall.billingpreprocessor.integration.db.model;

import static jakarta.persistence.FetchType.EAGER;
import static java.util.Optional.ofNullable;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "invoice_row")
public class InvoiceRowEntity implements Serializable {
	private static final long serialVersionUID = 5194419145209880931L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "invoice.id", nullable = false, foreignKey = @ForeignKey(name = "fk_invoice_id_invoice_row"))
	private InvoiceEntity invoice;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "invoiceRow", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DescriptionEntity> descriptions;

	@Column(name = "total_amount")
	private Float totalAmount;

	@Column(name = "vat_code")
	private String vatCode;

	@Column(name = "cost_per_unit")
	private Float costPerUnit;

	@Column(name = "quantity")
	private Float quantity;

	@ElementCollection(fetch = EAGER)
	@CollectionTable(
		indexes = {
			@Index(name = "idx_invoice_row_id", columnList = "invoice_row_id")
		},
		name = "account_information",
		joinColumns = @JoinColumn(
			name = "invoice_row_id",
			referencedColumnName = "id",
			foreignKey = @ForeignKey(name = "fk_invoice_row_id_account_information")))
	private List<AccountInformationEmbeddable> accountInformation;

	public static InvoiceRowEntity create() {
		return new InvoiceRowEntity();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public InvoiceRowEntity withId(long id) {
		this.id = id;
		return this;
	}

	public InvoiceEntity getInvoice() {
		return invoice;
	}

	public void setInvoice(InvoiceEntity invoice) {
		this.invoice = invoice;
	}

	public InvoiceRowEntity withInvoice(InvoiceEntity invoice) {
		this.invoice = invoice;
		return this;
	}

	public List<DescriptionEntity> getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(List<DescriptionEntity> descriptions) {
		ofNullable(this.descriptions).ifPresentOrElse(List::clear, () -> this.descriptions = new ArrayList<>());
		ofNullable(descriptions).ifPresent(value -> this.descriptions.addAll(value));
	}

	public InvoiceRowEntity withDescriptions(List<DescriptionEntity> descriptions) {
		ofNullable(this.descriptions).ifPresentOrElse(List::clear, () -> this.descriptions = new ArrayList<>());
		ofNullable(descriptions).ifPresent(value -> this.descriptions.addAll(value));
		return this;
	}

	public Float getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Float totalAmount) {
		this.totalAmount = totalAmount;
	}

	public InvoiceRowEntity withTotalAmount(Float totalAmount) {
		this.totalAmount = totalAmount;
		return this;
	}

	public String getVatCode() {
		return vatCode;
	}

	public void setVatCode(String vatCode) {
		this.vatCode = vatCode;
	}

	public InvoiceRowEntity withVatCode(String vatCode) {
		this.vatCode = vatCode;
		return this;
	}

	public Float getCostPerUnit() {
		return costPerUnit;
	}

	public void setCostPerUnit(Float costPerUnit) {
		this.costPerUnit = costPerUnit;
	}

	public InvoiceRowEntity withCostPerUnit(Float costPerUnit) {
		this.costPerUnit = costPerUnit;
		return this;
	}

	public Float getQuantity() {
		return quantity;
	}

	public void setQuantity(Float quantity) {
		this.quantity = quantity;
	}

	public InvoiceRowEntity withQuantity(Float quantity) {
		this.quantity = quantity;
		return this;
	}

	public List<AccountInformationEmbeddable> getAccountInformation() {
		return accountInformation;
	}

	public void setAccountInformation(List<AccountInformationEmbeddable> accountInformation) {
		this.accountInformation = accountInformation;
	}

	public InvoiceRowEntity withAccountInformation(List<AccountInformationEmbeddable> accountInformation) {
		this.accountInformation = accountInformation;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(accountInformation, costPerUnit, descriptions, id, invoice, quantity, totalAmount, vatCode);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		final var other = (InvoiceRowEntity) obj;
		return Objects.equals(accountInformation, other.accountInformation) && Objects.equals(costPerUnit, other.costPerUnit) && Objects.equals(descriptions, other.descriptions) && id == other.id && Objects.equals(invoice,
			other.invoice) && Objects.equals(quantity, other.quantity) && Objects.equals(totalAmount, other.totalAmount) && Objects.equals(vatCode, other.vatCode);
	}

	@Override
	public String toString() {
		final var invoiceEntityId = invoice == null ? null : invoice.getId();
		final var builder = new StringBuilder();
		builder.append("InvoiceRowEntity [id=").append(id)
			.append(", invoice=").append(invoiceEntityId)
			.append(", descriptions=").append(descriptions)
			.append(", totalAmount=").append(totalAmount)
			.append(", vatCode=").append(vatCode)
			.append(", costPerUnit=").append(costPerUnit)
			.append(", quantity=").append(quantity)
			.append(", accountInformation=").append(accountInformation).append("]");
		return builder.toString();
	}
}
