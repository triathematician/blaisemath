/**
 * PlanePlotComponent.java
 * Created on Jul 30, 2009
 */

package org.bm.blaise.specto.plane;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import org.bm.blaise.specto.plane.PlaneAxes.AxisStyle;
import org.bm.blaise.specto.visometry.PlotComponent;
import org.bm.utils.PlaneGridSampleSet;
import scio.coordinate.sample.SampleCoordinateSetGenerator;

/**
 * <p>
 *   <code>PlanePlotComponent</code> is a <code>PlotComponent</code> for a
 *   two-dimensional Euclidean plane.
 * </p>
 *
 * @author Elisha Peterson
 */
public class PlanePlotComponent extends PlotComponent<Point2D.Double> {

    //
    // CONSTRUCTORS
    //

    /** 
     * Sets up default visometry and corresponding canvas. Also adds an underlying
     * object to listen for mouse events if no plottables handle them.
     */
    public PlanePlotComponent() {
        super(new PlaneVisometry());
        ((PlaneVisometry) visometry).setDesiredRange(-5, -5, 5, 5);
        visometryGraphics = new PlaneGraphics(visometry);
        PlanePlotResizer resizer = new PlanePlotResizer((PlaneVisometry) visometry, this);
        defaultMouseListener = resizer;
        defaultMouseWheelListener = resizer;
    }

    /** 
     * Constructs plot component which borrows graphics and visometry objects from
     * another plot component. This intentionally makes it so adjusting one component
     * also adjusts the other component.
     * @param cpt another plot component
     */
    public PlanePlotComponent(PlanePlotComponent cpt) {
        super(cpt.visometry);
        visometryGraphics = cpt.visometryGraphics;
        PlanePlotResizer resizer = new PlanePlotResizer((PlaneVisometry) visometry, this);
        defaultMouseListener = resizer;
        defaultMouseWheelListener = resizer;
    }

    /**
     * Constructs plot component which borrows graphics and visometry objects from
     * another plot component. This intentionally makes it so adjusting one component
     * also adjusts the other component.
     * @param cpt another plot component
     */
    public PlanePlotComponent(PlanePlotComponent cpt, String title, AxisStyle axisStyle) {
        this(cpt);
        setTitle(title);
        addPlottable(PlaneAxes.instance(axisStyle, "x", "y"));
    }

    /** Initializes with a title and axes labeled by x and y. */
    public PlanePlotComponent(String title, AxisStyle axisStyle) {
        this(title, axisStyle, "x", "y");
    }

    /** Initializes with axes labeled by specified strings. */
    public PlanePlotComponent(String title, AxisStyle axisStyle, String xLabel, String yLabel) {
        this();
        setTitle(title);
        addPlottable(PlaneAxes.instance(axisStyle, xLabel, yLabel));
    }

    //
    // GETTERS & SETTERS
    //

    public SampleCoordinateSetGenerator<Point2D.Double> getPlotSampleSetGenerator() {
        return new PlaneGridSampleSet() {
            @Override
            public RectangularShape getBounds() {
                return ((PlaneVisometry) visometry).displayRange;
            }
        };
    }

    /**
     * Sets the desired range of values to display.
     */
    public void setDesiredRange(double minX, double minY, double maxX, double maxY) {
        ((PlaneVisometry) visometry).setDesiredRange(minX, minY, maxX, maxY);
    }

    /**
     * Returns the desired range of values to display.
     * @return a rectangle with the desired values.
     */
    public Rectangle2D.Double getDesiredRange() {
        return ((PlaneVisometry) visometry).getDesiredRange();
    }

    /**
     * Sets the desired range of values to display.
     */
    public void setDesiredRange(Rectangle2D.Double rect) {
        setDesiredRange(rect.getMinX(), rect.getMinY(), rect.getMaxX(), rect.getMaxY());
    }

    /**
     * @return current aspect ratio of the plot
     */
    public double getAspectRatio() {
        return ((PlaneVisometry) visometry).getAspectRatio();
    }

    /**
     * Sets the aspect ratio of the plot.
     * @param newRatio new value for the aspect ratio.
     */
    public void setAspectRatio(double newRatio) {
        ((PlaneVisometry) visometry).setAspectRatio(newRatio);
    }
}
