package jaSkopik.Types;

import java.util.Iterator;

public interface ISkopikObject {
    Object GetData();
    void SetData(Object value);

    SkopikDataType getDataType();

    boolean getIsNone();
    boolean getIsNull();
    boolean getIsArray();
    boolean getIsScope();
    boolean getIsTuple();
    boolean getIsValue();

    Iterator<ISkopikObject> iterator();
}
