package jaSkopik.Types;

import java.util.LinkedHashMap;

public interface ISkopikScope extends ISkopikBlock {
    ISkopikObject get(String name);
    void set(String name, ISkopikObject value);

    LinkedHashMap<String, ISkopikObject> getEntries();

    boolean HasEntry(String name);

    ISkopikObject GetEntry(String name);
    void SetEntry(String name, ISkopikObject data);
}
