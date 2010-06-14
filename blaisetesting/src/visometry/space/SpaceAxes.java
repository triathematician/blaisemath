/*
 * SpaceAxes.java
 * Created on Oct 22, 2009
 */

package visometry.space;

import coordinate.DomainHint;
import coordinate.RealIntervalNiceSampler;
import java.util.ArrayList;
import java.util.List;
import primitive.GraphicRuledLine;
import primitive.style.RuledLineStyle;
import scio.coordinate.Point3D;
import scio.coordinate.sample.SampleSet;
import util.ChangeBroadcaster;
import visometry.VPrimitiveEntry;
import visometry.plottable.Plottable;
import visometry.plottable.PlottableConstants;

/**
 * Displays coordinate axes in 3D space.
 *
 * @author Elisha Peterson
 */
public class SpaceAxes extends Plottable<Point3D> {

    /** Enum encoding type of axes */
    public enum AxesType { STANDARD, OCTANT1, UPPER_HALF }

    /** Type of axis to display. */
    private AxesType type = null;
    /** AxesType used to draw the axes. */
    private RuledLineStyle style = new RuledLineStyle();

    /** Axis entries. */
    private VPrimitiveEntry entry1, entry2, entry3;
    /** Line primitives. */
    private GraphicRuledLine<Point3D> line1, line2, line3;

    /** Determines the "ideal" spacing between tick marks, in terms of pixels. */
    private int PIXEL_SPACING = 80;
    /** Determines "buffer" zone between last numeric label and side of window. */
    private int BUFFER = 20;

    /** 
     * Construct with provided axis labels and default axes type
     * @param x label on axis 1
     * @param y label on axis 2
     * @param z label on axis 3
     */
    public SpaceAxes(String x, String y, String z) {
        this(x, y, z, AxesType.STANDARD);
    }

    /**
     * Construct with provided axis labels.
     * @param x label on axis 1
     * @param y label on axis 2
     * @param z label on axis 3
     * @param type format of axes
     */
    public SpaceAxes(String x, String y, String z, AxesType type) {
        addPrimitive(
                entry1 = new VPrimitiveEntry( line1 = new GraphicRuledLine<Point3D>(new Point3D(), new Point3D(), x, null, null), style ),
                entry2 = new VPrimitiveEntry( line2 = new GraphicRuledLine<Point3D>(new Point3D(), new Point3D(), y, null, null), style ),
                entry3 = new VPrimitiveEntry( line3 = new GraphicRuledLine<Point3D>(new Point3D(), new Point3D(), z, null, null), style ) );
        setType(type);
    }

    @Override
    public SpaceAxes clone() {
        return new SpaceAxes(getLabel1(), getLabel2(), getLabel3(), type);
    }

    @Override
    public String toString() {
        return "Axes ["+getLabel1() + "," + getLabel2() + "," + getLabel3() + "]";
    }

    //
    // BEAN PROPERTY PATTERNS
    //

    /** Returns first axis label. */
    public String getLabel1() { return line1.label; }
    /** Sets first axis label. */
    public void setLabel1(String label) { if (!line1.label.equals(label)) { line1.label = label; firePlottableStyleChanged(); } }
    /** Returns second axis label. */
    public String getLabel2() { return line2.label; }
    /** Sets second axis label. */
    public void setLabel2(String label) { if (!line2.label.equals(label)) { line2.label = label; firePlottableStyleChanged(); } }
    /** Returns third axis label. */
    public String getLabel3() { return line3.label; }
    /** Sets third axis label. */
    public void setLabel3(String label) { if (!line3.label.equals(label)) { line3.label = label; firePlottableStyleChanged(); } }
    
    /** @return general style of axes drawn . */
    public AxesType getType() { return type; }
    /** Sets style. */
    public void setType(AxesType type) { if (this.type != type) { this.type = type; firePlottableChanged(); } }

    //
    // COMPUTATIONS (mostly determining where the ticks are displayed)
    //

    /** Sampling elements */
    transient SampleSet<Double> sample1, sample2, sample3;

    @Override
    protected void recompute() {
        if (sample1 == null || sample2 == null || sample3 == null) {
            sample1 = parent.requestScreenSampleDomain("x", Double.class, PIXEL_SPACING, DomainHint.PREFER_INTS);
            sample2 = parent.requestScreenSampleDomain("y", Double.class, PIXEL_SPACING, DomainHint.PREFER_INTS);
            sample3 = parent.requestScreenSampleDomain("z", Double.class, PIXEL_SPACING, DomainHint.PREFER_INTS);
            if (sample1 == null || sample2 == null || sample3 == null)
                throw new IllegalStateException("Unable to retrieve appropriate domain from parent class!");
            ((ChangeBroadcaster)sample1).addChangeListener(this);
            ((ChangeBroadcaster)sample2).addChangeListener(this);
            ((ChangeBroadcaster)sample3).addChangeListener(this);
        }

        RealIntervalNiceSampler rins1 = (RealIntervalNiceSampler) sample1;
        RealIntervalNiceSampler rins2 = (RealIntervalNiceSampler) sample2;
        RealIntervalNiceSampler rins3 = (RealIntervalNiceSampler) sample3;
        float min1 = (float)(double) rins1.getMinimum();
        float max1 = (float)(double) rins1.getMaximum();
        float min2 = (float)(double) rins2.getMinimum();
        float max2 = (float)(double) rins2.getMaximum();
        float min3 = (float)(double) rins3.getMinimum();
        float max3 = (float)(double) rins3.getMaximum();

        line1.start.setLocation(type == AxesType.OCTANT1 ? 0 : min1, 0, 0);
        line1.end.setLocation(max1, 0, 0);
        line2.start.setLocation(0, type == AxesType.OCTANT1 ? 0 : min2, 0);
        line2.end.setLocation(0, max2, 0);
        line3.start.setLocation(0, 0, type == AxesType.OCTANT1 || type == AxesType.UPPER_HALF ? 0 : min3);
        line3.end.setLocation(0, 0, max3);

        assignTicks(line1, sample1.getSamples(), "x");
        assignTicks(line2, sample2.getSamples(), "y");
        assignTicks(line3, sample2.getSamples(), "z");

        needsComputation = false;
    }

    /**
     * Method for assiging the tick values to a graphic line using a provided list of sample values.
     * The method ensures that all ticks are within the boundaries of the line
     * @param line the line to assign
     * @param ts tick values
     * @param var "x", "y", or "z", depending upon which value is being considered on the line
     */
    private static void assignTicks(GraphicRuledLine<Point3D> line, List<Double> ts, String var) {
        ArrayList<Double> ts2 = new ArrayList<Double>();
        double lStart, lEnd;
        if (var.equals("x")) { lStart = line.start.x; lEnd = line.end.x; }
        else if (var.equals("y")) { lStart = line.start.y; lEnd = line.end.y; }
        else { lStart = line.start.z; lEnd = line.end.z; }

        for(Double t : ts)
            if (t >= lStart && t <= lEnd)
                ts2.add(t);

        int size = ts2.size();
        line.ticks = new double[size];
        line.tickLabels = new String[size];

        double length = lEnd - lStart;
        for(int i = 0; i < size; i++) {
            line.ticks[i] = (ts2.get(i) - lStart) / length;
            line.tickLabels[i] = ts2.get(i) == 0.0 ? null : PlottableConstants.FLOAT_FORMAT.format(ts2.get(i));
        }
    }
}
