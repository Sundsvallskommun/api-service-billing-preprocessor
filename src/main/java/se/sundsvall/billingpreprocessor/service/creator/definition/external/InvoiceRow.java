package se.sundsvall.billingpreprocessor.service.creator.definition.external;

import java.math.BigDecimal;
import java.util.Objects;
import org.beanio.annotation.Field;
import org.beanio.annotation.Fields;
import org.beanio.annotation.Record;
import se.sundsvall.billingpreprocessor.service.creator.config.ExternalInvoiceBigDecimalTypeHandler;

@Record
@Fields({
	@Field(at = 0, length = 1, name = "recordType", rid = true, literal = "R")
})
public class InvoiceRow {

	@Field(at = 1, length = 10)
	private String legalId;

	@Field(at = 11, length = 30)
	private String text;

	@Field(at = 41, length = 8, handlerName = ExternalInvoiceBigDecimalTypeHandler.NAME)
	private BigDecimal quantity;

	@Field(at = 49, length = 9, handlerName = ExternalInvoiceBigDecimalTypeHandler.NAME)
	private BigDecimal costPerUnit;

	@Field(at = 58, length = 15, handlerName = ExternalInvoiceBigDecimalTypeHandler.NAME)
	private BigDecimal totalAmount;

	@Field(at = 73, length = 2)
	private String vatCode;

	public static InvoiceRow create() {
		return new InvoiceRow();
	}

	public String getLegalId() {
		return legalId;
	}

	public void setLegalId(String legalId) {
		this.legalId = legalId;
	}

	public InvoiceRow withLegalId(String legalId) {
		this.legalId = legalId;
		return this;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public InvoiceRow withText(String text) {
		this.text = text;
		return this;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public InvoiceRow withQuantity(BigDecimal quantity) {
		this.quantity = quantity;
		return this;
	}

	public BigDecimal getCostPerUnit() {
		return costPerUnit;
	}

	public void setCostPerUnit(BigDecimal costPerUnit) {
		this.costPerUnit = costPerUnit;
	}

	public InvoiceRow withCostPerUnit(BigDecimal costPerUnit) {
		this.costPerUnit = costPerUnit;
		return this;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public InvoiceRow withTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
		return this;
	}

	public String getVatCode() {
		return vatCode;
	}

	public void setVatCode(String vatCode) {
		this.vatCode = vatCode;
	}

	public InvoiceRow withVatCode(String vatCode) {
		this.vatCode = vatCode;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(costPerUnit, legalId, quantity, text, totalAmount, vatCode);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof final InvoiceRow other)) {
			return false;
		}
		return Objects.equals(costPerUnit, other.costPerUnit) && Objects.equals(legalId, other.legalId) && Objects.equals(quantity, other.quantity) && Objects.equals(text, other.text) && Objects.equals(totalAmount, other.totalAmount) && Objects
			.equals(vatCode, other.vatCode);
	}

	@Override
	public String toString() {
		final var builder = new StringBuilder();
		builder.append("InvoiceRow [legalId=").append(legalId).append(", text=").append(text).append(", quantity=").append(quantity).append(", costPerUnit=").append(costPerUnit).append(", totalAmount=").append(totalAmount).append(", vatCode=")
			.append(vatCode).append("]");
		return builder.toString();
	}
}
