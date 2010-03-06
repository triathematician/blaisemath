/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package update2.token;

/**
 * Describes the type of token and where it fits in the tree.
 * @author elisha
 */
public enum TokenType {

    IDENTIFIER(true, true),
    NUMBER(true, true),
    FUNCTION(true, false),
    PARENTHETICAL_OPEN(true, false),
    PARENTHETICAL_CLOSE(false, true),
    UNARY_OPERATOR(true, false),
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
