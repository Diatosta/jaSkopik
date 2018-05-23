package jaSkopik.Types;

import jaSkopik.SkopikHelper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class SkopikBlock extends SkopikObject implements ISkopikBlock {
    public abstract int getCount();

    public abstract void CopyTo(ArrayList array, int index);

    public abstract Iterator<ISkopikObject> iterator();

    public abstract ISkopikObject get(int index);
    public abstract void set(int index, ISkopikObject value);

    public String Name;

    public String getName() { return Name; }

    public final boolean getIsAnonymous(){
        return SkopikHelper.IsNullOrEmpty(Name);
    }

    public abstract boolean getIsEmpty();

    protected SkopikBlock(SkopikDataType type){
        super(type);
    }
}
