package se.sundsvall.billingpreprocessor.service.creator.definition.external;

import java.util.Objects;

import org.beanio.annotation.Field;
import org.beanio.annotation.Fields;
import org.beanio.annotation.Record;

@Record
@Fields({
	@Field(at = 0, length = 1, name = "recordType", rid = true, literal = "S")
})
public class CustomerRow {

	@Field(at = 1, length = 10)
	private String legalId;

	@Field(at = 11, length = 35)
	private String customerName;

	@Field(at = 46, length = 35)
	private String careOf;

	@Field(at = 81, length = 35)
	private String streetAddress;

	@Field(at = 116, length = 35)
	private String zipCodeAndCity;

	@Field(at = 151, length = 3)
	private String counterpart;

	public static CustomerRow create() {
		return new CustomerRow();
	}

	public String getLegalId() {
		return legalId;
	}

	public void setLegalId(String legalId) {
		this.legalId = legalId;
	}

	public CustomerRow withLegalId(String legalId) {
		setLegalId(legalId);
		return this;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public CustomerRow withCustomerName(String customerName) {
		setCustomerName(customerName);
		return this;
	}

	public String getCareOf() {
		return careOf;
	}

	public void setCareOf(String careOf) {
		this.careOf = careOf;
	}

	public CustomerRow withCareOf(String careOf) {
		setCareOf(careOf);
		return this;
	}

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	public CustomerRow withStreetAddress(String streetAddress) {
		setStreetAddress(streetAddress);
		return this;
	}

	public String getZipCodeAndCity() {
		return zipCodeAndCity;
	}

	public void setZipCodeAndCity(String zipCodeAndCity) {
		this.zipCodeAndCity = zipCodeAndCity;
	}

	public CustomerRow withZipCodeAndCity(String zipCodeAndCity) {
		setZipCodeAndCity(zipCodeAndCity);
		return this;
	}

	public String getCounterpart() {
		return counterpart;
	}

	public void setCounterpart(String counterpart) {
		this.counterpart = counterpart;
	}

	public CustomerRow withCounterpart(String counterpart) {
		setCounterpart(counterpart);
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(careOf, counterpart, customerName, legalId, streetAddress, zipCodeAndCity);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof CustomerRow)) {
			return false;
		}
		CustomerRow other = (CustomerRow) obj;
		return Objects.equals(careOf, other.careOf) && Objects.equals(counterpart, other.counterpart) && Objects.equals(customerName, other.customerName) && Objects.equals(legalId, other.legalId) && Objects.equals(streetAddress, other.streetAddress)
			&& Objects.equals(zipCodeAndCity, other.zipCodeAndCity);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CustomerRow [legalId=").append(legalId).append(", customerName=").append(customerName).append(", careOf=").append(careOf).append(", streetAddress=").append(streetAddress).append(", zipCodeAndCity=").append(zipCodeAndCity)
			.append(", counterpart=").append(counterpart).append("]");
		return builder.toString();
	}
}
