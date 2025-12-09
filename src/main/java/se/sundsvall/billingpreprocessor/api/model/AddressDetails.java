package se.sundsvall.billingpreprocessor.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

@Schema(description = "Address details model")
public class AddressDetails {

	@Schema(description = "Street name and number. Mandatory for EXTERNAL billing record.", examples = "Sesame Street 7")
	private String street;

	@Schema(description = "Care of name", examples = "Abby Cadabby")
	private String careOf;

	@Schema(description = "Postal code. Mandatory for EXTERNAL billing record.", examples = "12345")
	private String postalCode;

	@Schema(description = "City. Mandatory for EXTERNAL billing record.", examples = "Grouchytown")
	private String city;

	public static AddressDetails create() {
		return new AddressDetails();
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public AddressDetails withStreet(String street) {
		this.street = street;
		return this;
	}

	public String getCareOf() {
		return careOf;
	}

	public void setCareOf(String careOf) {
		this.careOf = careOf;
	}

	public AddressDetails withCareOf(String careOf) {
		this.careOf = careOf;
		return this;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public AddressDetails withtPostalCode(String postalCode) {
		this.postalCode = postalCode;
		return this;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public AddressDetails withCity(String city) {
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
		AddressDetails other = (AddressDetails) obj;
		return Objects.equals(careOf, other.careOf) && Objects.equals(city, other.city) && Objects.equals(street, other.street) && Objects.equals(postalCode, other.postalCode);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AddressDetails [street=").append(street)
			.append(", careOf=").append(careOf)
			.append(", postalCode=").append(postalCode)
			.append(", city=").append(city).append("]");
		return builder.toString();
	}
}
