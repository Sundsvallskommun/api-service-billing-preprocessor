package se.sundsvall.billingpreprocessor.service.creator.definition.internal;

import java.math.BigDecimal;
import java.util.Objects;
import org.beanio.annotation.Field;
import org.beanio.annotation.Fields;
import org.beanio.annotation.Record;
import se.sundsvall.billingpreprocessor.service.creator.config.InternalInvoiceBigDecimalTypeHandler;

@Record(minOccurs = 1, maxOccurs = 1)
@Fields({
	@Field(at = 0, length = 2, name = "recordType", rid = true, literal = "T")
})
public class FileFooterRow {

	@Field(at = 2, length = 15, handlerName = InternalInvoiceBigDecimalTypeHandler.NAME)
	private BigDecimal totalAmount;

	private FileFooterRow() {}

	public static FileFooterRow create() {
		return new FileFooterRow();
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public FileFooterRow withTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(totalAmount);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof final FileFooterRow other)) {
			return false;
		}
		return Objects.equals(totalAmount, other.totalAmount);
	}

	@Override
	public String toString() {
		final var builder = new StringBuilder();
		builder.append("FileFooterRow [totalAmount=").append(totalAmount).append("]");
		return builder.toString();
	}
}
