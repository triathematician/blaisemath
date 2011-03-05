/**
 * PlaneParametricCurve.java
 * Created on Aug 9, 2009
 */
package later.visometry.plane;

import coordinate.RealIntervalUniformSampler;
import java.awt.geom.GeneralPath;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateVectorialFunction;
import org.bm.blaise.scio.coordinate.sample.SampleSet;
import org.bm.blaise.scio.function.utils.DemoCurve2D;
import util.ChangeBroadcaster;
import minimal.visometry.plane.PlanePathPlottable;
import later.visometry.plottable.VSegment;

/**
 * <p>
 *   <code>PlaneParametricCurve</code> plots a curve depending on a single parameter.
 * </p>
 *
 * @author Elisha Peterson
 */
public class PlaneParametricCurve extends PlanePathPlottable {

    /** The underlying function */
    UnivariateVectorialFunction func;
    /** Domain of function */
    SampleSet<Double> sampler;

    /** Segment-representation of the domain of the plottable. */
    VSegment<Double> domainPlottable;


    /**
     * Initializes with an underlying function and min/max domain
     */
    public PlaneParametricCurve() {
        this(new UnivariateVectorialFunction(){ public double[] value(double x) { return new double[] { Math.sqrt(Math.abs(x))*Math.cos(x), Math.sqrt(Math.abs(x))*Math.sin(x) }; } },
                0, 2*Math.PI, 100);
    }

    /**
     * Initializes with an underlying function and min/max domain
     * @param func the function
     * @param min the min value in domain
     * @param max the max value in domain
     */
    public PlaneParametricCurve(UnivariateVectorialFunction func, double min, double max) {
        this(func, min, max, 100);
    }

    /**
     * Initializes with an underlying function and a step rate for going through parameter values.
     * @param func the function
     * @param min the min value in domain
     * @param max the max value in domain
     * @param numSamples the number of samples
     */
    public PlaneParametricCurve(UnivariateVectorialFunction func, double min, double max, int numSamples) {
        setFunction(func);
        setDomain(new RealIntervalUniformSampler(min, max, numSamples));
    }

    @Override
    public String toString() {
        return "Parametric Curve 2D";
    }

    //
    // FUNCTION METHODS
    //

    DemoCurve2D demo = DemoCurve2D.ARCHIMEDEAN_SPIRAL;

    /** @return the function for the parametric curve */
    public UnivariateVectorialFunction getFunction() {
        return demo == demo.NONE ? func : demo;
    }

    /** Sets the function for the parametric curve. */
    public void setFunction(UnivariateVectorialFunction func) {
        if (func != null && this.func != func) {
            this.func = func;
            demo = demo.NONE;
            firePlottableChanged();
        }
    }
    
    /** @return currently set demo curve */
    public DemoCurve2D getDemoCurve() {
        return demo;
    }

    /** Sets the underlying function to be specified demo curve */
    public void setDemoCurve(DemoCurve2D demo) {
        this.demo = demo;
        if (demo != DemoCurve2D.NONE) {
            setDomain(new RealIntervalUniformSampler(demo.t0, demo.t1, 200));
            domainPlottable.setPoint1(demo.t0);
            domainPlottable.setPoint2(demo.t1);
        }
        firePlottableChanged();
    }

    @Override
    protected void recompute() {
        UnivariateVectorialFunction useFunc = demo == DemoCurve2D.NONE ? func : demo;
        entry.local = path = new GeneralPath();
        try {
            List<Double> tt = sampler.getSamples();
            double[] coords = useFunc.value(tt.get(0));
            path.moveTo((float) coords[0], (float) coords[1]);
            for (double x : tt) {
                coords = useFunc.value(x);
                path.lineTo((float) coords[0], (float) coords[1]);
            }
        } catch (FunctionEvaluationException ex) {
            Logger.getLogger(PlaneParametricCurve.class.getName()).log(Level.SEVERE, null, ex);
        }
        entry.needsConversion = true;
    }

    //
    // DOMAIN METHODS
    //

    /** @return representation of the curve's domain */
    public VSegment<Double> getDomainPlottable() {
        return domainPlottable;
    }
    
    /** @return domain of the curve's parameter */
    public SampleSet<Double> getDomain() {
        return sampler;
    }

    /** Sets domain of the curve's parameter. */
    public void setDomain(SampleSet<Double> newDomain) {
        if (sampler != newDomain && newDomain != null) {
            if (sampler instanceof ChangeBroadcaster)
                ((ChangeBroadcaster) sampler).removeChangeListener(this);
            sampler = newDomain;
            if (sampler instanceof ChangeBroadcaster)
                ((ChangeBroadcaster) sampler).addChangeListener(this);
            updateDomainPlottable();
            firePlottableChanged();
        }
    }

    /** Updates the domain plottable based upon the sampler. */
    void updateDomainPlottable() {
        adjusting = true;
        RealIntervalUniformSampler rius = (RealIntervalUniformSampler) sampler;
        if (domainPlottable == null) {
            domainPlottable = new VSegment<Double>( rius.getMinimum(), rius.getMaximum() );
            domainPlottable.getStrokeStyle().setThickness(2f);
            domainPlottable.addChangeListener(this);
        } else {
            domainPlottable.setPoint1(rius.getMinimum());
            domainPlottable.setPoint2(rius.getMaximum());
        }
        adjusting = false;
    }

    /** Updates the samplers based upon changes in the domain plottable. */
    void updateSamplers() {
        adjusting = true;
        RealIntervalUniformSampler rius = (RealIntervalUniformSampler) sampler;
        rius.setMinimum( domainPlottable.getPoint1() );
        rius.setMaximum( domainPlottable.getPoint2() );
        adjusting = false;
    }

    //
    // CHANGE HANDLING
    //

    transient boolean adjusting = false;

    @Override
    public void stateChanged(ChangeEvent e) {
        if (adjusting)
            return;
        if (e.getSource() == domainPlottable)
            updateSamplers();
        else if (e.getSource() == sampler)
            updateDomainPlottable();
        super.stateChanged(e);
    }

}
