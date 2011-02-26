/**
 * SpaceFunctionGraph.java
 * Created on Oct 22, 2009
 */

package visometry.space;

import coordinate.RealIntervalUniformSampler;
import java.util.List;
import javax.swing.event.ChangeEvent;
import org.apache.commons.math.analysis.MultivariateRealFunction;
import primitive.GraphicMesh;
import primitive.style.MeshStyle;
import scio.coordinate.Point3D;
import scio.coordinate.sample.SampleSet;
import util.ChangeBroadcaster;
import visometry.VPrimitiveEntry;
import visometry.plane.PlaneRectangle;
import visometry.plottable.Plottable;

/**
 * <p>
 *   <code>SpaceFunctionGraph</code> plots a function with two inputs and a single output.
 * </p>
 *
 * @author Elisha Peterson
 */
public class SpaceFunctionGraph extends Plottable<Point3D> {

    /** Model function */
    MultivariateRealFunction func;
    /** Domains of function */
    SampleSet<Double> sampler1, sampler2;

    /** Entry containing the function's mesh visual and style */
    VPrimitiveEntry entry;
    /** Rectangle-representation of the domain of the plottable */
    PlaneRectangle domainPlottable;

    /**
     * Initializes with an underlying function and min/max domain
     */
    public SpaceFunctionGraph() {
        this( new MultivariateRealFunction(){ public double value(double[] uv) {
            return uv[0]*Math.cos(uv[1]);
        } }, -2.0, -2.0, 2.0, 2.0 );
    }

    /**
     * Initializes with an underlying function and min/max domain
     * @param func the function
     * @param min1 first domain min
     * @param min2 second domain min
     * @param max1 first domain max
     * @param max2 second domain max
     */
    public SpaceFunctionGraph(MultivariateRealFunction func, double min1, double min2, double max1, double max2) {
        this(func, min1, min2, max1, max2, 12, 12);
    }

    /**
     * Initializes with an underlying function and a step rate for going through parameter values.
     * @param func the function
     * @param min1 first domain min
     * @param min2 second domain min
     * @param max1 first domain max
     * @param max2 second domain max
     * @param sample1 samples in first direction
     * @param sample2 samples in second direction
     */
    public SpaceFunctionGraph(MultivariateRealFunction func, double min1, double min2, double max1, double max2, int sample1, int sample2) {
        setFunction(func);
        setDomain1(new RealIntervalUniformSampler(min1, max1, sample1));
        setDomain2(new RealIntervalUniformSampler(min2, max2, sample2));
        addPrimitive(entry = new VPrimitiveEntry(null, new MeshStyle()));
    }

    @Override
    public String toString() {
        return "Function Graph 3D";
    }
    //
    // FUNCTION METHODS
    //

    /** @return the function for the parametric curve */
    public MultivariateRealFunction getFunction() {
        return func;
    }

    /** Sets the function for the parametric curve. */
    public void setFunction(MultivariateRealFunction func) {
        if (func != null && this.func != func) {
            this.func = func;
            firePlottableChanged();
        }
    }

    @Override
    protected void recompute() {
        List<Double> sample1 = sampler1.getSamples();
        List<Double> sample2 = sampler2.getSamples();
        int n = sample1.size(), m = sample2.size();
        Point3D[][] pts = new Point3D[n][m];
        try {
            for (int i = 0; i < n; i++)
                for (int j = 0; j < m; j++) {
                    double val = func.value(new double[]{sample1.get(i), sample2.get(j)});
                    pts[i][j] = new Point3D(sample1.get(i), sample2.get(j), val);
                }
            entry.local = GraphicMesh.createGridMesh(pts, Point3D.class);
        } catch (Exception ex) {
            System.out.println(ex);
        }
        entry.needsConversion = true;
    }

    //
    // STYLE METHODS
    //

    /** @return current style of the mesh */
    public MeshStyle getStyle() { return (MeshStyle) entry.style; }
    /** Set current style of stroke for this plottable */
    public void setStyle(MeshStyle newValue) { if (entry.style != newValue) { entry.style = newValue; firePlottableStyleChanged(); } }

    //
    // DOMAIN METHODS
    //

    /** @return representation of the curve's domain */
    public PlaneRectangle getDomainPlottable() {
        return domainPlottable;
    }

    /** @return domain of the first parameter */
    public SampleSet<Double> getDomain1() {
        return sampler1;
    }

    /** Sets domain of the first parameter. */
    public void setDomain1(SampleSet<Double> newDomain) {
        if (sampler1 != newDomain && newDomain != null) {
            if (sampler1 instanceof ChangeBroadcaster)
                ((ChangeBroadcaster) sampler1).removeChangeListener(this);
            sampler1 = newDomain;
            if (sampler1 instanceof ChangeBroadcaster)
                ((ChangeBroadcaster) sampler1).addChangeListener(this);
            updateDomainPlottable();
            firePlottableChanged();
        }
    }

    /** @return domain of the second parameter */
    public SampleSet<Double> getDomain2() {
        return sampler2;
    }

    /** Sets domain of the second parameter. */
    public void setDomain2(SampleSet<Double> newDomain) {
        if (sampler2 != newDomain && newDomain != null) {
            if (sampler2 instanceof ChangeBroadcaster)
                ((ChangeBroadcaster) sampler2).removeChangeListener(this);
            sampler2 = newDomain;
            if (sampler2 instanceof ChangeBroadcaster)
                ((ChangeBroadcaster) sampler2).addChangeListener(this);
            updateDomainPlottable();
            firePlottableChanged();
        }
    }

    /** Updates the domain plottable based upon the sampler. */
    void updateDomainPlottable() {
        if (sampler1 == null || sampler2 == null)
            return;
        adjusting = true;
        RealIntervalUniformSampler rius1 = (RealIntervalUniformSampler) sampler1;
        RealIntervalUniformSampler rius2 = (RealIntervalUniformSampler) sampler2;
        if (domainPlottable == null) {
            domainPlottable = new PlaneRectangle (
                    rius1.getMinimum(), rius2.getMinimum(),
                    rius1.getMaximum(), rius2.getMaximum() );
            domainPlottable.addChangeListener(this);
        } else {
            domainPlottable.setMinX(rius1.getMinimum());
            domainPlottable.setMaxX(rius1.getMaximum());
            domainPlottable.setMinY(rius1.getMinimum());
            domainPlottable.setMaxY(rius1.getMaximum());
        }
        adjusting = false;
    }

    /** Updates the samplers based upon changes in the domain plottable. */
    void updateSamplers() {
        adjusting = true;
        RealIntervalUniformSampler rius1 = (RealIntervalUniformSampler) sampler1;
        RealIntervalUniformSampler rius2 = (RealIntervalUniformSampler) sampler2;
        rius1.setMinimum( domainPlottable.getMinX() );
        rius1.setMaximum( domainPlottable.getMaxX() );
        rius2.setMinimum( domainPlottable.getMinY() );
        rius2.setMaximum( domainPlottable.getMaxY() );
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
        else if (e.getSource() == sampler1 || e.getSource() == sampler2)
            updateDomainPlottable();
        super.stateChanged(e);
    }

}
