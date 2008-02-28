/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package specto;

import java.beans.PropertyChangeEvent;
import sequor.model.FiresChangeEvents;

/**
 *
 * @author ae3263
 */
public abstract class VisualStyle extends FiresChangeEvents {
    @Override
    public void setValue(String s) {}
    @Override
    public String toLongString() {return "Visual style";}
    @Override
    public PropertyChangeEvent getChangeEvent(String s) {return new PropertyChangeEvent(this,s,null,null);}
}
