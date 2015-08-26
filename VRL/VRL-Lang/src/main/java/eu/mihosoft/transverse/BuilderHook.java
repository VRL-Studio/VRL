package eu.mihosoft.transverse;

import java.util.HashMap;
import java.util.Map;

public class BuilderHook implements TraverseHook {

	Buildlet[] buildlets;
	Map<Object, Object> result = new HashMap<Object, Object>();

	public BuilderHook(Buildlet... buildlets) {
		this.buildlets = buildlets;
	}

	@Override
	public void handle(Object in) {
		for (Buildlet b : buildlets) {
			try {
				Object r = b.build(in);
				result.put(in, r);
			} catch (ClassCastException e) {
				// nop
			}
		}
	}

	public <T> T getResult(Object in, Class<T> cls) {
		return cls.cast(result.get(in));
	}

	public Map<Object, Object> getResults() {
		return result;
	}

}
