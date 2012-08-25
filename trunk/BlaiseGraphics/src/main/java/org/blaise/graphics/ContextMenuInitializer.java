/**
 * ContextMenuInitializer.java
 * Created Aug 25, 2012
 */
package org.blaise.graphics;

import java.awt.Point;
import javax.swing.JPopupMenu;

/**
 * Listener that can be added to a {@link Graphic} to customize its context menu.
 * 
 * @author Elisha
 */
public interface ContextMenuInitializer {

    /**
     * Initialize the context menu
     * @param menu context menu
     * @param point mouse location
     */
    public void initialize(JPopupMenu menu, Point point);
    
}
