package eu.mihosoft.transverse;


@FunctionalInterface
public interface Buildlet<In, Out> {
	Out build(In in);
}