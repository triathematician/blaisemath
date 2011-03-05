/**
 * PlaneFunctionGraph.java
 * Created on Aug 9, 2009
 */
package visometry.plane;

import coordinate.DomainHint;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.List;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import scio.coordinate.sample.SampleSet;
import util.ChangeBroadcaster;
import visometry.plottable.PointBounder;

/**
 * <p>
 *   <code>PlaneFunctionGraph</code> plots a function with a single input and a single output.
 * </p>
 *
 * @author Elisha Peterson
 */
public class PlaneFunctionGraph extends PlanePathPlottable
        implements PointBounder<Point2D.Double> {

    /** Model function */
    UnivariateRealFunction func;
    /** Domain of function */
    SampleSet<Double> sampler;

    /** Construct with a default function, f(x)=x */
    public PlaneFunctionGraph() {
        this(new UnivariateRealFunction(){public double value(double x) { return Math.cos(x); } });
    }

    /** Construct with a particular function. */
    public PlaneFunctionGraph(UnivariateRealFunction func) {
        setFunction(func);
    }

    @Override
    public String toString() { return "Function Graph"; }

    //
    // function
    //

    /** @return function used to generate the graph */
    public UnivariateRealFunction getFunction() {
        return func;
    }

    /** Set function used to generate the graph */
    public void setFunction(UnivariateRealFunction f) {
        if (f != null && f != func) {
            if (this.func instanceof ChangeBroadcaster) ((ChangeBroadcaster)this.func).removeChangeListener(this);
            this.func = f;
            if (f instanceof ChangeBroadcaster) ((ChangeBroadcaster)f).addChangeListener(this);
            firePlottableChanged();
        }
    }

    @Override
    protected void recompute() {
        if (sampler == null) {
            sampler = parent.requestScreenSampleDomain("x", Double.class, 1f, DomainHint.REGULAR);
            if (sampler == null)
                throw new IllegalStateException("Unable to retrieve appropriate domain from parent class!");
            ((ChangeBroadcaster)sampler).addChangeListener(this);
        }

        List<Double> xx = sampler.getSamples();
        boolean moveTo = true;
        entry.local = path = new GeneralPath();
        float fx = 0;
        for (Double x : xx) {
            try {
                fx = (float) func.value(x);
                if (moveTo) {
                    path.moveTo(x.floatValue(), fx);
                    moveTo = false;
                } else
                    path.lineTo(x.floatValue(), fx);
            } catch (Exception e) {
                moveTo = true;
            }
        }
        needsComputation = false;
        entry.needsConversion = true;
    }
    
    /**
     * @return sampler used to generate the plot
     */
    public SampleSet<Double> getDomain() {
        return sampler;
    }

    /**
     * Sets sampler of the visualization and initializes appropriate event handling.
     * If argument is null, will use the plot window's domain.
     */
    public void setDomain(SampleSet<Double> newDomain) {
        if (sampler != newDomain) {
            if (sampler instanceof ChangeBroadcaster)
                ((ChangeBroadcaster) sampler).removeChangeListener(this);
            sampler = newDomain;
            if (sampler instanceof ChangeBroadcaster)
                ((ChangeBroadcaster) sampler).addChangeListener(this);
            firePlottableChanged();
        }
    }

    public Point2D.Double getConstrainedValue(Point2D.Double coord) {
        try {
            return new Point2D.Double(coord.x, func.value(coord.x));
        } catch (FunctionEvaluationException ex) {
            return null;
        }
    }

}
