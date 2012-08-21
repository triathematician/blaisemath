/**
 * PlaneParametricFunction.java
 * Created on Aug 9, 2009
 */
package org.bm.blaise.specto.plane.function;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.MultivariateVectorialFunction;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import org.bm.blaise.specto.visometry.VisometryMouseEvent;
import org.bm.blaise.specto.plane.ComputedPath;
import org.bm.blaise.specto.plottable.VRectangle;
import scio.coordinate.MaxMinDomain;
import scio.coordinate.sample.RealIntervalSampler;

/**
 * <p>
 *   <code>PlaneParametricArea</code> shows how a piece of the plane maps under
 *   a function with 2 inputs and 2 outputs.
 * </p>
 *
 * @author Elisha Peterson
 */
public class PlaneParametricArea extends ComputedPath {

    /** The underlying function, 2 inputs, 2 outputs */
    MultivariateVectorialFunction func;
    
    /** Range of u-values for display purposes */
    RealIntervalSampler domainU;

    /** Range of v-valeus for display purposes */
    RealIntervalSampler domainV;

    /** Stores rectangle used to adjust the range. */
    VRectangle<Point2D.Double> domainPlottable;

    /** Initializes with an underlying function and a step rate for going through parameter values.
     * @param func the function
     * @param min minimum values of u and v
     * @param max maximum values of u and v
     */
    public PlaneParametricArea(MultivariateVectorialFunction func, Point2D.Double min, Point2D.Double max) {
        setFunction(func);
        setDomainU(new RealIntervalSampler(min.x, max.x, 10));
        setDomainV(new RealIntervalSampler(min.x, max.x, 10));
        domainPlottable = new VRectangle<Point2D.Double>(min, max);
        domainPlottable.addChangeListener(this);
    }

    @Override
    public String toString() {
        return "Parametric Area";
    }

    //
    // BEAN PATTERNS
    //

    public MultivariateVectorialFunction getFunction() {
        return func;
    }

    public void setFunction(MultivariateVectorialFunction func) {
        if (func != null && this.func != func) {
            this.func = func;
            needsComputation = true;
        }
    }

    public MaxMinDomain<Double> getDomainU() {
        return domainU;
    }

    public void setDomainU(MaxMinDomain<Double> range) {
        if (range != null) {
            if (range instanceof RealIntervalSampler) {
                this.domainU = (RealIntervalSampler) range;
            } else {
                this.domainU.setMin(range.getMin());
                this.domainU.setMin(range.getMax());
                this.domainU.setMinInclusive(range.isMinInclusive());
                this.domainU.setMaxInclusive(range.isMaxInclusive());
            }
            needsComputation = true;
        }
    }

    public MaxMinDomain<Double> getDomainV() {
        return domainV;
    }

    public void setDomainV(MaxMinDomain<Double> range) {
        if (range != null) {
            if (range instanceof RealIntervalSampler) {
                this.domainV = (RealIntervalSampler) range;
            } else {
                this.domainV.setMin(range.getMin());
                this.domainV.setMin(range.getMax());
                this.domainV.setMinInclusive(range.isMinInclusive());
                this.domainV.setMaxInclusive(range.isMaxInclusive());
            }
            needsComputation = true;
        }
    }

    public VRectangle<Point2D.Double> getDomainPlottable() {
        return domainPlottable;
    }


    //
    // DRAW METHODS
    //

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == domainPlottable) {
            domainU.setMin(domainPlottable.getPoint1().x);
            domainU.setMax(domainPlottable.getPoint2().x);
            domainV.setMin(domainPlottable.getPoint1().y);
            domainV.setMax(domainPlottable.getPoint2().y);
            needsComputation = true;
        }
        super.stateChanged(e);
    }

    /** Recomputes the visual path for the function. */
    protected GeneralPath getPath(VisometryGraphics<Point2D.Double> vg) {
        GeneralPath path = new GeneralPath();
        try {
            List<Double> xx = domainU.getSamples();
            List<Double> yy = domainV.getSamples();

            double[] fval = new double[]{0, 0};

            // add path for each x value in sample
            for (Double x : xx) {
                fval = func.value(new double[]{x, domainV.getMin()});
                path.moveTo((float) fval[0], (float) fval[1]);
                for (Double y : yy) {
                    fval = func.value(new double[]{x, y});
                    path.lineTo((float) fval[0], (float) fval[1]);
                }
            }
            // add path for each y value in sample
            for (Double y : yy) {
                fval = func.value(new double[]{domainU.getMin(), y});
                path.moveTo((float) fval[0], (float) fval[1]);
                for (Double x : xx) {
                    fval = func.value(new double[]{x, y});
                    path.lineTo((float) fval[0], (float) fval[1]);
                }
            }
        } catch (FunctionEvaluationException ex) {
            Logger.getLogger(PlaneParametricArea.class.getName()).log(Level.SEVERE, null, ex);
        }
        return path;
    }

    @Override
    public boolean isClickablyCloseTo(VisometryMouseEvent<Point2D.Double> e) {
        return domainPlottable.isClickablyCloseTo(e);
    }

    @Override
    public void mouseDragged(VisometryMouseEvent<Point2D.Double> e) {
        domainPlottable.mouseDragged(e);
    }

    @Override
    public void mousePressed(VisometryMouseEvent<Point2D.Double> e) {
        domainPlottable.mousePressed(e);
    }

    @Override
    public void mouseReleased(VisometryMouseEvent<Point2D.Double> e) {
        domainPlottable.mouseReleased(e);
    }
}
