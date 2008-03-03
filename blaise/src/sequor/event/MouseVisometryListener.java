/*
 * MouseVisometryListener.java
 * Created on Mar 2, 2008
 */

package sequor.event;

import specto.Visometry;

/**
 * <p>
 * MouseVisometryListener is ...
 * </p>
 * @author Elisha Peterson
 */
public interface MouseVisometryListener<V extends Visometry> {
    public static final int CLICK_EDIT_RANGE=8;
    
    public void setAdjusting(boolean newValue);
    public void setEditable(boolean newValue);
    public boolean isAdjusting();
    public boolean isEditable();
    
    public boolean clicked(MouseVisometryEvent<V> e);
    
    public void mouseClicked(MouseVisometryEvent<V> e);
    public void mousePressed(MouseVisometryEvent<V> e);
    public void mouseReleased(MouseVisometryEvent<V> e);
    public void mouseEntered(MouseVisometryEvent<V> e);
    public void mouseExited(MouseVisometryEvent<V> e);
    public void mouseDragged(MouseVisometryEvent<V> e);
    public void mouseMoved(MouseVisometryEvent<V> e);
}
