package eu.mihosoft.vrl.instrumentation.composites;

import eu.mihosoft.transverse.Validatelet;
import eu.mihosoft.vrl.lang.model.Operator;
import eu.mihosoft.vrl.lang.model.SimpleForDeclaration;
import eu.mihosoft.vrl.lang.model.Type;
import eu.mihosoft.vrl.lang.model.WhileDeclaration;

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

	Validatelet<WhileDeclaration> wd1 = (in, ctx) -> {
		if (!Type.BOOLEAN.equals(in.getCheck().getType())) {
			ctx.err("while-loop: must contain boolean"
					+ " expression! Only boolean condition statements are "
					+ "supported.");
		}
	};
	
	Validatelet<WhileDeclaration> wd2 = (in, ctx) -> {
		if (Type.VOID.equals(in.getCheck().getType()))
		{
			ctx.err("while-loop: must contain boolean"
					+ " expression! Null not supported. Only boolean condition statements are "
					+ " allowed.");
		}
	};
	
	

	@SuppressWarnings("rawtypes")
	public Validatelet[] validations() {
		return new Validatelet[] { sfd1, wd2, wd1 };
	}

}
