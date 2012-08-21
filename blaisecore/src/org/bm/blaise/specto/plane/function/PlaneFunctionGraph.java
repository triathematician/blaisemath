/**
 * PlaneFunctionGraph.java
 * Created on Aug 9, 2009
 */
package org.bm.blaise.specto.plane.function;

import org.bm.blaise.specto.plane.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import org.apache.commons.math.ArgumentOutsideDomainException;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import org.bm.blaise.specto.visometry.Visometry;
import org.bm.blaise.specto.plane.ComputedPath;
import org.bm.blaise.specto.visometry.VisometryChangeListener;
import org.bm.blaise.specto.visometry.VisometryMouseEvent;

/**
 * <p>
 *   <code>PlaneFunctionGraph</code> plots a function with a single input and a single output.
 * </p>
 *
 * @author Elisha Peterson
 */
public class PlaneFunctionGraph extends ComputedPath implements VisometryChangeListener {

    
    /** Underlying function */
    UnivariateRealFunction func;

    /** Construct with a default function, f(x)=x */
    public PlaneFunctionGraph() {
        this(new UnivariateRealFunction(){public double value(double x) { return x; } });
    }
    
    public PlaneFunctionGraph(UnivariateRealFunction func) {
        setFunction(func);
    }

    //
    // OBJECT METHODS
    //

    @Override
    public String toString() {
        return "Function Graph";
    }

    //
    // VALUE METHODS
    //
    
    public UnivariateRealFunction getFunction() {
        return func;
    }

    public void setFunction(UnivariateRealFunction func) {
        if (func != null && this.func != func) {
            this.func = func;
            needsComputation = true;
            fireStateChanged();
        }
    }

    //
    // PAINT ROUTINES
    //

    /** Recomputes the visual path for the function. Also performs domain checking. */
    protected GeneralPath getPath(VisometryGraphics<Point2D.Double> vg) {
        double xStep = ((PlaneGraphics) vg).getIdealHStepForPixelSpacing(0.5); // set to sample every 0.5 pixels
        GeneralPath path = new GeneralPath();
        double x = vg.getMinCoord().x;
        double fx = 0;
        while (x <= vg.getMaxCoord().x) {
            boolean outsideDomain = true;
            while (outsideDomain && x <= vg.getMaxCoord().x) {
                try {
                    fx = func.value(x);
                    outsideDomain = false;
                } catch (ArgumentOutsideDomainException e) {
                } catch (FunctionEvaluationException e) {
                }
                x += xStep;
            }
            path.moveTo( (float) x, (float) fx );
            while (!outsideDomain && x <= vg.getMaxCoord().x) {
                try {
                    path.lineTo( (float) x, (float) func.value(x));
                    outsideDomain = false;
                } catch (ArgumentOutsideDomainException e) {
                } catch (FunctionEvaluationException e) {
                }
                x += xStep;
            }
        }
        return path;
    }

    @Override
    public boolean isClickablyCloseTo(VisometryMouseEvent<Point2D.Double> e) {
        double x = e.getCoordinate().x;
        double y;
        try {
            y = func.value(x);
        } catch (FunctionEvaluationException ex) {
            return false;
        }
        return e.withinRangeOf(new Point2D.Double(x, y), 4);
    }
}
