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

    private <T> T ConvertTo(Function<Object, T> convertFn)
    {
        Object data = GetValue();

        if (data != null)
        {
            return convertFn.apply(data);
        }

        return null;
    }

    /*public final boolean ToBoolean()
    {
        return ConvertTo(Convert.ToBoolean);
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: byte IConvertible.ToByte(IFormatProvider provider)
    public final byte ToByte(IFormatProvider provider)
    {
        return ConvertTo(Convert.ToByte, provider);
    }

    public final char ToChar(IFormatProvider provider)
    {
        return ConvertTo(Convert.ToChar, provider);
    }

    public final LocalDateTime ToDateTime(IFormatProvider provider)
    {
        throw new UnsupportedOperationException("Unsupported value type cast.");
    }

    public final BigDecimal ToDecimal(IFormatProvider provider)
    {
        return ConvertTo(Convert.ToDecimal, provider);
    }

    public final double ToDouble(IFormatProvider provider)
    {
        return ConvertTo(Convert.ToDouble, provider);
    }

    public final short ToInt16(IFormatProvider provider)
    {
        return ConvertTo(Convert.ToInt16, provider);
    }

    public final int ToInt32(IFormatProvider provider)
    {
        return ConvertTo(Convert.ToInt32, provider);
    }

    public final long ToInt64(IFormatProvider provider)
    {
        return ConvertTo(Convert.ToInt64, provider);
    }

    public final byte ToSByte(IFormatProvider provider)
    {
        return ConvertTo(Convert.ToSByte, provider);
    }

    public final float ToSingle(IFormatProvider provider)
    {
        return ConvertTo(Convert.ToSingle, provider);
    }

    public final String toString(IFormatProvider provider)
    {
        return ConvertTo(String.valueOf, provider);
    }

    public final Object ToType(java.lang.Class conversionType, IFormatProvider provider)
    {
        Object data = GetValue();

        if (data instanceof IConvertible)
        {
            return Convert.ChangeType(data, conversionType, provider);
        }

        throw new IllegalStateException(String.format("Cannot convert object of type '%1$s' to type '%2$s'.", data.getClass().getSimpleName(), conversionType.getSimpleName()));
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: ushort IConvertible.ToUInt16(IFormatProvider provider)
    public final short ToUInt16(IFormatProvider provider)
    {
        return ConvertTo(Convert.ToUInt16, provider);
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: uint IConvertible.ToUInt32(IFormatProvider provider)
    public final int ToUInt32(IFormatProvider provider)
    {
        return ConvertTo(Convert.ToUInt32, provider);
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: ulong IConvertible.ToUInt64(IFormatProvider provider)
    public final long ToUInt64(IFormatProvider provider)
    {
        return ConvertTo(Convert.ToUInt64, provider);
    }*/

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