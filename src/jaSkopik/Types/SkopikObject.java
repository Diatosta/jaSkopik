package jaSkopik.Types;

import java.util.Iterator;

public class SkopikObject implements ISkopikObject {
    @Override
    public Object GetData() {
        return null;
    }

    @Override
    public void SetData(Object value) {
        throw new UnsupportedOperationException("Cannot set value on an empty/null value!");
    }

    public static SkopikObject Null(){
        return new SkopikObject(SkopikDataType.Null);
    }

    public SkopikDataType DataType;

    public boolean getIsNone(){
        return (DataType.id & SkopikDataType.Null.id) != 0;
    }

    public boolean getIsArray(){
        return (DataType.id & SkopikDataType.Array.id) != 0;
    }

    public boolean getIsScope(){
        return (DataType.id & SkopikDataType.Scope.id) != 0;
    }

    public boolean getIsTuple(){
        return (DataType.id & SkopikDataType.Tuple.id) != 0;
    }

    public boolean getIsValue(){
        return !(getIsArray() | getIsScope() | getIsTuple());
    }

    public SkopikObject(SkopikDataType type){
        DataType = type;
    }

    @Override
    public SkopikDataType getDataType() {
        return null;
    }

    @Override
    public boolean getIsNull() {
        return false;
    }

    @Override
    public Iterator<ISkopikObject> iterator() {
        return null;
    }
}
