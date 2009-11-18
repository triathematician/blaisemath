/**
 * PlaneVectorField.java
 * Created on Sep 3, 2009
 */

package org.bm.blaise.specto.plane.function;

import java.util.List;
import scio.coordinate.sample.SampleCoordinateSetGenerator;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.MultivariateRealFunction;
import org.bm.blaise.specto.plane.PlaneVisometry;
import org.bm.blaise.specto.plottable.VPrimitiveMappingPlottable;
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
public class PlaneSurfaceFunction extends VPrimitiveMappingPlottable<Point2D.Double, GraphicPoint> {

    /** Style to use for plotting points in the grid. */
    private static PointStyle DEFAULT_STYLE = new PointStyle(new Color(100, 100, 0), new Color(200, 200, 100));

    /** Multiplier for dot size. */
    double lengthMultiplier = 1;

    /** Underlying function */
    MultivariateRealFunction func;

    public PlaneSurfaceFunction(MultivariateRealFunction func, SampleCoordinateSetGenerator<Point2D.Double> ssg) {
        super(DEFAULT_STYLE, ssg);
        setFunc(func);
    }

    //
    //
    // BEAN PATTERNS
    //
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

    public PointStyle getStyle() {
        return (PointStyle) style;
    }

    public void setStyle(PointStyle style) {
        this.style = style;
    }

    public double getLengthMultiplier() {
        return lengthMultiplier;
    }

    public void setLengthMultiplier(double lengthMultiplier) {
        this.lengthMultiplier = lengthMultiplier;
    }


    //
    //
    // PrimitiveMapper INTERFACE METHODS
    //
    //

    @Override
    public void scalePrimitives(Visometry vis) {
        PlaneVisometry pv = (PlaneVisometry) vis;
        double maxRad = Math.min(Math.abs(ssg.getSampleDiff().x * pv.getScaleX()), Math.abs(ssg.getSampleDiff().y * pv.getScaleY()));
        GraphicPoint.scalePoints(primitives, lengthMultiplier * maxRad / 1.5, 0.5);
    }

    public GraphicPoint primitiveAt(Point2D.Double coord, Visometry<Point2D.Double> vis, VisometryGraphics<Point2D.Double> vg) {
        double value = 0;
        try {
            value = func.value(new double[]{coord.x, coord.y});
        } catch (FunctionEvaluationException ex) {
            Logger.getLogger(PlaneSurfaceFunction.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(PlaneSurfaceFunction.class.getName()).log(Level.SEVERE, null, ex);
        }
        // TODO - error checking/functionality
        return new GraphicPoint(vis.getWindowPointOf(coord), 5 * value);
    }

    public GraphicPoint[] primitivesAt(List<Point2D.Double> coords, Visometry<Point2D.Double> vis, VisometryGraphics<Point2D.Double> vg) {
        GraphicPoint[] result = new GraphicPoint[coords.size()];
        int i = 0;
        for (Point2D.Double c : coords) {
            result[i] = primitiveAt(c, vis, vg);
            i++;
        }
        return result;
    }
}
