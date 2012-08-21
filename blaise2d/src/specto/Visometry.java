/*
 * Visometry.java
 * Created on Sep 14, 2007, 7:42:38 AM
 */

package specto;

import scio.coordinate.Coordinate;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.util.Vector;
import javax.swing.JMenuItem;
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
public abstract class Visometry<C extends Coordinate> 
        implements ActionListener,ComponentListener,ChangeListener,MouseListener,MouseMotionListener,MouseWheelListener{
    
    // PROPERTIES
    /** The enclosing panel */
    protected PlotPanel container;    
    
    
    // CONSTRUCTORS    
    public Visometry(){container=null;}
    public Visometry(PlotPanel p){initContainer(p);}
    
    
    // INITIALIZERS
    public void initContainer(PlotPanel p){
        if(p!=null){
            container=p;        
            computeTransformation();
        }else{
            container=new PlotPanel() {};
            computeTransformation();
        }
    }
    public abstract Vector<JMenuItem> getMenuItems();
    
    
    // TRANSLATORS
    public abstract Point2D.Double toWindow(C cp);
    public abstract C toGeometry(Point wp);
    public Point2D.Double getWindowMin(){return new Point2D.Double(0,container.getHeight());}
    public Point2D.Double getWindowMax(){return new Point2D.Double(container.getWidth(),0);}
    public int getWindowWidth(){return container.getWidth();}
    public int getWindowHeight(){return container.getHeight();}
    
    
    // UPDATERS    
    public abstract void setBounds(C minPoint,C maxPoint);
    public abstract double computeTransformation();
    
    
    // EVENT HANDLING
    /** Indicates the window has been resized... then recompute the transformation! */
    public void componentResized(ComponentEvent e){if(container!=null){computeTransformation();}}    
    public void actionPerformed(ActionEvent e){}
    public void stateChanged(ChangeEvent e){}
    public void componentMoved(ComponentEvent e){}
    public void componentShown(ComponentEvent e){}
    public void componentHidden(ComponentEvent e){}
    public void mouseMoved(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    public void mouseClicked(MouseEvent e){}
    public void mousePressed(MouseEvent e){}
    public void mouseDragged(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}
    public void mouseWheelMoved(MouseWheelEvent e){}
    
    
    /** Event handling code copied from DefaultBoundedRangeModel. */      
    protected ChangeEvent changeEvent=new ChangeEvent("Visometry");
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
