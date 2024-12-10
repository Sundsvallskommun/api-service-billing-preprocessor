package se.sundsvall.billingpreprocessor.service.creator.definition.external;

import java.time.LocalDate;
import java.util.Objects;
import org.beanio.annotation.Field;
import org.beanio.annotation.Fields;
import org.beanio.annotation.Record;

@Record
@Fields({
	@Field(at = 0, length = 1, name = "recordType", rid = true, literal = "H")
})
public class InvoiceHeaderRow {

	@Field(at = 1, length = 10)
	private String legalId;

	@Field(at = 11, length = 6, format = "yyMMdd")
	private LocalDate dueDate;

	@Field(at = 17, length = 30)
	private String customerReference;

	@Field(at = 47, length = 30)
	private String ourReference;

	public static InvoiceHeaderRow create() {
		return new InvoiceHeaderRow();
	}

	public String getLegalId() {
		return legalId;
	}

	public void setLegalId(String legalId) {
		this.legalId = legalId;
	}

	public InvoiceHeaderRow withLegalId(String legalId) {
		this.legalId = legalId;
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
		return Objects.hash(customerReference, dueDate, legalId, ourReference);
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
		return Objects.equals(customerReference, other.customerReference) && Objects.equals(dueDate, other.dueDate) && Objects.equals(legalId, other.legalId) && Objects.equals(ourReference, other.ourReference);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("InvoiceHeaderRow [legalId=").append(legalId).append(", dueDate=").append(dueDate).append(", customerReference=").append(customerReference).append(", ourReference=").append(ourReference).append("]");
		return builder.toString();
	}
}
