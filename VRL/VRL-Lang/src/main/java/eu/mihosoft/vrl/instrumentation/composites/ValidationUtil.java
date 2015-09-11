package eu.mihosoft.vrl.instrumentation.composites;

import eu.mihosoft.transverse.Validatelet;
import eu.mihosoft.vrl.lang.model.IfDeclaration;
import eu.mihosoft.vrl.lang.model.Operator;
import eu.mihosoft.vrl.lang.model.SimpleForDeclaration;

public class ValidationUtil {

	Validatelet<SimpleForDeclaration> sfd1 = (in, ctx) -> {
		if (in.getInc() > 0
				&& Operator.GREATER_EQUALS.equals(in.getOperation())) {
			// throw new IllegalStateException("In for-loop: infinite loops"
			// + " are not supported! Change '>=' to '<=' to prevent that."
			// );
			ctx.err("In for-loop: infinite loops"
					+ " are not supported! Change '>=' to '<=' to prevent that.");
		}
	};

	public Validatelet[] validations() {
		return new Validatelet[] { sfd1 };
	}

}
