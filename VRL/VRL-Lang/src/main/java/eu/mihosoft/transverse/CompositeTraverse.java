package eu.mihosoft.transverse;


public class CompositeTraverse {
	Traverselet[] traverselets;
	TraverseHook hook;

	CompositeTraverse(Traverselet... traverselets) {
		this.traverselets = traverselets;
	}

	public CompositeTraverse(TraverseHook hook, Traverselet... traverselets) {
		this(traverselets);
		this.hook = hook;
	}

	public void traverse(Object in) {
		if (hook != null)
			hook.handle(in);
		for (Traverselet t : traverselets) {
			try {
				t.traverse(in, this);
			} catch (ClassCastException e) {
				// nope
			}
		}
	}
}