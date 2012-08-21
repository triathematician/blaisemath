package org.bm.blaise.specto.plane.diffeq;

/**
 * PlaneVectorField.java
 * Created on Sep 3, 2009
 */



import java.awt.geom.Point2D;
import java.util.List;
import javax.swing.event.ChangeEvent;
import org.apache.commons.math.analysis.MultivariateVectorialFunction;
import org.bm.blaise.specto.plottable.SamplerPlottable;
import org.bm.blaise.specto.primitive.ArrowStyle;
import org.bm.blaise.specto.primitive.BlaisePalette;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import scio.coordinate.sample.SampleGenerator;
import util.ChangeEventHandler;

/**
 * <p>
 *   <code>PlaneVectorField</code> displays a vector field, specified by an underlying
 *   function with 2 inputs and 2 outputs (a <code>MultivariateVectorialFunction</code>).
 * </p>
 *
 * @author Elisha Peterson
 */
public class PlaneVectorField extends SamplerPlottable<double[], Point2D[], Point2D.Double> {

    /** Underlying function */
    MultivariateVectorialFunction func;

    /** Whether arrows are centered at sample points. */
    boolean centered = false;
    /** Multiplier for vec field length. */
    double lengthMultiplier = 1;

    /**
     * Construct the vector field.
     * @param func underlying function that determines the vectors
     */
    public PlaneVectorField(MultivariateVectorialFunction func, SampleGenerator<Point2D.Double> sg) {
        super(sg);
        setFunction(func);
        setStyle(new ArrowStyle(BlaisePalette.STANDARD.vector(), ArrowStyle.Shape.REGULAR, 5));
    }

    @Override
    public String toString() {
        return "Vector Field [" + getSampleGenerator() + "]";
    }

    //
    // VALUE PATTERNS
    //

    /** @return function describing the field */
    public MultivariateVectorialFunction getFunction() {
        return func;
    }

    /**
     * Sets the function for the field.
     * @param func the function
     */
    public void setFunction(MultivariateVectorialFunction func) {
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

    public boolean isCentered() {
        return centered;
    }

    public void setCentered(boolean centered) {
        if (this.centered != centered) {
            this.centered = centered;
            needsPrimitiveComputation = true;
            fireStateChanged();
        }
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

    protected double[] getValue(Point2D.Double sample) {
        try {
            return func.value(new double[]{sample.x, sample.y});
        } catch (Exception ex) {
            return new double[] { Double.NaN, Double.NaN };
        }
    }

    @Override
    protected double getScaleFactor(List<double[]> values, Point2D.Double diff) {
        double maxLengthSq = 0;
        for (double[] val : values)
            maxLengthSq = Math.max( maxLengthSq, val[0]*val[0] + val[1]*val[1]);
        return lengthMultiplier 
                * ( maxLengthSq == 0 ? 1
                : .95 * Math.min(Math.abs(diff.x), Math.abs(diff.y)) 
                / Math.sqrt( maxLengthSq ) );
    }

    /** Constructs the graphic arrows to be actually displayed. */
    protected Point2D[][] getPrimitives(List<Point2D.Double> samples, List<double[]> values, double scaleFactor, VisometryGraphics<Point2D.Double> vg) {
        int n = samples.size();
        Point2D[][] arrows = new Point2D[n][2];
        Point2D.Double anchor, head;
        if (centered)
            for (int i = 0; i < n; i++) {
                anchor = new Point2D.Double(
                        samples.get(i).x - scaleFactor * values.get(i)[0] / 2.0,
                        samples.get(i).y - scaleFactor * values.get(i)[1] / 2.0
                        );
                head = new Point2D.Double(
                        samples.get(i).x + scaleFactor * values.get(i)[0] / 2.0,
                        samples.get(i).y + scaleFactor * values.get(i)[1] / 2.0);
                arrows[i][0] = vg.getWindowPointOf(anchor);
                arrows[i][1] = vg.getWindowPointOf(head);
            }
        else
            for (int i = 0; i < n; i++) {
                anchor = samples.get(i);
                head = new Point2D.Double(
                        anchor.x + scaleFactor * values.get(i)[0],
                        anchor.y + scaleFactor * values.get(i)[1]);
                arrows[i][0] = vg.getWindowPointOf(anchor);
                arrows[i][1] = vg.getWindowPointOf(head);
            }
        return arrows;
    }
    
    //
    // EVENT HANDLING
    //

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == func)
            needsComputation = true;
        super.stateChanged(e);
    }
}
