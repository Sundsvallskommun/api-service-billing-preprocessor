package se.sundsvall.billingpreprocessor.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import se.sundsvall.dept44.common.validators.annotation.ValidUuid;

import java.util.Objects;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "Billing issuer model")
public class Issuer {

	@Schema(description = "Unique id for the person issuing the billing record. Mandatory for EXTERNAL billing record.", example = "f0882f1d-06bc-47fd-b017-1d8307f5ce95")
	@ValidUuid(nullable = true)
	private String partyId;

	@Schema(description = "Name of issuing organization of the billing record if the issuer is an organization", example = "Sesame Merc AB")
	private String organizationName;

	@Schema(description = "First name of issuer of the billing record", example = "Alice")
	private String firstName;

	@Schema(description = "Last name of issuer of the billing record", example = "Snuffleupagus")
	private String lastName;

	@Schema(description = "User id of issuer of the billing record", example = "ALI22SNU")
	private String userId;

	@Schema(implementation = AddressDetails.class, requiredMode = REQUIRED)
	@Valid
	private AddressDetails addressDetails;

	public static Issuer create() {
		return new Issuer();
	}

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public Issuer withPartyId(String partyId) {
		this.partyId = partyId;
		return this;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public Issuer withOrganizationName(String organizationName) {
		this.organizationName = organizationName;
		return this;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public Issuer withFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Issuer withLastName(String lastName) {
		this.lastName = lastName;
		return this;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Issuer withUserId(String userId) {
		this.userId = userId;
		return this;
	}

	public AddressDetails getAddressDetails() {
		return addressDetails;
	}

	public void setAddressDetails(AddressDetails addressDetails) {
		this.addressDetails = addressDetails;
	}

	public Issuer withAddressDetails(AddressDetails addressDetails) {
		this.addressDetails = addressDetails;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(addressDetails, organizationName, lastName, firstName, partyId, userId);
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
		Issuer other = (Issuer) obj;
		return Objects.equals(addressDetails, other.addressDetails) && Objects.equals(organizationName, other.organizationName) && Objects.equals(firstName, other.firstName) && Objects.equals(partyId, other.partyId) && Objects.equals(lastName,
			other.lastName) && Objects.equals(userId, other.userId);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Issuer [partyId=").append(partyId)
			.append(", organizationName=").append(organizationName)
			.append(", firstName=").append(firstName)
			.append(", lastName=").append(lastName)
			.append(", userId=").append(userId)
			.append(", addressDetails=").append(addressDetails).append("]");
		return builder.toString();
	}
}
