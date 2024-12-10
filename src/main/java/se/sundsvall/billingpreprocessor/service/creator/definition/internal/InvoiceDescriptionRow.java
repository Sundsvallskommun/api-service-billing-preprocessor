package se.sundsvall.billingpreprocessor.service.creator.definition.internal;

import java.util.Objects;
import org.beanio.annotation.Field;
import org.beanio.annotation.Fields;
import org.beanio.annotation.Record;

@Record
@Fields({
	@Field(at = 0, length = 2, name = "recordType", rid = true, literal = "R")
})
public class InvoiceDescriptionRow {

	@Field(at = 2, length = 100)
	private String description;

	public static InvoiceDescriptionRow create() {
		return new InvoiceDescriptionRow();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public InvoiceDescriptionRow withDescription(String description) {
		this.description = description;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(description);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof InvoiceDescriptionRow)) {
			return false;
		}
		InvoiceDescriptionRow other = (InvoiceDescriptionRow) obj;
		return Objects.equals(description, other.description);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("InvoiceDescriptionRow [description=").append(description).append("]");
		return builder.toString();
	}
}
