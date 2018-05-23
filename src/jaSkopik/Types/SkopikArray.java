package jaSkopik.Types;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

public class SkopikArray extends SkopikBlock implements ISkopikArray {
    private ArrayList<ISkopikObject> m_entries;

    public final ArrayList<ISkopikObject> getEntries(){
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

    @Override
    public ISkopikObject get(int index)
    {
        return GetEntry(index);
    }
    @Override
    public void set(int index, ISkopikObject value)
    {
        SetEntry(index, value);
    }

    public final ISkopikObject GetEntry(int index)
    {
        return (index < m_entries.size()) ? m_entries.get(index) : null;
    }

    public final void SetEntry(int index, ISkopikObject data)
    {
        if (index < m_entries.size())
        {
            m_entries.set(index, data);
        }
        else
        {
            m_entries.add(index, data);
        }
    }

    public SkopikArray()
    {
        super(SkopikDataType.Array);
        m_entries = new ArrayList<ISkopikObject>();
    }

    public SkopikArray(String name)
    {
        this();
        Name = name;
    }

    @Override
    public Iterator<ISkopikObject> iterator() {
        return m_entries.iterator();
    }

    protected SkopikArray(SkopikDataType type) {
        super(type);
    }
}
