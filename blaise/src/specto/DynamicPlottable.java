/*
 * VisualEditor.java
 * 
 * Created on Sep 14, 2007, 7:55:27 AM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package specto;


import scio.coordinate.Coordinate;
import sequor.event.MouseVisometryEvent;
import sequor.event.MouseVisometryListener;

/**
 * <p>
 * This class takes the <b>Plottable</b> class and adds in basic support for mouse event handling,
 * allowing the firing of events, handling of menu, etc. In order to properly handle incoming events,
 * the mouse positions should be passed through the visometry to convert coordinates and determine whether
 * the object has actually been clicked.
 * </p>
 * @author Elisha Peterson
 */
public abstract class DynamicPlottable<V extends Visometry> extends Plottable<V> implements MouseVisometryListener<V> {
    
    public boolean adjusting=false;
    public boolean editable=true;

    // BEAN PATTERNS
    
    public boolean isAdjusting() {return adjusting;}
    public boolean isEditable() {return editable;}
    public void setAdjusting(boolean newValue) {adjusting=newValue;}
    public void setEditable(boolean newValue) {editable=newValue;}    
    
    // HANDLING MOUSE EVENTS
    
    public boolean clicked(MouseVisometryEvent<V> e) {return false;}
    public boolean withinClickRange(MouseVisometryEvent<V> e,Coordinate c){
        java.awt.geom.Point2D.Double point=e.getSourceVisometry().toWindow(c);
        return Math.abs(e.getX()-point.x)+Math.abs(e.getY()-point.y)<CLICK_EDIT_RANGE;        
    }
    
    public void mouseClicked(MouseVisometryEvent<V> e) {if(clicked(e)){adjusting=true;}}
    public void mousePressed(MouseVisometryEvent<V> e) {if(clicked(e)){adjusting=true;}}
    public void mouseDragged(MouseVisometryEvent<V> e) {}
    public void mouseReleased(MouseVisometryEvent<V> e) {mouseDragged(e);adjusting=false;}  
    public void mouseEntered(MouseVisometryEvent<V> e) {}
    public void mouseExited(MouseVisometryEvent<V> e) {}
    public void mouseMoved(MouseVisometryEvent<V> e) {}
}
