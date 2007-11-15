/*
 * Plottable.java
 * Created on Sep 14, 2007, 7:49:09 AM
 */

package specto;

import java.awt.Graphics2D;
import javax.swing.JMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

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
    
    protected V visometry;
    /** Determines whether this adds to options menu. */
    protected boolean optionsMenuBuilding;

    public Plottable(){
        setOptionsMenuBuilding(false);
    }
    
    public abstract void recompute();
    public abstract void paintComponent(Graphics2D g);
    
    public void setVisometry(V newVis){visometry=newVis;}
    
    public void setOptionsMenuBuilding(boolean newValue){optionsMenuBuilding=newValue;}
    public boolean isOptionsMenuBuilding(){return optionsMenuBuilding;}
    public abstract JMenu getOptionsMenu();
    
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
