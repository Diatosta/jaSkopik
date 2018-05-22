package jaSkopik.Types;

import jdk.jfr.Unsigned;

import java.util.BitSet;
import java.util.Map;
import java.util.function.Function;

public class SkopikFactory {
    public static Map<SkopikDataType, Class> TypeLookup = Map.ofEntries(
            Map.entry(SkopikDataType.Binary, BitSet.class),
            Map.entry(SkopikDataType.String, String.class),
            Map.entry(SkopikDataType.Boolean, Boolean.class),
            Map.entry(SkopikDataType.Integer32, Integer.class),
            Map.entry(SkopikDataType.Integer64, Integer.class),
            Map.entry(SkopikDataType.UInteger32, Unsigned.class),
            Map.entry(SkopikDataType.UInteger64, Unsigned.class),
            Map.entry(SkopikDataType.Float, Float.class),
            Map.entry(SkopikDataType.Double, Double.class)
    );

    public static Class GetType(SkopikDataType type){
        return TypeLookup.get(type);
    }

    public static SkopikDataType GetValueType(Class type){
        for(Map.Entry<SkopikDataType, Class> kv : TypeLookup.entrySet()){
            if(kv.getValue() == type){
                return kv.getKey();
            }
        }

        return SkopikDataType.None;
    }

    public static boolean IsValueType(Object value, SkopikDataType type)
    {
        Class classType = GetType(type);
        boolean equal = value.getClass().isAssignableFrom(classType.getClass());
        return equal;
    }

    public static boolean IsValueType(Class value, SkopikDataType type)
    {
        return GetType(type).isAssignableFrom(value);
    }

    public static boolean IsValueType(Object value)
    {
        var type = value.getClass();

        return IsValueType(type);
    }

    public static boolean IsValueType(Class type)
    {
        for (Map.Entry<SkopikDataType, Class> kv : TypeLookup.entrySet())
        {
            var valueType = kv.getValue();

            if (valueType == type)
            {
                return true;
            }
        }

        return false;
    }

    public static ISkopikValue CreateValue(Object value)
    {
        Class type = value.getClass();

        for (Map.Entry<SkopikDataType, Class> kv : TypeLookup.entrySet())
        {
            Class valueType = kv.getValue();

            if (valueType == type)
            {
                try
                {
                    SkopikValue1 instance = new SkopikValue1(type);
                    instance.setPrivateValue(value);

                    return instance;
                }
                catch (RuntimeException e) {
                    throw new IllegalStateException(String.format("Error creating value object: %1$s", e.getMessage()));
                }
                catch (Exception e){
                    System.out.println("Exception thrown: " + e);
                }
            }
        }

        throw new IllegalStateException("Could not create a value object using the specified data.");
    }

    public static <T> ISkopikValue CreateValue(String textValue, Function<String, T> parseFn)
    {
        T value = null;

        try
        {
            value = parseFn.apply(textValue);
        }
        catch (RuntimeException e)
        {
            throw new IllegalStateException(String.format("Error parsing value '%1$s': %2$s", textValue, e.getMessage()));
        }

        return CreateValue(value);
    }
}
