/**
 * PlaneVectorField.java
 * Created on Sep 3, 2009
 */

package visometry.plane;

import coordinate.ScreenSampleDomainProvider;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.MultivariateVectorialFunction;
import primitive.GraphicPointDir;
import primitive.style.ArrowStyle;
import primitive.style.PointDirStyle;
import scio.coordinate.sample.SampleSet;
import scio.function.utils.DemoField2D;
import util.ChangeBroadcaster;
import visometry.VPrimitiveEntry;
import visometry.plottable.Plottable;

/**
 * <p>
 *   <code>PlaneVectorField</code> displays a vector field, specified by an underlying
 *   function with 2 inputs and 2 outputs (a <code>MultivariateVectorialFunction</code>).
 * </p>
 *
 * @author Elisha Peterson
 */
public class PlaneVectorField extends Plottable<Point2D.Double> {

    /** Model function */
    MultivariateVectorialFunction func;
    /** Location of sampling points */
    SampleSet<Point2D.Double> sampler;

    /** Determines the "ideal" spacing between elements of the field, in terms of pixels. */
    private int DEFAULT_PIXEL_SPACING = 60;
    /** Whether arrows are centered at sample points. */
    boolean centered = false;
    /** Multiplier for vec field length. */
    double lengthMultiplier = 1;

    /** Entry containing the displayed arrows and style */
    VPrimitiveEntry entry;

    /** Construct with a default vector field. */
    public PlaneVectorField() {
        this(DemoField2D.AROUND);
    }

    /**
     * Construct the vector field.
     * @param func underlying function that determines the vectors
     */
    public PlaneVectorField(MultivariateVectorialFunction func) {
        addPrimitive(entry = new VPrimitiveEntry(null, new PointDirStyle(new Color(96, 192, 96), ArrowStyle.ArrowShape.REGULAR, 5)));
        setFunction(func);
    }

    @Override
    public String toString() {
        return "Vector Field";
    }

    //
    // BEAN PROPERTY PATTERNS
    //

    /** @return function describing the field */
    public MultivariateVectorialFunction getFunction() {
        return func;
    }

    /**
     * Sets the function for the field.
     * @param f the function
     */
    public void setFunction(MultivariateVectorialFunction f) {
        if (f != null && this.func != f) {
            if (this.func instanceof ChangeBroadcaster) ((ChangeBroadcaster)this.func).removeChangeListener(this);
            this.func = f;
            if (f instanceof ChangeBroadcaster) ((ChangeBroadcaster)f).addChangeListener(this);
            firePlottableChanged();
        }
    }

    /** @return true if current display style centers arrows about the sample points */
    public boolean isCentered() {
        return centered;
    }

    /** Sets style of display of arrows, based on whether or not they are centered around the sample points. */
    public void setCentered(boolean centered) {
        if (this.centered != centered) {
            this.centered = centered;
            firePlottableChanged();
        }
    }

    public double getLengthMultiplier() {
        return lengthMultiplier;
    }

    public void setLengthMultiplier(double lengthMultiplier) {
        if (this.lengthMultiplier != lengthMultiplier) {
            this.lengthMultiplier = lengthMultiplier;
            firePlottableChanged();
        }
    }

    //
    // COMPUTATIONS
    //

    @Override
    protected void recompute() {
        if (sampler == null) {
            sampler = parent.requestScreenSampleDomain("xy", Point2D.Double.class, DEFAULT_PIXEL_SPACING, ScreenSampleDomainProvider.HINT_PREFER_WHOLE_NUMBERS);
            if (sampler == null)
                throw new IllegalStateException("Unable to retrieve appropriate domain from parent class!");
            ((ChangeBroadcaster)sampler).addChangeListener(this);
        }

        List<Point2D.Double> samples = sampler.getSamples();
        int n = samples.size();
        double[][] values = new double[n][2];
        try {
            for (int i = 0; i < n; i++) {
                values[i] = func.value(new double[]{samples.get(i).x, samples.get(i).y});
            }
        } catch (FunctionEvaluationException ex) {
            Logger.getLogger(PlaneVectorField.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(PlaneVectorField.class.getName()).log(Level.SEVERE, null, ex);
        }

        GraphicPointDir[] arrows = new GraphicPointDir[n];
        for (int i = 0; i < n; i++)
            arrows[i] = new GraphicPointDir<Point2D.Double>(samples.get(i), new Point2D.Double(values[i][0], values[i][1]));

        ((PointDirStyle)entry.style).setMaxLength(DEFAULT_PIXEL_SPACING * lengthMultiplier);
        ((PointDirStyle)entry.style).setCentered(centered);

        entry.local = arrows;
        entry.needsConversion = true;
    }



    //
    // STYLE METHODS
    //

    /** @return current style of stroke for this plottable */
    public PointDirStyle getStyle() { return (PointDirStyle) entry.style; }
    /** Set current style of stroke for this plottable */
    public void setStyle(PointDirStyle newValue) { if (entry.style != newValue) { entry.style = newValue; firePlottableStyleChanged(); } }


}
