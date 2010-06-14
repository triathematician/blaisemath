/**
 * PlaneSurfaceFunction.java
 * Created Sep 2009
 */

package visometry.plane;

import coordinate.DomainHint;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.MultivariateRealFunction;
import primitive.GraphicPointRadius;
import primitive.style.PointDirStyle;
import primitive.style.PointRadiusStyle;
import scio.coordinate.sample.SampleSet;
import util.ChangeBroadcaster;
import visometry.VPrimitiveEntry;
import visometry.plottable.Plottable;

/**
 * <p>
 *   <code>PlaneSurfaceFunction</code> represents a function that has two inputs and one output, i.e. of the form z=f(x,y).
 *   The function is displayed as a collection of points of varying radii.
 * </p>
 *
 * @author Elisha Peterson
 */
public class PlaneSurfaceFunction extends Plottable<Point2D.Double> {

    /** Underlying function */
    MultivariateRealFunction func;
    /** Location of sampling points */
    SampleSet<Point2D.Double> sampler;

    /** Determines the "ideal" spacing between elements of the field, in terms of pixels. */
    private int DEFAULT_PIXEL_SPACING = 30;
    /** Multiplier for vec field length. */
    double radiusMultiplier = 1;

    /** Entry containing the displayed arrows and style */
    VPrimitiveEntry entry;

    /** Construct with a default vector field. */
    public PlaneSurfaceFunction() {
        this(new MultivariateRealFunction(){
            public double value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
                return Math.sin(point[0]) * Math.sqrt(Math.abs(point[1]));
            }
        });
    }

    /**
     * Construct the vector field.
     * @param func underlying function that determines the vectors
     */
    public PlaneSurfaceFunction(MultivariateRealFunction func) {
        addPrimitive(entry = new VPrimitiveEntry(null, new PointRadiusStyle()));
        setFunction(func);
    }

    @Override
    public String toString() {
        return "Surface Function";
    }

    //
    // BEAN PROPERTY PATTERNS
    //

    /** @return function describing the field */
    public MultivariateRealFunction getFunction() {
        return func;
    }

    /**
     * Sets the function for the field.
     * @param f the function
     */
    public void setFunction(MultivariateRealFunction f) {
        if (f != null && this.func != f) {
            if (this.func instanceof ChangeBroadcaster) ((ChangeBroadcaster)this.func).removeChangeListener(this);
            this.func = f;
            if (f instanceof ChangeBroadcaster) ((ChangeBroadcaster)f).addChangeListener(this);
            firePlottableChanged();
        }
    }

    public double getRadiusMultiplier() {
        return radiusMultiplier;
    }

    public void setRadiusMultiplier(double radiusMultiplier) {
        if (this.radiusMultiplier != radiusMultiplier) {
            this.radiusMultiplier = radiusMultiplier;
            firePlottableChanged();
        }
    }

    //
    // COMPUTATIONS
    //

    @Override
    protected void recompute() {
        if (sampler == null) {
            sampler = parent.requestScreenSampleDomain("xy", Point2D.Double.class, DEFAULT_PIXEL_SPACING, DomainHint.PREFER_INTS);
            if (sampler == null)
                throw new IllegalStateException("Unable to retrieve appropriate domain from parent class!");
            ((ChangeBroadcaster)sampler).addChangeListener(this);
        }

        List<Point2D.Double> samples = sampler.getSamples();
        int n = samples.size();
        double[] values = new double[n];
        try {
            for (int i = 0; i < n; i++) {
                values[i] = func.value(new double[]{samples.get(i).x, samples.get(i).y});
            }
        } catch (FunctionEvaluationException ex) {
            Logger.getLogger(PlaneSurfaceFunction.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(PlaneSurfaceFunction.class.getName()).log(Level.SEVERE, null, ex);
        }

        GraphicPointRadius[] pts = new GraphicPointRadius[n];
        for (int i = 0; i < n; i++)
            pts[i] = new GraphicPointRadius<Point2D.Double>(samples.get(i), values[i]);

        ((PointRadiusStyle)entry.style).setMaxRadius(DEFAULT_PIXEL_SPACING * radiusMultiplier);

        entry.local = pts;
        entry.needsConversion = true;
    }



    //
    // STYLE METHODS
    //

    /** @return current style of stroke for this plottable */
    public PointRadiusStyle getStyle() { return (PointRadiusStyle) entry.style; }
    /** Set current style of stroke for this plottable */
    public void setStyle(PointRadiusStyle newValue) { if (entry.style != newValue) { entry.style = newValue; firePlottableStyleChanged(); } }


}
