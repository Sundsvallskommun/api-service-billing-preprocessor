package se.sundsvall.billingpreprocessor.service.creator.definition.external;

import java.time.LocalDate;
import java.util.Objects;
import org.beanio.annotation.Field;
import org.beanio.annotation.Fields;
import org.beanio.annotation.Record;

@Record(minOccurs = 1, maxOccurs = 1)
@Fields({
	@Field(at = 0, length = 1, name = "recordType", rid = true, literal = "!")
})
public class FileHeaderRow {

	@Field(at = 1, length = 20)
	private String generatingSystem;

	@Field(at = 21, length = 6, format = "yyMMdd")
	private LocalDate createdDate;

	@Field(at = 28, length = 126)
	private String invoiceType;

	public static FileHeaderRow create() {
		return new FileHeaderRow();
	}

	public String getGeneratingSystem() {
		return generatingSystem;
	}

	public void setGeneratingSystem(String generatingSystem) {
		this.generatingSystem = generatingSystem;
	}

	public FileHeaderRow withGeneratingSystem(String generatingSystem) {
		this.generatingSystem = generatingSystem;
		return this;
	}

	public LocalDate getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDate createdDate) {
		this.createdDate = createdDate;
	}

	public FileHeaderRow withCreatedDate(LocalDate createdDate) {
		this.createdDate = createdDate;
		return this;
	}

	public String getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}

	public FileHeaderRow withInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(createdDate, generatingSystem, invoiceType);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof FileHeaderRow)) {
			return false;
		}
		FileHeaderRow other = (FileHeaderRow) obj;
		return Objects.equals(createdDate, other.createdDate) && Objects.equals(generatingSystem, other.generatingSystem) && Objects.equals(invoiceType, other.invoiceType);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FileHeaderRow [generatingSystem=").append(generatingSystem).append(", createdDate=").append(createdDate).append(", invoiceType=").append(invoiceType).append("]");
		return builder.toString();
	}
}
