package eu.mihosoft.vrl.lang.model;

public interface ConstantValue extends CodeEntity {
	
	public IType getType();
	public <T> T getValue(Class<T> t);
}

class ConstantValueImpl extends CodeEntityImpl implements ConstantValue
{

	final Object value;
	final IType type;
	
	public ConstantValueImpl(Object value, IType type)
	{
		this.value = value;
		this.type = type;
	}
	
	@Override
	public final IType getType() {
		return type;
	}

	@Override
	public final <T> T getValue(Class<T> t) {
		return t.cast(value);
	}
}
