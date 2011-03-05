/**
 * SpaceVectorField.java
 * Created on Sep 3, 2009
 */

package later.visometry.space;

import coordinate.DomainHint;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.MultivariateVectorialFunction;
import graphics.GraphicPointDir;
import primitive.style.temp.ArrowStyle;
import primitive.style.temp.PointDirStyle;
import org.bm.blaise.scio.coordinate.Point3D;
import org.bm.blaise.scio.coordinate.sample.SampleSet;
import org.bm.blaise.scio.function.utils.DemoField3D;
import util.ChangeBroadcaster;
import visometry.graphics.VGraphicEntry;
import visometry.plottable.AbstractPlottable;

/**
 * <p>
 *   <code>SpaceVectorField</code> displays a vector field, specified by an underlying
 *   function with 3 inputs and 3 outputs (a <code>MultivariateVectorialFunction</code>).
 * </p>
 *
 * @author Elisha Peterson
 */
public class SpaceVectorField extends AbstractPlottable<Point3D> {

    /** Model function */
    MultivariateVectorialFunction func;
    /** Location of sampling points */
    SampleSet<Point3D> sampler;

    /** Determines the "ideal" spacing between elements of the field, in terms of pixels. */
    private int DEFAULT_PIXEL_SPACING = 60;
    /** Whether arrows are centered at sample points. */
    boolean centered = false;
    /** Multiplier for vec field length. */
    double lengthMultiplier = 1;

    /** Entry containing the displayed arrows and style */
    VGraphicEntry entry;

    /** Construct with a default vector field. */
    public SpaceVectorField() {
        this(DemoField3D.WHIRLPOOL);
    }

    /**
     * Construct the vector field.
     * @param func underlying function that determines the vectors
     */
    public SpaceVectorField(MultivariateVectorialFunction func) {
        addPrimitive(entry = new VGraphicEntry(null, new PointDirStyle(new Color(96, 192, 96), ArrowStyle.ArrowShape.REGULAR, 5)));
        setFunction(func);
    }

    @Override
    public String toString() {
        return "3D Vector Field";
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
            sampler = parent.requestScreenSampleDomain("xyz", Point3D.class, DEFAULT_PIXEL_SPACING, DomainHint.PREFER_INTS);
            if (sampler == null)
                throw new IllegalStateException("Unable to retrieve appropriate domain from parent class!");
            ((ChangeBroadcaster)sampler).addChangeListener(this);
        }

        List<Point3D> samples = sampler.getSamples();
        int n = samples.size();
        double[][] values = new double[n][3];
        try {
            for (int i = 0; i < n; i++)
                values[i] = func.value(new double[]{samples.get(i).x, samples.get(i).y, samples.get(i).z});
        } catch (FunctionEvaluationException ex) {
            Logger.getLogger(SpaceVectorField.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(SpaceVectorField.class.getName()).log(Level.SEVERE, null, ex);
        }

        GraphicPointDir[] arrows = new GraphicPointDir[n];
        for (int i = 0; i < n; i++)
            arrows[i] = new GraphicPointDir<Point3D>(samples.get(i), new Point3D(values[i][0], values[i][1], values[i][2]));

        ((PointDirStyle)entry.renderer).setMaxLength(DEFAULT_PIXEL_SPACING * lengthMultiplier);
        ((PointDirStyle)entry.renderer).setCentered(centered);

        entry.local = arrows;
        entry.needsConversion = true;
    }



}
