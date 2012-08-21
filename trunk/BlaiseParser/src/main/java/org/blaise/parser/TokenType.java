/*
 * TokenType.java
 * Created Nov 2009
 */
package org.blaise.parser;

/**
 * <p>
 *  Describes the type of token and where it fits in the tree.
 * </p>
 * @author Elisha Peterson
 */
enum TokenType {

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
    /** Whether the type can be followed by an n-ary operator */
    public boolean naryFollow;

    TokenType(boolean canStartPhrase, boolean naryFollow) {
        this.canStartPhrase = canStartPhrase;
        this.naryFollow = naryFollow;
    }
}
