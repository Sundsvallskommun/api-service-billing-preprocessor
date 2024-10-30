package se.sundsvall.billingpreprocessor.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static java.util.Optional.ofNullable;

@Entity
@Table(name = "invoice")
public class InvoiceEntity implements Serializable {
	private static final long serialVersionUID = 7908651054386284628L;

	@Id
	@Column(name = "id")
	private String id;

	@OneToOne(fetch = LAZY)
	@MapsId
	@JoinColumn(name = "id", foreignKey = @ForeignKey(name = "fk_billing_record_id_invoice"))
	private BillingRecordEntity billingRecord;

	@Column(name = "customer_id")
	private String customerId;

	@Column(name = "description")
	private String description;

	@Column(name = "our_reference")
	private String ourReference;

	@Column(name = "customer_reference")
	private String customerReference;

	@Column(name = "reference_id")
	private String referenceId;

	@Column(name = "`date`", columnDefinition = "date")
	private LocalDate date;

	@Column(name = "due_date", columnDefinition = "date")
	private LocalDate dueDate;

	@Column(name = "total_amount")
	private Float totalAmount;

	@OneToMany(fetch = LAZY, mappedBy = "invoice", cascade = ALL, orphanRemoval = true)
	private List<InvoiceRowEntity> invoiceRows;

	public static InvoiceEntity create() {
		return new InvoiceEntity();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public InvoiceEntity withId(String id) {
		this.id = id;
		return this;
	}

	public BillingRecordEntity getBillingRecord() {
		return billingRecord;
	}

	public void setBillingRecord(BillingRecordEntity billingRecord) {
		this.billingRecord = billingRecord;
	}

	public InvoiceEntity withBillingRecord(BillingRecordEntity billingRecord) {
		this.billingRecord = billingRecord;
		return this;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public InvoiceEntity withCustomerId(String customerId) {
		this.customerId = customerId;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public InvoiceEntity withDescription(String description) {
		this.description = description;
		return this;
	}

	public String getOurReference() {
		return ourReference;
	}

	public InvoiceEntity withOurReference(String ourReference) {
		this.ourReference = ourReference;
		return this;
	}

	public void setOurReference(String ourReference) {
		this.ourReference = ourReference;
	}

	public String getCustomerReference() {
		return customerReference;
	}

	public void setCustomerReference(String customerReference) {
		this.customerReference = customerReference;
	}

	public InvoiceEntity withCustomerReference(String customerReference) {
		this.customerReference = customerReference;
		return this;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public InvoiceEntity withReferenceId(String referenceId) {
		this.referenceId = referenceId;
		return this;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public InvoiceEntity withDate(LocalDate date) {
		this.date = date;
		return this;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}

	public InvoiceEntity withDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
		return this;
	}

	public Float getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Float totalAmount) {
		this.totalAmount = totalAmount;
	}

	public InvoiceEntity withTotalAmount(Float totalAmount) {
		this.totalAmount = totalAmount;
		return this;
	}

	public List<InvoiceRowEntity> getInvoiceRows() {
		return invoiceRows;
	}

	public void setInvoiceRows(List<InvoiceRowEntity> invoiceRows) {
		ofNullable(this.invoiceRows).ifPresentOrElse(List::clear, () -> this.invoiceRows = new ArrayList<>());
		ofNullable(invoiceRows).ifPresent(value -> this.invoiceRows.addAll(value));
	}

	public InvoiceEntity withInvoiceRows(List<InvoiceRowEntity> invoiceRows) {
		ofNullable(this.invoiceRows).ifPresentOrElse(List::clear, () -> this.invoiceRows = new ArrayList<>());
		ofNullable(invoiceRows).ifPresent(value -> this.invoiceRows.addAll(value));
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(billingRecord, customerId, customerReference, description, date, dueDate, id, invoiceRows, ourReference, referenceId, totalAmount);
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
		InvoiceEntity other = (InvoiceEntity) obj;
		return Objects.equals(billingRecord, other.billingRecord) && Objects.equals(customerId, other.customerId) && Objects.equals(customerReference, other.customerReference) && Objects.equals(description, other.description) && Objects.equals(date,
			other.date) && Objects.equals(dueDate, other.dueDate) && Objects.equals(id, other.id) && Objects.equals(invoiceRows, other.invoiceRows) && Objects.equals(ourReference, other.ourReference) && Objects.equals(referenceId, other.referenceId)
			&& Objects.equals(totalAmount, other.totalAmount);
	}

	@Override
	public String toString() {
		final var billingRecordId = billingRecord == null ? null : billingRecord.getId();
		StringBuilder builder = new StringBuilder();
		builder.append("InvoiceEntity [id=").append(id)
			.append(", billingRecord=").append(billingRecordId)
			.append(", customerId=").append(customerId)
			.append(", description=").append(description)
			.append(", ourReference=").append(ourReference)
			.append(", customerReference=").append(customerReference)
			.append(", referenceId=").append(referenceId)
			.append(", date=").append(date)
			.append(", dueDate=").append(dueDate)
			.append(", totalAmount=").append(totalAmount)
			.append(", invoiceRows=").append(invoiceRows).append("]");
		return builder.toString();
	}
}
