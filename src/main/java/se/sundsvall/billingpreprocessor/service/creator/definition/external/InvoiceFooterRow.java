package se.sundsvall.billingpreprocessor.service.creator.definition.external;

import static org.beanio.builder.Align.RIGHT;

import java.util.Objects;
import org.beanio.annotation.Field;
import org.beanio.annotation.Fields;
import org.beanio.annotation.Record;
import se.sundsvall.billingpreprocessor.service.creator.config.ExternalInvoiceFloatTypeHandler;

@Record
@Fields({
	@Field(at = 0, length = 1, name = "recordType", rid = true, literal = "T")
})
public class InvoiceFooterRow {

	@Field(at = 1, length = 15, handlerName = ExternalInvoiceFloatTypeHandler.NAME, format = "+00000000000000;-00000000000000", align = RIGHT)
	private Float totalAmount;

	public static InvoiceFooterRow create() {
		return new InvoiceFooterRow();
	}

	public Float getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Float totalAmount) {
		this.totalAmount = totalAmount;
	}

	public InvoiceFooterRow withTotalAmount(Float totalAmount) {
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
		if (!(obj instanceof InvoiceFooterRow)) {
			return false;
		}
		InvoiceFooterRow other = (InvoiceFooterRow) obj;
		return Objects.equals(totalAmount, other.totalAmount);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("InvoiceFooterRow [totalAmount=").append(totalAmount).append("]");
		return builder.toString();
	}
}
