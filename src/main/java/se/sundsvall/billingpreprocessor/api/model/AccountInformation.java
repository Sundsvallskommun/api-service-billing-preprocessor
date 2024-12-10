package se.sundsvall.billingpreprocessor.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

@Schema(description = "Account information model")
public class AccountInformation {

	@Schema(description = "Cost center", example = "15800100")
	private String costCenter;

	@Schema(description = "Subaccount", example = "936300")
	private String subaccount;

	@Schema(description = "Department", example = "920360")
	private String department;

	@Schema(description = "Accural key", example = "5647")
	private String accuralKey;

	@Schema(description = "Activity", example = "5756")
	private String activity;

	@Schema(description = "Article", example = "Electric bicycle")
	private String article;

	@Schema(description = "Project", example = "11041")
	private String project;

	@Schema(description = "Counterpart", example = "11830000")
	private String counterpart;

	public static AccountInformation create() {
		return new AccountInformation();
	}

	public String getCostCenter() {
		return costCenter;
	}

	public void setCostCenter(String costCenter) {
		this.costCenter = costCenter;
	}

	public AccountInformation withCostCenter(String costCenter) {
		this.costCenter = costCenter;
		return this;
	}

	public String getSubaccount() {
		return subaccount;
	}

	public void setSubaccount(String subaccount) {
		this.subaccount = subaccount;
	}

	public AccountInformation withSubaccount(String subaccount) {
		this.subaccount = subaccount;
		return this;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public AccountInformation withDepartment(String department) {
		this.department = department;
		return this;
	}

	public String getAccuralKey() {
		return accuralKey;
	}

	public void setAccuralKey(String accuralKey) {
		this.accuralKey = accuralKey;
	}

	public AccountInformation withAccuralKey(String accuralKey) {
		this.accuralKey = accuralKey;
		return this;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public AccountInformation withActivity(String activity) {
		this.activity = activity;
		return this;
	}

	public String getArticle() {
		return article;
	}

	public void setArticle(String article) {
		this.article = article;
	}

	public AccountInformation withArticle(String article) {
		this.article = article;
		return this;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public AccountInformation withProject(String project) {
		this.project = project;
		return this;
	}

	public String getCounterpart() {
		return counterpart;
	}

	public void setCounterpart(String counterpart) {
		this.counterpart = counterpart;
	}

	public AccountInformation withCounterpart(String counterpart) {
		this.counterpart = counterpart;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(accuralKey, activity, costCenter, counterpart, department, article, project, subaccount);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		AccountInformation other = (AccountInformation) obj;
		return Objects.equals(accuralKey, other.accuralKey) && Objects.equals(activity, other.activity) && Objects.equals(costCenter, other.costCenter) && Objects.equals(counterpart, other.counterpart) && Objects.equals(department, other.department)
			&& Objects.equals(article, other.article) && Objects.equals(project, other.project) && Objects.equals(subaccount, other.subaccount);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AccountInformation [costCenter=").append(costCenter)
			.append(", subaccount=").append(subaccount)
			.append(", department=").append(department)
			.append(", activity=").append(activity)
			.append(", accuralKey=").append(accuralKey)
			.append(", article=").append(article)
			.append(", project=").append(project)
			.append(", counterpart=").append(counterpart).append("]");
		return builder.toString();
	}
}
