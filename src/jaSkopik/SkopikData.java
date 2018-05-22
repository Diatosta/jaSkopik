package jaSkopik;

import jaSkopik.Types.SkopikScope;

import javax.naming.OperationNotSupportedException;
import java.io.*;
import java.nio.file.Files;

public class SkopikData {
    public static final String DefaultName = "<global>";

    SkopikScope GlobalScope;
    public SkopikScope getGlobalScope(){
        return GlobalScope;
    }
    public  void setGlobalScope(SkopikScope globalScope){
        GlobalScope = globalScope;
    }

    public static SkopikData Load(String filename) throws OperationNotSupportedException, IOException {
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

        return new SkopikData(name, buffer);
    }

    public SkopikData(){
        this(DefaultName);
    }

    public SkopikData(byte[] buffer) throws IOException, OperationNotSupportedException{
        this(DefaultName, buffer);
    }

    public SkopikData(String name){
        GlobalScope = new SkopikScope(name);
    }

    public SkopikData(String name, byte[] buffer) throws IOException, OperationNotSupportedException {
        try(ByteArrayInputStream bais = new ByteArrayInputStream(buffer, 0, buffer.length);
            SkopikReader skop = new SkopikReader(bais)){
            GlobalScope = skop.ReadScope(name);
        }
    }

    public SkopikData(String name, InputStream stream) throws OperationNotSupportedException, IOException{
        try(SkopikReader skop = new SkopikReader(stream)){
            GlobalScope = skop.ReadScope(name);
        }
    }
}
