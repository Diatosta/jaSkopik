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
    public Object GetValue()
    {
        return getValue();
    }

    @Override
    public void SetValue(Object value)
    {
        if (!SkopikFactory.IsValueType(value, getDataType()))
        {
            throw new UnsupportedOperationException("Value does not match underlying value type.");
        }

        setPrivateValue(value);
    }

    public final Object GetData()
    {
        return getValue();
    }

    public final void SetData(Object value)
    {
        SetValue(value);
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

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public void setValue(Object value) {
        if (!SkopikFactory.IsValueType(value, getDataType()))
        {
            throw new UnsupportedOperationException("Value does not match underlying value type.");
        }

        setPrivateValue(value);
    }
}