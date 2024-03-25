package se.sundsvall.billingpreprocessor.service.error;

import java.util.Objects;

public class InvoiceFileError {
	private String message;
	private String entityId;

	private InvoiceFileError() {}

	public static InvoiceFileError create() {
		return new InvoiceFileError();
	}

	public static InvoiceFileError create(String message) {
		return create(null, message);
	}

	public static InvoiceFileError create(String entityId, String message) {
		return InvoiceFileError.create()
			.withEntityId(entityId)
			.withMessage(message);
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public InvoiceFileError withEntityId(String entityId) {
		this.entityId = entityId;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public InvoiceFileError withMessage(String messsage) {
		this.message = messsage;
		return this;
	}

	public boolean isCommonError() {
		return Objects.isNull(entityId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(entityId, message);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof InvoiceFileError)) {
			return false;
		}
		InvoiceFileError other = (InvoiceFileError) obj;
		return Objects.equals(entityId, other.entityId) && Objects.equals(message, other.message);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("InvoiceFileError [message=").append(message).append(", entityId=").append(entityId).append("]");
		return builder.toString();
	}

}
