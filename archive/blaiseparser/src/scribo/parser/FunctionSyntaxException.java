/*
 * SyntaxException.java
 * Created on Oct 27, 2007, 8:00:04 AM
 */

package scribo.parser;

/**
 * <p>
 * Represents a syntax error which occurs when trying to compile a function string.
 * </p>
 * @author Elisha Peterson
 */
public class FunctionSyntaxException extends Exception {
    int errorType;
    Integer position=null;

    /**
     * <p>
     * Default constructor, indicates an unknown type.
     * </p>
     */
    public FunctionSyntaxException(){
        this(UNKNOWN);
    }

    /**
     * <p>
     * Constructs with a given type.
     * </p>
     * @param type code representing why the exception was generated
     */
    public FunctionSyntaxException(int type){
        errorType=type;
    }

    /**
     * <p>
     * Construct with a specific type and location within the input string.
     * </p>
     * @param type code representing why the exception was generated
     * @param pos position in input string
     */
    public FunctionSyntaxException(int type, int pos){
        errorType=type;
        position=pos;
    }

    /** Returns message corresponding to the error type. */
    @Override
    public String getMessage(){
        String result="Error when parsing function string (" + errorMessage[errorType]+")";
        if(position!=null){result+=" at position "+position.toString();}
        return result;
    }
    
    /** code representing unknown reason for exception */
    public static final int UNKNOWN=0;
    /** code representing use of unknown function */
    public static final int UNKNOWN_FUNCTION=1;
    /** code representing use of unknown symbol */
    public static final int UNKNOWN_SYMBOL=2;
    /** code representing mismatching parentheses */
    public static final int PARENTHETICAL=3;
    /** code representing bad use of an operator */
    public static final int OPERATOR=4;
    /** code representing incomplete input */
    public static final int INCOMPLETE_INPUT=5;
    /** code represent improper number of arguments for some function requiring a list of arguments */
    public static final int ARGUMENT_NUMBER=6;
    /** code representing bad type of argument attempting to be applied to some node */
    public static final int ARGUMENT_TYPE=7;

    /** strings representing the error message. */
    static final String[] errorMessage =
    { "unknown error", "unknown function", "unknown symbol",
      "parenthetical error", "operator error", "incomplete input",
      "argument number error", "argument type error"
    };
}
