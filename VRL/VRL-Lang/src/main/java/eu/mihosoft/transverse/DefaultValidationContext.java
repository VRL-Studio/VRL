package eu.mihosoft.transverse;

import java.util.ArrayList;
import java.util.List;

public class DefaultValidationContext implements
		ValidationContext {
	Object current;
	List<ValidationMessage> messages = new ArrayList<ValidationMessage>();

	public List<ValidationMessage> getMessages() {
		return messages;
	}

	public void setCurrent(Object current) {
		this.current = current;
	}

	@Override
	public void info(String message) {
		createMsg(ValidationMessageType.INFO, message);
	}

	@Override
	public void warn(String message) {
		createMsg(ValidationMessageType.WARN, message);
	}

	@Override
	public void err(String message) {
		createMsg(ValidationMessageType.ERR, message);
	}

	@Override
	public void fail(String message) {
		createMsg(ValidationMessageType.FAIL, message);

	}

	private void createMsg(ValidationMessageType type, String message) {
		ValidationMessage msg = new ValidationMessage(type, message,
				current);
		messages.add(msg);
	}

}