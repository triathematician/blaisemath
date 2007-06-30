package Blaise;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.EventListenerList;
import org.nfunk.jep.JEP;

/**
 * <b>BParser.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>June 20, 2007, 11:05 AM</i><br><br>
 *
 * Extension of JEP with event handling.<br><br>
 *
 * Initializes to default f(x)=... function expressions.<br><br>
 *
 * When expression is being set, generates one ActionEvent "error" if the expression
 * has errors, and another "change" if the new expression works.
 */
public class BParser extends JEP {
    
    /** Default string to use */
    public static String DEFAULT_STRING="4-x^2";
    
    /** Constructor: creates a new instance of BParser */
    public BParser(){
        super();
        setImplicitMul(true);
        addStandardFunctions();
        addStandardConstants();
        addVariable("x",0);
        setExpressionString(DEFAULT_STRING);
    }
    
    /** Sets the underlying expression, detects errors. */
    public void setExpressionString(String newString) {
        parseExpression(newString);
        if(hasError()){fireActionPerformed("error");}
        else{fireActionPerformed("change");}
    }
    
    /** @return The value of the function at an x value of the parameter. */
    public double getY(double x){addVariable("x",x);return getValue();}
    
    /** Event handling code modified from DefaultBoundedRangeModel. */
    protected EventListenerList listenerList=new EventListenerList();
    public void addActionListener(ActionListener l){listenerList.add(ActionListener.class,l);}
    public void removeActionListener(ActionListener l){listenerList.remove(ActionListener.class,l);}
    protected void fireActionPerformed(String code){
        Object[] listeners=listenerList.getListenerList();
        for(int i=listeners.length-2; i>=0; i-=2){
            if(listeners[i]==ActionListener.class){
                ((ActionListener)listeners[i+1]).actionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,code));
            }
        }
    }
}
