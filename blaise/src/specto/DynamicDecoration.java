/*
 * DynamicDecoration.java
 * Created on Mar 1, 2008
 */

package specto;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * <p>
 * DynamicDecoration is ...
 * </p>
 * @author Elisha Peterson
 */
public abstract class DynamicDecoration<V extends Visometry> extends DynamicPlottable<V> implements ChangeListener {
    public DynamicDecoration(Plottable<V> parent){setParent(parent);}
    
    protected Plottable<V> parent;
    
    public void setParent(Plottable<V> parent){
        this.parent=parent;
        color=parent.color;
        parent.addChangeListener(this);
    }
    public Plottable<V> getParent(){return parent;}
        
    @Override
    public void stateChanged(ChangeEvent e){recompute();}

    @Override
    protected void fireStateChanged() {parent.redraw();}
}
