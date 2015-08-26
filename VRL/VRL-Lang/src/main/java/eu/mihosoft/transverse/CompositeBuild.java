package eu.mihosoft.transverse;


public class CompositeBuild implements Buildlet {
	Buildlet[] buildlets;

	public CompositeBuild(Buildlet... transformlets) {
		this.buildlets = transformlets;
	}

	@Override
	public Object build(Object in) {
		for (Buildlet t : buildlets) {
			try {
				return t.build(in);
			} catch (ClassCastException e) {
				// nope
			}
		}
		return null;
	}
}