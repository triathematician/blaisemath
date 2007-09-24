/*
 * Visometry.java
 * Created on Sep 14, 2007, 7:42:38 AM
 */

package specto;

import java.awt.Point;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 * This abstract class handles the "visometry", or visual geometry for plotting various things.
 * What this MEANS is support for converting between a given geometry coordinate system and
 * the enclosing window.
 * <br><br>
 * @param C The underlying coordinate system in use 
 * <br><br>
 * @author ae3263
 */
public abstract class Visometry<C extends Coordinate> implements ChangeListener {

    
    // PROPERTIES
    /** The enclosing panel */
    protected PlotPanel container;    
    
    
    // CONSTRUCTORS    
    public Visometry(){container=null;}
    public Visometry(PlotPanel p){initContainer(p);computeTransformation();}
    
    
    // INITIALIZERS
    public void initContainer(PlotPanel p){container=p;addChangeListener(container);computeTransformation();}
    
    
    // TRANSLATORS
    public abstract Point toWindow(C cp);
    public abstract C toGeometry(Point wp);
    public Point getWindowMin(){return new Point(0,-1);}
    public Point getWindowMax(){return new Point(-1,0);}
    
    
    // UPDATERS    
    public abstract void setBounds(C minPoint,C maxPoint);
    public abstract double computeTransformation();
    
    
    // EVENT HANDLING
    /** Indicates the window has been resized... then recompute the transformation! */
    public void stateChanged(ChangeEvent e){if(container!=null){computeTransformation();}}
    
    /** Event handling code copied from DefaultBoundedRangeModel. */      
    protected ChangeEvent changeEvent=null;
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
