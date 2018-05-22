package jaSkopik;

import javax.naming.OperationNotSupportedException;
import java.io.*;
import java.util.ArrayList;

public class TokenReader implements Closeable {
    private int m_line = 0;

    private int m_tokenIndex = 0;
    private String[] m_tokenBuffer;

    protected  boolean IsBufferEmpty() {
        return (m_tokenBuffer == null || (m_tokenBuffer.length == 0));
    }

    protected FileReader Reader;
    protected BufferedReader bufferedReader;

    public void setReader(FileReader reader) {
        this.Reader = reader;
    }

    public FileReader getReader() {
        return this.Reader;
    }

    public void setBufferedReaderReader(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }

    public BufferedReader getBufferedReader() {
        return this.bufferedReader;
    }

    public int CurrentLine() {
        return m_line;
    }

    public boolean EndOfLine() {
        return (m_tokenBuffer != null) ? (m_tokenIndex >= m_tokenBuffer.length) : true;
    }

    public boolean EndOfStream(BufferedReader br){
        boolean result = true;

        try{
            result = !br.ready();
        }
        catch (IOException e){
            System.err.println(e);
        }

        return result;
    }

    public int TokenIndex() {
        return m_tokenIndex;
    }

    public int TokenCount() {
        return (m_tokenBuffer != null) ? m_tokenBuffer.length : -1;
    }

    @Override
    public void close() throws IOException {
        if(bufferedReader != null){
            bufferedReader.close();
        }
    }

    /**
     * Reads in the tokens on the next line and returns the number of tokens loaded.
     *
     * @returns The number of tokens parsed, otherwise -1 if end of stream reached.
     */
    protected int ReadInTokens() {
        try{
            String line;
            while(!EndOfStream(bufferedReader)){
                // Read in the next line of tokens
                line = bufferedReader.readLine();
                int startLine = ++m_line;

                if(SkopikHelper.IsNullOrWhiteSpace(line)){
                    continue;
                }

                // Read the next line of safe to do so
                if(Tokenizer.IsCommentLine(line))
                    continue;

                // End of multi-line comments at beginning of lines break parser,
                // so we need hacks unfortunately
                if(line.startsWith(Skopik.CommentBlockCloseKey)){
                    String buf = line.substring(2);

                    ArrayList<String> tokens = new ArrayList<>();
                    tokens.add(line.substring(0, 2));

                    for(String token : Tokenizer.SplitTokens(buf)){
                        tokens.add(token);
                    }

                    // Convert the ArrayList() to String[]
                    m_tokenBuffer = tokens.stream().toArray(String[]::new);
                }
                else{
                    // Split them up into the token buffer and reset the index
                    m_tokenBuffer = Tokenizer.SplitTokens(line);
                }

                m_tokenIndex = 0;

                // Return number of tokens brought in
                return m_tokenBuffer.length;

                // Read the next line if safe to do so
            }
        }
        catch(IOException ex){
            System.out.println(ex);
        }

        // End of stream
        return -1;
    }

    protected boolean CheckToken(int tokenIndex){
        // Verifies index into the buffer is accessible
        if(!IsBufferEmpty())
            return (tokenIndex < m_tokenBuffer.length);

        return false;
    }

    public boolean NextLine(){
        // Try filling in the buffer
        // won't affect token index if it fails
        return (ReadInTokens() != -1);
    }

    public String GetToken(int index){
        if(CheckToken(index))
            return (m_tokenBuffer[index]);

        // Failed to get token :(
        return null;
    }

    public String PopToken(int offset) throws OperationNotSupportedException {
        m_tokenIndex += offset;

        // Don't let the user pop too many values
        if(m_tokenIndex < 0){
            throw new OperationNotSupportedException("PopToken() -- offset caused negative index, too many values popped!");
        }

        return GetToken(m_tokenIndex++);
    }

    private String ReadTokenInternal(){
        String token = null;

        while(token == null){
            if(EndOfLine()){
                // Don't proceed any further
                if(EndOfStream(bufferedReader))
                    return null;

                NextLine();
            }

            token = GetToken(m_tokenIndex++);
        }

        return token;
    }

    /**
     * Reads the next valid token from the buffer and increments the token index
     *
     * @returns The next valid token from the buffer; otherwise, null
     */
    public String ReadToken() throws OperationNotSupportedException{
        String token = ReadTokenInternal();

        if(Tokenizer.IsCommentBlock(token) == 0){
            if(MatchToken(Tokenizer.CommentBlockKeys[1], Tokenizer.CommentBlockKeys[0]))
                token = ReadTokenInternal();
        }

        return token;
    }

    /**
     * Gets the next token from the buffer without incrementing the token index or filling the buffer
     *
     * @returns The next token from the buffer; otherwise, null
     */
    public String PeekToken(){
        return GetToken(m_tokenIndex);
    }

    public String PeekToken(int offset){
        return GetToken(m_tokenIndex + offset);
    }

    public boolean Seek(int offset) throws OperationNotSupportedException {
        m_tokenIndex += offset;

        if(m_tokenIndex < 0)
            throw new OperationNotSupportedException("Seek() -- offset caused negative index!");

        return CheckToken(m_tokenIndex);
    }

    public int FindPattern(String[] tokens, int index) throws OperationNotSupportedException {
        if(EndOfStream(bufferedReader))
            throw new OperationNotSupportedException("GetTokensIndex() -- end of stream exception.");

        // Do not look past the end of the line
        // the user may decide to move to the next line if necessary
        if(EndOfLine() || ((index + tokens.length) >= m_tokenBuffer.length))
            return -1;

        // Index into the tokens we're looking for
        int tokenIndex = 0;

        // Iterate through the tokens available in the buffer
        for(int i = index; i < m_tokenBuffer.length; i++){
            if(m_tokenBuffer[i] == tokens[tokenIndex]){
                // Stop when the pattern is found
                if((tokenIndex + 1) == tokens.length){
                    /*
                        if "BAZ" in "FOOBARBAZ":
                            tokenIndex = 2
                            i = 8
                        therefore:
                            tokensIndex = 6
                     */
                    return (i - tokenIndex);
                }

                ++tokenIndex;
            }
            else{
                // Reset the token index if needed
                if(tokenIndex > 0)
                    tokenIndex = 0;
            }
        }

        return -1;
    }

    public int FindNextPattern(String[] tokens) throws OperationNotSupportedException{
        return FindPattern(tokens, m_tokenIndex);
    }

    public boolean MatchToken(String matchToken, String nestedToken) throws OperationNotSupportedException {
        int startLine = CurrentLine();

        // TODO: Fix this?
        if(nestedToken.length() != matchToken.length())
            throw new OperationNotSupportedException("MatchToken() -- length of nested token isn't equal to the match token.");

        // Did we find the match?
        boolean match = false;
        String token = "";

        while(!match && (token = ReadTokenInternal()) != null){
            if(token.equals(matchToken)){
                match = true;
                break;
            }
            else if(token.equals(nestedToken)){
                int nestLine = CurrentLine();

                // Nested blocks
                if(!MatchToken(matchToken, nestedToken))
                    throw new OperationNotSupportedException("MatchToken() -- nested token '" + nestedToken + "' on line " + nestLine + " wasn't closed before the original token '" + matchToken + "' on line " + startLine + ".");
            }
            else if(CurrentLine() > startLine){
                // Multi-line match
                NextLine();
            }
        }

        return match;
    }

    public TokenReader(Reader stream) throws OperationNotSupportedException {
        bufferedReader = new BufferedReader(stream);

        if(ReadInTokens() == -1)
            throw new OperationNotSupportedException("Failed to create TokenReader -- could not read in tokens.");
    }
}
