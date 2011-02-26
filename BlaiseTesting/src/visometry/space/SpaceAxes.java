/*
 * SpaceAxes.java
 * Created on Oct 22, 2009
 */

package visometry.space;

import coordinate.DomainHint;
import coordinate.RealIntervalNiceSampler;
import primitive.style.ArrowStyle;
import primitive.style.RuledLineStyle;
import primitive.style.StringStyle;
import scio.coordinate.Point3D;
import scio.coordinate.sample.SampleSet;
import util.ChangeBroadcaster;
import visometry.plottable.PlottableGroup;
import visometry.plottable.VAxis;

/**
 * Displays coordinate axes in 3D space.
 *
 * @author Elisha Peterson
 */
public class SpaceAxes extends PlottableGroup<Point3D> {

    /** Enum encoding type of axes */
    public enum AxesType { STANDARD, OCTANT1, UPPER_HALF }

    /** Type of axis to display. */
    private AxesType type = null;
    /** Axes */
    VAxis<Point3D> axis1, axis2, axis3;

    /** Style for axes */
    ArrowStyle axisStyle;
    /** Style for tick marks & line rules */
    RuledLineStyle ruleStyle;
    /** Style for strings */
    StringStyle labelStyle;

    /** Determines the "ideal" spacing between tick marks, in terms of pixels. */
    private int PIXEL_SPACING = 60;
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
        axis1 = new VAxis<Point3D>(x, new Point3D[]{new Point3D(-10.0,0.0,0.0), new Point3D(10.0,0.0,0.0)});
          labelStyle = axis1.getLabelStyle();
          axisStyle = axis1.getAxisStyle();
          ruleStyle = axis1.getRuleStyle();
        axis2 = new VAxis<Point3D>(y, new Point3D[]{new Point3D(0.0,-10.0,0.0), new Point3D(0.0,10.0,0.0)});
          axis2.setStyles(labelStyle, axisStyle, ruleStyle);
        axis3 = new VAxis<Point3D>(z, new Point3D[]{new Point3D(0.0,0.0,-10.0), new Point3D(0.0,0.0,10.0)});
          axis3.setStyles(labelStyle, axisStyle, ruleStyle);

        setType(type);
        add(axis1);
        add(axis2);
        add(axis3);
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
    public String getLabel1() { return axis1.getLabel(); }
    /** Sets first axis label. */
    public void setLabel1(String label) { axis1.setLabel(label); }
    /** Returns second axis label. */
    public String getLabel2() { return axis2.getLabel(); }
    /** Sets second axis label. */
    public void setLabel2(String label) { axis2.setLabel(label); }
    /** Returns third axis label. */
    public String getLabel3() { return axis3.getLabel(); }
    /** Sets third axis label. */
    public void setLabel3(String label) { axis3.setLabel(label); }
    
    /** @return general style of axes drawn . */
    public AxesType getType() { return type; }
    /** Sets style. */
    public final void setType(AxesType type) { if (this.type != type) { this.type = type; firePlottableChanged(); } }

    //
    // COMPUTATIONS (mostly determining where the ticks are displayed)
    //

    /** Sampling elements */
    transient SampleSet<Double> sample1, sample2, sample3;

    @Override
    public void recompute() {
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
        double min1 = type == AxesType.OCTANT1 ? 0 : rins1.getMinimum(),
                max1 = rins1.getMaximum(),
                d1 = max1-min1;
        double min2 = type == AxesType.OCTANT1 ? 0 : rins1.getMinimum(),
                max2 = rins1.getMaximum(),
                d2 = max1-min1;
        double min3 = (type == AxesType.OCTANT1 || type == AxesType.UPPER_HALF) ? 0 : rins1.getMinimum(),
                max3 = rins1.getMaximum(),
                d3 = max1-min1;

        axis1.updateAxis(new Point3D(min1, 0, 0), new Point3D(max1, 0, 0), new Point3D(max1-.1*d1, 0, 0),
                rins1.getSamples(),
                min1, max1, min1+.02*d1, max1-.02*d1, false);
        axis2.updateAxis(new Point3D(0, min2, 0), new Point3D(0, max2, 0), new Point3D(0, max2-.1*d2, 0),
                rins2.getSamples(),
                min2, max2, min2+.02*d1, max2-.02*d2, false);
        axis3.updateAxis(new Point3D(0, 0, min3), new Point3D(0, 0, max3), new Point3D(0, 0, max3-.1*d3),
                rins3.getSamples(),
                min3, max3, min3+.02*d1, max3-.02*d3, false);

        needsComputation = false;
    }

}
