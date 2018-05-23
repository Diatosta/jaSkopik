package jaSkopik.Types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class SkopikScope extends SkopikBlock implements ISkopikScope {
    private LinkedHashMap<String, ISkopikObject> m_entries;

    public final LinkedHashMap<String, ISkopikObject> getEntries(){
        return m_entries;
    }

    @Override
    public int getCount() {
        return m_entries.size();
    }

    @Override
    public void CopyTo(ArrayList array, int index) {
        if(index >= array.size()){
            throw new IllegalArgumentException("Index is bigger than the array size!");
        }

        for(int i = index,  j = 0; i < array.size(); i++, j++){
            array.set(i, m_entries.get(j));
        }
    }

    @Override
    public boolean getIsEmpty() {
        return m_entries.size() == 0;
    }

    public ISkopikObject getItem(int index)
    {
        return new ArrayList<>(m_entries.values()).get(index);
    }

    @Override
    public ISkopikObject get(int index)
    {
        return getEntries().get(index);
    }
    @Override
    public void set(int index, ISkopikObject value)
    {
        if (index > getEntries().size())
        {
            throw new IndexOutOfBoundsException("Index " + index + " out of range in scope.");
        }

        String key = getEntries().keySet().toArray()[index].toString();

        getEntries().put(key, value);
    }

    public final ISkopikObject getItem(String name)
    {
        return GetEntry(name);
    }
    public final void setItem(String name, ISkopikObject value)
    {
        SetEntry(name, value);
    }

    public final boolean HasEntry(String name)
    {
        return m_entries.containsKey(name);
    }

    public final ISkopikObject GetEntry(String name)
    {
        return (HasEntry(name)) ? m_entries.get(name) : null;
    }

    public final void SetEntry(String name, ISkopikObject data)
    {
        if (HasEntry(name))
        {
            m_entries.put(name, data);
        }
        else
        {
            m_entries.put(name, data);
        }
    }

    public SkopikScope()
    {
        super(SkopikDataType.Scope);
        m_entries = new LinkedHashMap<>();
    }

    public SkopikScope(String name)
    {
        this();
        Name = name;
    }

    @Override
    public Iterator<ISkopikObject> iterator() {
        return m_entries.values().iterator();
    }

    protected SkopikScope(SkopikDataType type) {
        super(type);
    }

    @Override
    public ISkopikObject get(String name) {
        return null;
    }

    @Override
    public void set(String name, ISkopikObject value) {

    }
}
