package eu.mihosoft.vrl.instrumentation.composites;

import eu.mihosoft.transverse.Validatelet;
import eu.mihosoft.vrl.lang.model.IfDeclaration;

public class ValidationUtil {

	Validatelet<IfDeclaration> vif = (in, ctx) -> {

	};

	public Validatelet[] validations() {
		return new Validatelet[] { vif };
	}

}
