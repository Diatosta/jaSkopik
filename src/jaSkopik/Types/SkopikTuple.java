package jaSkopik.Types;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;

public class SkopikTuple extends SkopikBlock implements ISkopikTuple {
    private ArrayList<ISkopikObject> m_entries;

    public final ArrayList<ISkopikObject> getEntries(){
        return m_entries;
    }

    @Override
    public int getCount() {
        return m_entries.size();
    }

    @Override
    public boolean getIsEmpty() {
        return m_entries.size() == 0;
    }

    private SkopikDataType privateTupleType = SkopikDataType.values()[0];
    public final SkopikDataType getTupleType()
    {
        return privateTupleType;
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
        if (data.getDataType() != getTupleType())
        {
            throw new UnsupportedOperationException("Tuple data mismatch!");
        }

        if (index < m_entries.size())
        {
            m_entries.set(index, data);
        }
        else
        {
            m_entries.add(index, data);
        }
    }

    public SkopikTuple(SkopikDataType tupleType)
    {
        super(SkopikDataType.Tuple);
        m_entries = new ArrayList<ISkopikObject>();
        privateTupleType = tupleType;
    }

    public SkopikTuple(SkopikDataType tupleType, String name)
    {
        this(tupleType);
        Name = name;
    }

    @Override
    public void forEach(Consumer action) {

    }

    @Override
    public Iterable<Object> GetValues() {
        /*for (ISkopikObject entry : getEntries())
        {
            if (!entry.getIsValue())
            {
                yield break;
            }

            yield return entry.GetData();
        }*/

        return null;
    }

    @Override
    public Iterator<ISkopikObject> iterator() {
        return m_entries.iterator();
    }
}
