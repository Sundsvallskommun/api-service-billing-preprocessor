package se.sundsvall.billingpreprocessor.service.creator.definition.internal;

import java.util.Objects;

import org.beanio.annotation.Field;
import org.beanio.annotation.Fields;
import org.beanio.annotation.Record;

import se.sundsvall.billingpreprocessor.service.creator.config.InternalInvoiceFloatTypeHandler;

@Record
@Fields({
	@Field(at = 0, length = 2, name = "recordType", rid = true, literal = "R")
})
public class InvoiceRow {

	@Field(at = 2, length = 30)
	private String description;

	@Field(at = 54, length = 7, handlerName = InternalInvoiceFloatTypeHandler.NAME)
	private Float quantity;

	@Field(at = 61, length = 13, handlerName = InternalInvoiceFloatTypeHandler.NAME)
	private Float costPerUnit;

	@Field(at = 89, length = 15, handlerName = InternalInvoiceFloatTypeHandler.NAME)
	private Float totalAmount;

	public static InvoiceRow create() {
		return new InvoiceRow();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public InvoiceRow withDescription(String description) {
		this.description = description;
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

	@Override
	public int hashCode() {
		return Objects.hash(costPerUnit, description, quantity, totalAmount);
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
		return Objects.equals(costPerUnit, other.costPerUnit) && Objects.equals(description, other.description) && Objects.equals(quantity, other.quantity) && Objects.equals(totalAmount, other.totalAmount);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("InvoiceRow [description=").append(description).append(", quantity=").append(quantity).append(", costPerUnit=").append(costPerUnit).append(", totalAmount=").append(totalAmount).append("]");
		return builder.toString();
	}
}
