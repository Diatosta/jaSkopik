package jaSkopik.Types;

public interface ISkopikBlock extends ISkopikObject, Iterable<ISkopikObject> {
    ISkopikObject get(int index);
    void set(int index, ISkopikObject value);

    /**
     * Gets wether or not this scoped object is anonymous
     */
    boolean getIsAnonymous();

    /**
     * Gets wether or not this scoped object is empty
     */
    boolean getIsEmpty();
}
