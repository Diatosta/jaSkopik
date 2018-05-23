package jaSkopik.Types;

public interface ISkopikValue extends ISkopikObject
{
    Object getValue();
    void setValue(Object value);

    <T> T GetValue(Class<T> defaultClass, T defaultValue);
}