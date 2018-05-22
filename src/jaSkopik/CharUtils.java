package jaSkopik;

public class CharUtils {
    enum CharacterTypeFlags {
        Null(1 << 0),
        NewLine(1 << 1),
        Tab(1 << 2),
        Whitespace(1 << 3),
        TabOrWhitespace(1 << 2 | 1 << 3),
        Digit(1 << 4),
        Lowercase(1 << 5),
        Uppercase(1 << 6),
        Quote(1 << 7),
        Letter(1 << 5 | 1 << 6),
        Alphanumerical((1 << 5 | 1 << 6) | 1 << 4),
        EndOfToken(1 << 0 | 1 << 1 | (1 << 2 | 1 << 3)),
        Control(1 << 8),
        Operator(1 << 9),
        Separator(1 << 10),
        OpenBrace(1 << 11),
        CloseBrace(1 << 12),
        Brace(1 << 11 | 1 << 12),
        ExtendedOperators((1 << 9) | (1 << 10) | ((1 << 11 | 1 << 12))),
        Unknown(1 << 15);

        int id;

        CharacterTypeFlags(int id){
            this.id = id;
        }
    }

    // Fast lookup
    static boolean IsLookupReady = false;
    static int[] LookupTypes = new int[128];

    static void MapLookupTypes(){
        // Map once
        if(IsLookupReady){
            return;
        }

        for(int i = 0; i < LookupTypes.length; i++){
            int type = 0;

            if(i >= 127)
                type |= CharacterTypeFlags.Unknown.id;

            if(i >= 'a' && i <= 'z')
                type |= CharacterTypeFlags.Lowercase.id;

            if(i >= 'A' && i <= 'Z')
                type |= CharacterTypeFlags.Uppercase.id;

            if(i >= '0' && i <= '9')
                type |= CharacterTypeFlags.Digit.id;

            if(i >= 1 && i <= 31)
                type |= CharacterTypeFlags.Control.id;

            if(i == ' ')
                type |= CharacterTypeFlags.Whitespace.id;

            if(i == '\t')
                type |= CharacterTypeFlags.Tab.id;

            if(i == '\0')
                type |= CharacterTypeFlags.Null.id;

            switch(i){
                case '"':
                    type |= CharacterTypeFlags.Quote.id;
                    break;

                case '\r': case '\n':
                    type |= CharacterTypeFlags.NewLine.id;
                    break;

                case '$': case '@':
                    type |= CharacterTypeFlags.Operator.id;
                    break;

                case ',': case ';':
                    type |= CharacterTypeFlags.Separator.id;
                    break;

                case '(': case '{': case '[':
                    type |= CharacterTypeFlags.OpenBrace.id;
                    break;

                case ')': case '}': case ']':
                    type |= CharacterTypeFlags.CloseBrace.id;
                    break;
            }

            LookupTypes[i] = type;
        }

        IsLookupReady = true;
    }

    static int GetCharFlags(int value){
        MapLookupTypes();
        return ((value >= 0) && (value <= 127)) ? LookupTypes[value] : CharacterTypeFlags.Unknown.id;
    }

    static boolean HasCharFlags(int value, CharacterTypeFlags charFlags){
        return ((GetCharFlags(value) & charFlags.id) != 0);
    }
}
