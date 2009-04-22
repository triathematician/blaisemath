/**
 * MouseGestureEvent.java
 * Created on Mar 26, 2008
 */

package sequor.control.gestures;

import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Vector;

/**
 *
 * @author Elisha Peterson
 */
public class MouseGestureEvent extends MouseEvent {
    Vector<String> gesture;

    public MouseGestureEvent(Vector<String> gesture,MouseEvent e){        
        super(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(), e.getX(), e.getY(), e.getClickCount(), false, e.getButton());
        this.gesture=gesture;
    }
    

    
    /** Looks for a subset in the gesture, or any rotation of the subset. */
    public boolean contains(String[] subset,boolean cyclic){
        String gString=gesture.toString();
        String subString;
        int nCycles = cyclic ? subset.length : 1;
        //System.out.println(gString);
        for(int i=0;i<nCycles;i++){
            subString="";
            for(int j=0;j<subset.length-1;j++){subString+=subset[(i+j)%subset.length]+", ";}
            subString+=subset[(i+subset.length-1)%subset.length];
            //System.out.println(" : ["+subString+"]");
            if(gString.contains(subString)){return true;}
        }
        return false;
    }
    
    /** Returns element in HashMap corresponding to a particular gesture. */
    public Object getObject(HashMap<String[],Object> map,boolean cyclic){
        for(String[] ss:map.keySet()){
            if(contains(ss,cyclic)){return map.get(ss);}
        }
        return null;
    }
}
