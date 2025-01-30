package se.sundsvall.billingpreprocessor.service.creator.definition.internal;

import java.math.BigDecimal;
import java.util.Objects;
import org.beanio.annotation.Field;
import org.beanio.annotation.Fields;
import org.beanio.annotation.Record;
import se.sundsvall.billingpreprocessor.service.creator.config.InternalInvoiceBigDecimalTypeHandler;

@Record
@Fields({
	@Field(at = 0, length = 2, name = "recordType", rid = true, literal = "T")
})
public class InvoiceFooterRow {

	@Field(at = 2, length = 15, handlerName = InternalInvoiceBigDecimalTypeHandler.NAME)
	private BigDecimal totalAmount;

	public static InvoiceFooterRow create() {
		return new InvoiceFooterRow();
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public InvoiceFooterRow withTotalAmount(BigDecimal totalAmount) {
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
		if (!(obj instanceof final InvoiceFooterRow other)) {
			return false;
		}
		return Objects.equals(totalAmount, other.totalAmount);
	}

	@Override
	public String toString() {
		final var builder = new StringBuilder();
		builder.append("InvoiceFooterRow [totalAmount=").append(totalAmount).append("]");
		return builder.toString();
	}
}
