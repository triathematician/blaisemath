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
    public SpaceVectorField(MultivariateVectorialFunction func, SampleGenerator<Point3D> ssg) {
        super(ssg);
        setFunction(func);
        setStyle(new ArrowStyle(BlaisePalette.STANDARD.vector(), ArrowStyle.Shape.REGULAR, 5));
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
            if (this.func instanceof ChangeEventHandler)
                ((ChangeEventHandler)this.func).removeChangeListener(this);
            this.func = func;
            if (func instanceof ChangeEventHandler)
                ((ChangeEventHandler)func).addChangeListener(this);
            fireStateChanged();
        }
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
        if (this.lengthMultiplier != lengthMultiplier) {
            this.lengthMultiplier = lengthMultiplier;
            needsPrimitiveComputation = true;
            fireStateChanged();
        }
    }

    public ArrowStyle getStyle() {
        return (ArrowStyle) style;
    }

    public void setStyle(ArrowStyle arrowStyle) {
        if (this.style != arrowStyle) {
            this.style = arrowStyle;
            fireStateChanged();
        }
    }
    
    //
    // DRAW METHODS
    //

    protected double[] getValue(Point3D sample) {
        try {
            return func.value(new double[]{sample.x, sample.y, sample.z});
        } catch (Exception ex) {
            return new double[] { Double.NaN, Double.NaN, Double.NaN };
        }
    }

    @Override
    protected double getScaleFactor(List<double[]> values, Point3D diff) {
        double maxLengthSq = 0;
        for (double[] val : values)
            maxLengthSq = Math.max( maxLengthSq, val[0]*val[0] + val[1]*val[1] + val[2]*val[2]);
        return lengthMultiplier
                * ( maxLengthSq == 0 ? 1
                : .95 * Math.min(Math.min(Math.abs(diff.x), Math.abs(diff.y)), Math.abs(diff.z))
                    / Math.sqrt( maxLengthSq ) );
    }

    /** Constructs the graphic arrows to be actually displayed. */
    protected Point3D[][] getPrimitives(List<Point3D> samples, List<double[]> values, double scaleFactor, VisometryGraphics<Point3D> vg) {
        int n = samples.size();
        Point3D[][] arrows = new Point3D[n][2];
        Point3D sample;
        double[] value = new double[3];
        if (centered)
            for (int i = 0; i < n; i++) {
                sample = samples.get(i);
                value = values.get(i);
                arrows[i][0] = new Point3D(
                        sample.x - scaleFactor * value[0] / 2.0,
                        sample.y - scaleFactor * value[1] / 2.0,
                        sample.z - scaleFactor * value[2] / 2.0
                        );
                arrows[i][1] = new Point3D(
                        sample.x + scaleFactor * value[0] / 2.0,
                        sample.y + scaleFactor * value[1] / 2.0,
                        sample.z + scaleFactor * value[2] / 2.0
                        );
            }
        else
            for (int i = 0; i < n; i++) {
                sample = samples.get(i);
                value = values.get(i);
                arrows[i][0] = sample;
                arrows[i][1] = new Point3D(
                        sample.x + scaleFactor * value[0],
                        sample.y + scaleFactor * value[1],
                        sample.z + scaleFactor * value[2]
                        );
            }
        return arrows;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == func)
            needsComputation = true;
        super.stateChanged(e);
    }
}
