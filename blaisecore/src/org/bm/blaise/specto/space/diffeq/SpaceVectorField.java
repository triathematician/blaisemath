/**
 * PlaneVectorField.java
 * Created on Sep 3, 2009
 */

package org.bm.blaise.specto.space.diffeq;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.MultivariateVectorialFunction;
import org.bm.blaise.specto.plottable.VPrimitiveMappingPlottable;
import org.bm.blaise.specto.primitive.ArrowStyle;
import org.bm.blaise.specto.primitive.BlaisePalette;
import org.bm.blaise.specto.space.SpaceGraphics;
import org.bm.blaise.specto.visometry.Visometry;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import scio.coordinate.P3D;
import scio.coordinate.sample.SampleCoordinateSetGenerator;
import scio.function.utils.SampleField3D;

/**
 * <p>
 *   <code>PlaneVectorField</code> displays a vector field, specified by an underlying
 *   function with 2 inputs and 2 outputs (a <code>MultivariateVectorialFunction</code>).
 * </p>
 *
 * @author Elisha Peterson
 */
public class SpaceVectorField extends VPrimitiveMappingPlottable<P3D, P3D[]> {

    // SAMPLES

    SampleField3D sample = SampleField3D.WHIRLPOOL;

    public SampleField3D getSample() {
        return sample;
    }

    public void setSample(SampleField3D sample) {
        this.sample = sample;
        func = sample;
    }

    /** Style for drawing the vectors. */
    private static ArrowStyle DEFAULT_STYLE = new ArrowStyle(BlaisePalette.STANDARD.vector(), ArrowStyle.ArrowShape.REGULAR, 5);

    /** Underlying function */
    MultivariateVectorialFunction func;

    /** Whether arrows are centered at sample points. */
    boolean centered;

    /**
     * Construct the vector field.
     * @param func underlying function that determines the vectors
     */
    public SpaceVectorField(MultivariateVectorialFunction func, SampleCoordinateSetGenerator<P3D> ssg) {
        super(DEFAULT_STYLE, ssg);
        setFunc(func);
    }


    //
    //
    // BEAN PATTERNS
    //
    //

    /** @return function describing the field */
    public MultivariateVectorialFunction getFunc() {
        return func;
    }

    /** Sets the function for the field.
     * @param func the function
     */
    public void setFunc(MultivariateVectorialFunction func) {
        this.func = func;
    }

    public ArrowStyle getStyle() {
        return (ArrowStyle) style;
    }

    public void setStyle(ArrowStyle arrowStyle) {
        this.style = arrowStyle;
    }

    public boolean isCentered() {
        return centered;
    }

    public void setCentered(boolean centered) {
        this.centered = centered;
    }

    
    //
    //
    // DRAW METHODS
    //
    //

    /**
     * Scales an array of vectors, ensuring that the maximum value of the length is less than specified max
     *
     * @param maxLength the maximum permissible length
     * @param exponent the exponent to scale the radius by (1 is linear, 0.5 makes fewer small circles, 2 makes fewer large circles)
     * @param centered <code>true</code> if the vectors should scale about their centers
     */
    public static void scaleVectors(P3D[][] vectors, double maxLength, double exponent, boolean centered) {
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
                P3D dNorm = vectors[i][1].minus(vectors[i][0]).normalized();
                P3D half = vectors[i][0].plus(vectors[i][1]).times(.5);
                vectors[i] = new P3D[]{ half.minus(dNorm.times(length/2)), half.plus(dNorm.times(length/2)) };
            } else {
                P3D dNorm = vectors[i][1].minus(vectors[i][0]).normalized();
                vectors[i][1] = vectors[i][0].plus(dNorm.times(length));
            }
        }
    }

    @Override
    public void scalePrimitives(Visometry vis) {
        scaleVectors(primitives, ssg.getSampleDiff().magnitude() / 2, 0.8, centered);
    }

    @Override
    public void paintComponent(VisometryGraphics<P3D> vg) {
        if (primitives != null) {
            ((SpaceGraphics) vg).addToScene(primitives, style);
        }
    }

    public P3D[] primitiveAt(P3D coord, Visometry<P3D> vis, VisometryGraphics<P3D> vg) {
        double[] value = new double[]{0, 0, 0};
        try {
            value = func.value(new double[]{coord.x, coord.y, coord.z});
        } catch (FunctionEvaluationException ex) {
            Logger.getLogger(SpaceVectorField.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(SpaceVectorField.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (centered) {
            return new P3D[] {
                new P3D(coord.x - value[0]/2.0, coord.y - value[1]/2.0, coord.z - value[2]/2.0),
                new P3D(coord.x + value[0]/2.0, coord.y + value[1]/2.0, coord.z + value[2]/2.0) };
        } else {
            return new P3D[] {
                coord,
                new P3D(coord.x + value[0], coord.y + value[1], coord.z + value[2]) };
        }
    }

    public P3D[][] primitivesAt(List<P3D> coords, Visometry<P3D> vis, VisometryGraphics<P3D> vg) {
        P3D[][] result = new P3D[coords.size()][2];
        int i = 0;
        for (P3D c : coords) {
            result[i] = primitiveAt(c, vis, vg);
            i++;
        }
        return result;
    }

}
