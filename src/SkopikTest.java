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

        SkopikData skopData = null;

        try{
            Path path = Paths.get(filename);
            fileContents = Files.readAllBytes(path);

            skopData = new SkopikData(fileContents);
        }
        catch (Exception ex){
            System.out.println(ex);
        }

        SkopikScope data = skopData.getGlobalScope();

        for(int i = 0; i < data.getCount(); i++){
            ISkopikObject obj = data.getItem(i);
            String className = "ERROR!!!";

            if(obj != null){
                Class objClass = obj.getClass();

                className = objClass.getName();

                if(className.startsWith("jaSkopik"))
                    className = className.substring(6);

                if(obj instanceof ISkopikValue)
                    className = className + " : '" + obj + "'";
            }

            System.out.println("[" + i + "]: " + className);
        }
    }
}
