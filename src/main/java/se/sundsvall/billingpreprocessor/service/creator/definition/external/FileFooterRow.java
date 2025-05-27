package se.sundsvall.billingpreprocessor.service.creator.definition.external;

import static org.beanio.builder.Align.RIGHT;

import java.math.BigDecimal;
import java.util.Objects;
import org.beanio.annotation.Field;
import org.beanio.annotation.Fields;
import org.beanio.annotation.Record;
import se.sundsvall.billingpreprocessor.service.creator.config.ExternalInvoiceBigDecimalTypeHandler;

@Record(minOccurs = 1, maxOccurs = 1)
@Fields({
	@Field(at = 0, length = 1, name = "recordType", rid = true, literal = "T")
})
public class FileFooterRow {

	@Field(at = 1, length = 15, handlerName = ExternalInvoiceBigDecimalTypeHandler.NAME, format = "+00000000000000;-00000000000000", align = RIGHT)
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
