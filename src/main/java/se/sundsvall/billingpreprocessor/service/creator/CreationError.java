package se.sundsvall.billingpreprocessor.service.creator;

import java.util.Objects;

public class CreationError {
	private String message;
	private String entityId;

	private CreationError() {}

	public static CreationError create() {
		return new CreationError();
	}

	public static CreationError create(String message) {
		return create(null, message);
	}

	public static CreationError create(String entityId, String message) {
		return CreationError.create()
			.withEntityId(entityId)
			.withMessage(message);
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public CreationError withEntityId(String entityId) {
		this.entityId = entityId;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public CreationError withMessage(String messsage) {
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
		if (!(obj instanceof CreationError)) {
			return false;
		}
		CreationError other = (CreationError) obj;
		return Objects.equals(entityId, other.entityId) && Objects.equals(message, other.message);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CreationError [message=").append(message).append(", entityId=").append(entityId).append("]");
		return builder.toString();
	}
}
