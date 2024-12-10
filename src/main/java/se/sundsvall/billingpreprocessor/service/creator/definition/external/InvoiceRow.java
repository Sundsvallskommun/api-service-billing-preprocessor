package se.sundsvall.billingpreprocessor.service.creator.definition.external;

import java.util.Objects;
import org.beanio.annotation.Field;
import org.beanio.annotation.Fields;
import org.beanio.annotation.Record;
import se.sundsvall.billingpreprocessor.service.creator.config.ExternalInvoiceFloatTypeHandler;

@Record
@Fields({
	@Field(at = 0, length = 1, name = "recordType", rid = true, literal = "R")
})
public class InvoiceRow {

	@Field(at = 1, length = 10)
	private String legalId;

	@Field(at = 11, length = 30)
	private String text;

	@Field(at = 41, length = 8, handlerName = ExternalInvoiceFloatTypeHandler.NAME)
	private Float quantity;

	@Field(at = 49, length = 9, handlerName = ExternalInvoiceFloatTypeHandler.NAME)
	private Float costPerUnit;

	@Field(at = 58, length = 15, handlerName = ExternalInvoiceFloatTypeHandler.NAME)
	private Float totalAmount;

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

	public Float getQuantity() {
		return quantity;
	}

	public void setQuantity(Float quantity) {
		this.quantity = quantity;
	}

	public InvoiceRow withQuantity(Float quantity) {
		this.quantity = quantity;
		return this;
	}

	public Float getCostPerUnit() {
		return costPerUnit;
	}

	public void setCostPerUnit(Float costPerUnit) {
		this.costPerUnit = costPerUnit;
	}

	public InvoiceRow withCostPerUnit(Float costPerUnit) {
		this.costPerUnit = costPerUnit;
		return this;
	}

	public Float getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Float totalAmount) {
		this.totalAmount = totalAmount;
	}

	public InvoiceRow withTotalAmount(Float totalAmount) {
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
		if (!(obj instanceof InvoiceRow)) {
			return false;
		}
		InvoiceRow other = (InvoiceRow) obj;
		return Objects.equals(costPerUnit, other.costPerUnit) && Objects.equals(legalId, other.legalId) && Objects.equals(quantity, other.quantity) && Objects.equals(text, other.text) && Objects.equals(totalAmount, other.totalAmount) && Objects
			.equals(vatCode, other.vatCode);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("InvoiceRow [legalId=").append(legalId).append(", text=").append(text).append(", quantity=").append(quantity).append(", costPerUnit=").append(costPerUnit).append(", totalAmount=").append(totalAmount).append(", vatCode=")
			.append(vatCode).append("]");
		return builder.toString();
	}
}
