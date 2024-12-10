package se.sundsvall.billingpreprocessor.service.creator.definition.internal;

import java.time.LocalDate;
import java.util.Objects;
import org.beanio.annotation.Field;
import org.beanio.annotation.Fields;
import org.beanio.annotation.Record;

@Record
@Fields({
	@Field(at = 0, length = 2, name = "recordType", rid = true, literal = "H")
})
public class InvoiceHeaderRow {

	@Field(at = 2, length = 10)
	private String customerId;

	@Field(at = 12, length = 8, format = "yyyyMMdd")
	private LocalDate date;

	@Field(at = 20, length = 19, format = "yyyyMMdd")
	private LocalDate dueDate;

	@Field(at = 39, length = 63)
	private String customerReference;

	@Field(at = 102, length = 30)
	private String ourReference;

	public static InvoiceHeaderRow create() {
		return new InvoiceHeaderRow();
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public InvoiceHeaderRow withCustomerId(String customerId) {
		this.customerId = customerId;
		return this;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public InvoiceHeaderRow withDate(LocalDate date) {
		this.date = date;
		return this;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}

	public InvoiceHeaderRow withDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
		return this;
	}

	public String getCustomerReference() {
		return customerReference;
	}

	public void setCustomerReference(String customerReference) {
		this.customerReference = customerReference;
	}

	public InvoiceHeaderRow withCustomerReference(String customerReference) {
		this.customerReference = customerReference;
		return this;
	}

	public String getOurReference() {
		return ourReference;
	}

	public void setOurReference(String ourReference) {
		this.ourReference = ourReference;
	}

	public InvoiceHeaderRow withOurReference(String ourReference) {
		this.ourReference = ourReference;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(customerId, customerReference, date, dueDate, ourReference);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof InvoiceHeaderRow)) {
			return false;
		}
		InvoiceHeaderRow other = (InvoiceHeaderRow) obj;
		return Objects.equals(customerId, other.customerId) && Objects.equals(customerReference, other.customerReference) && Objects.equals(date, other.date) && Objects.equals(dueDate, other.dueDate) && Objects.equals(ourReference,
			other.ourReference);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("InvoiceHeaderRow [customerId=").append(customerId).append(", date=").append(date).append(", dueDate=").append(dueDate).append(", customerReference=").append(customerReference).append(", ourReference=").append(ourReference)
			.append("]");
		return builder.toString();
	}
}
