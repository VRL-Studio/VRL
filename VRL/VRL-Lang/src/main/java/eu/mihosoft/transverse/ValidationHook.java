package eu.mihosoft.transverse;

import java.util.List;

public class ValidationHook implements TraverseHook {

	DefaultValidationContext context = new DefaultValidationContext();
	Validatelet[] validations;

	public ValidationHook(Validatelet... validations) {
		this.validations = validations;
	}

	@Override
	public void handle(Object in) {
		for (Validatelet v : validations) {
			try {
				context.setCurrent(in);
				v.validate(in, context);
			} catch (ClassCastException e) {
				// nop
			}
		}
	}

	public List<ValidationMessage> getMessages() {
		return context.getMessages();
	}

	public void reset() {
		context.getMessages().clear();
	}
}
