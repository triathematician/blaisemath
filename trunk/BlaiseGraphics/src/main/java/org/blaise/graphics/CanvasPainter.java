/**
 * CanvasPainter.java
 * Created Aug 1, 2012
 */
package org.blaise.graphics;

import java.awt.Graphics2D;
import javax.swing.JComponent;

/**
 * <p>
 *  An object that paints on a graphics canvas.
 * </p>
 * @author elisha
 */
public interface CanvasPainter {

    /**
     * Paint on canvas
     * @param component source component
     * @param canvas graphics canvas
     */
    public void paint(JComponent component, Graphics2D canvas);

}
