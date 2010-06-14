/*
 * PlaneAxes.java
 * Created on Mar 22, 2008
 */

package visometry.plane;

import coordinate.DomainHint;
import coordinate.RealIntervalNiceSampler;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import primitive.GraphicRuledLine;
import primitive.style.RuledLineStyle;
import scio.coordinate.sample.SampleSet;
import util.ChangeBroadcaster;
import visometry.VPrimitiveEntry;
import visometry.plottable.Plottable;
import visometry.plottable.PlottableConstants;

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
public class PlaneAxes extends Plottable<Point2D.Double> {

    /** Enum encoding type of axes */
    public enum AxesType { STANDARD, QUADRANT1, UPPER_HALF, BOX }

    /** Type of axis to display. */
    private AxesType type = null;
    /** AxesType used to draw the axes. */
    private RuledLineStyle style = new RuledLineStyle();
    /** Used to draw top/right axes in box mode */
    private RuledLineStyle.Opposite oppositeStyle = new RuledLineStyle.Opposite(style);

    /** Horizontal axis entry. */
    private VPrimitiveEntry hEntry, hEntry2;
    /** Horizontal line primitive. */
    private GraphicRuledLine<Point2D.Double> hLine, hLine2;
    /** Vertical axis entry. */
    private VPrimitiveEntry vEntry, vEntry2;
    /** Vertical line primitive. */
    private GraphicRuledLine<Point2D.Double> vLine, vLine2;

    /** Determines the "ideal" spacing between tick marks, in terms of pixels. */
    private int PIXEL_SPACING = 60;
    /** Whether to use multiples of pi for the tick marks. */
    private boolean usePiX = false, usePiY = false;

    //
    // CONSTRUCTORS
    //

    /** Construct using defaults. */
    public PlaneAxes() {
        this("x", "y", AxesType.STANDARD);
    }

    /** Construct with specified labels. */
    public PlaneAxes(String xLabel, String yLabel) {
        this(xLabel, yLabel, AxesType.STANDARD);
    }

    /** Construct with specified labels. */
    public PlaneAxes(String xLabel, String yLabel, AxesType type) {
        addPrimitive(
                hEntry = new VPrimitiveEntry( hLine = new GraphicRuledLine<Point2D.Double>(new Point2D.Double(), new Point2D.Double(), xLabel, null, null), style ),
                hEntry2 = new VPrimitiveEntry( hLine2 = new GraphicRuledLine<Point2D.Double>(new Point2D.Double(), new Point2D.Double(), xLabel, null, null), oppositeStyle ),
                vEntry = new VPrimitiveEntry( vLine = new GraphicRuledLine<Point2D.Double>(new Point2D.Double(), new Point2D.Double(), yLabel, null, null), style ),
                vEntry2 = new VPrimitiveEntry( vLine2 = new GraphicRuledLine<Point2D.Double>(new Point2D.Double(), new Point2D.Double(), yLabel, null, null), oppositeStyle ) );
        hEntry2.visible = false;
        vEntry2.visible = false;
        setType(type);
    }

    @Override
    public PlaneAxes clone() {
        return new PlaneAxes(getLabel1(), getLabel2(), type);
    }

    @Override
    public String toString() {
        return "Axes ["+getLabel1() + "," + getLabel2() + "]";
    }

    //
    // BEAN PROPERTY PATTERNS
    //

    /** Returns horizontal label. */
    public String getLabel1() { return hLine.label; }
    /** Sets horizontal label. */
    public void setLabel1(String label) { 
        if (!hLine.label.equals(label)) {
            hLine.label = label;
            hLine2.label = label;
            firePlottableStyleChanged();
        }
    }
    /** Returns vertical label. */
    public String getLabel2() { return vLine.label; }
    /** Sets vertical label. */
    public void setLabel2(String label) { 
        if (!vLine.label.equals(label)) {
            vLine.label = label;
            vLine2.label = label;
            firePlottableStyleChanged();
        }
    }

    /** @return true if first axis uses multiples of pi. */
    public boolean isUsePiHorizontal() { return usePiX; }
    /** Sets pi status for horizontal axis */
    public void setUsePiHorizontal(boolean value) { if (usePiX != value) { usePiX = value; firePlottableStyleChanged(); } }
    /** @return true if first axis uses multiples of pi. */
    public boolean isUsePiVertical() { return usePiY; }
    /** Sets pi status for vertical axis */
    public void setUsePiVertical(boolean value) { if (usePiY != value) { usePiY = value; firePlottableStyleChanged(); } }

    /** @return general style of axes drawn . */
    public AxesType getType() { return type; }
    /** Sets style. */
    public void setType(AxesType type) { 
        if (this.type != type) {
            this.type = type;
            style.setDrawArrow(type != AxesType.BOX);
            hEntry.needsConversion = true;
            hEntry2.needsConversion = true;
            vEntry.needsConversion = true;
            vEntry2.needsConversion = true;
            firePlottableChanged();
        }
    }

    //
    // COMPUTATIONS (mostly determining where the ticks are displayed)
    //

    /** Sampling elements */
    transient SampleSet<Double> sampleX, sampleY;
    
    @Override
    protected void recompute() {
        if (sampleX == null || sampleY == null) {
            sampleX = parent.requestScreenSampleDomain("x", Double.class, PIXEL_SPACING, usePiX ? DomainHint.PREFER_PI : DomainHint.REGULAR );
            sampleY = parent.requestScreenSampleDomain("y", Double.class, PIXEL_SPACING, usePiY ? DomainHint.PREFER_PI : DomainHint.REGULAR );
            if (sampleX == null || sampleY == null)
                throw new IllegalStateException("Unable to retrieve appropriate domain from parent class!");
            ((ChangeBroadcaster)sampleX).addChangeListener(this);
            ((ChangeBroadcaster)sampleY).addChangeListener(this);
        }

        RealIntervalNiceSampler rins1 = (RealIntervalNiceSampler) sampleX;
        RealIntervalNiceSampler rins2 = (RealIntervalNiceSampler) sampleY;
        float minX = (float)(double) rins1.getMinimum();
        float maxX = (float)(double) rins1.getMaximum();
        float minY = (float)(double) rins2.getMinimum();
        float maxY = (float)(double) rins2.getMaximum();

        if (type == AxesType.BOX) {
            float dx = maxX-minX, dy = maxY-minY;
            minX += .01f*dx; maxX -= .01f*dx;
            minY += .01f*dy; maxY -= .01f*dy;
            hLine.start.setLocation(minX, minY);
            hLine.end.setLocation(maxX, minY);
            vLine.start.setLocation(minX, minY);
            vLine.end.setLocation(minX, maxY);
            hLine2.start.setLocation(minX, maxY);
            hLine2.end.setLocation(maxX, maxY);
            vLine2.start.setLocation(maxX, minY);
            vLine2.end.setLocation(maxX, maxY);
            hEntry2.visible = true;
            vEntry2.visible = true;
        } else {
            hLine.start.setLocation(type == AxesType.QUADRANT1 ? 0 : minX, 0);
            hLine.end.setLocation(maxX, 0);
            vLine.start.setLocation(0, type == AxesType.QUADRANT1 || type == AxesType.UPPER_HALF ? 0 : minY);
            vLine.end.setLocation(0, maxY);
            hEntry2.visible = false;
            vEntry2.visible = false;
        }

        boolean showZero = type == AxesType.BOX;
        assignTicks(hLine, sampleX.getSamples(), "x", showZero);
        hLine2.ticks = hLine.ticks;
        hLine2.tickLabels = hLine.tickLabels;

        assignTicks(vLine, sampleY.getSamples(), "y", showZero);
        vLine2.ticks = vLine.ticks;
        vLine2.tickLabels = vLine.tickLabels;

        needsComputation = false;
    }



    /**
     * Method for assiging the tick values to a graphic line using a provided list of sample values.
     * The method ensures that all ticks are within the boundaries of the line
     * @param line the line to assign
     * @param ts tick values
     * @param var "x" or "y", depending upon which value is being considered on the line
     * @param showZero whether a 0 should be displayed along with the other values
     */
    private static void assignTicks(GraphicRuledLine<Point2D.Double> line, List<Double> ts, String var, boolean showZero) {
        ArrayList<Double> ts2 = new ArrayList<Double>();
        double lStart, lEnd;
        if (var.equals("x")) { lStart = line.start.x; lEnd = line.end.x; }
        else { lStart = line.start.y; lEnd = line.end.y; }

        for(Double t : ts) 
            if (t >= lStart && t <= lEnd)
                ts2.add(t);

        int size = ts2.size();
        line.ticks = new double[size];
        line.tickLabels = new String[size];

        double length = lEnd - lStart;
        for(int i = 0; i < size; i++) {
            line.ticks[i] = (ts2.get(i) - lStart) / length;
            line.tickLabels[i] = (showZero || ts2.get(i) != 0.0) ? PlottableConstants.FLOAT_FORMAT.format(ts2.get(i)) : null;
        }
    }

}
