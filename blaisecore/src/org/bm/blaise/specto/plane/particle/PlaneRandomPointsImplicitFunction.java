/**
 * PlaneRandomPointsImplicitFunction.java
 * Created on Sep 25, 2009
 */
package org.bm.blaise.specto.plane.particle;

import org.bm.blaise.specto.plane.*;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;
import org.apache.commons.math.analysis.MultivariateRealFunction;
import scio.function.Newton1DSpace;
import org.bm.blaise.specto.primitive.PointStyle;
import org.bm.blaise.specto.visometry.AbstractPlottable;
import org.bm.blaise.specto.visometry.Visometry;
import org.bm.blaise.specto.visometry.VisometryChangeListener;
import org.bm.blaise.specto.visometry.VisometryGraphics;

/**
 * <p>
 *   (Experimental) <code>PlaneRandomPointsImplicitFunction</code> uses randomized points and directions, together
 *   with a newton algorithm, to generate random points along an implicit curve.
 * </p>
 *
 * @author Elisha Peterson
 */
public class PlaneRandomPointsImplicitFunction extends AbstractPlottable<Point2D.Double> implements VisometryChangeListener {

    //
    //
    // PROPERTIES
    //
    //
    /** Underlying function */
    MultivariateRealFunction func;
    /** Values to find */
    double[] values;
    /** Number of points per value */
    int nRoots = 100;
    /** Style of point to display. */
    PointStyle style = new PointStyle(
            PointStyle.PointShape.CIRCLE,
            PointStyle.DEFAULT_STROKE, Color.BLACK,
            Color.GRAY,
            3);


    //
    //
    // CONSTRUCTORS
    //
    //
    public PlaneRandomPointsImplicitFunction(MultivariateRealFunction func, double[] values) {
        setFunc(func);
    }


    //
    //
    // BEAN PATTERNS
    //
    //
    public MultivariateRealFunction getFunc() {
        return func;
    }

    public void setFunc(MultivariateRealFunction func) {
        this.func = func;
    }

    public int getNRoots() {
        return nRoots;
    }

    public void setNRoots(int nRoots) {
        this.nRoots = nRoots;
        resetRoots();
    }

    public double[] getValues() {
        return values;
    }

    public void setValues(double[] values) {
        this.values = values;
    }

    public PointStyle getStyle() {
        return style;
    }

    public void setStyle(PointStyle style) {
        this.style = style;
    }
    //
    //
    // PAINT METHODS
    //
    //
    transient double winScaleFactor;
    transient Rectangle2D.Double bounds;
    transient double[][] roots;

    void resetRoots() {
        roots = Newton1DSpace.getNRoots(nRoots, func, new double[]{bounds.x, bounds.y}, new double[]{bounds.x + bounds.width, bounds.x + bounds.height});
    }

    public void visometryChanged(Visometry vis, VisometryGraphics canvas) {
        PlaneVisometry pv = (PlaneVisometry) vis;
        bounds = pv.getVisibleRange();
        winScaleFactor = 0.5 * (Math.abs(pv.getScaleX()) + Math.abs(pv.getScaleY()));
        if (roots == null) {
            resetRoots();
        } else {
            int nRefresh = Math.min(roots.length / 20, 100);
            double[][] root2 = Newton1DSpace.getNRoots(nRefresh, func, new double[]{bounds.x, bounds.y}, new double[]{bounds.x + bounds.width, bounds.x + bounds.height});
            for (int i = 0; i < nRefresh; i++) {
                int pos = (int) Math.floor(Math.random() * roots.length);
                roots[pos] = root2[i];
            }
        }
    }

    @Override
    public void paintComponent(VisometryGraphics<Double> vg) {
        vg.setPointStyle(style);
        Point2D.Double pt = new Point2D.Double();
        for (int i = 0; i < roots.length; i++) {
            if (roots[i] == null) {
                continue;
            }
            pt.x = roots[i][0];
            pt.y = roots[i][1];
            vg.drawPoint(pt);
        }
    }
}
