package eu.mihosoft.transverse;

public class CompositeTransform {
	@SuppressWarnings("rawtypes")
	Transformlet[] transformations;

	public CompositeTransform(
			@SuppressWarnings("rawtypes") Transformlet... transformations) {
		this.transformations = transformations;
	}

	public <In, Out> Out transform(In in, Class<In> cli, Class<Out> clo) {
		for (@SuppressWarnings("rawtypes")
		Transformlet t : transformations) {
			try {
				Out r = clo.cast(t.transform(in, this));
				return r;
			} catch (ClassCastException e) {
				System.out
						.println("Finding the proper transformation by try&fail. Build a cache to avoid running into this case.");
			}
		}
		return null;
	}
}