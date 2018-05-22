package jaSkopik.Types;

public interface ISkopikTuple extends ISkopikBlock {
    SkopikDataType getTupleType();

    Iterable<Object> GetValues();
}
