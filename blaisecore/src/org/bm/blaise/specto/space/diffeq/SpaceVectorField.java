/**
 * PlaneVectorField.java
 * Created on Sep 3, 2009
 */

package org.bm.blaise.specto.space.diffeq;

import java.awt.Graphics2D;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.MultivariateVectorialFunction;
import org.bm.blaise.specto.plottable.SamplerPlottable;
import org.bm.blaise.specto.primitive.ArrowStyle;
import org.bm.blaise.specto.primitive.BlaisePalette;
import org.bm.blaise.specto.primitive.PrimitiveStyle;
import org.bm.blaise.specto.space.SpaceGraphics;
import org.bm.blaise.specto.visometry.Visometry;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import scio.coordinate.Point3D;
import scio.coordinate.sample.SampleGenerator;
import util.ChangeEventHandler;

/**
 * <p>
 *   <code>PlaneVectorField</code> displays a vector field, specified by an underlying
 *   function with 2 inputs and 2 outputs (a <code>MultivariateVectorialFunction</code>).
 *   One must also supply a class that desribes where the field should be sampled.
 * </p>
 *
 * @author Elisha Peterson
 */
public class SpaceVectorField extends SamplerPlottable<double[], Point3D[], Point3D> {

    //
    // PROPERTIES
    //

    /** Style for drawing the vectors. */
    static private class ArrowStyle3D extends PrimitiveStyle<Point3D[]> {
        public ArrowStyle arrow = new ArrowStyle(BlaisePalette.STANDARD.vector(), ArrowStyle.Shape.REGULAR, 5);
        @Override
        public void draw(Graphics2D canvas, Point3D[] primitive, boolean selected) {}
    }
    private static PrimitiveStyle<Point3D[]> DEFAULT_STYLE = new ArrowStyle3D();

    /** Underlying function */
    MultivariateVectorialFunction func;

    /** Whether arrows are centered at sample points. */
    boolean centered;

    /**
     * Construct the vector field.
     * @param func underlying function that determines the vectors
     */
    public SpaceVectorField(MultivariateVectorialFunction func, SampleGenerator<Point3D> ssg) {
        super(DEFAULT_STYLE, ssg);
        setFunction(func);
    }

    @Override
    public String toString() {
        return "Vector Field [" + getSampleGenerator() + "]";
    }

    //
    // GETTERS & SETTERS
    //

    /** @return function describing the field */
    public MultivariateVectorialFunction getFunction() {
        return func;
    }

    /** Sets the function for the field.
     * @param func the function
     */
    public void setFunction(MultivariateVectorialFunction func) {
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

    public ArrowStyle getStyle() {
        return ((ArrowStyle3D)style).arrow;
    }

    public void setStyle(ArrowStyle arrowStyle) {
        ((ArrowStyle3D)style).arrow = arrowStyle;
    }

    public boolean isCentered() {
        return centered;
    }

    public void setCentered(boolean centered) {
        this.centered = centered;
    }
    
    //
    // DRAW METHODS
    //

    /**
     * Scales an array of vectors, ensuring that the maximum value of the length is less than specified max
     *
     * @param maxLength the maximum permissible length
     * @param exponent the exponent to scale the radius by (1 is linear, 0.5 makes fewer small circles, 2 makes fewer large circles)
     * @param centered <code>true</code> if the vectors should scale about their centers
     */
    public static void scaleVectors(Point3D[][] vectors, double maxLength, double exponent, boolean centered) {
        double mr = 0;
        double l = 0;
        for (int i = 0; i < vectors.length; i++) {
            l = vectors[i][0].distanceSq(vectors[i][1]);
            if (l < 1e10 && l > mr) {
                mr = l;
            }
        }
        mr = Math.sqrt(mr);
        for (int i = 0; i < vectors.length; i++) {
            double length = vectors[i][0].distance(vectors[i][1]) / mr;
            length = maxLength * Math.pow(length, exponent);
            if (centered) {
                Point3D dNorm = vectors[i][1].minus(vectors[i][0]).normalized();
                Point3D half = vectors[i][0].plus(vectors[i][1]).times(.5);
                vectors[i] = new Point3D[]{ half.minus(dNorm.times(length/2)), half.plus(dNorm.times(length/2)) };
            } else {
                Point3D dNorm = vectors[i][1].minus(vectors[i][0]).normalized();
                vectors[i][1] = vectors[i][0].plus(dNorm.times(length));
            }
        }
    }

    @Override
    public void scalePrimitives(Visometry vis) {
        scaleVectors(primitives, ssg.getSampleDiff().magnitude() / 2, 0.8, centered);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        super.stateChanged(e);
        visometryChanged(null, null);
    }

    @Override
    public void draw(VisometryGraphics<Point3D> vg) {
        if (primitives != null) {
            ((SpaceGraphics) vg).addToScene(primitives, style);
        }
    }

    public Point3D[] primitiveAt(Point3D coord, VisometryGraphics<Point3D> vg) {
        double[] value = new double[]{0, 0, 0};
        try {
            value = func.value(new double[]{coord.x, coord.y, coord.z});
        } catch (FunctionEvaluationException ex) {
            Logger.getLogger(SpaceVectorField.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(SpaceVectorField.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (centered)
            return new Point3D[] {
                new Point3D(coord.x - value[0]/2.0, coord.y - value[1]/2.0, coord.z - value[2]/2.0),
                new Point3D(coord.x + value[0]/2.0, coord.y + value[1]/2.0, coord.z + value[2]/2.0) };
        else
            return new Point3D[] {
                coord,
                new Point3D(coord.x + value[0], coord.y + value[1], coord.z + value[2]) };
    }

    public Point3D[][] primitivesAt(List<Point3D> coords, VisometryGraphics<Point3D> vg) {
        Point3D[][] result = new Point3D[coords.size()][2];
        int i = 0;
        for (Point3D c : coords)
            result[i++] = primitiveAt(c, vg);
        return result;
    }
}
