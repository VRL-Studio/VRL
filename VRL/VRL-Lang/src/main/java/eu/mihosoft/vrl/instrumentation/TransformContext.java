package eu.mihosoft.vrl.instrumentation;

import java.util.Stack;

public interface TransformContext {
	
	void bind(String key, Object input);
	
	<T> T resolve(String key, Class<T> outputType);
	
	Stack<Object> getInputPath();
	Stack<Object> getOutputPath();

}
