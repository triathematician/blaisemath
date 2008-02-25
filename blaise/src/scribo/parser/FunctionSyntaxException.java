/*
 * SyntaxException.java
 * Created on Oct 27, 2007, 8:00:04 AM
 */

package scribo.parser;

/**
 * Represents a syntax error which occurs when trying to compile a function string.<br><br>
 * @author Elisha
 */
public class FunctionSyntaxException extends Exception {
    int errorType;
    Integer position=null;

    public FunctionSyntaxException(int type){errorType=type;}
    public FunctionSyntaxException(int type,int pos){errorType=type;position=pos;}
    
    @Override
    public String getMessage(){
        String result="Error when parsing function string ";
        switch(errorType){
        case PARENTHETICAL: result+="(parenthesis mismatch)"; break;
        case OPERATOR: result+="(operator problem)";break; 
        case UNKNOWN_FUNCTION: result+="(unknown function)";break;
        case UNKNOWN_SYMBOL: result+="(unknown symbol)";break;
        case INCOMPLETE_INPUT: result+="(incomplete input)";break;
        case ARGUMENT_NUMBER: result+="(improper argument number)";break;
        case ARGUMENT_TYPE: result+="(improper argument type)";break;
        default: result+="(unknown error)";break;
        }
        if(position!=null){result+=" at position "+position.toString();}
        return result;
    }
    
    public static final int UNKNOWN=0;
    public static final int UNKNOWN_FUNCTION=1;
    public static final int UNKNOWN_SYMBOL=2;
    public static final int PARENTHETICAL=3;
    public static final int OPERATOR=4;
    public static final int INCOMPLETE_INPUT=5;
    public static final int ARGUMENT_NUMBER=6;
    public static final int ARGUMENT_TYPE=7;
}
