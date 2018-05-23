package jaSkopik;

import jaSkopik.Types.*;

import javax.naming.OperationNotSupportedException;
import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class SkopikData implements Iterable {
    public ISkopikBlock Block;
    protected HashMap<String, ISkopikObject> Entries;

    public  String Name;

    public boolean IsScope(){
        return Entries != null;
    }

    @Override
    public void forEach(Consumer action) {

    }

    @Override
    public Spliterator spliterator() {
        return null;
    }

    @Override
    public Iterator iterator() {
        if(IsScope())
            return Entries.values().iterator();

        int index = 0;

        HashMap<String, ISkopikObject> result = new HashMap<>();
        for(ISkopikObject object : Block){
            result.put("item_" + index++, object);
        }

        return result.entrySet().iterator();
    }

    public int getCount(){
        return Block.getCount();
    }

    public void CopyTo(ArrayList array, int index){
        Block.CopyTo(array, index);
    }

    public ISkopikObject GetObject(int index){
        if(index < Block.getCount()){
            return Block.get(index);
        }

        throw new IllegalArgumentException("Object index is out of range.");
    }

    public ISkopikObject GetObject(String name) throws OperationNotSupportedException{
        if(IsScope()){
            return Entries.get(name);
        }

        throw new OperationNotSupportedException("Cannot get object from a non-scope block.");
    }

    public SkopikData getSkopikData(String name) throws OperationNotSupportedException{
        ISkopikObject result = null;

        if((result = GetObject(name)) instanceof  ISkopikBlock){
            return new SkopikData((ISkopikBlock)result);
        }

        throw new OperationNotSupportedException("Couldn't get data block '" + name + "'!");
    }

    public SkopikData getSkopikData(int index) throws OperationNotSupportedException, IllegalArgumentException{
        if(index < Block.getCount()){
            ISkopikObject result = null;

            if((result = GetObject(index)) instanceof ISkopikBlock)
                return new SkopikData((ISkopikBlock)result);

            throw new OperationNotSupportedException("Couldn't get data block from index '" + index + "'!");
        }

        throw new IllegalArgumentException("Data block index out of range.");
    }

    public <T extends SkopikObject> T Get(String name) throws OperationNotSupportedException{
        ISkopikObject obj = GetObject(name);

        return (obj instanceof SkopikObject) ? (T)obj : null;
    }

    public <T> T GetValue(Class<T> type, String name) throws OperationNotSupportedException{
        ISkopikObject obj = GetObject(name);

        if(obj instanceof ISkopikValue){
            ISkopikValue val = (ISkopikValue)obj;
            T result = null;

            if((result = val.GetValue(type, result)) != null){
                return result;
            }

            throw new ClassCastException("Cannot cast valuue '" + name + "' to type '" + type + "'.");
        }

        throw new OperationNotSupportedException("Object '" + name + "' is not a value type.");
    }

    public void SetObject(String name, ISkopikObject value) throws OperationNotSupportedException{
        if(IsScope()){
            if(Entries.containsKey(name)){
                Entries.replace(name, value);
            }
            else{
                Entries.put(name, value);
            }
        }

        throw new OperationNotSupportedException("Cannot set object in a non-scope block.");
    }

    public void SetObject(int index, ISkopikObject value){
        // Scopes will throw exceptions due to not being able to add by index
        Block.set(index, value);
    }

    public Object GetValue(String name) throws OperationNotSupportedException{
        if(IsScope()){
            ISkopikValue result = (ISkopikValue) Entries.get(name);

            if(result == null)
                throw new ClassCastException("Object '" + name + "' is not a value type.");

            return result.getValue();
        }

        throw new OperationNotSupportedException("Cannot get value from a non-scope block.");
    }

    public void SetValue(String name, Object value) throws OperationNotSupportedException{
        if(IsScope())
            Entries.replace(name, SkopikFactory.CreateValue(value));

        throw new OperationNotSupportedException("Cannot set value in a non-scope block.");
    }

    public Object GetValue(int index){
        if(index < Block.getCount()){
            ISkopikValue result = (ISkopikValue)Block.get(index);

            if(result == null)
                throw new ClassCastException("Object at index " + index + " is not a value type.");

            return result.getValue();
        }

        throw new IllegalArgumentException("Value index out of range.");
    }

    public void SetValue(int index, Object value){
        Block.set(index, SkopikFactory.CreateValue(value));
    }

    public static SkopikData Load(InputStream stream, String name) throws OperationNotSupportedException, IOException{
        ISkopikBlock block = null;

        try(SkopikReader skop = new SkopikReader(stream)){
            block = skop.ReadScope(name);
        }

        return new SkopikData(block);
    }

    public static SkopikData Load(byte[] buffer, String name) throws IOException, OperationNotSupportedException{
        try(ByteArrayInputStream bais = new ByteArrayInputStream(buffer, 0, buffer.length)){
            return Load(bais, name);
        }
    }

    public static SkopikData Load(String filename) throws FileNotFoundException, IOException, OperationNotSupportedException{
        File f = new File(filename);
        if(!f.exists()){
            throw new FileNotFoundException("jaSkopik file was not found.");
        }

        String name = SkopikHelper.getFileNameWithoutExtension(filename);
        byte[] buffer;
        try {
            buffer = Files.readAllBytes(f.toPath());
        }
        catch(Exception ex){
            throw ex;
        }

        return Load(buffer, name);
    }

    public SkopikData(ISkopikBlock block){
        Block = block;

        if(Block instanceof ISkopikScope)
            Entries = ((ISkopikScope)Block).getEntries();

        Name = block.getName();
    }

    public SkopikData(String name){
        this(new SkopikScope(name));
    }
}
