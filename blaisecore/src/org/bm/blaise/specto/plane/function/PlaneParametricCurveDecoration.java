/**
 * PlaneParametricCurveDecoration.java
 * Created on Sep 3, 2009
 */

package org.bm.blaise.specto.plane.function;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.DifferentiableUnivariateVectorialFunction;
import org.apache.commons.math.analysis.UnivariateVectorialFunction;
import org.bm.blaise.specto.plane.PlaneVisometry;
import org.bm.blaise.specto.plottable.VPrimitiveMappingPlottable;
import org.bm.blaise.specto.primitive.GraphicArrow;
import org.bm.blaise.specto.primitive.ArrowStyle;
import org.bm.blaise.specto.primitive.BlaisePalette;
import org.bm.blaise.specto.visometry.Visometry;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import scio.coordinate.sample.SampleCoordinateSetGenerator;
import scio.function.utils.MultivariableUtils;

/**
 * <p>
 *   <code>PlaneParametricCurveDecoration</code> displays decorations on a parametric curve (e.g. tangent vectors, acceleration vectors)
 * </p>
 *
 * @author Elisha Peterson
 */
public class PlaneParametricCurveDecoration extends VPrimitiveMappingPlottable<Double, GraphicArrow, Point2D.Double> {

    /** Style for drawing the vectors. */
    private static ArrowStyle DEFAULT_STYLE = new ArrowStyle(BlaisePalette.STANDARD.vector(), ArrowStyle.Shape.REGULAR, 5);

    /** Underlying function */
    DifferentiableUnivariateVectorialFunction curve;

    /**
     * Construct the decorations.
     * @param curve underlying function that determines the vectors
     */
    public PlaneParametricCurveDecoration(UnivariateVectorialFunction curve, SampleCoordinateSetGenerator<Double> ssg) {
        super((ArrowStyle) DEFAULT_STYLE.clone(), ssg);
        setCurve(curve);
    }

    @Override
    public String toString() {
        return "Curve Decorations [" + ssg + "]";
    }

    //
    // BEAN PATTERNS
    //

    /** @return function describing the field */
    public DifferentiableUnivariateVectorialFunction getCurve() {
        return curve;
    }

    /** Sets the function for the field.
     * @param curve the function
     */
    public void setCurve(UnivariateVectorialFunction curve) {
        if (curve != null && this.curve != curve) {
            if (curve instanceof DifferentiableUnivariateVectorialFunction) {
                this.curve = (DifferentiableUnivariateVectorialFunction) curve;
            } else {
                this.curve = MultivariableUtils.asDifferentiableFunction(curve, 1e-10);
            }
            // TODO - change listening
            fireStateChanged();
        }
    }
    
    public ArrowStyle getVectorStyle() {
        return (ArrowStyle) style;
    }

    public void setVectorStyle(ArrowStyle arrowStyle) {
        this.style = arrowStyle;
    }
    
    //
    // DRAW METHODS
    //

    @Override
    public void scalePrimitives(Visometry<Point2D.Double> vis) {
        GraphicArrow.scaleVectors(primitives, 50.0, 1.0, false);
    }

    public GraphicArrow primitiveAt(Double coord, VisometryGraphics<Point2D.Double> vg) {
        double[] deriv = new double[]{0, 0};
        double[] point = new double[]{0, 0};
        try {
            point = curve.value(coord);
            deriv = curve.derivative().value(coord);
        } catch (FunctionEvaluationException ex) {
            Logger.getLogger(PlaneParametricCurveDecoration.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(PlaneParametricCurveDecoration.class.getName()).log(Level.SEVERE, null, ex);
        }
        GraphicArrow result = new GraphicArrow(
                vg.getWindowPointOf(new Point2D.Double(point[0], point[1])),
                vg.getWindowPointOf(new Point2D.Double(point[0] + deriv[0], point[1] + deriv[1])));
        return result;
    }

    public GraphicArrow[] primitivesAt(List<Double> coords, VisometryGraphics<Point2D.Double> vg) {
        GraphicArrow[] result = new GraphicArrow[coords.size()];
        int i = 0;
        for (Double c : coords)
            result[i++] = primitiveAt(c, vg);
        return result;
    }
}
