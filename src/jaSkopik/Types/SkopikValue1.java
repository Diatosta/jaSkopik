package jaSkopik.Types;

public class SkopikValue1<T> extends SkopikValue implements ISkopikObject
{
    private Object privateValue;
    public final Object getPrivateValue()
    {
        return privateValue;
    }

    public final void setPrivateValue(Object value)
    {
        privateValue = value;
    }

    @Override
    public Object getValue()
    {
        return getPrivateValue();
    }

    @Override
    public void setValue(Object value)
    {
        if (!SkopikFactory.IsValueType(value, getDataType()))
        {
            throw new UnsupportedOperationException("Value does not match underlying value type.");
        }

        setPrivateValue(value);
    }

    @Override
    public <T> T GetValue(Class<T> defaultClass, T defaultValue) {
        Class type = defaultClass;

        if(SkopikFactory.IsValueType(type, DataType)){
            return (T)getValue();
        }

        return defaultValue;
    }

    public final Object GetData()
    {
        return getValue();
    }

    public final void SetData(Object value)
    {
        setValue(value);
    }

    @Override
    public String toString()
    {
        return ((privateValue.toString()) != null) ? privateValue.toString() : "<null>";
    }

    public SkopikValue1(Class value)
    {
        super(SkopikFactory.GetValueType(value));
        setValue(value);
    }

    @Override
    public boolean getIsArray() {
        return false;
    }

    @Override
    public SkopikDataType getDataType() {
        return DataType;
    }

    @Override
    public boolean getIsNone() {
        return false;
    }

    @Override
    public boolean getIsNull() {
        return false;
    }

    @Override
    public boolean getIsScope() {
        return false;
    }

    @Override
    public boolean getIsTuple() {
        return false;
    }

    @Override
    public boolean getIsValue() {
        return false;
    }
}