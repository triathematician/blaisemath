/**
 * FunctionEditor.java
 * Created on Dec 2, 2009
 */

package data.propertysheet.editor;

import java.beans.PropertyEditorManager;
import org.bm.blaise.scio.function.ParsedUnivariateRealFunction;
import org.bm.blaise.scribo.parser.ParseException;

/**
 * <p>
 *    This class allows the user to edit a function, using a String.
 * </p>
 * @author Elisha Peterson
 */
public class FunctionEditor extends StringEditor {

    public static void register() {
        PropertyEditorManager.registerEditor(ParsedUnivariateRealFunction.class, FunctionEditor.class);
    }
    
    ParsedUnivariateRealFunction func() {
        return (ParsedUnivariateRealFunction) getValue();
    }

    ParsedUnivariateRealFunction newfunc() {
        return (ParsedUnivariateRealFunction) getNewValue();
    }

    @Override
    public void setAsText(String text) {
        try { func().setFunctionString(text); } catch (ParseException ex) { }
    }

    public void setNewAsText(String text) {
        try { newfunc().setFunctionString(text); } catch (ParseException ex) { }
    }

    @Override
    public String getJavaInitializationString() {
        return "new ParsedUnivariateRealFunction("+func().getFunctionString()+")";
    }
}
