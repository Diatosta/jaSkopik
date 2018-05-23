package jaSkopik.Types;

import java.util.ArrayList;

public interface ISkopikBlock extends ISkopikObject, Iterable<ISkopikObject> {
    ISkopikObject get(int index);
    void set(int index, ISkopikObject value);

    int getCount();

    String getName();

    void CopyTo(ArrayList array, int index);

    /**
     * Gets wether or not this scoped object is anonymous
     */
    boolean getIsAnonymous();

    /**
     * Gets wether or not this scoped object is empty
     */
    boolean getIsEmpty();
}
