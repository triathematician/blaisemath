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
public abstract class Decoration<P extends Plottable,V extends Visometry> extends Plottable<V> implements ChangeListener {
    private P parent;
    
    public void setParent(P parent){
        this.parent=parent;
        parent.addChangeListener(this);
    }
    public P getParent(){return parent;}
    
    public void setVisometry(V v){
        super.setVisometry(v);
        parent.setVisometry(v);
    }
}
