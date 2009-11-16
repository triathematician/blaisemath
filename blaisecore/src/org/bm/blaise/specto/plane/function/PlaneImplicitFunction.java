/**
 * PlaneImplicitFunction.java
 * Created on Sep 25, 2009
 */

package org.bm.blaise.specto.plane.function;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import org.apache.commons.math.analysis.MultivariateRealFunction;
import org.bm.blaise.specto.primitive.PathStyle;
import org.bm.blaise.specto.visometry.AbstractPlottable;
import org.bm.blaise.specto.visometry.Visometry;
import org.bm.blaise.specto.visometry.VisometryChangeListener;
import org.bm.blaise.specto.visometry.VisometryGraphics;

/**
 * <p>
 *   <code>PlaneImplicitFunction</code> is a plottable that shows the zero set of a
 *   function with two inputs.
 * </p>
 *
 * @author Elisha Peterson
 */
public class PlaneImplicitFunction extends AbstractPlottable<Point2D.Double> implements VisometryChangeListener {

    //
    //
    // PROPERTIES
    //
    //

    /** Underlying function */
    MultivariateRealFunction func;

    /** Style to use for plotting points in the grid. */
    private PathStyle style = new PathStyle();



    //
    //
    // CONSTRUCTORS
    //
    //

    public PlaneImplicitFunction(MultivariateRealFunction func) {
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

    public PathStyle getStyle() {
        return style;
    }

    public void setStyle(PathStyle style) {
        this.style = style;
    }


    //
    //
    // DRAW METHODS
    //
    //

    transient Point2D.Double[][] solutions;

    public void visometryChanged(Visometry vis, VisometryGraphics canvas) {
        // TODO - compute paths of solution curves here
    }

    @Override
    public void paintComponent(VisometryGraphics<Double> vg) {
        vg.setPathStyle(style);
        vg.drawPaths(solutions);
    }
}
