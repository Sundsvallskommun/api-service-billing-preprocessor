package se.sundsvall.billingpreprocessor.service.creator.definition.internal;

import java.util.Objects;
import org.beanio.annotation.Field;
import org.beanio.annotation.Fields;
import org.beanio.annotation.Record;

@Record
@Fields({
	@Field(at = 0, length = 2, name = "recordType", rid = true, literal = "U")
})
public class InvoiceRowDescriptionRow {

	@Field(at = 2, length = 100)
	private String description;

	public static InvoiceRowDescriptionRow create() {
		return new InvoiceRowDescriptionRow();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public InvoiceRowDescriptionRow withDescription(String description) {
		this.description = description;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(description);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (!(obj instanceof final InvoiceRowDescriptionRow other)) { return false; }
		return Objects.equals(description, other.description);
	}

	@Override
	public String toString() {
		final var builder = new StringBuilder();
		builder.append("InvoiceRowDescriptionRow [description=").append(description).append("]");
		return builder.toString();
	}

}
