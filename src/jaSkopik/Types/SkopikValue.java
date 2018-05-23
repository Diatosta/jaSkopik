package jaSkopik.Types;

import java.util.function.Function;

public abstract class SkopikValue extends SkopikObject implements ISkopikValue
{
    public abstract Object GetValue();
    public abstract void SetValue(Object value);

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
        return (boolean)GetValue();
    }

    public final byte ToByte()
    {
        return (byte)GetValue();
    }

    public final char ToChar()
    {
        return (char)GetValue();
    }

    public final int ToDecimal()
    {
        return (int)GetValue();
    }

    public final double ToDouble()
    {
        return (double)GetValue();
    }

    public final short ToInt16()
    {
        return (short)GetValue();
    }

    public final int ToInt32()
    {
        return (int)GetValue();
    }

    public final long ToInt64()
    {
        return (long)GetValue();
    }

    public final byte ToSByte()
    {
        return (byte)GetValue();
    }

    public final float ToSingle()
    {
        return (float)GetValue();
    }

    public final short ToUInt16()
    {
        return (short)GetValue();
    }

    public final int ToUInt32()
    {
        return (int)GetValue();
    }

    public final long ToUInt64()
    {
        return (long)GetValue();
    }

    @Override
    public String toString()
    {
        return GetValue() == null ? null : ((GetValue().toString()) != null) ? GetValue().toString() : "<null>";
    }

    protected SkopikValue(SkopikDataType dataType)
    {
        super(dataType);
    }
}