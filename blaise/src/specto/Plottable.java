/*
 * Plottable.java
 * Created on Sep 14, 2007, 7:49:09 AM
 */

package specto;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.util.Vector;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import sequor.component.IntegerRangeTimer;
import sequor.model.ColorModel;

/**
 * This abstract class includes basic functionality for the plotting of some object on
 * a plot panel. Adds in support for firing change events, if the underlying settings
 * have changed for some reason.
 * <p>
 * Plottable's store all computations before displaying, allowing for plotpanel's to
 * pick and choose which elements to recompute before drawing. This allows the program
 * to avoid redundant computations. The PlotPanel class handles the recomputation
 * method before painting.
 * </p>
 * @author Elisha Peterson
 */
public abstract class Plottable<V extends Visometry> implements ChangeListener {
    /** Color used by the element. */
    protected ColorModel color;
    /** List of decorations. */
    protected PlottableGroup<V> decorations;

    public Plottable() {
        color=new ColorModel();
        color.addChangeListener(this);
    }        
    
    public Color getColor(){return color.getValue();}
    public void setColor(Color newValue){color.setValue(newValue);}
    public void addDecoration(Plottable p){
        if(decorations==null){decorations=new PlottableGroup<V>();}
        decorations.add(p);
        if(p instanceof Decoration){((Decoration)p).setParent(this);}
    }
    /** Returns menu containing any desired options. By default, returns null...
     * otherwise whatever is returned may be added to the plotpanel context menu. */
    public JMenu getOptionsMenu(){return null;}
    public Vector<JMenuItem> getDecorationMenuItems(){
        if(decorations==null){return null;}
        Vector<JMenuItem> result=new Vector<JMenuItem>();
        for(Plottable<V> decoration:decorations.getElements()){
            result.add(decoration.getOptionsMenu());
        }
        return result;
    }
    /** Returns button which when pressed opens a color palette to change the color of the given item. */
    public Component getColorMenuItem(){        
        return color.getMenuItem();
    }

    public abstract void recompute();
    public void redraw(){fireStateChanged();}
    public void stateChanged(ChangeEvent e){changeEvent=e;redraw();}
    public void paintDecorations(Graphics2D g,V v){if(decorations!=null){decorations.paintComponent(g,v);}}
    public void paintDecorations(Graphics2D g,V v,IntegerRangeTimer t){if(decorations!=null){decorations.paintComponent(g,v,t);}}
    public abstract void paintComponent(Graphics2D g,V v);           
    
    
    // EVENT HANDLING
    
    /** Event handling code copied from DefaultBoundedRangeModel. */      
    protected ChangeEvent changeEvent=new ChangeEvent("Plottable");
    protected EventListenerList listenerList=new EventListenerList();    
    public void addChangeListener(ChangeListener l){listenerList.add(ChangeListener.class,l);}
    public void removeChangeListener(ChangeListener l){listenerList.remove(ChangeListener.class,l);}
    protected void fireStateChanged(){
        Object[] listeners=listenerList.getListenerList();
        for(int i=listeners.length-2; i>=0; i-=2){
            if(listeners[i]==ChangeListener.class){
                if(changeEvent==null){return;}
                ((ChangeListener)listeners[i+1]).stateChanged(changeEvent);
            }
        }
    }
}
