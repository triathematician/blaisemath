/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * GestureInputPath.java
 * Created on Mar 26, 2008
 */

package sequor.control.gestures;

import java.awt.Color;
import java.awt.Shape;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.EventListenerList;
import scio.coordinate.R2;
import sequor.control.DrawnPath;
import sequor.control.gestures.GestureGenerator.Interval;
import sequor.control.gestures.MouseGestureEvent;
import sequor.control.gestures.MouseGestureListener;

/**
 *
 * @author Elisha Peterson
 */
public class GestureInputPath extends DrawnPath {


    public GestureInputPath() {}
    public GestureInputPath(Color c, int style) {
        super(c, style);
        addGestureListener(new MouseGestureListener(){
            public void gestureMade(MouseGestureEvent e) {
                path=new Path2D.Double();
                fireStateChanged();
            }        
        });
    }
    
    
    // MOUSE HANDLING

    @Override
    public void mouseReleased(MouseEvent e) {
        markovOutput(e);
    }
    
    
    // UNDERLYING ALGORITHM    
    
    /**  Outputs left/right sequence of a path */
    public void markovOutput(MouseEvent me){
        if(observed.size()==0){return;}
        try {
            R2[] observations = new R2[observed.size()-1];
            for(int i=1;i<observed.size();i++){
                observations[i-1]=observed.get(i).minus(observed.get(i-1));
            }
            
            GestureGenerator g3=new AngleGestureGenerator.FourDir();
            GestureGenerator g4=new AngleGestureGenerator.EightDir();
            GestureGenerator g5=new AngleGestureGenerator.SixteenDir();
            
            g4.processOutput(g4.computePath(observations),"0");  
            
            Vector<Interval<String>> resultA = g5.processOutput(g5.computePath(observations),"0");   
            Shape s=g5.getArcOutput(resultA, observed);
            tempShape=new Path2D.Double();
            if(s!=null){tempShape.append(s,false);}
            if(tempShape!=null){fireStateChanged();}
            me.setSource(this);
            
            fireGestureEvent(GestureGenerator.getStrVector(g3.removeDuplicates(g3.computePath(observations))),me);
        } catch (Exception e) {
            Logger.getLogger(ShapeGestureGenerator.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    
    // EVENT HANDLING
     
    protected MouseGestureEvent gestureEvent=null;
    protected EventListenerList gestureListenerList=new EventListenerList();    
    public void addGestureListener(MouseGestureListener l){gestureListenerList.add(MouseGestureListener.class,l);}
    public void removeGestureListener(MouseGestureListener l){gestureListenerList.remove(MouseGestureListener.class,l);}
    protected void fireGestureEvent(Vector<String> gesture,MouseEvent e){
        gestureEvent=new MouseGestureEvent(gesture,e);
        Object[] listeners=gestureListenerList.getListenerList();
        for(int i=listeners.length-2; i>=0; i-=2){
            if(listeners[i]==MouseGestureListener.class){
                ((MouseGestureListener)listeners[i+1]).gestureMade(gestureEvent);
            }
        }
    }
}
