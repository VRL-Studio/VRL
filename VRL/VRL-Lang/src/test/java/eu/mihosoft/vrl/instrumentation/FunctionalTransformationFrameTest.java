package eu.mihosoft.vrl.instrumentation;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import org.junit.Test;

public class FunctionalTransformationFrameTest {

	static Transformation<Integer, String> integerToString() {
		return (in, cli, clo, ctx) -> {
			return in.toString();
		};
	}

	static Transformation<List<Integer>, List<String>> listToList() {
		return (in, cli, clo, ctx) -> {
			List<String> r = new ArrayList<String>();
			for (Integer i : in) {
				r.add(ctx.transform(i, Integer.class, String.class));
			}
			return r;
		};
	}

	@Test
	public void testTransformation() {
		CompositeTransformation tf = new CompositeTransformation(
				integerToString(), listToList());
		String s = tf
				.transform(Integer.valueOf(5), Integer.class, String.class);
		assertEquals("5", s);

		List<String> ls = tf.transform(Arrays.asList(1, 2, 3), List.class,
				List.class);
		assertEquals(3, ls.size());
		assertEquals(String.class, ls.get(0).getClass());
		assertEquals("[1, 2, 3]", ls.toString());
	}

	@Test
	public void testTraverse() {
		StringBuffer buf = new StringBuffer();
		Traverselet<String> t1 = (in, ctx) -> {
			buf.append(in);
			ctx.traverse(in.length());
		};
		Traverselet<Integer> t2 = (in, ctx) -> {
			buf.append(in);
		};
		CompositeTraverse tr = new CompositeTraverse(t1, t2);
		tr.traverse("test");
		assertEquals("test4", buf.toString());
	}

	@Test
	public void testTraverseAndBuild() {
		Traverselet<String> t1 = (in, ctx) -> {
			ctx.traverse(in.length());
		};
		Builder<String, List, Builder.Root> b1 = (in, p, stack, cli, clo, clp) -> {
			List<String> res = new ArrayList();
			res.add(in);
			return res;
		};
		Builder<Integer, List, String> b2 = (in, p, stack, cli, clo, clp) -> {
			List<Integer> res = new ArrayList();
			res.add(in);
			return res;
		};
		CompositeTraverse ct = new CompositeTraverse(new CompositeBuilder(b1),
				t1);
		ct.traverse("Hello");
		List res = ct.getResult(List.class);
		assertEquals("Hello", res.get(0));

	}
}

@FunctionalInterface
interface Transformation<In, Out> {
	Out transform(In in, Class<In> cli, Class<Out> clo,
			CompositeTransformation context);
}

class CompositeTransformation {
	@SuppressWarnings("rawtypes")
	Transformation[] transformations;

	public CompositeTransformation(
			@SuppressWarnings("rawtypes") Transformation... transformations) {
		this.transformations = transformations;
	}

	public <In, Out> Out transform(In in, Class<In> cli, Class<Out> clo) {
		for (@SuppressWarnings("rawtypes")
		Transformation t : transformations) {
			try {
				Out r = clo.cast(t.transform(in, cli, clo, this));
				return r;
			} catch (ClassCastException e) {
				System.out
						.println("Finding the proper transformation by try&fail. Build a cache to avoid running into this case.");
			}
		}
		return null;
	}
}

@FunctionalInterface
interface Traverselet<In> {
	void traverse(In o, CompositeTraverse ctx);
}

class CompositeTraverse {
	Traverselet[] traverselets;
	Builder builder = (in, p, stack, cli, clo, clp) -> {
		return null;
	};
	Stack<Object> stack = new Stack<Object>();
	Object result;

	CompositeTraverse(Traverselet... traverselets) {
		this.traverselets = traverselets;
		stack.push(new Builder.Root());
	}

	public CompositeTraverse(Builder builder, Traverselet... traverselets) {
		this(traverselets);
		this.builder = builder;
	}

	void traverse(Object in) {
		Object parent = stack.peek();
		stack.push(in);
		Object result = builder.build(in, parent, stack, in.getClass(), null,
				stack.peek().getClass());
		for (Traverselet t : traverselets) {
			try {
				t.traverse(in, this);
			} catch (ClassCastException e) {
				// nope
			}
		}
		if (result != null)
			this.result = result;
		stack.pop();
	}

	public <T> T getResult(Class<T> clo) {
		return clo.cast(result);
	}

}

@FunctionalInterface
interface Builder<In, Out, Parent> {
	Out build(In in, Parent p, Stack<?> stackIn, Class<In> cli, Class<Out> clo,
			Class<Parent> clp);

	class Root {
	}
}

class CompositeBuilder implements Builder {
	Builder[] buildlets;

	public CompositeBuilder(Builder... transformlets) {
		this.buildlets = transformlets;
	}

	@Override
	public Object build(Object in, Object p, Stack stackIn, Class cli,
			Class clo, Class clp) {
		for (Builder t : buildlets) {
			try {
				return t.build(in, p, stackIn, cli, clo, clp);
			} catch (ClassCastException e) {
				// nope
			}
		}
		return null;
	}
}
