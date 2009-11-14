/**
 * PlaneInterpolatingFunction.java
 * Created on Sep 25, 2009
 */
package org.bm.blaise.specto.plane.data;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MathException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.analysis.interpolation.DividedDifferenceInterpolator;
import org.apache.commons.math.analysis.interpolation.NevilleInterpolator;
import org.apache.commons.math.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math.analysis.interpolation.UnivariateRealInterpolator;
import org.bm.blaise.specto.plane.PlaneGraphics;
import org.bm.blaise.specto.plane.function.PlaneFunctionGraph;
import org.bm.blaise.specto.plottable.VPointSet;
import org.bm.blaise.specto.primitive.BlaisePalette;
import org.bm.blaise.specto.primitive.PathStyle;
import org.bm.blaise.specto.visometry.Visometry;
import org.bm.blaise.specto.visometry.VisometryChangeListener;
import org.bm.blaise.specto.visometry.VisometryGraphics;


/**
 * <p>
 *   <code>PlaneInterpolatingFunction</code> displays a path representing the interpolation
 *   of multiple dynamic points.
 * </p>
 *
 * @author Elisha Peterson
 */
public class PlaneInterpolatingFunction extends VPointSet<Point2D.Double> implements VisometryChangeListener {

    //
    //
    // PROPERTIES
    //
    //

    protected Interpolator interpolator = Interpolator.SPLINE;

    /** Style of stroke */
    protected PathStyle strokeStyle = new PathStyle(BlaisePalette.STANDARD.function());

    public PlaneInterpolatingFunction(Point2D.Double[] values) {
        super(values);
        addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                setInterpolatingFunction();
            }
        });
        setInterpolatingFunction();
    }

    //
    //
    // BEAN PATTERNS
    //
    //

    public PathStyle getStrokeStyle() {
        return strokeStyle;
    }

    public void setStrokeStyle(PathStyle style) {
        this.strokeStyle = style;
    }

    public Interpolator getInterpolator() {
        return interpolator;
    }

    public void setInterpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
    }

    

    // PAINT METHODS

    transient UnivariateRealFunction func;

    private void setInterpolatingFunction() {
        // sort values by x
        TreeSet<Point2D.Double> sortedPts = new TreeSet<Point2D.Double>(
                new Comparator<Point2D.Double>() {
                    public int compare(Point2D.Double o1, Point2D.Double o2) {
                        return (int) Math.signum(o1.x - o2.x);
                    }
                });
        for (int i = 0; i < values.length; i++) {
            sortedPts.add(values[i]);
        }

        double[] xVals = new double[values.length];
        double[] yVals = new double[values.length];

        int i = 0;
        for (Point2D.Double pt : sortedPts) {
            xVals[i] = pt.x;
            if (i > 0 && xVals[i] <= xVals[i-1]) {
                xVals[i] = xVals[i-1] + 1e-10;
            }
            yVals[i] = pt.y;
            i++;
        }
        try {
            func = interpolator.interp.interpolate(xVals, yVals);
        } catch (MathException ex) {
            Logger.getLogger(PlaneInterpolatingFunction.class.getName()).log(Level.SEVERE, null, ex);
        }
        needsComputation = true;
    }
    
    boolean needsComputation = true;
    double xmin;
    double xmax;
    GeneralPath path;

    public void recompute(VisometryGraphics<Point2D.Double> vg) {
        xmin = values[0].x;
        xmax = values[0].x;
        for (int i = 1; i < values.length; i++) {
            xmin = Math.min(xmin, values[i].x);
            xmax = Math.max(xmax, values[i].x);
        }
        double xStep = ((PlaneGraphics) vg).getIdealHStepForPixelSpacing(2.0); // set to sample every 2 pixels
        try {
            if (path == null) {
                path = new GeneralPath();
            } else {
                path.reset();
            }
            path.moveTo((float) xmin, (float) func.value(xmin));
            for (double x = xmin; x <= xmax; x += xStep) {
                path.lineTo((float) x, (float) func.value(x));
            }
            path.lineTo((float) xmax, (float) func.value(xmax));
        } catch (FunctionEvaluationException ex) {
            Logger.getLogger(PlaneFunctionGraph.class.getName()).log(Level.SEVERE, null, ex);
        }
        needsComputation = false;
    }

    public void visometryChanged(Visometry vis, VisometryGraphics canvas) {
        needsComputation = true;
    }

    @Override
    public void paintComponent(VisometryGraphics<Point2D.Double> vg) {
        super.paintComponent(vg);
        if (needsComputation) {
            recompute(vg);
        }
        vg.setPathStyle(strokeStyle);
        vg.drawWinPath(path);
    }

    /**
     * The type of interpolator to use.
     */
    public enum Interpolator {
        SPLINE("Spline", new SplineInterpolator()),
        DIVDIFF("Div Diff", new DividedDifferenceInterpolator()),
  //      LOESS("Loess", new LoessInterpolator()),
        NEVILLE("Neville", new NevilleInterpolator());

        UnivariateRealInterpolator interp;
        String name;
        @Override
        public String toString() { return name; }

        Interpolator(String name, UnivariateRealInterpolator interp) {
            this.name = name;
            this.interp = interp;
        }
    }
}
