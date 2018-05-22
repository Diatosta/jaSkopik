package jaSkopik;

public class SkopikHelper {
    public static boolean IsNullOrWhiteSpace(String s) {
        return s == null || IsWhitespace(s);
    }

    public static boolean IsWhitespace(String s) {
        int length = s.length();
        if(length > 0){
            for(int start = 0, middle = length / 2, end = length - 1; start <= middle; start++, end--){
                if(s.charAt(start) > ' ' || s.charAt(end) > ' '){
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static boolean IsNullOrEmpty(String s){
        return s == null || s.isEmpty();
    }

    public static String getFileNameWithoutExtension(String fileName) {
        int pos = fileName.lastIndexOf(".");
        if (pos == -1)
            return fileName;

        return fileName.substring(0, pos);
    }

    public static String stripQuotesFromString(String sval) {
        String result = null;
        if (sval != null && !sval.isEmpty()) {
            // if the string contains a single quote or space, leave the double quotes
            if (sval.indexOf("'") != -1 || sval.indexOf(" ") != -1)
                result = sval;
            else {
                if (sval.startsWith("\"")) {
                    result = sval.substring(1, sval.length() - 1);
                } else {
                    result = sval;
                }
            }
        }
        return result;
    }
}
