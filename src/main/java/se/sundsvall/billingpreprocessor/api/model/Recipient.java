package se.sundsvall.billingpreprocessor.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;

import se.sundsvall.dept44.common.validators.annotation.ValidOrganizationNumber;
import se.sundsvall.dept44.common.validators.annotation.ValidUuid;

import java.util.Objects;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "Billing recipient model")
public class Recipient {

	@Schema(description = "Unique id for the person issuing the billing record. Mandatory for EXTERNAL billing record if organizationNumber is null.", example = "f0882f1d-06bc-47fd-b017-1d8307f5ce95")
	@ValidUuid(nullable = true)
	private String partyId;
	@Schema(description = "OrganizationNumber for the organization issuing the billing record. Mandatory for EXTERNAL billing record if partyId is null.", example = "3456789123")
	@ValidOrganizationNumber(nullable = true)
	private String organizationNumber;

	@Schema(description = "Name of issuing organization of the billing record if the recipient is an organization", example = "Sesame Merc AB")
	private String organizationName;

	@Schema(description = "First name of recipient of the billing record", example = "Alice")
	private String firstName;

	@Schema(description = "Last name of recipient of the billing record", example = "Snuffleupagus")
	private String lastName;

	@Schema(description = "User id of recipient of the billing record", example = "ALI22SNU")
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

	public String getOrganizationNumber() {
		return organizationNumber;
	}

	public void setOrganizationNumber(String organizationNumber) {
		this.organizationNumber = organizationNumber;
	}

	public Recipient withOrganizationNumber(String organizationNumber) {
		this.organizationNumber = organizationNumber;
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
		return Objects.hash(addressDetails, organizationName, lastName, firstName, partyId, organizationNumber, userId);
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
		Recipient other = (Recipient) obj;
		return Objects.equals(addressDetails, other.addressDetails)
			&& Objects.equals(organizationName, other.organizationName)
			&& Objects.equals(firstName, other.firstName)
			&& Objects.equals(partyId, other.partyId)
			&& Objects.equals(organizationNumber, other.organizationNumber)
			&& Objects.equals(lastName, other.lastName)
			&& Objects.equals(userId, other.userId);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Recipient [partyId=").append(partyId)
			.append(", organizationNumber=").append(organizationNumber)
			.append(", organizationName=").append(organizationName)
			.append(", firstName=").append(firstName)
			.append(", lastName=").append(lastName)
			.append(", userId=").append(userId)
			.append(", addressDetails=").append(addressDetails).append("]");
		return builder.toString();
	}
}
