package jaSkopik;

import jaSkopik.Types.SkopikDataType;

import javax.naming.OperationNotSupportedException;

class Skopik {
    private static boolean m_lookupReady = false;
    private static int[] m_lookup = new int[128];

    static final String[] Keywords = { "true", "false", "null" };

    static final String CommentLineKey = "//";
    static final String CommentBlockOpenKey = "/*";
    static final String CommentBlockCloseKey = "*/";

    static final Character AssignmentKey = '=';
    static final Character ScopeBlockKey = ':'; // Opening of a scope block

    static final Character[] BlockKeys = {
            '{', '}',
            '[', ']',
            '(', ')'
    };

    static final Character[] OperatorKeys = { '@', '"', '\'' };
    static final Character[] DelimiterKeys = { ',', ';' };

    static final Character[] SuffixKeys = { 'b', 'd', 'f', 'u', 'U', 'L' };

    static final String HexadecimalPrefix = "0x";

    static final SkopikDataType[] WordLookup = {
            SkopikDataType.Boolean,     // 'true'
            SkopikDataType.Boolean,     // 'false'
            SkopikDataType.Null,        // 'null'
    };

    static final SkopikDataType[] BlockLookup = {
            SkopikDataType.Scope,      // '{}'
            SkopikDataType.Array,      // '[]'
            SkopikDataType.Tuple,      // '()'
    };

    static final SkopikDataType[] OperatorLookup = {
            SkopikDataType.Keyword,     // '@'
            SkopikDataType.String,      // ' " '
            SkopikDataType.String,      // ' ' '
    };

    static final SkopikDataType[] DelimiterLookup = {
            SkopikDataType.ArrayANDTuple,   // ','
            SkopikDataType.Scope,           // ';'
    };

    static final SkopikDataType[] SuffixLookup = {
            SkopikDataType.Binary,      // 'b'
            SkopikDataType.Double,      // 'd'
            SkopikDataType.Float,       // 'f'
            SkopikDataType.Unsigned,    // 'u'
            SkopikDataType.Unsigned,    // 'U'
            SkopikDataType.Long,        // 'L'
    };

    static void MapLookupTypes(){
        for(int i = 0; i < BlockKeys.length; i += 2){
            SkopikDataType opBlockType = BlockLookup[i >> 1];

            // Set open/close controls
            m_lookup[BlockKeys[i + 0]] = (opBlockType.id | SkopikDataType.OpBlockOpen.id);
            m_lookup[BlockKeys[i + 1]] = (opBlockType.id | SkopikDataType.OpBlockClose.id);
        }

        for(int i = 0; i < OperatorKeys.length; i++){
            m_lookup[OperatorKeys[i]] = OperatorLookup[i].id;
        }

        for(int i = 0; i < DelimiterKeys.length; i++){
            m_lookup[DelimiterKeys[i]] = (DelimiterLookup[i].id | SkopikDataType.OpBlockDelim.id);
        }

        for(int i = 0; i < SuffixKeys.length; i++){
            m_lookup[SuffixKeys[i]] = SuffixLookup[i].id;
        }

        m_lookup[AssignmentKey] = SkopikDataType.OpStmtAssignmt.id;
        m_lookup[ScopeBlockKey] = SkopikDataType.OpStmtBlock.id;
    }

    // TODO: give this a datatype to work with
    static String SanitizeNumber(String value, boolean isHex){
        int length = 0;

        for(Character c : value.toCharArray()){
            int flags = CharUtils.GetCharFlags(c);

            // Don't allow decimals/negative hexadecimal numbers
            // (negative hex numbers need to be processed at another point)
            if(!isHex){
                if(c == '.' || c == '-'){
                    ++length;
                    continue;
                }
            }
            if((flags & CharUtils.CharacterTypeFlags.Digit.id) != 0){
                ++length;
                continue;
            }
            if((flags & CharUtils.CharacterTypeFlags.Letter.id) != 0){
                // ABCDEF or abcdef?
                if(isHex && ((c & ~0x67) == 0)){
                    ++length;
                    continue;
                }

                int type = GetDataType(c);

                if(type != SkopikDataType.None.id){
                    // First suffix character, we can safely break
                    break;
                }
            }

            // Can't sanitize the string
            // reset the length and stop
            length = 0;
            break;
        }
        return (length > 0) ? value.substring(0, length) : "";
    }

    static int GetDataType(char value){
        if(!m_lookupReady){
            MapLookupTypes();
            m_lookupReady = true;
        }

        return m_lookup[value];
    }

    static int GetDataType(String value){
        return GetDataType(value.charAt(0));
    }

    static boolean IsDataType(char value, int dataType){
        int type = GetDataType(value);
        int checkType = dataType;

        return ((type & checkType) == checkType);
    }

    static int GetWordDataType(String value){
        for(int i = 0; i < Keywords.length; i++){
            String k = Keywords[i];

            if(value.length() != k.length())
                continue;

            if(value.equalsIgnoreCase(k))
                return WordLookup[i].id;
        }

        return SkopikDataType.None.id;
    }

    static int GetNumberDataType(String value) throws OperationNotSupportedException {
        if(value.length() < 1)
            return SkopikDataType.None.id;

        int strIndex = 0;

        boolean isNegative = false;
        boolean isHex = false;

        boolean hasExponent = false;
        boolean hasDigit = false;
        boolean hasSeparator = false;   // Floats

        int numberType = SkopikDataType.None.id;
        int suffixType = SkopikDataType.None.id;

        if(IsHexadecimalNumber(value)){
            strIndex += 2;
            isHex = true;

            if(strIndex == value.length())
                throw new OperationNotSupportedException("Malformed hexadecimal number data: '" + value + "'");
        }

        for(int i = strIndex; i < value.length(); i++){
            char c = value.charAt(i);
            int flags = CharUtils.GetCharFlags(c);

            if((flags & CharUtils.CharacterTypeFlags.Digit.id) != 0){
                hasDigit = true;
            }
            else if((flags & CharUtils.CharacterTypeFlags.Letter.id) != 0){
                suffixType |= GetDataType(c);

                if(suffixType == SkopikDataType.None.id){
                    // Check for exponential float
                    if(!isHex && ((c & ~0x65) == 0)){
                        if(hasExponent || (!hasDigit || (!hasDigit && !hasSeparator)))
                            throw new OperationNotSupportedException("Malformed number data: '" + value + "'");

                        hasExponent = true;
                    }
                }
            }
            else{
                if(c == '.'){
                    if(!hasDigit || hasSeparator)
                        throw new OperationNotSupportedException("Malformed number data: '" + value + "'");

                    hasSeparator = true;
                }
                else if(c == '-'){
                    if(hasExponent){
                        if(!hasDigit || (!hasDigit && !hasSeparator))
                            throw new OperationNotSupportedException("Malformed number data: '" + value + "'");

                        // Exponential float
                        // just continue normally
                    }
                    else{
                        if(isNegative || hasDigit || hasSeparator)
                            throw new OperationNotSupportedException("Malformed number data: '" + value + "'");

                        // Negative number
                        isNegative = true;
                    }
                }
            }
        }

        // Figure out the number type
        if(suffixType == SkopikDataType.None.id){
            // Setup the default value if we couldn't figure it out above
            if(hasSeparator){
                if(hasExponent)
                    System.out.println("Successfully detected an exponential decimal: '" + value + "'");

                numberType |= SkopikDataType.Double.id;
            }
            else{
                numberType |= SkopikDataType.Integer32.id;
            }
        }
        else{
            if((suffixType & (SkopikDataType.Float.id | SkopikDataType.Double.id)) != 0)
                numberType |= (suffixType & (SkopikDataType.Float.id | SkopikDataType.Double.id));

            if((suffixType & SkopikDataType.NumberFlagMask.id) != 0){
                numberType |= SkopikDataType.Integer.id;

                // Binary data has no sign
                if((suffixType & SkopikDataType.Binary.id) == 0){
                    // Signed?
                    if((suffixType & SkopikDataType.Unsigned.id) == 0)
                        numberType |= SkopikDataType.Signed.id;
                }

                numberType |= (suffixType & (SkopikDataType.NumberFlagMask.id));
            }
        }

        // Can still be invalid!
        return numberType;
    }

    static int GetAnyDataType(String value) throws OperationNotSupportedException{
        int dataType = SkopikDataType.None.id;

        if(value.length() != 1){
            // Word data?
            dataType = GetWordDataType(value);
        }

        // Control flow data?
        if(dataType == SkopikDataType.None.id)
            dataType = GetDataType(value.charAt(0));

        // Number data?
        if(dataType == SkopikDataType.None.id)
            dataType = GetNumberDataType(value);

        // May still be invalid
        return dataType;
    }

    static boolean IsAssignmentOperator(String value){
        return IsDataType(value.charAt(0), SkopikDataType.OpStmtAssignmt.id);
    }

    static boolean IsScopeBlockOperator(String value){
        return IsDataType(value.charAt(0), SkopikDataType.OpStmtBlock.id);
    }

    static boolean IsEndStatementOperator(String value){
        return IsDataType(value.charAt(0), SkopikDataType.OpBlockDelim.id);
    }

    static boolean IsArraySeparator(String value){
        return IsDataType(value.charAt(0), SkopikDataType.OpArrayDelim.id);
    }

    static boolean IsScopeSeparator(String value)
    {
        return IsDataType(value.charAt(0), SkopikDataType.OpScopeDelim.id);
    }

    static boolean IsDelimiter(String value, SkopikDataType subType)
    {
        if(subType == null)
            subType = SkopikDataType.None;

        return IsDataType(value.charAt(0), subType.id | SkopikDataType.OpBlockDelim.id);
    }

    static boolean IsOpeningBrace(String value, SkopikDataType subType)
    {
        if(subType == null)
            subType = SkopikDataType.None;

        return IsDataType(value.charAt(0), subType.id | SkopikDataType.OpBlockOpen.id);
    }

    static boolean IsClosingBrace(String value, SkopikDataType subType)
    {
        if(subType == null)
            subType = SkopikDataType.None;

        char character = value.charAt(0);
        return IsDataType(character, subType.id | SkopikDataType.OpBlockClose.id);
    }

    static boolean IsNegativeNumber(String value)
    {
        return ((value.length() > 1) && (value.charAt(0) == '-'));
    }

    static boolean IsHexadecimalNumber(String value)
    {
        if (value.length() < HexadecimalPrefix.length())
            return false;

        var strIndex = 0;

        if (IsNegativeNumber(value))
            ++strIndex;

        for (int i = 0; i < HexadecimalPrefix.length(); i++)
        {
            if (value.charAt(strIndex + i) != HexadecimalPrefix.charAt(i))
                return false;
        }

        return true;
    }

    static boolean IsNumberValue(int dataType)
    {
        return ((dataType & SkopikDataType.Integer.id) != 0);
    }

    static boolean IsDecimalNumberValue(int dataType)
    {
        return ((dataType & (SkopikDataType.Float.id | SkopikDataType.Double.id)) != 0);
    }
}
