package eu.mihosoft.vrl.lang.model;

public class ConstantValueFactory {
	
	public static ConstantValue createConstantValue(Object value, IType type)
	{
		return new ConstantValueImpl(value, type);
	}

}
