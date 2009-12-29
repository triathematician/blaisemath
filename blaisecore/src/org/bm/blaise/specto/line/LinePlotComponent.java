/**
 * LinePlotComponent.java
 * Created on Sep 19, 2009
 */

package org.bm.blaise.specto.line;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import org.bm.blaise.specto.visometry.PlotComponent;

/**
 * <p>
 *   <code>LinePlotComponent</code> is a <code>PlotComponent</code> for a
 *   two-dimensional Euclidean plane.
 * </p>
 *
 * @author Elisha Peterson
 */
public class LinePlotComponent extends PlotComponent<Double> {

    /**
     * Sets up default visometry and corresponding canvas. Also adds an underlying
     * object to listen for mouse events if no plottables handle them.
     */
    public LinePlotComponent() {
        super(new LineVisometry());
        ((LineVisometry) visometry).setDesiredRange(-5, 5);
        LinePlotResizer resizer = new LinePlotResizer((LineVisometry) visometry, this);
        defaultMouseListener = resizer;
        defaultMouseWheelListener = resizer;
    }

    /** Retrieve current maximum coordinate */
    public Double getMaxPointVisible() {
        return visometry.getMaxPointVisible();
    }

    /** Set current maximum coordinate */
    public void setMaxPointVisible(Double maximum) {
        ((LineVisometry) visometry).setMaxPointVisible(maximum);
    }

    /** Retrieve current minimum coordinate */
    public Double getMinPointVisible() {
        return visometry.getMinPointVisible();
    }

    /** Set current minimum coordinate */
    public void setMinPointVisible(Double minimum) {
        ((LineVisometry) visometry).setMinPointVisible(minimum);
    }

    public void setDesiredRange(double min, double max) {
        ((LineVisometry) visometry).setDesiredRange(min, max);
    }

    Rectangle2D.Double overlayBox;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (overlayBox != null) {
            ((Graphics2D) g).draw(overlayBox);
        }
    }

}
