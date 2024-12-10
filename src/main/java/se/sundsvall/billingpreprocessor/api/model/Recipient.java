package se.sundsvall.billingpreprocessor.api.model;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.util.Objects;
import se.sundsvall.dept44.common.validators.annotation.ValidUuid;

@Schema(description = "Billing recipient model")
public class Recipient {

	@Schema(description = "Unique id for the person issuing the billing record. Mandatory for EXTERNAL billing record if legalId is null.", example = "f0882f1d-06bc-47fd-b017-1d8307f5ce95")
	@ValidUuid(nullable = true)
	private String partyId;

	@Schema(description = "LegalId for the organization issuing the billing record. Mandatory for EXTERNAL billing record if partyId is null.", example = "3456789123")
	private String legalId;

	@Schema(description = "Name of issuing organization of the billing record if the recipient is an organization", example = "Sesame Merc AB")
	private String organizationName;

	@Schema(description = "First name of the billing record recipient", example = "Alice")
	private String firstName;

	@Schema(description = "Last name of the billing record recipient", example = "Snuffleupagus")
	private String lastName;

	@Schema(description = "User id of the billing record recipient", example = "ALI22SNU")
	private String userId;

	@Schema(implementation = AddressDetails.class, requiredMode = REQUIRED)
	@Valid
	private AddressDetails addressDetails;

	public static Recipient create() {
		return new Recipient();
	}

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public Recipient withPartyId(String partyId) {
		this.partyId = partyId;
		return this;
	}

	public String getLegalId() {
		return legalId;
	}

	public void setLegalId(String legalId) {
		this.legalId = legalId;
	}

	public Recipient withLegalId(String legalId) {
		this.legalId = legalId;
		return this;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public Recipient withOrganizationName(String organizationName) {
		this.organizationName = organizationName;
		return this;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public Recipient withFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Recipient withLastName(String lastName) {
		this.lastName = lastName;
		return this;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Recipient withUserId(String userId) {
		this.userId = userId;
		return this;
	}

	public AddressDetails getAddressDetails() {
		return addressDetails;
	}

	public void setAddressDetails(AddressDetails addressDetails) {
		this.addressDetails = addressDetails;
	}

	public Recipient withAddressDetails(AddressDetails addressDetails) {
		this.addressDetails = addressDetails;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(addressDetails, organizationName, lastName, firstName, partyId, legalId, userId);
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
		final Recipient other = (Recipient) obj;
		return Objects.equals(addressDetails, other.addressDetails)
			&& Objects.equals(organizationName, other.organizationName)
			&& Objects.equals(firstName, other.firstName)
			&& Objects.equals(partyId, other.partyId)
			&& Objects.equals(legalId, other.legalId)
			&& Objects.equals(lastName, other.lastName)
			&& Objects.equals(userId, other.userId);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Recipient [partyId=").append(partyId)
			.append(", legalId=").append(legalId)
			.append(", organizationName=").append(organizationName)
			.append(", firstName=").append(firstName)
			.append(", lastName=").append(lastName)
			.append(", userId=").append(userId)
			.append(", addressDetails=").append(addressDetails).append("]");
		return builder.toString();
	}
}
