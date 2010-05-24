/*
 * LineAxis.java
 * Created on Sep 19, 2009
 */

package visometry.line;

import coordinate.RealIntervalNiceSampler;
import coordinate.ScreenSampleDomainProvider;
import java.util.ArrayList;
import java.util.List;
import primitive.GraphicRuledLine;
import primitive.style.ArrowStyle;
import primitive.style.RuledLineStyle;
import scio.coordinate.sample.SampleSet;
import util.ChangeBroadcaster;
import visometry.VPrimitiveEntry;
import visometry.plottable.Plottable;
import visometry.plottable.PlottableConstants;

/**
 * Displays an axis along a line.
 * 
 * @author Elisha Peterson
 */
public class LineAxis extends Plottable<Double> {

    /** AxisType used to drawArray the axes. */
    private RuledLineStyle style = new RuledLineStyle();

    /** Horizontal axis entry. */
    private VPrimitiveEntry entry;
    /** Horizontal line primitive. */
    private GraphicRuledLine<Double> line;

    /** Determines the "ideal" spacing between tick marks, in terms of pixels. */
    private int PIXEL_SPACING = 60;
    /** Determines "buffer" zone between last numeric label and side of window. */
    private int BUFFER = 20;
    /** Whether to use multiples of pi for the tick marks. */
    private boolean usePi = false;


    //
    // CONSTRUCTORS
    //

    /** Construct using defaults. */
    public LineAxis() {
        this("t");
    }

    /** Construct with specified label. */
    public LineAxis(String label) {
        addPrimitive( entry = new VPrimitiveEntry( line = new GraphicRuledLine<Double>(-10.0, 10.0, label, null, null), style ) );
        style.getLineStyle().setAnchorShape(ArrowStyle.ArrowShape.REGULAR);
    }

    @Override
    public LineAxis clone() {
        return new LineAxis(getLabel());
    }

    @Override
    public String toString() {
        return "Axis ["+getLabel() + "]";
    }
    
    //
    // BEAN PROPERTY PATTERNS
    //

    /** Returns horizontal label. */
    public String getLabel() { return line.label; }
    /** Sets horizontal label. */
    public void setLabel(String label) { if (!line.label.equals(label)) { line.label = label; firePlottableStyleChanged(); } }

    /** @return true if first axis uses multiples of pi. */
    public boolean isUsePi() { return usePi; }
    /** Sets pi status */
    public void setUsePi(boolean value) { if (usePi != value) { usePi = value; firePlottableStyleChanged(); } }

    /** Sampling elements */
    transient SampleSet<Double> sample;

    @Override
    protected void recompute() {
        if (sample == null ) {
            sample = parent.requestScreenSampleDomain("t", Double.class, PIXEL_SPACING, usePi ? ScreenSampleDomainProvider.HINT_PREFER_PI : ScreenSampleDomainProvider.HINT_PREFER_WHOLE_NUMBERS);
            if (sample == null)
                throw new IllegalStateException("Unable to retrieve appropriate domain from parent class!");
            ((ChangeBroadcaster)sample).addChangeListener(this);
        }

        RealIntervalNiceSampler rins = (RealIntervalNiceSampler) sample;
        line.start = rins.getMinimum();
        line.end = rins.getMaximum();

        List<Double> xx = sample.getSamples();
        ArrayList<Double> xx2 = new ArrayList<Double>();
        for(Double x : xx)
            if (x >= line.start && x <= line.end)
                xx2.add(x);
        double length = line.end - line.start;
        int size = xx2.size();
        line.ticks = new double[size];
        line.tickLabels = new String[size];
        for(int i = 0; i < size; i++) {
            line.ticks[i] = (xx2.get(i) - line.start) / length;
            line.tickLabels[i] = PlottableConstants.floatFormat.format(xx2.get(i));
        }

        needsComputation = false;
    }
}
