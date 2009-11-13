/**
 * PlaneFunctionGraph.java
 * Created on Aug 9, 2009
 */
package org.bm.blaise.specto.plane.function;

import org.bm.blaise.specto.plane.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import org.bm.blaise.specto.visometry.VisometryMouseEvent;
import org.bm.blaise.specto.visometry.Visometry;
import org.bm.blaise.specto.plottable.VComputedPath;

/**
 * <p>
 *   <code>PlaneFunctionGraph</code> plots a parametric function with a parameter input and a two-variable output.
 * </p>
 *
 * @author Elisha Peterson
 */
public class PlaneFunctionGraph extends VComputedPath<Point2D.Double> {

    //
    //
    // PROPERTIES
    //
    //
    
    /** Underlying function */
    UnivariateRealFunction func;

    //
    //
    // CONSTRUCTOR
    //
    //

    public PlaneFunctionGraph(UnivariateRealFunction func) {
        this.func = func;
    }

    //
    //
    // BEANS
    //
    //
    
    public UnivariateRealFunction getFunction() {
        return func;
    }

    public void setFunction(UnivariateRealFunction func) {
        if (func != null && this.func != func) {
            this.func = func;
            needsComputation = true;
        }
    }

    //
    //
    // PAINT ROUTINES
    //
    //

    public void visometryChanged(Visometry vis, VisometryGraphics canvas) {
        recompute((PlaneGraphics) canvas);
    }

    /** Recomputes the visual path for the function. */
    protected void recompute(VisometryGraphics<Point2D.Double> vg) {
        double xStep = ((PlaneGraphics) vg).getIdealHStepForPixelSpacing(0.5); // set to sample every 0.5 pixels
        try {
            if (path == null) {
                path = new GeneralPath();
            } else {
                path.reset();
            }
            path.moveTo((float) vg.getMinimumVisible().x, (float) func.value(vg.getMinimumVisible().x));
            for (double x = vg.getMinimumVisible().x; x <= vg.getMaximumVisible().x; x += xStep) {
                path.lineTo((float) x, (float) func.value(x));
            }
        } catch (FunctionEvaluationException ex) {
            Logger.getLogger(PlaneFunctionGraph.class.getName()).log(Level.SEVERE, null, ex);
        }
        needsComputation = false;
    }

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
