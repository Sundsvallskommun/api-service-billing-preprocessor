package se.sundsvall.billingpreprocessor.service.creator.definition.internal;

import org.beanio.annotation.Field;
import org.beanio.annotation.Fields;
import org.beanio.annotation.Record;

@Record(minOccurs = 1, maxOccurs = 1)
@Fields({
	@Field(at = 0, length = 8, name = "recordType", rid = true, literal = "01300SKC")
})
public class FileHeaderRow {
	private FileHeaderRow() {}

	public static FileHeaderRow create() {
		return new FileHeaderRow();
	}
}
