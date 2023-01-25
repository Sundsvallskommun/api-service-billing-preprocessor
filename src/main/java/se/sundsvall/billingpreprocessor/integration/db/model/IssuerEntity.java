package se.sundsvall.billingpreprocessor.integration.db.model;

import static javax.persistence.FetchType.LAZY;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "issuer")
public class IssuerEntity implements Serializable {
	private static final long serialVersionUID = -5163409193887622123L;

	@Id
	@Column(name = "id")
	private String id;

	@OneToOne(fetch = LAZY)
	@MapsId
	@JoinColumn(name = "id", foreignKey = @ForeignKey(name = "fk_billing_record_id_issuer"))
	private BillingRecordEntity billingRecord;

	@Column(name = "party_id", nullable = false)
	private String partyId;

	@Column(name = "organization_name")
	private String organizationName;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "user_id")
	private String userId;

	@Embedded
	private AddressDetailsEmbeddable addressDetails;

	public static IssuerEntity create() {
		return new IssuerEntity();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public IssuerEntity withId(String id) {
		this.id = id;
		return this;
	}

	public BillingRecordEntity getBillingRecord() {
		return billingRecord;
	}

	public void setBillingRecord(BillingRecordEntity billingRecord) {
		this.billingRecord = billingRecord;
	}

	public IssuerEntity withBillingRecord(BillingRecordEntity billingRecord) {
		this.billingRecord = billingRecord;
		return this;
	}

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public IssuerEntity withPartyId(String partyId) {
		this.partyId = partyId;
		return this;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public IssuerEntity withOrganizationName(String organizationName) {
		this.organizationName = organizationName;
		return this;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public IssuerEntity withFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public IssuerEntity withLastName(String lastName) {
		this.lastName = lastName;
		return this;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public IssuerEntity withUserId(String userId) {
		this.userId = userId;
		return this;
	}

	public AddressDetailsEmbeddable getAddressDetails() {
		return addressDetails;
	}

	public void setAddressDetails(AddressDetailsEmbeddable addressDetails) {
		this.addressDetails = addressDetails;
	}

	public IssuerEntity withAddressDetails(AddressDetailsEmbeddable addressDetails) {
		this.addressDetails = addressDetails;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(addressDetails, billingRecord, firstName, id, lastName, organizationName, partyId, userId);
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
		IssuerEntity other = (IssuerEntity) obj;
		return Objects.equals(addressDetails, other.addressDetails) && Objects.equals(billingRecord, other.billingRecord) && Objects.equals(firstName, other.firstName) && Objects.equals(id, other.id) && Objects.equals(lastName, other.lastName) && Objects
			.equals(organizationName, other.organizationName) && Objects.equals(partyId, other.partyId) && Objects.equals(userId, other.userId);
	}

	@Override
	public String toString() {
		final var billingRecordId = billingRecord == null ? null : billingRecord.getId();
		StringBuilder builder = new StringBuilder();
		builder.append("IssuerEntity [id=").append(id)
			.append(", billingRecord=").append(billingRecordId)
			.append(", partyId=").append(partyId)
			.append(", organizationName=").append(organizationName)
			.append(", firstName=").append(firstName)
			.append(", lastName=").append(lastName)
			.append(", userId=").append(userId)
			.append(", addressDetails=").append(addressDetails).append("]");
		return builder.toString();
	}
}
