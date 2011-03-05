/**
 * LineFunction.java
 * Created on Sep 21, 2009
 */

package later.visometry.line;

import java.awt.Color;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import graphics.GraphicString;
import org.bm.blaise.graphics.renderer.Anchor;
import primitive.style.temp.PointLabeledStyle;
import primitive.style.temp.PointStyle;
import primitive.style.temp.StringStyle;
import visometry.graphics.VGraphicEntry;
import other.PlottableConstants;
import visometry.plottable.VPoint;

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
    /** Entry for the output variable */
    VGraphicEntry oEntry;

    /** Construct with a default function, f(x)=x */
    public LineFunction() {
        this(new UnivariateRealFunction(){public double value(double x) { return Math.sin(x); } });
    }

    /** Construct with a particular function. */
    public LineFunction(UnivariateRealFunction func) {
        super(0.0);
        primitives.add(oEntry = new VGraphicEntry(new GraphicString<Double>(0.0, PlottableConstants.FLOAT_FORMAT.format(0.0)),
                new PointLabeledStyle(Color.BLACK, Color.YELLOW, Anchor.North)));
        setFunction(func);
    }

    @Override
    public String toString() { return "Function"; }


    /** @return function used to generate the graph */
    public UnivariateRealFunction getFunction() {
        return func;
    }

    /** Set function used to generate the graph */
    public void setFunction(UnivariateRealFunction f) {
        if (f != null && f != func) {
            func = f;
            firePlottableChanged();
        }
    }

    @Override
    protected void firePlottableChanged() {
        try {
            double value = func.value(getPoint());
            GraphicString<Double> gs = ((GraphicString<Double>)oEntry.local);
            gs.anchor = value;
            gs.string = PlottableConstants.FLOAT_FORMAT.format(value);
            oEntry.needsConversion = true;
        } catch (FunctionEvaluationException ex) {
        }
        super.firePlottableChanged();
    }

    /** @return current style of stroke for this plottable */
    public PointStyle getOutputStyle() { return (PointStyle) oEntry.renderer; }
    /** Set current style of stroke for this plottable */
    public void setOutputStyle(PointStyle newValue) { if (oEntry.renderer != newValue) { oEntry.renderer = newValue; firePlottableStyleChanged(); } }
}
