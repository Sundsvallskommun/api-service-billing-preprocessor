package se.sundsvall.billingpreprocessor.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AddressDetailsEmbeddable implements Serializable {
	private static final long serialVersionUID = -4466983844392817L;

	@Column(name = "street")
	private String street;

	@Column(name = "care_of")
	private String careOf;

	@Column(name = "postal_code")
	private String postalCode;

	@Column(name = "city")
	private String city;

	public static AddressDetailsEmbeddable create() {
		return new AddressDetailsEmbeddable();
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public AddressDetailsEmbeddable withStreet(String street) {
		this.street = street;
		return this;
	}

	public String getCareOf() {
		return careOf;
	}

	public void setCareOf(String careOf) {
		this.careOf = careOf;
	}

	public AddressDetailsEmbeddable withCareOf(String careOf) {
		this.careOf = careOf;
		return this;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public AddressDetailsEmbeddable withPostalCode(String postalCode) {
		this.postalCode = postalCode;
		return this;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public AddressDetailsEmbeddable withCity(String city) {
		this.city = city;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(careOf, city, street, postalCode);
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
		AddressDetailsEmbeddable other = (AddressDetailsEmbeddable) obj;
		return Objects.equals(careOf, other.careOf) && Objects.equals(city, other.city) && Objects.equals(street, other.street) && Objects.equals(postalCode, other.postalCode);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AddressDetailsEmbeddable [street=").append(street)
			.append(", careOf=").append(careOf)
			.append(", postalCode=").append(postalCode)
			.append(", city=").append(city).append("]");
		return builder.toString();
	}
}
