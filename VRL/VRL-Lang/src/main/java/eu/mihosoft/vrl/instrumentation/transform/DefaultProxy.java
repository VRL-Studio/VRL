package eu.mihosoft.vrl.instrumentation.transform;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class DefaultProxy<T> implements InvocationHandler {
	T proxied;
	Class<T> proxiedClass;
	Object source;
	String key;
	boolean resolved = false;

	public DefaultProxy(String key, Object source, Class<T> proxiedClass) {
		this.source = source;
		this.key = key;
		this.proxiedClass = proxiedClass;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		if (!resolved || proxied == null)
			throw new IllegalStateException(
					"Call to proxy instance created for source type '"
							+ source.getClass().getSimpleName()
							+ "' with key '"
							+ key
							+ "' failed: object transformation has been requested, but never has been performed: "
							+ source);
		return method.invoke(proxied, args);

	}

	public void setProxied(T proxied) {
		if (proxied == null || !proxiedClass.isInstance(proxied))
			throw new IllegalArgumentException(
					"Wrong type of proxied instance for key '" + key
							+ "', expected " + proxiedClass.getName()
							+ ", but got: "
							+ (proxied != null ? proxied.getClass() : null)
							+ ", source obj is " + source);
		this.proxied = proxied;
		this.resolved = true;
	}

	public T getProxied() {
		return proxied;
	}

	public String toString() {
		return "Proxy: key=" + key + ", resolved=" + resolved + ", type="
				+ proxiedClass.getSimpleName() + ", source=" + source;
	}
}