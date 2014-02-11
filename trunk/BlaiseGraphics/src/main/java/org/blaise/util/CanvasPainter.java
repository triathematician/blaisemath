/**
 * CanvasPainter.java
 * Created Aug 1, 2012
 */
package org.blaise.util;

import java.awt.Component;
import java.awt.Graphics2D;

/**
 * <p>
 *  An object that paints on a graphics canvas.
 * </p>
 * @author elisha
 */
public interface CanvasPainter {

    /**
     * Paint on canvas of given component.
     * @param component component owning the canvas
     * @param canvas the canvas
     */
    void paint(Component component, Graphics2D canvas);

}
