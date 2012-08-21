/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package update2.token;

/**
 *
 * @author elisha
 */
public class ParseException extends Exception {
    ParseErrorCode code = ParseErrorCode.UNKNOWN_ERROR;

    public ParseException() {
    }

    public ParseException(String message) {
        super(message);
    }

    public ParseException(String message, ParseErrorCode code) {
        super(message);
        this.code = code;
    }

    @Override
    public String toString() { return code.name() + " " + super.toString(); }



    public static enum ParseErrorCode {
        PARENTHETICAL_ERROR,
        UNRECOGNIZED_SYMBOL,
        INVALID_OPERATOR_POSITION,
        ENDED_EARLY,
        UNKNOWN_ERROR
    };
}
