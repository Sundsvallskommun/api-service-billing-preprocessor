package se.sundsvall.billingpreprocessor.api.model;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Null;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Schema(description = "Invoice model")
public class Invoice {

	@Schema(description = "Customer number in Raindance", example = "16", requiredMode = REQUIRED)
	@NotBlank
	private String customerId;

	@Schema(description = "Description of the invoice", example = "Errand number: 2113-01784", requiredMode = REQUIRED)
	@NotBlank
	private String description;

	@Schema(description = "Our reference", example = "Harvey Kneeslapper")
	private String ourReference;

	@Schema(description = "Customer reference", example = "Alice Snuffleupagus", requiredMode = REQUIRED)
	@NotBlank
	private String customerReference;

	@Schema(description = "Date for the invoice", example = "2022-12-24")
	@DateTimeFormat(iso = ISO.DATE)
	private LocalDate date;

	@Schema(description = "Due date for the invoice", example = "2022-12-24")
	@DateTimeFormat(iso = ISO.DATE)
	private LocalDate dueDate;

	@Schema(description = "Total sum of all invoice rows", example = "1399.95", accessMode = READ_ONLY)
	@Null
	private BigDecimal totalAmount;

	@ArraySchema(schema = @Schema(implementation = InvoiceRow.class, requiredMode = REQUIRED))
	@NotEmpty
	@Valid
	private List<InvoiceRow> invoiceRows;

	public static Invoice create() {
		return new Invoice();
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public Invoice withCustomerId(String customerId) {
		this.customerId = customerId;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Invoice withDescription(String description) {
		this.description = description;
		return this;
	}

	public String getOurReference() {
		return ourReference;
	}

	public void setOurReference(String ourReference) {
		this.ourReference = ourReference;
	}

	public Invoice withOurReference(String ourReference) {
		this.ourReference = ourReference;
		return this;
	}

	public String getCustomerReference() {
		return customerReference;
	}

	public void setCustomerReference(String customerReference) {
		this.customerReference = customerReference;
	}

	public Invoice withCustomerReference(String customerReference) {
		this.customerReference = customerReference;
		return this;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public Invoice withDate(LocalDate date) {
		this.date = date;
		return this;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}

	public Invoice withDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
		return this;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Invoice withTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
		return this;
	}

	public List<InvoiceRow> getInvoiceRows() {
		return invoiceRows;
	}

	public void setInvoiceRows(List<InvoiceRow> invoiceRows) {
		this.invoiceRows = invoiceRows;
	}

	public Invoice withInvoiceRows(List<InvoiceRow> invoiceRows) {
		this.invoiceRows = invoiceRows;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(customerId, customerReference, description, date, dueDate, invoiceRows, ourReference, totalAmount);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		final var other = (Invoice) obj;
		return Objects.equals(customerId, other.customerId) && Objects.equals(customerReference, other.customerReference) && Objects.equals(description, other.description) && Objects.equals(invoiceRows, other.invoiceRows) && Objects.equals(
			ourReference, other.ourReference) && Objects.equals(date, other.date) && Objects.equals(dueDate, other.dueDate) && Objects.equals(totalAmount, other.totalAmount);
	}

	@Override
	public String toString() {
		final var builder = new StringBuilder();
		builder.append("Invoice [customerId=").append(customerId)
			.append(", description=").append(description)
			.append(", ourReference=").append(ourReference)
			.append(", customerReference=").append(customerReference)
			.append(", date=").append(date)
			.append(", dueDate=").append(dueDate)
			.append(", totalAmount=").append(totalAmount)
			.append(", invoiceRows=").append(invoiceRows).append("]");
		return builder.toString();
	}
}
