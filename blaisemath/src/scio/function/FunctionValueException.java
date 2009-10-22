/*
 * FunctionValueException.java
 * Created in 2008
 */

package scio.function;

/**
 * <p>
 * Represents an error in evaluating a function; may be caused by an infinite value, by insufficient input,
 * or a variety of other reasons.
 * </p>
 * @author Elisha Peterson
 */
@Deprecated // use org.apache.commons.math.MathException and its subclasses
public class FunctionValueException extends Exception {

    /** Code giving the type of error. */
    int errorType;

    /** Default constructor, indicates an unknown type. */
    public FunctionValueException(){
        this(UNKNOWN);
    }

    /** Constructs with a given type.
     * @param type code representing why the exception was generated
     */
    public FunctionValueException(int type){
        errorType=type;
    }

    /** Returns message corresponding to the error type. */
    @Override
    public String getMessage(){
        String result="Error when evaluating function (" + errorMessage[errorType]+")";
        return result;
    }
    /** code representing unknown reason for exception */
    public static final int UNKNOWN=0;
    /** code representing attempt to get value without knowing enough information */
    public static final int TOO_FEW_INPUTS=1;

    /** strings representing the error message. */
    static final String[] errorMessage =
    { "unknown error", "too few inputs" };

} // class FunctionValueException
