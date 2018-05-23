package jaSkopik;

import jaSkopik.Types.*;

import javax.naming.OperationNotSupportedException;
import java.io.*;
import java.util.BitSet;

// Internal class for reading .skop files quickly
class SkopikReader implements Closeable {
    protected TokenReader Reader;
    protected TokenReader getReader(){
        return Reader;
    }

    @Override
    public void close() throws IOException {
        if(Reader != null){
            Reader.close();
        }
    }

    public ISkopikObject ReadBlock(int type, String name) throws OperationNotSupportedException{
        if(name == null){
            name = "";
        }

        if(type == SkopikDataType.OpArrayOpen.id){
            return ReadArray(name);
        }
        else if(type == SkopikDataType.OpScopeOpen.id){
            return ReadScope(name);
        }
        else if(type == SkopikDataType.OpTupleOpen.id){
            return ReadTuple(name);
        }

        throw new OperationNotSupportedException("ReadBlock() -- could not determine block type to load!");
    }

    public ISkopikObject ReadObject(ISkopikBlock parent) throws IllegalArgumentException, OperationNotSupportedException {
        if(parent == null){
            throw new IllegalArgumentException("Parent cannot be null.");
        }

        String token = Reader.ReadToken();
        int dataType = SkopikDataType.None.id;

        try{
            dataType = Skopik.GetAnyDataType(token);
        }
        catch (Exception e){
            throw new OperationNotSupportedException("ReadObject() -- horrific failure on line " + Reader.CurrentLine() + ", couldn't parse '" + token + "'.");
        }

        if(dataType == SkopikDataType.None.id)
            System.out.println("ReadObject() -- Couldn't determine data type @ line " + Reader.CurrentLine() + ": '" + token + "'.");

        // Check for either strings or named scopes (e.g. ' "Inlined scope" : { ... } '
        if(dataType == SkopikDataType.String.id){
            String strValue = SkopikHelper.stripQuotesFromString(token);

            if(!Reader.EndOfLine()){
                // Peek for the scope assignment operator
                String nextToken = Reader.PeekToken();

                if(Skopik.IsScopeBlockOperator(nextToken)){
                    Reader.PopToken(0);

                    // No need to peek for this one
                    nextToken = Reader.ReadToken();

                    // Named scopes
                    if(!Skopik.IsOpeningBrace(nextToken, SkopikDataType.None))
                        throw new OperationNotSupportedException("ReadObject() -- malformed data on line " + Reader.CurrentLine() + ".");

                    // Wrap in single quotes
                    String scopeName = SkopikHelper.stripQuotesFromString(token);

                    return ReadBlock(Skopik.GetDataType(nextToken), scopeName);
                }
            }

            return SkopikFactory.CreateValue(strValue);
        }

        if(dataType == SkopikDataType.Keyword.id)
            System.out.println("Unknown data @ line " + Reader.CurrentLine() + ": '" + token + "'");

        if(dataType == SkopikDataType.Boolean.id)
            return SkopikFactory.CreateValue(token);

        if(dataType == SkopikDataType.Null.id)
            return SkopikObject.Null();

        // Anonymous scope/array?
        if(Skopik.IsOpeningBrace(token, SkopikDataType.None))
            return ReadBlock(Skopik.GetDataType(token), "");

        if(Skopik.IsDecimalNumberValue(dataType)){
            String decimalToken = Skopik.SanitizeNumber(token, false);

            if(SkopikHelper.IsNullOrEmpty(decimalToken)){
                throw new OperationNotSupportedException("ReadObject() -- could not sanitize decimal token '" + token + "' on line " + Reader.CurrentLine() + ".");
            }

            if(dataType == SkopikDataType.Float.id){
                return SkopikFactory.CreateValue(decimalToken);
            }
            else if(dataType == SkopikDataType.Double.id){
                return SkopikFactory.CreateValue(decimalToken);
            }
        }

        if(Skopik.IsNumberValue(dataType)){
            boolean isNegative = Skopik.IsNegativeNumber(token);
            int strIndex = 0;

            if((dataType & SkopikDataType.BitField.id) != 0){
                if((dataType & (SkopikDataType.NumberFlagMask.id & ~SkopikDataType.BitField.id)) != 0)
                    throw new OperationNotSupportedException("ReadObject() -- invalid binary token '" + token + "' on line " + Reader.CurrentLine() + ".");

                if(isNegative)
                    strIndex += 1;

                String binaryToken = Skopik.SanitizeNumber(token.substring(strIndex), false);

                if(SkopikHelper.IsNullOrEmpty(binaryToken))
                    throw new OperationNotSupportedException("ReadObject() -- could not sanitize binary token '" + token + "' in line " + Reader.CurrentLine() + ".");

                BitSet bits;

                if(isNegative){
                    int val = Integer.parseInt(binaryToken, 2);

                    if(isNegative)
                        val = -val;

                    // Convert the int to a BitSet
                    bits = BitSet.valueOf(new long[]{val});
                }
                else{
                    int val = Integer.parseUnsignedInt(binaryToken, 2);

                    bits = BitSet.valueOf(new long[]{val});
                }

                return SkopikFactory.CreateValue(bits);
            }
            else{
                // Don't know what happened!

                boolean isHex = Skopik.IsHexadecimalNumber(token);

                if(isHex){
                    if(isNegative){
                        strIndex += 1;
                    }

                    strIndex += 2;
                }

                String numberToken = Skopik.SanitizeNumber(token.substring(strIndex), isHex);

                if(SkopikHelper.IsNullOrEmpty(numberToken))
                    throw new OperationNotSupportedException("ReadObject() -- could not sanitize number token '" + token + "' on line " + Reader.CurrentLine() + ".");

                int numberBase = (!isHex) ? 10 : 16;

                if(dataType == SkopikDataType.Integer32.id){
                    int val = Integer.parseInt(numberToken, numberBase);

                    if(isHex && isNegative)
                        val = -val;

                    return SkopikFactory.CreateValue(val);
                }
                else if(dataType == SkopikDataType.Integer64.id){
                    int val = Integer.parseInt(numberToken, numberBase);

                    if(isHex && isNegative)
                        val = -val;

                    return SkopikFactory.CreateValue(val);
                }
                else if(dataType == SkopikDataType.UInteger32.id){
                    int val = Integer.parseUnsignedInt(numberToken, numberBase);

                    if(isHex && isNegative)
                        val = -val;

                    return SkopikFactory.CreateValue(val);
                }
                else if(dataType == SkopikDataType.UInteger64.id){
                    int val = Integer.parseUnsignedInt(numberToken, numberBase);

                    if(isHex && isNegative)
                        val = -val;

                    return SkopikFactory.CreateValue(val);
                }

                throw new OperationNotSupportedException("ReadObject() -- the resulting value for '" + token + "' on line " + Reader.CurrentLine() + " was null.");
            }
        }

        // We couldn't determine the data type, but let's not break the parser!
        return SkopikObject.Null();
    }

    public ISkopikObject ReadStatement(ISkopikBlock parent) throws OperationNotSupportedException{
        if(parent == null)
            throw new IllegalArgumentException("Parent cannot be null.");

        ISkopikObject obj = SkopikObject.Null();

        String name = Reader.ReadToken();

        if(!Reader.EndOfLine()){
            String op = Reader.PeekToken();

            if(Skopik.IsScopeBlockOperator(op)){
                Reader.PopToken(0);

                op = Reader.ReadToken();

                // Named scopes
                if(Skopik.IsOpeningBrace(op, SkopikDataType.None)){
                    name = SkopikHelper.stripQuotesFromString(name);

                    obj = ReadBlock(Skopik.GetDataType(op), name);
                }
                else{
                    throw new OperationNotSupportedException("ReadObject() -- malformed data on line " + Reader.CurrentLine() + ".");
                }
            }
            else{
                if(Skopik.IsAssignmentOperator(op)){
                    // Move past assignment operator
                    Reader.PopToken(0);
                }
                else{
                    // Move back to the beginning
                    Reader.Seek(-1);
                }

                // Try reading the object normally
                obj = ReadObject(parent);
            }

            if(!Reader.EndOfLine()){
                String nextToken = Reader.PeekToken();

                // Move to next statement
                if(Skopik.IsScopeSeparator(nextToken))
                    Reader.PopToken(0);
                if(Tokenizer.IsCommentLine(nextToken))
                    Reader.NextLine();
            }
        }

        if(parent instanceof SkopikScope){
            // Add to the parent scope as a variable
            ((SkopikScope) parent).getEntries().put(name, obj);
        }

        return obj;
    }

    public SkopikArray ReadArray(String arrayName) throws OperationNotSupportedException{
        SkopikArray array = new SkopikArray(arrayName);

        int maxIndex = -1;

        while(!Reader.EndOfStream(getReader().getBufferedReader())){
            String token = Reader.ReadToken();

            if(SkopikHelper.IsNullOrEmpty(token))
                continue;

            ISkopikObject obj = null;

            int index = (maxIndex + 1);
            boolean hasExplicitIndex = false;

            // Explicit index?
            if(Skopik.IsOpeningBrace(token, SkopikDataType.None)){
                if(Reader.FindNextPattern(new String[] { "]", ":" }) != -1){
                    String nextToken = Reader.ReadToken();
                    int dataType = Skopik.GetNumberDataType(nextToken);

                    int strIndex = 0;

                    if(dataType != SkopikDataType.Integer32.id)
                        throw new OperationNotSupportedException("ReadArray() -- invalid explicit indice definition on line " + Reader.CurrentLine() + ".");

                    boolean isHex = Skopik.IsHexadecimalNumber(nextToken);

                    if(isHex)
                        strIndex += 2;

                    nextToken = Skopik.SanitizeNumber(nextToken.substring(strIndex), isHex);

                    try{
                        index = Integer.parseInt(nextToken);
                    }
                    catch(Exception ex){
                        throw new OperationNotSupportedException("ReadArray() -- error parsing int on line " + Reader.CurrentLine() + "!");
                    }

                    if(index < 0)
                        throw new OperationNotSupportedException("ReadArray() -- negative explicit indice on line " + Reader.CurrentLine() + ".");
                    if(index <= maxIndex)
                        throw new OperationNotSupportedException("ReadArray() -- explicit indice on line " + Reader.CurrentLine() + " is less than the highest index.");

                    // Move past the indice definition
                    Reader.PopToken(1);

                    hasExplicitIndex = true;
                }
            }

            // End of current array?
            if(Skopik.IsClosingBrace(token, SkopikDataType.Array))
                break;

            if(hasExplicitIndex){
                // Add null entries if needed
                for(int i = (maxIndex + 1); i < index; i++){
                    array.getEntries().add(i, SkopikObject.Null());
                }
            }
            else{
                // Move back to the beginning
                Reader.Seek(-1);
            }

            obj = ReadStatement(array);

            // Stop if there's nothing left to read
            if(obj == null)
                break;

            array.getEntries().add(index, obj);

            if(!Reader.EndOfLine()){
                String op = Reader.PeekToken();

                // Increase highest index
                if(Skopik.IsArraySeparator(op)){
                    Reader.PopToken(0);
                    maxIndex = index;
                }
            }
        }

        return array;
    }

    public SkopikScope ReadScope(String scopeName) throws OperationNotSupportedException{
        SkopikScope scope = new SkopikScope(scopeName);

        while(!Reader.EndOfStream(getReader().getBufferedReader())){
            String token = Reader.ReadToken();

            if(SkopikHelper.IsNullOrEmpty(token))
                continue;

            ISkopikObject obj = null;

            // End of current scope?
            if(Skopik.IsClosingBrace(token, SkopikDataType.Scope))
                break;

            if(Skopik.IsClosingBrace(token, SkopikDataType.None))
                throw new OperationNotSupportedException("Unexpected brace in scope in line " + Reader.CurrentLine() + ".");

            // Move to beginning of statement
            Reader.Seek(-1);

            obj = ReadStatement(scope);

            // Stop if there's nothing left to read
            if(obj == null)
                break;
        }

        return scope;
    }

    public SkopikTuple ReadTuple(String name) throws OperationNotSupportedException{
        SkopikDataType lastType = SkopikDataType.None;

        SkopikArray tempArray = new SkopikArray("<temp>");

        ISkopikBlock block = tempArray;

        SkopikTuple tuple = null;

        while (!Reader.EndOfStream(getReader().getBufferedReader())){
            String token = Reader.ReadToken();

            if(SkopikHelper.IsNullOrEmpty(token))
                continue;

            ISkopikObject obj = null;

            // End of current tuple?
            if(Skopik.IsClosingBrace(token, SkopikDataType.Tuple))
                break;

            // Check for braces we weren't expecting
            if(Skopik.IsOpeningBrace(token, SkopikDataType.None) || Skopik.IsClosingBrace(token, SkopikDataType.None))
                throw new OperationNotSupportedException("Unexpected brace in tuple on line " + Reader.CurrentLine() + ".");

            // Move back to the beggining
            Reader.Seek(-1);

            obj = ReadStatement(block);

            // Stop if there's nothing left to read
            if(obj == null)
                break;

            if(lastType != SkopikDataType.None){
                if(obj.getDataType() != lastType)
                    throw new OperationNotSupportedException("Error reading tuple on line " + Reader.CurrentLine() + ": Type mismatch!");
            }
            else{
                // Setup the tuple
                lastType = obj.getDataType();

                tuple = new SkopikTuple(lastType, name);
                block = tuple;
            }

            tuple.getEntries().add(obj);

            if(!Reader.EndOfLine()){
                String op = Reader.PeekToken();

                if(Skopik.IsDelimiter(op, SkopikDataType.Tuple))
                    Reader.PopToken(0);
            }
        }

        return tuple;
    }

    public SkopikReader(InputStream stream) throws IllegalArgumentException, OperationNotSupportedException{
        if(stream == null)
            throw new IllegalArgumentException("Stream cannot be null.");

        Reader targetStream = new InputStreamReader(stream);

        Reader = new TokenReader(targetStream);
    }
}
