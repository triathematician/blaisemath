/**
 * GestureController.java
 * Created on Mar 26, 2008
 */

package sequor.control.gestures;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import javax.swing.event.EventListenerList;

/**
 * This class is used to generate ActionEvent's based on input gestures. Specific substrings may be registered
 * to particular ActionCommand's.
 * 
 * @author Elisha Peterson
 */
public class GestureController implements MouseGestureListener {

    /** Used to determine appropriate actions given inputs. */
    HashMap<String[],String> actionMap;
    
    /** Default constructor. */
    public GestureController(){actionMap=new HashMap<String[],String>();}
    
    /** Construct with map. */
    GestureController(HashMap<String[], String> stringMap) {actionMap=stringMap;}
    
    /** Adds map to gesture "library" */
    public void add(String[] gesture,String command){
        actionMap.put(gesture,command);
    }
    
    /** Fires appropriate command if the particular gesture is made. */    
    public void gestureMade(MouseGestureEvent e) { 
        for(String[] ss:actionMap.keySet()){
            if(e.contains(ss, true)){
                fireActionPerformed(actionMap.get(ss));
                return;
            }
        }
    }
    
    
    // ACTION EVENT HANDLING
     
    protected ActionEvent actionEvent=null;
    protected EventListenerList actionListenerList=new EventListenerList();    
    public void addActionListener(ActionListener l){actionListenerList.add(ActionListener.class,l);}
    public void removeActionListener(ActionListener l){actionListenerList.remove(ActionListener.class,l);}
    protected void fireActionPerformed(String s){
        actionEvent=new ActionEvent(this,0,s);
        Object[] listeners=actionListenerList.getListenerList();
        for(int i=listeners.length-2; i>=0; i-=2){
            if(listeners[i]==ActionListener.class){
                if(actionEvent==null){actionEvent=new ActionEvent(this,0,s);}
                ((ActionListener)listeners[i+1]).actionPerformed(actionEvent);
            }
        }
    }
}
