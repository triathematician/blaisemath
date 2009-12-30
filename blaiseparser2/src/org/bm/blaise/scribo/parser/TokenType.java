/*
 * TokenType.java
 * Created Nov 2009
 */
package org.bm.blaise.scribo.parser;

/**
 * <p>
 *  Describes the type of token and where it fits in the tree.
 * </p>
 * @author Elisha Peterson
 */
public enum TokenType {

    IDENTIFIER(true, true),
    NUMBER(true, true),
    FUNCTION(true, false),
    PARENTHETICAL_OPEN(true, false),
    PARENTHETICAL_CLOSE(false, true),
    PRE_UNARY_OPERATOR(true, false),
    POST_UNARY_OPERATOR(false, false),
    BINARY_OPERATOR(false, false),
    MULTARY_OPERATOR(false, false);
    
    /** Whether the type makes sense at the beginning of a phrase */
    public boolean canStartPhrase;
    /** Whether the type can be followed by a binary operator */
    public boolean binaryFollow;

    TokenType(boolean canStartPhrase, boolean binaryFollow) {
        this.canStartPhrase = canStartPhrase;
        this.binaryFollow = binaryFollow;
    }
}
