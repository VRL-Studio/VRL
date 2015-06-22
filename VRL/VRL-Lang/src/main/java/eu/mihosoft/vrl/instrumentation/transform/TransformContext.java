package eu.mihosoft.vrl.instrumentation.transform;

import java.util.Stack;

public interface TransformContext {
	
	<T> T resolve(String key, Object input, Class<T> outputType);
	
	Stack<Object> getInputPath();
	Stack<Object> getOutputPath();

}
