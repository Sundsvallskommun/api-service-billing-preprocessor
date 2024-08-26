package se.sundsvall.billingpreprocessor.service.creator.definition.internal;

import java.util.Objects;

import org.beanio.annotation.Field;
import org.beanio.annotation.Fields;
import org.beanio.annotation.Record;

import org.beanio.builder.Align;
import se.sundsvall.billingpreprocessor.service.creator.config.InternalInvoiceFloatTypeHandler;

@Record
@Fields({
	@Field(at = 0, length = 2, name = "recordType", rid = true, literal = "K")
})
public class InvoiceAccountingRow {

	@Field(at = 2, length = 8)
	private String costCenter;

	@Field(at = 12, length = 6)
	private String subAccount;

	@Field(at = 22, length = 6)
	private String department;

	@Field(at = 32, length = 4)
	private String activity;

	@Field(at = 42, length = 5)
	private String project;

	@Field(at = 52, length = 7)
	private String object;

	@Field(at = 62, length = 3)
	private String counterpart;

	@Field(at = 103, length = 15, handlerName = InternalInvoiceFloatTypeHandler.NAME, align = Align.RIGHT)
	private Float totalAmount;

	@Field(at = 119, length = 13)
	private String accuralKey;

	public static InvoiceAccountingRow create() {
		return new InvoiceAccountingRow();
	}

	public String getCostCenter() {
		return costCenter;
	}

	public void setCostCenter(String costCenter) {
		this.costCenter = costCenter;
	}

	public InvoiceAccountingRow withCostCenter(String costCenter) {
		this.costCenter = costCenter;
		return this;
	}

	public String getSubAccount() {
		return subAccount;
	}

	public void setSubAccount(String subAccount) {
		this.subAccount = subAccount;
	}

	public InvoiceAccountingRow withSubAccount(String subAccount) {
		this.subAccount = subAccount;
		return this;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public InvoiceAccountingRow withDepartment(String department) {
		this.department = department;
		return this;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public InvoiceAccountingRow withActivity(String activity) {
		this.activity = activity;
		return this;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public InvoiceAccountingRow withProject(String project) {
		this.project = project;
		return this;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public InvoiceAccountingRow withObject(String object) {
		this.object = object;
		return this;
	}

	public String getCounterpart() {
		return counterpart;
	}

	public void setCounterpart(String counterpart) {
		this.counterpart = counterpart;
	}

	public InvoiceAccountingRow withCounterpart(String counterpart) {
		this.counterpart = counterpart;
		return this;
	}

	public Float getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Float totalAmount) {
		this.totalAmount = totalAmount;
	}

	public InvoiceAccountingRow withTotalAmount(Float totalAmount) {
		this.totalAmount = totalAmount;
		return this;
	}

	public String getAccuralKey() {
		return accuralKey;
	}

	public void setAccuralKey(String accuralKey) {
		this.accuralKey = accuralKey;
	}

	public InvoiceAccountingRow withAccuralKey(String accuralKey) {
		this.accuralKey = accuralKey;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(accuralKey, activity, costCenter, counterpart, department, object, project, subAccount, totalAmount);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof InvoiceAccountingRow)) {
			return false;
		}
		InvoiceAccountingRow other = (InvoiceAccountingRow) obj;
		return Objects.equals(accuralKey, other.accuralKey) && Objects.equals(activity, other.activity) && Objects.equals(costCenter, other.costCenter) && Objects.equals(counterpart, other.counterpart) && Objects.equals(department, other.department)
			&& Objects.equals(object, other.object) && Objects.equals(project, other.project) && Objects.equals(subAccount, other.subAccount) && Objects.equals(totalAmount, other.totalAmount);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("InvoiceAccountingRow [costCenter=").append(costCenter).append(", subAccount=").append(subAccount).append(", department=").append(department).append(", activity=").append(activity).append(", project=").append(project).append(
			", object=").append(object).append(", counterpart=").append(counterpart).append(", totalAmount=").append(totalAmount).append(", accuralKey=").append(accuralKey).append("]");
		return builder.toString();
	}
}
