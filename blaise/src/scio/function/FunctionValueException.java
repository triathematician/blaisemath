/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package scio.function;

/**
 *
 * @author ae3263
 */
public class FunctionValueException extends Exception {

    public FunctionValueException() {
    }
    
    @Override
    public String getMessage(){
        return "Error when evaluating function.";
    }
}
