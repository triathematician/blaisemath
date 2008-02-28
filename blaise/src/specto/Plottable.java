/*
 * Plottable.java
 * Created on Sep 14, 2007, 7:49:09 AM
 */

package specto;

import java.awt.Color;
import java.awt.Graphics2D;
import javax.swing.JMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import sequor.component.RangeTimer;

/**
 * This abstract class includes basic functionality for the plotting of some object on
 * a plot panel. Adds in support for firing change events, if the underlying settings
 * have changed for some reason.
 * <br><br>
 * Plottable's store all computations before displaying, allowing for plotpanel's to
 * pick and choose which elements to recompute before drawing. This allows the program
 * to avoid redundant computations. The PlotPanel class handles the recomputation
 * method before painting.
 * <br><br>
 * @author Elisha Peterson
 */
public abstract class Plottable<V extends Visometry> {
    
    /** Underlying visometry used by the element. */
    protected V visometry;
    /** Color used by the element. */
    protected Color color;
    /** Determines whether this adds to options menu. */
    protected boolean optionsMenuBuilding=false;
    /** List of decorations. */
    protected PlottableGroup<V> decorations;

    public Plottable(V v){setVisometry(v);}
    
    public void setVisometry(V newVis){visometry=newVis;}    
    public V getVisometry(){return visometry;}
    public Color getColor(){return color;}
    public void setColor(Color newValue){
        if(color!=newValue){
            color=newValue;
            if(decorations!=null){decorations.setColor(newValue);}
            fireStateChanged();
        }
    }
    public void setOptionsMenuBuilding(boolean newValue){optionsMenuBuilding=newValue;}
    public boolean isOptionsMenuBuilding(){return optionsMenuBuilding;}
    public void addDecoration(Decoration<V> d){
        if(decorations==null){decorations=new PlottableGroup<V>(visometry);}
        decorations.add(d);
        d.setParent(this);
    }
    public abstract JMenu getOptionsMenu();

    public abstract void recompute();
    public void paintDecorations(Graphics2D g){if(decorations!=null){decorations.paintComponent(g);}}
    public void paintDecorations(Graphics2D g,RangeTimer t){if(decorations!=null){decorations.paintComponent(g,t);}}
    public abstract void paintComponent(Graphics2D g);
    
    
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
