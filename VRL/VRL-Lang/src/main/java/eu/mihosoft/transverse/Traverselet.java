package eu.mihosoft.transverse;

@FunctionalInterface
public interface Traverselet<In> {
	void traverse(In o, CompositeTraverse ctx);
}