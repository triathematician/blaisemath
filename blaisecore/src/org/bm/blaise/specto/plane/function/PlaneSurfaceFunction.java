/**
 * PlaneVectorField.java
 * Created on Sep 3, 2009
 */

package org.bm.blaise.specto.plane.function;

import java.util.List;
import scio.coordinate.sample.SampleGenerator;
import java.awt.geom.Point2D;
import javax.swing.event.ChangeEvent;
import org.apache.commons.math.analysis.MultivariateRealFunction;
import org.bm.blaise.specto.plane.PlaneGraphics;
import org.bm.blaise.specto.plottable.SamplerPlottable;
import org.bm.blaise.specto.primitive.BlaisePalette;
import org.bm.blaise.specto.primitive.GraphicPoint;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import org.bm.blaise.specto.primitive.PointStyle;
import util.ChangeEventHandler;

/**
 * <p>
 *   <code>PlaneVectorField</code> represents a function that has two inputs and one output, i.e. of the form z=f(x,y).
 *   The function is displayed as a collection of points of varying radii.
 * </p>
 *
 * @author Elisha Peterson
 */
public class PlaneSurfaceFunction extends SamplerPlottable<Double, GraphicPoint, Point2D.Double> {

    /** Underlying function */
    MultivariateRealFunction func;
    /** Multiplier for dot size. */
    double lengthMultiplier = 1;


    public PlaneSurfaceFunction(MultivariateRealFunction func, SampleGenerator<Point2D.Double> sg) {
        super(sg);
        setFunc(func);
        setStyle(new PointStyle(BlaisePalette.STANDARD.func2(), BlaisePalette.STANDARD.func2light()));
    }

    @Override
    public String toString() {
        return "2-Variable Function [" + getSampleGenerator() + "]";
    }

    //
    // VALUE PATTERNS
    //

    /** @return function describing the surface */
    public MultivariateRealFunction getFunc() {
        return func;
    }

    /**
     * Sets the function for the surface.
     * @param func the function
     */
    public void setFunc(MultivariateRealFunction func) {
        if (func != null && this.func != func) {
            if (this.func instanceof ChangeEventHandler)
                ((ChangeEventHandler)this.func).removeChangeListener(this);
            this.func = func;
            if (func instanceof ChangeEventHandler)
                ((ChangeEventHandler)func).addChangeListener(this);
            needsComputation = true;
            fireStateChanged();
        }
    }

    //
    // STYLE PATTERNS
    //

    public double getLengthMultiplier() {
        return lengthMultiplier;
    }

    public void setLengthMultiplier(double lengthMultiplier) {
        if (this.lengthMultiplier != lengthMultiplier) {
            this.lengthMultiplier = lengthMultiplier;
            needsPrimitiveComputation = true;
            fireStateChanged();
        }
    }

    public PointStyle getStyle() {
        return (PointStyle) style;
    }

    public void setStyle(PointStyle pointStyle) {
        if (this.style != pointStyle) {
            this.style = pointStyle;
            fireStateChanged();
        }
    }

    //
    // DRAW METHODS
    //

    protected Double getValue(Point2D.Double sample) {
        try {
            return func.value(new double[]{sample.x, sample.y});
        } catch (Exception ex) {
            return Double.NaN;
        }
    }

    @Override
    protected double getScaleFactor(List<Double> values, Point2D.Double diff) {
        double maxAbsValue = 0.0;
        for (Double d : values)
            maxAbsValue = Math.max( maxAbsValue, Math.abs(d) );
        return lengthMultiplier * (maxAbsValue == 0 ? 1
                : .95 * Math.min(Math.abs(diff.x), Math.abs(diff.y)) / maxAbsValue);
    }

    protected GraphicPoint[] getPrimitives(List<Point2D.Double> samples, List<Double> values, double scaleFactor, VisometryGraphics<Point2D.Double> vg) {
        double maxRad = .5 * Math.min(((PlaneGraphics)vg).getScaleX(), ((PlaneGraphics)vg).getScaleY());
        GraphicPoint[] points = new GraphicPoint[samples.size()];
        for (int i = 0; i < samples.size(); i++)
            points[i] = new GraphicPoint(vg.getWindowPointOf(samples.get(i)), maxRad * scaleFactor * values.get(i));
        return points;
    }
    
    //    
    // CHANGE HANDLING
    //

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == func)
            needsComputation = true;
        super.stateChanged(e);
    }
}
