package jaSkopik;

import java.util.ArrayList;

public class Tokenizer {
    public static final String CommentLineKey = "//";
    public static final String[] CommentBlockKeys = {"/*", "*/"};

    public static boolean IsCommentLine(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Argument cannot be null");
        }

        if (value.length() < CommentLineKey.length()) {
            return false;
        }

        for (int i = 0; i < CommentLineKey.length(); i++) {
            if(value.charAt(i) != CommentLineKey.charAt(i)){
                return false;
            }
        }

        return true;
    }

    public static int IsCommentBlock(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Argument cannot be null");
        }

        int strLen = value.length();

        for (int i = 0; i < CommentBlockKeys.length; i++) {
            String cb = CommentBlockKeys[i];

            if (strLen < cb.length()) {
                continue;
            }

            boolean match = true;

            for (int j = 0; j < cb.length(); j++) {
                if (value.charAt(j) != cb.charAt(j)) {
                    match = false;
                }
            }

            if (match) {
                return i;
            }
        }

        return -1;
    }

    public static String[] SplitTokens(String str) {
        if (str == null) {
            throw new IllegalArgumentException("Argument cannot be null");
        }

        if (str.length() < 1) {
            return new String[]{str};
        }

        ArrayList<String> values = new ArrayList<>(32);

        int start = 0;
        int length = 0;

        boolean stringOpen = false;
        boolean stringEscaped = false;

        boolean commentOpen = false;

        for (int i = 0; i < str.length(); i++) {
            Character c = str.charAt(i);
            int flags = CharUtils.GetCharFlags(c);

            // Break on null
            if ((flags & CharUtils.CharacterTypeFlags.Null.id) != 0)
                break;

            if ((flags & CharUtils.CharacterTypeFlags.TabOrWhitespace.id) != 0) {
                // Process tabs/whitespace outside of strings/comments
                if (!stringOpen) {
                    if (!commentOpen) {
                        if (length > 0) {
                            values.add(str.substring(start, start + length));
                        }

                        start = (i + 1); // "ABC|  DEF" -> "ABC | DEF" -> "ABC  |DEF"
                        length = 0;
                    }
                    continue;
                }
            }

            // Check for inline comments
            if (((flags & CharUtils.CharacterTypeFlags.Alphanumerical.id) == 0) && ((i + 2) < str.length())) {
                String tok = str.substring(i, i + 2);
                int cbType = IsCommentBlock(tok);

                boolean isCommentLine = false;
                boolean isCommentBlock = false;

                switch (cbType) {
                    case -1:
                        if (!commentOpen && IsCommentLine(tok))
                            isCommentLine = true;
                        break;

                    case 0:
                        isCommentBlock = true;
                        commentOpen = true;
                        break;

                    case 1:
                        isCommentBlock = true;
                        commentOpen = false;
                        break;
                }

                if (isCommentLine)
                    break;

                if (isCommentBlock) {
                    // Add the token separately
                    values.add(tok);

                    // Move ahead 2 spaces
                    i += 2;

                    start = i;
                    length = 0;

                    continue;
                }
            }

            if (commentOpen)
                continue;

            if ((flags & CharUtils.CharacterTypeFlags.ExtendedOperators.id) != 0) {
                if (!stringOpen) {
                    if (length > 0)
                        values.add(str.substring(start, start + length));

                    values.add(c.toString());

                    start = (i + 1);
                    length = 0;

                    continue;
                }
            }

            // Increase string length
            ++length;

            if ((flags & CharUtils.CharacterTypeFlags.Quote.id) != 0) {
                if (stringOpen) {
                    if (stringEscaped) {
                        stringEscaped = false;
                    } else {
                        // Complete the string (include last quote)
                        if (length > 0)
                            values.add(str.substring(start, start + length + 1));

                        start = (i + 1); // "ABC|" -> "ABC"|
                        length = 0;

                        stringOpen = false;
                    }
                } else {
                    start = i; // |"ABC"
                    length = 0;

                    stringOpen = true;
                }
            } else if (stringEscaped) {
                // Not an escape sequence
                stringEscaped = false;
                continue;
            }
            if (stringOpen && (c == '\\')) {
                stringEscaped = true;
                continue;
            }
        }

        // Final add
        if (length > 0 && !commentOpen)
            values.add(str.substring(start, start + length));

        // Convert the ArrayList() to String[]
        return values.stream().toArray(String[]::new);
    }
}