package se.sundsvall.billingpreprocessor.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AccountInformationEmbeddable implements Serializable {
	private static final long serialVersionUID = 7640213197247528711L;

	@Column(name = "cost_center")
	private String costCenter;

	@Column(name = "subaccount")
	private String subaccount;

	@Column(name = "department")
	private String department;

	@Column(name = "accural_key")
	private String accuralKey;

	@Column(name = "activity")
	private String activity;

	@Column(name = "article")
	private String article;

	@Column(name = "project")
	private String project;

	@Column(name = "counter_part")
	private String counterpart;

	@Column(name = "amount")
	private Float amount;

	public static AccountInformationEmbeddable create() {
		return new AccountInformationEmbeddable();
	}

	public String getCostCenter() {
		return costCenter;
	}

	public void setCostCenter(String costCenter) {
		this.costCenter = costCenter;
	}

	public AccountInformationEmbeddable withCostCenter(String costCenter) {
		this.costCenter = costCenter;
		return this;
	}

	public String getSubaccount() {
		return subaccount;
	}

	public void setSubaccount(String subaccount) {
		this.subaccount = subaccount;
	}

	public AccountInformationEmbeddable withSubaccount(String subaccount) {
		this.subaccount = subaccount;
		return this;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public AccountInformationEmbeddable withDepartment(String department) {
		this.department = department;
		return this;
	}

	public String getAccuralKey() {
		return accuralKey;
	}

	public void setAccuralKey(String accuralKey) {
		this.accuralKey = accuralKey;
	}

	public AccountInformationEmbeddable withAccuralKey(String accuralKey) {
		this.accuralKey = accuralKey;
		return this;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public AccountInformationEmbeddable withActivity(String activity) {
		this.activity = activity;
		return this;
	}

	public String getArticle() {
		return article;
	}

	public void setArticle(String article) {
		this.article = article;
	}

	public AccountInformationEmbeddable withArticle(String article) {
		this.article = article;
		return this;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public AccountInformationEmbeddable withProject(String project) {
		this.project = project;
		return this;
	}

	public String getCounterpart() {
		return counterpart;
	}

	public void setCounterpart(String counterpart) {
		this.counterpart = counterpart;
	}

	public AccountInformationEmbeddable withCounterpart(String counterpart) {
		this.counterpart = counterpart;
		return this;
	}

	public Float getAmount() {
		return amount;
	}

	public void setAmount(Float amount) {
		this.amount = amount;
	}

	public AccountInformationEmbeddable withAmount(Float amount) {
		this.amount = amount;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(accuralKey, activity, amount, article, costCenter, counterpart, department, project, subaccount);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (!(obj instanceof final AccountInformationEmbeddable other)) { return false; }
		return Objects.equals(accuralKey, other.accuralKey) && Objects.equals(activity, other.activity) && Objects.equals(amount, other.amount) && Objects.equals(article, other.article) && Objects.equals(costCenter, other.costCenter) && Objects.equals(
			counterpart, other.counterpart) && Objects.equals(department, other.department) && Objects.equals(project, other.project) && Objects.equals(subaccount, other.subaccount);
	}

	@Override
	public String toString() {
		final var builder = new StringBuilder();
		builder.append("AccountInformationEmbeddable [costCenter=").append(costCenter).append(", subaccount=").append(subaccount).append(", department=").append(department).append(", accuralKey=").append(accuralKey).append(", activity=").append(activity)
			.append(", article=").append(article).append(", project=").append(project).append(", counterpart=").append(counterpart).append(", amount=").append(amount).append("]");
		return builder.toString();
	}
}
