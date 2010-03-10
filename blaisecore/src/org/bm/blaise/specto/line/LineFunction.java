/**
 * LineFunction.java
 * Created on Sep 21, 2009
 */

package org.bm.blaise.specto.line;

import java.awt.Color;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.bm.blaise.specto.primitive.PointStyle;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import org.bm.blaise.specto.plottable.VPoint;

/**
 * <p>
 *   <code>LineFunction</code> represents a one input/one output function using two
 *   dots along the line. Separate styles distinguish the input and output style.
 * </p>
 *
 * @author Elisha Peterson
 */
public class LineFunction extends VPoint<Double> {
    
    /** Underlying function */
    UnivariateRealFunction func;

    /** Style of output dot */
    PointStyle outputStyle = new PointStyle(Color.BLACK, Color.YELLOW);

    /** Constructs with a new point at 0. */
    public LineFunction(UnivariateRealFunction func) {
        super(0.0);
        this.func = func;
    }
   
    //
    //
    // BEAN PATTERNS
    //
    //


    public UnivariateRealFunction getFunc() {
        return func;
    }

    public void setFunc(UnivariateRealFunction func) {
        this.func = func;
    }

    public PointStyle getOutputStyle() {
        return outputStyle;
    }

    public void setOutputStyle(PointStyle outputStyle) {
        this.outputStyle = outputStyle;
    }

    //
    //
    // DRAW METHODS
    //
    //

    @Override
    public void paintComponent(VisometryGraphics<Double> vg) {
        // paints input point
        super.paintComponent(vg);

        // try evaluating function
        boolean evaluates = true;
        double output = 0.0;
        try {
            output = func.value(this.value);
        } catch (FunctionEvaluationException ex) {
            evaluates = false;
        }

        if (evaluates)
            vg.drawPoint(output, outputStyle);
    }

}
