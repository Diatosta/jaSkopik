package jaSkopik.Types;

public interface ISkopikArray extends ISkopikBlock {
    ISkopikObject GetEntry(int index);
    void SetEntry(int index, ISkopikObject data);
}
