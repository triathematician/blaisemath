/**
 * PlanePlotComponent.java
 * Created on Jul 30, 2009
 */

package org.blaise.visometry.plane;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import org.blaise.math.line.RealInterval;
import org.blaise.visometry.PlotComponent;

/**
 * <p>
 *   <code>PlanePlotComponent</code> is a <code>VGraphicComponent</code> for a
 *   two-dimensional Euclidean plane.
 * </p>
 *
 * @author Elisha Peterson
 */
public class PlanePlotComponent extends PlotComponent<Point2D.Double> {

    /** Handles mouse gestures on the component, e.g. drag and zoom */
    PlanePlotMouseHandler mouseListener;

    /** Construct */
    public PlanePlotComponent() {
        super(new PlaneVisometry());

        PlaneVisometry pv = (PlaneVisometry) getVisometry();

        addMouseListener(mouseListener = new PlanePlotMouseHandler(pv, this));
        addMouseMotionListener(mouseListener);
        addMouseWheelListener(mouseListener);
        
        // set up the default domains for the plot
        plottables.registerDomain("x", pv.getHorizontalDomain(), Double.class);
        plottables.registerDomain("y", pv.getVerticalDomain(), Double.class);
        plottables.registerDomain("xy", pv.getPlaneDomain(), Point2D.Double.class);
        plottables.registerDomain("time", new RealInterval(0, 100), Double.class);
        
        setPreferredSize(new java.awt.Dimension(400, 400));
    }

    @Override
    protected void renderOverlay(Graphics2D canvas) {
        mouseListener.paint(canvas);
    }

    //
    // DELEGATE METHODS
    //

    /**
     * Set desired range of values to display.
     * Recomputes transformation after setting.
     * @param min1 first coordinate min
     * @param min2 second coordinate min
     * @param max1 first coordinate max
     * @param max2 second coordinate max
     */
    public void setDesiredRange(double min1, double min2, double max1, double max2) {
        ((PlaneVisometry) getVisometry()).setDesiredRange(min1, min2, max1, max2);
    }

    /**
     * Sets aspect ratio of plot
     * Recomputes transformation after setting
     * @param ratio new aspect ratio
     */
    public void setAspectRatio(double ratio) {
        ((PlaneVisometry) getVisometry()).setAspectRatio(ratio);
    }
}
