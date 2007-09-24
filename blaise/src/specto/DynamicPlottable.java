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
 * This class takes the Plottable class and adds in support for mouse event handling,
 * allowing the firing of events, handling of menu, etc.
 * <br><br>
 * @author Elisha Peterson
 */
public abstract class DynamicPlottable<V extends Visometry> extends Plottable<V> implements MouseListener,MouseMotionListener {
    
// EVENT HANDLING

    public static final int CLICK_EDIT_RANGE=8;
    
    public void mouseClicked(MouseEvent e){}
    public void mousePressed(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    public void mouseDragged(MouseEvent e){}
    public void mouseMoved(MouseEvent e){}
}
