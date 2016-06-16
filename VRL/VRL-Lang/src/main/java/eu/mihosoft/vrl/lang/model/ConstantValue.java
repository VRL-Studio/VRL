package eu.mihosoft.vrl.lang.model;

public interface ConstantValue extends CodeEntity {

    public IType getType();

    public <T> T getValue(Class<T> t);

    public void setType(IType type);
}

class ConstantValueImpl extends CodeEntityImpl implements ConstantValue {

    final Object value;
    IType type; // final

    public ConstantValueImpl(Object value, IType type) {
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

    @Override
    public void setType(IType type) {
        this.type = type;
    }
}
