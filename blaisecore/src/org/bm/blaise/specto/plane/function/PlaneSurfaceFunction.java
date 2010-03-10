/**
 * PlaneVectorField.java
 * Created on Sep 3, 2009
 */

package org.bm.blaise.specto.plane.function;

import java.util.List;
import scio.coordinate.sample.SampleCoordinateSetGenerator;
import java.awt.geom.Point2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.MultivariateRealFunction;
import org.bm.blaise.specto.plane.PlaneVisometry;
import org.bm.blaise.specto.plottable.VPrimitiveMappingPlottable;
import org.bm.blaise.specto.primitive.BlaisePalette;
import org.bm.blaise.specto.primitive.GraphicPoint;
import org.bm.blaise.specto.visometry.Visometry;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import org.bm.blaise.specto.primitive.PointStyle;

/**
 * <p>
 *   <code>PlaneVectorField</code> represents a function that has two inputs and one output, i.e. of the form z=f(x,y).
 *   The function is displayed as a collection of points of varying radii.
 * </p>
 *
 * @author Elisha Peterson
 */
public class PlaneSurfaceFunction extends VPrimitiveMappingPlottable<Point2D.Double, Point2D, Point2D.Double> {

    /** Multiplier for dot size. */
    double lengthMultiplier = 1;

    /** Underlying function */
    MultivariateRealFunction func;

    public PlaneSurfaceFunction(MultivariateRealFunction func, SampleCoordinateSetGenerator<Point2D.Double> ssg) {
        super(new PointStyle(BlaisePalette.STANDARD.func2(), BlaisePalette.STANDARD.func2light()), ssg);
        setFunc(func);
    }

    @Override
    public String toString() {
        return "2-Variable Function [" + ssg + "]";
    }

    //
    // BEAN PATTERNS
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
        this.func = func;
    }

    public double getLengthMultiplier() {
        return lengthMultiplier;
    }

    public void setLengthMultiplier(double lengthMultiplier) {
        this.lengthMultiplier = lengthMultiplier;
    }

    //
    // PrimitiveMapper INTERFACE METHODS
    //

    @Override
    public void scalePrimitives(Visometry vis) {
        PlaneVisometry pv = (PlaneVisometry) vis;
        double maxRad = Math.min(Math.abs(ssg.getSampleDiff().x * pv.getScaleX()), Math.abs(ssg.getSampleDiff().y * pv.getScaleY()));
        GraphicPoint.scalePoints(primitives, lengthMultiplier * maxRad / 1.5, 0.5);
    }

    public Point2D primitiveAt(Point2D.Double coord, VisometryGraphics<Point2D.Double> vg) {
        double value = 0;
        try {
            value = func.value(new double[]{coord.x, coord.y});
        } catch (FunctionEvaluationException ex) {
            Logger.getLogger(PlaneSurfaceFunction.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(PlaneSurfaceFunction.class.getName()).log(Level.SEVERE, null, ex);
        }
        // TODO - error checking/functionality
        return new GraphicPoint(vg.getWindowPointOf(coord), 5 * value);
    }

    public Point2D[] primitivesAt(List<Point2D.Double> coords, VisometryGraphics<Point2D.Double> vg) {
        Point2D[] result = new Point2D[coords.size()];
        int i = 0;
        for (Point2D.Double c : coords)
            result[i++] = primitiveAt(c, vg);
        return result;
    }
}
