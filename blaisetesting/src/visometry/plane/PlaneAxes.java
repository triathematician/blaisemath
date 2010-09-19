/*
 * PlaneAxes.java
 * Created on Mar 22, 2008
 */

package visometry.plane;

import coordinate.DomainHint;
import coordinate.RealIntervalNiceSampler;
import java.awt.geom.Point2D;
import primitive.style.Anchor;
import primitive.style.ArrowStyle;
import primitive.style.ArrowStyle.ArrowShape;
import primitive.style.RuledLineStyle;
import primitive.style.StringStyle;
import scio.coordinate.sample.SampleSet;
import util.ChangeBroadcaster;
import visometry.plottable.PlottableGroup;
import visometry.plottable.VAxis;

/**
 * <p>
 *      <code>PlaneAxes</code> represents the second generation of axes drawing,
 *      with support for multiple drawing modes, labels, etc.
 *      The <code>PlaneGrid</code> class separately maintains the grid.
 *      The spacing of the two classes is adjusted separately.
 * </p>
 *
 * TODO - moveable labels
 *
 * @author Elisha Peterson
 */
public class PlaneAxes extends PlottableGroup<Point2D.Double> {

    /** Enum encoding type of axes */
    public enum AxesType { STANDARD, QUADRANT1, UPPER_HALF, BOX }

    /** Type of axis to display. */
    private AxesType type = null;
    /** Horizontal axis */
    VAxis<Point2D.Double> hAxis, hAxis2;
    /** Vertical axis */
    VAxis<Point2D.Double> vAxis, vAxis2;

    /** Style for axes */
    ArrowStyle axisStyle;
    /** Style for tick marks & line rules */
    RuledLineStyle ruleStyle;
    /** Style for strings */
    StringStyle labelStyle;

    /** Determines the "ideal" spacing between tick marks, in terms of pixels. */
    private int PIXEL_SPACING = 60;

    //
    // CONSTRUCTORS
    //

    /** Construct using defaults. */
    public PlaneAxes() {
        this("x", "y", AxesType.STANDARD);
    }

    /**
     * Construct with specified labels.
     * @param hLabel label for horizontal axis
     * @param vLabel label for vertical axis
     */
    public PlaneAxes(String hLabel, String vLabel) {
        this(hLabel, vLabel, AxesType.STANDARD);
    }

    /**
     * Construct with specified labels.
     * @param hLabel label for horizontal axis
     * @param vLabel label for vertical axis
     */
    public PlaneAxes(String hLabel, String vLabel, AxesType type) {
        hAxis = new VAxis<Point2D.Double>(hLabel, new Point2D.Double[]{new Point2D.Double(-10.0,0.0), new Point2D.Double(10.0,0.0)});
          labelStyle = hAxis.getLabelStyle();
          axisStyle = hAxis.getAxisStyle();
          ruleStyle = hAxis.getRuleStyle();
        hAxis2 = new VAxis<Point2D.Double>(null, new Point2D.Double[]{new Point2D.Double(-10.0,0.0), new Point2D.Double(10.0,0.0)});
          hAxis2.setStyles(labelStyle, axisStyle, ruleStyle);
        vAxis = new VAxis<Point2D.Double>(vLabel, new Point2D.Double[]{new Point2D.Double(0.0,-10.0), new Point2D.Double(0.0,10.0)});
          vAxis.setStyles(labelStyle, axisStyle, ruleStyle);
        vAxis2 = new VAxis<Point2D.Double>(null, new Point2D.Double[]{new Point2D.Double(0.0,-10.0), new Point2D.Double(0.0,10.0)});
          vAxis2.setStyles(labelStyle, axisStyle, ruleStyle);

        axisStyle.setAnchorShape(ArrowShape.NONE);
        ruleStyle.setLabelPosition(1.2);
        ruleStyle.getLabelStyle().setAnchor(Anchor.South);
        labelStyle.setAnchor(Anchor.Northeast);

        setType(type);
        add(hAxis);
        add(hAxis2);
        add(vAxis);
        add(vAxis2);
    }

    @Override
    public PlaneAxes clone() {
        return new PlaneAxes(getHLabel(), getVLabel(), type);
    }

    @Override
    public String toString() {
        return "Axes ["+getHLabel() + "," + getVLabel() + "]";
    }

    //
    // BEAN PROPERTY PATTERNS
    //

    /** Returns horizontal label. */
    public String getHLabel() { return hAxis.getLabel(); }
    /** Sets horizontal label. */
    public void setHLabel(String label) { hAxis.setLabel(label); }
    /** Returns vertical label. */
    public String getVLabel() { return vAxis.getLabel(); }
    /** Sets vertical label. */
    public void setVLabel(String label) { vAxis.setLabel(label); }

    /** @return true if first axis uses multiples of pi. */
    public boolean isHUsePi() { return hAxis.isUsePi(); }
    /** Sets pi status for horizontal axis */
    public void setHUsePi(boolean value) { hAxis.setUsePi(value); hAxis2.setUsePi(value); }
    /** @return true if first axis uses multiples of pi. */
    public boolean isVUsePi() { return vAxis.isUsePi(); }
    /** Sets pi status for vertical axis */
    public void setVUsePi(boolean value) { vAxis.setUsePi(value); vAxis2.setUsePi(value); }

    transient private ArrowShape cacheAS = ArrowShape.REGULAR;

    /** @return general style of axes drawn . */
    public AxesType getType() { return type; }
    /** Sets style. */
    public final void setType(AxesType type) {
        if (this.type != type) {
            this.type = type;
            if (type == AxesType.BOX) {
                cacheAS = axisStyle.getHeadShape();
                axisStyle.setHeadShape(ArrowShape.NONE);
                hAxis2.setVisible(true);
                vAxis2.setVisible(true);
            } else {
                if (cacheAS == null)
                    cacheAS = ArrowShape.REGULAR;
                axisStyle.setHeadShape(cacheAS);
                hAxis2.setVisible(false);
                vAxis2.setVisible(false);
            }
            firePlottableChanged();
        }
    }

    //
    // COMPUTATIONS (mostly determining where the ticks are displayed)
    //

    /** Sampling elements */
    transient SampleSet<Double> sampleX, sampleY;

    @Override
    public void recompute() {
        if (sampleX == null || sampleY == null) {
            sampleX = parent.requestScreenSampleDomain("x", Double.class, PIXEL_SPACING, isHUsePi() ? DomainHint.PREFER_PI : DomainHint.REGULAR );
            sampleY = parent.requestScreenSampleDomain("y", Double.class, PIXEL_SPACING, isVUsePi() ? DomainHint.PREFER_PI : DomainHint.REGULAR );
            if (sampleX == null || sampleY == null)
                throw new IllegalStateException("Unable to retrieve appropriate domain from parent class!");
            ((ChangeBroadcaster)sampleX).addChangeListener(this);
            ((ChangeBroadcaster)sampleY).addChangeListener(this);
        }

        RealIntervalNiceSampler rinsX = (RealIntervalNiceSampler) sampleX;
        RealIntervalNiceSampler rinsY = (RealIntervalNiceSampler) sampleY;
        double minX = (type == AxesType.QUADRANT1) ? 0 : rinsX.getMinimum(),
                maxX = rinsX.getMaximum(),
                dx = maxX - minX;
        double minY = (type == AxesType.QUADRANT1 || type == AxesType.UPPER_HALF) ? 0 : rinsY.getMinimum(),
                maxY = rinsY.getMaximum(),
                dy = maxY - minY;

        switch(type) {
            case STANDARD:
            case QUADRANT1:
            case UPPER_HALF:
                hAxis.updateAxis(new Point2D.Double(minX, 0), new Point2D.Double(maxX, 0),
                        new Point2D.Double(maxX-.1*dx, 0),
                        rinsX.getSamples(),
                        minX, maxX, minX+.02*dx, maxX-.02*dx, false);
                vAxis.updateAxis(new Point2D.Double(0, minY), new Point2D.Double(0, maxY),
                        new Point2D.Double(0, maxY-.1*dy),
                        rinsY.getSamples(),
                        minY, maxY, minY+.02*dy, maxY-.02*dy, false);
                break;
            case BOX:
                minX += .01f*dx; maxX -= .01f*dx;
                minY += .01f*dy; maxY -= .01f*dy;
                hAxis.updateAxis(new Point2D.Double(minX, minY), new Point2D.Double(maxX, minY), 
                        new Point2D.Double(maxX-.1*dx, minY),
                        rinsX.getSamples(),
                        minX, maxX, minX+.02*dx, maxX-.02*dx, true);
                hAxis2.updateAxis(new Point2D.Double(minX, maxY), new Point2D.Double(maxX, maxY), 
                        new Point2D.Double(maxX-.1*dx, maxY),
                        rinsX.getSamples(),
                        minX, maxX, minX+.02*dx, maxX-.02*dx, true);
                vAxis.updateAxis(new Point2D.Double(minX, minY), new Point2D.Double(minX, maxY), 
                        new Point2D.Double(minX, maxY-.1*dy),
                        rinsY.getSamples(),
                        minY, maxY, minY+.02*dy, maxY-.02*dy, true);
                vAxis2.updateAxis(new Point2D.Double(maxX, minY), new Point2D.Double(maxX, maxY),
                        new Point2D.Double(maxX, maxY-.1*dy),
                        rinsY.getSamples(),
                        minY, maxY, minY+.02*dy, maxY-.02*dy, true);
                break;
        }

        needsComputation = false;
    }

}
