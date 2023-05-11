package se.sundsvall.billingpreprocessor.api.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Objects;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Schema(description = "Invoice row model")
public class InvoiceRow {

	@ArraySchema(schema = @Schema(description = "Description of row", example = "Row with description"))
	private List<@Size(min = 1, max = 30) String> descriptions;

	@ArraySchema(schema = @Schema(description = "Detailed description of row", example = "Row with detailed description"))
	private List<@NotEmpty String> detailedDescriptions;

	@Schema(description = "Total sum of invoice row", example = "1399.95", accessMode = READ_ONLY)
	@Null
	private Float totalAmount;

	@Schema(description = "VAT code for invoice row", example = "25")
	@Pattern(regexp = "00|06|12|25", message = "must be one of 00, 06, 12 or 25")
	private String vatCode;

	@Schema(description = "Cost per unit", example = "155.55")
	private Float costPerUnit;

	@Schema(description = "Total amount of units", example = "9")
	private Integer quantity;

	@Schema(implementation = AccountInformation.class)
	@Valid
	private AccountInformation accountInformation;

	public static InvoiceRow create() {
		return new InvoiceRow();
	}

	public List<String> getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(List<String> descriptions) {
		this.descriptions = descriptions;
	}

	public InvoiceRow withDescriptions(List<String> descriptions) {
		this.descriptions = descriptions;
		return this;
	}

	public List<String> getDetailedDescriptions() {
		return detailedDescriptions;
	}

	public void setDetailedDescriptions(List<String> detailedDescriptions) {
		this.detailedDescriptions = detailedDescriptions;
	}

	public InvoiceRow withDetailedDescriptions(List<String> detailedDescriptions) {
		this.detailedDescriptions = detailedDescriptions;
		return this;
	}

	public Float getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Float totalAmount) {
		this.totalAmount = totalAmount;
	}

	public InvoiceRow withTotalAmount(Float totalAmount) {
		this.totalAmount = totalAmount;
		return this;
	}

	public String getVatCode() {
		return vatCode;
	}

	public void setVatCode(String vatCode) {
		this.vatCode = vatCode;
	}

	public InvoiceRow withVatCode(String vatCode) {
		this.vatCode = vatCode;
		return this;
	}

	public Float getCostPerUnit() {
		return costPerUnit;
	}

	public void setCostPerUnit(Float costPerUnit) {
		this.costPerUnit = costPerUnit;
	}

	public InvoiceRow withCostPerUnit(Float costPerUnit) {
		this.costPerUnit = costPerUnit;
		return this;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public InvoiceRow withQuantity(Integer quantity) {
		this.quantity = quantity;
		return this;
	}

	public AccountInformation getAccountInformation() {
		return accountInformation;
	}

	public void setAccountInformation(AccountInformation accountInformation) {
		this.accountInformation = accountInformation;
	}

	public InvoiceRow withAccountInformation(AccountInformation accountInformation) {
		this.accountInformation = accountInformation;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(accountInformation, costPerUnit, descriptions, detailedDescriptions, quantity, totalAmount, vatCode);
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
		InvoiceRow other = (InvoiceRow) obj;
		return Objects.equals(accountInformation, other.accountInformation) && Objects.equals(costPerUnit, other.costPerUnit) && Objects.equals(descriptions, other.descriptions) && Objects.equals(detailedDescriptions, other.detailedDescriptions)
			&& Objects.equals(quantity, other.quantity) && Objects.equals(totalAmount, other.totalAmount) && Objects.equals(vatCode, other.vatCode);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("InvoiceRow [descriptions=").append(descriptions)
			.append(", detailedDescriptions=").append(detailedDescriptions)
			.append(", totalAmount=").append(totalAmount)
			.append(", vatCode=").append(vatCode)
			.append(", costPerUnit=").append(costPerUnit)
			.append(", quantity=").append(quantity)
			.append(", accountInformation=").append(accountInformation).append("]");
		return builder.toString();
	}
}
