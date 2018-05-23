package jaSkopik.Types;

public abstract class SkopikValue extends SkopikObject implements ISkopikValue
{
    public abstract Object getValue();

    public abstract void setValue(Object value);

    public abstract <T> T GetValue(Class<T> defaultClass, T defaultValue);

    public final TypeCode GetTypeCode()
    {
        switch (getDataType())
        {
            case Null:
                return TypeCode.Empty;
            case Boolean:
                return TypeCode.Boolean;
            case Binary:
                return TypeCode.Int32;
            case String:
                return TypeCode.String;
            case Float:
                return TypeCode.Single;
            case Double:
                return TypeCode.Double;
            case Integer32:
                return TypeCode.Int32;
            case Integer64:
                return TypeCode.Int64;
            case UInteger32:
                return TypeCode.UInt32;
            case UInteger64:
                return TypeCode.UInt64;
        }

        return TypeCode.Object;
    }

    public final boolean ToBoolean()
    {
        return (boolean)getValue();
    }

    public final byte ToByte()
    {
        return (byte)getValue();
    }

    public final char ToChar()
    {
        return (char)getValue();
    }

    public final int ToDecimal()
    {
        return (int)getValue();
    }

    public final double ToDouble()
    {
        return (double)getValue();
    }

    public final short ToInt16()
    {
        return (short)getValue();
    }

    public final int ToInt32()
    {
        return (int)getValue();
    }

    public final long ToInt64()
    {
        return (long)getValue();
    }

    public final byte ToSByte()
    {
        return (byte)getValue();
    }

    public final float ToSingle()
    {
        return (float)getValue();
    }

    public final short ToUInt16()
    {
        return (short)getValue();
    }

    public final int ToUInt32()
    {
        return (int)getValue();
    }

    public final long ToUInt64()
    {
        return (long)getValue();
    }

    @Override
    public String toString()
    {
        return getValue() == null ? null : ((getValue().toString()) != null) ? getValue().toString() : "<null>";
    }

    protected SkopikValue(SkopikDataType dataType)
    {
        super(dataType);
    }
}