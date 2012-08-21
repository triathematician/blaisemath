/*
 * ParseException.java
 * Created Nov 2009
 */

package org.blaise.parser;

/**
 * <p>
 *  Basic exception handling for parsing string inputs.
 * </p>
 * @author Elisha Peterson
 */
public class ParseException extends Exception {
    public ParseErrorCode code = ParseErrorCode.UNKNOWN_ERROR;

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
        UNKNOWN_ERROR,
        UNKNOWN_FUNCTION_NAME,
        UNSUPPORTED_FEATURE,
        EMPTY_EXPRESSION
    };
}
