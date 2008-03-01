/*
 * Decoration.java
 * Created on Oct 19, 2007, 12:42:46 PM
 */

package specto;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This interface is designed to control and display a "decoration" of another plottable,
 * e.g. a point or vector along a function plot.
 * @author ae3263
 */
public abstract class Decoration<V extends Visometry> extends Plottable<V> implements ChangeListener {
    public Decoration(Plottable<V> parent){super(parent.getVisometry());setParent(parent);}
    
    protected Plottable<V> parent;
    
    public void setParent(Plottable<V> parent){
        this.parent=parent;
        setColor(parent.color);
        parent.addChangeListener(this);
    }
    public Plottable<V> getParent(){return parent;}
        
    @Override
    public void stateChanged(ChangeEvent e){recompute();}

    @Override
    protected void fireStateChanged() {parent.redraw();}
}
