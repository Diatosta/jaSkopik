package jaSkopik.Types;

public interface ISkopikScope extends ISkopikBlock {
    ISkopikObject get(String name);
    void set(String name, ISkopikObject value);

    boolean HasEntry(String name);

    ISkopikObject GetEntry(String name);
    void SetEntry(String name, ISkopikObject data);
}
