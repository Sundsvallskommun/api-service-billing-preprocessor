package se.sundsvall.billingpreprocessor.service.creator.definition.external;

import java.util.Objects;

import org.beanio.annotation.Field;
import org.beanio.annotation.Fields;
import org.beanio.annotation.Record;

@Record
@Fields({
	@Field(at = 0, length = 1, name = "recordType", rid = true, literal = "U")
})
public class InvoiceDescriptionRow {

	@Field(at = 1, length = 10)
	private String legalId;

	@Field(at = 11, length = 51)
	private String description;

	public static InvoiceDescriptionRow create() {
		return new InvoiceDescriptionRow();
	}

	public String getLegalId() {
		return legalId;
	}

	public void setLegalId(String legalId) {
		this.legalId = legalId;
	}

	public InvoiceDescriptionRow withLegalId(String legalId) {
		setLegalId(legalId);
		return this;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public InvoiceDescriptionRow withDescription(String description) {
		setDescription(description);
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(description, legalId);
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
		return Objects.equals(description, other.description) && Objects.equals(legalId, other.legalId);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("InvoiceDescriptionRow [legalId=").append(legalId).append(", description=").append(description).append("]");
		return builder.toString();
	}
}
