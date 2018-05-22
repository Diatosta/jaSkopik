package jaSkopik.Types;

public enum SkopikDataType {
    // Not a skopik data type
    // Does not necessarily mean it's invalid
    None(0),

    /*
        Object types
     */

    Null(1 << 0),
    Scope(1 << 1),
    Array(1 << 2),
    Tuple(1 << 3),

    // Special case, cause Java
    ArrayANDTuple(1 << 2 | 1 << 3),

    String(1 << 4),

    /*
        Special types
     */

    Keyword(1 << 5),
    Operator(1 << 6),
    Reserved(1 << 7),

    /*
        Number types
     */

    Boolean(1 << 8),
    Integer(1 << 9),
    Float(1 << 10),
    Double(1 << 11),

    /*
        Number flags
     */
    Signed(1 << 12),
    Unsigned(1 << 13),
    Long(1 << 14),
    BitField(1 << 15),
    NumberFlagMask(1 << 12 | 1 << 13 | 1 << 15 | 1 << 14),

    /*
        Composite number types
     */

    Binary(1 << 9 | 1 << 15),
    Integer32(1 << 9 | 1 << 12),
    Integer64(1 << 9 | 1 << 12 | 1 <<14),
    UInteger32(1 << 9 | 1 << 13),
    UInteger64(1 << 9 | 1 << 13 | 1 << 14),

    /*
        Composite operator types
     */

    OpStmtAssignmt(1 << 16 | 1 << 6),
    OpStmtBlock(1 << 17 | 1 << 6),

    OpBlockDelim(1 << 18 | 1 << 6),
    OpBlockOpen(1 << 19 | 1 << 6),
    OpBlockClose(1 << 20 | 1 << 6),

    OpScopeDelim(1 << 1 | (1 << 18 | 1 << 6)),
    OpScopeOpen(1 << 1 | (1 << 19 | 1 << 6)),
    OpScopeClose(1 << 1 | (1 << 20 | 1 << 6)),

    OpArrayDelim(1 << 2 | (1 << 18 | 1 << 6)),
    OpArrayOpen(1 << 2 | (1 << 19 | 1 << 6)),
    OpArrayClose(1 << 2 | (1 << 20 | 1 << 6)),

    OpTupleDelim(1 << 3 | (1 << 18 | 1 << 6)),
    OpTupleOpen(1 << 3 | (1 << 19 | 1 << 6)),
    OpTupleClose(1 << 3 | (1 << 20 | 1 << 6));

    public int id;

    SkopikDataType(int id){
        this.id = id;
    }
}
