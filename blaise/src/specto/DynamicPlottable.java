/*
 * VisualEditor.java
 * 
 * Created on Sep 14, 2007, 7:55:27 AM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package specto;


import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * <p>
 * This class takes the <b>Plottable</b> class and adds in basic support for mouse event handling,
 * allowing the firing of events, handling of menu, etc. In order to properly handle incoming events,
 * the mouse positions should be passed through the visometry to convert coordinates and determine whether
 * the object has actually been clicked.
 * </p>
 * @author Elisha Peterson
 */
public abstract class DynamicPlottable<V extends Visometry> extends Plottable<V> implements MouseListener,MouseMotionListener {
    
    public DynamicPlottable(V v){super(v);}
    
    // HOW TO DETERMINE WHEN TO PROCESS A MOUSE EVENT

    public static final int CLICK_EDIT_RANGE=8;
    
    public boolean adjusting=false;
    public boolean mobile=true;
    
    public void setMoving(boolean newValue){mobile=newValue;}
    
    public boolean clicked(MouseEvent e){return false;}
    
    
    // MOUSE EVENTS
    
    @Override
    public void mouseClicked(MouseEvent e){}
    @Override
    public void mousePressed(MouseEvent e){}
    @Override
    public void mouseReleased(MouseEvent e){}
    @Override
    public void mouseEntered(MouseEvent e){}
    @Override
    public void mouseExited(MouseEvent e){}
    @Override
    public void mouseDragged(MouseEvent e){}
    @Override
    public void mouseMoved(MouseEvent e){}
}
