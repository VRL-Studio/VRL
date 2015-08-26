package eu.mihosoft.transverse;

public class ValidationMessage {
	String message;
	Object sender;
	ValidationMessageType type;

	protected ValidationMessage(ValidationMessageType type, String message,
			Object sender) {
		this.message = message;
		this.sender = sender;
	}

	public boolean isError() {
		return type == ValidationMessageType.ERR;
	}

	public boolean isWarn() {
		return type == ValidationMessageType.WARN;
	}

	public boolean isFailure() {
		return type == ValidationMessageType.FAIL;
	}

	public boolean isInfo() {
		return type == ValidationMessageType.INFO;
	}

	public Object getSender() {
		return sender;
	}

	public String getMessage() {
		return message;
	}
}