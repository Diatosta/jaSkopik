import jaSkopik.SkopikData;
import jaSkopik.Types.ISkopikObject;
import jaSkopik.Types.ISkopikValue;
import jaSkopik.Types.SkopikScope;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SkopikTest {
    public static void main(String[] args){
        String filename = System.getProperty("user.dir") + "\\src\\test.skop";

        byte[] fileContents = null;

        SkopikData data = null;

        try{
            Path path = Paths.get(filename);
            fileContents = Files.readAllBytes(path);

            data = SkopikData.Load(fileContents, "test.skop");
        }
        catch (Exception ex){
            System.out.println(ex);
            ex.printStackTrace();
        }

        int i = 0;
        for(Object obj : data){
            String className = "ERROR!!!";

            if(obj != null){
                Class objClass = obj.getClass();

                className = objClass.getName();

                if(className.startsWith("jaSkopik"))
                    className = className.substring(6);

                if(obj instanceof ISkopikValue)
                    className = className + " : '" + obj + "'";
            }

            System.out.println("[" + i++ + "]: " + className);
        }
    }
}
