package org.bm.blaise.specto.plane.diffeq;

/**
 * PlaneVectorField.java
 * Created on Sep 3, 2009
 */



import java.awt.geom.Point2D;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.MultivariateVectorialFunction;
import org.bm.blaise.specto.plane.PlaneVisometry;
import org.bm.blaise.specto.plottable.VPrimitiveMappingPlottable;
import org.bm.blaise.specto.primitive.GraphicArrow;
import org.bm.blaise.specto.primitive.ArrowStyle;
import org.bm.blaise.specto.primitive.BlaisePalette;
import org.bm.blaise.specto.visometry.Visometry;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import scio.coordinate.sample.SampleCoordinateSetGenerator;
import util.ChangeEventHandler;

/**
 * <p>
 *   <code>PlaneVectorField</code> displays a vector field, specified by an underlying
 *   function with 2 inputs and 2 outputs (a <code>MultivariateVectorialFunction</code>).
 * </p>
 *
 * @author Elisha Peterson
 */
public class PlaneVectorField extends VPrimitiveMappingPlottable<Point2D.Double, GraphicArrow, Point2D.Double> {

    /** Style for drawing the vectors. */
    private static ArrowStyle DEFAULT_STYLE = new ArrowStyle(BlaisePalette.STANDARD.vector(), ArrowStyle.Shape.REGULAR, 5);

    /** Underlying function */
    MultivariateVectorialFunction func;

    /** Whether arrows are centered at sample points. */
    boolean centered;
    /** Multiplier for vec field length. */
    double lengthMultiplier = 1;

    /**
     * Construct the vector field.
     * @param func underlying function that determines the vectors
     */
    public PlaneVectorField(MultivariateVectorialFunction func, SampleCoordinateSetGenerator<Point2D.Double> ssg) {
        super((ArrowStyle) DEFAULT_STYLE.clone(), ssg);
        setFunc(func);
    }

    @Override
    public String toString() {
        return "Vector Field [" + ssg + "]";
    }

    //
    // BEAN PATTERNS
    //

    /** @return function describing the field */
    public MultivariateVectorialFunction getFunc() {
        return func;
    }

    /** Sets the function for the field.
     * @param func the function
     */
    public void setFunc(MultivariateVectorialFunction func) {
        if (func != null && this.func != func) {
            if (this.func instanceof ChangeEventHandler) {
                ((ChangeEventHandler)this.func).removeChangeListener(this);
            }
            this.func = func;
            if (func instanceof ChangeEventHandler) {
                ((ChangeEventHandler)func).addChangeListener(this);
            }
            fireStateChanged();
        }
    }

    public ArrowStyle getVectorStyle() {
        return (ArrowStyle) style;
    }

    public void setVectorStyle(ArrowStyle arrowStyle) {
        this.style = arrowStyle;
    }

    public boolean isCentered() {
        return centered;
    }

    public void setCentered(boolean centered) {
        this.centered = centered;
    }

    public double getLengthMultiplier() {
        return lengthMultiplier;
    }

    public void setLengthMultiplier(double lengthMultiplier) {
        this.lengthMultiplier = lengthMultiplier;
    }
    
    @Override
    public void stateChanged(ChangeEvent e) {
        super.stateChanged(e);
        // TODO - need to tell the plottable to redraw at this point
    }
    
    //
    // DRAW METHODS
    //

    @Override
    public void scalePrimitives(Visometry vis) {
        PlaneVisometry pv = (PlaneVisometry) vis;
        double maxRad = Math.min(Math.abs(ssg.getSampleDiff().x * pv.getScaleX()), Math.abs(ssg.getSampleDiff().y * pv.getScaleY()));
        GraphicArrow.scaleVectors(primitives, lengthMultiplier * maxRad, 0.9, centered);
    }

    public GraphicArrow primitiveAt(Point2D.Double coord, VisometryGraphics<Point2D.Double> vg) {
        MultivariateVectorialFunction useFunc = getFunc();
        double[] value = new double[]{0, 0};
        try {
            value = useFunc.value(new double[]{coord.x, coord.y});
        } catch (FunctionEvaluationException ex) {
            Logger.getLogger(PlaneVectorField.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(PlaneVectorField.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (centered)
            return new GraphicArrow(
                    vg.getWindowPointOf(new Point2D.Double(coord.x - value[0]/2.0, coord.y - value[1]/2.0)),
                    vg.getWindowPointOf(new Point2D.Double(coord.x + value[0]/2.0, coord.y + value[1]/2.0)));
        else
            return new GraphicArrow(
                    vg.getWindowPointOf(coord),
                    vg.getWindowPointOf(new Point2D.Double(coord.x + value[0], coord.y + value[1])));
    }

    public GraphicArrow[] primitivesAt(List<Point2D.Double> coords, VisometryGraphics<Point2D.Double> vg) {
        GraphicArrow[] result = new GraphicArrow[coords.size()];
        int i = 0;
        for (Point2D.Double c : coords)
            result[i++] = primitiveAt(c, vg);
        return result;
    }
}
