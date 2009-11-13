/*
 * LineAxes.java
 * Created on Sep 19, 2009
 */
package org.bm.blaise.specto.line;

import java.awt.Color;
import java.awt.Point;
import java.text.DecimalFormat;
import org.bm.blaise.specto.visometry.AbstractDynamicPlottable;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import org.bm.blaise.specto.visometry.VisometryMouseEvent;
import org.bm.blaise.specto.primitive.PathStyle;
import org.bm.blaise.specto.primitive.StringStyle;
import org.bm.blaise.specto.visometry.Visometry;
import org.bm.blaise.specto.visometry.VisometryChangeListener;
import org.bm.utils.NiceRangeGenerator;

/**
 * <p>
 *      <code>LineAxes</code> represents a drawn axis for points on the line.
 * </p>
 * 
 * @author Elisha Peterson
 */
public class LineAxes extends AbstractDynamicPlottable<Double> implements VisometryChangeListener {

    private static final int PIXEL_SPACING = 40;

    //
    //
    // PROPERTIES
    //
    //

    /** Label for the axis. */
    private String xLabel = "x";

    /** Style of the axis and tick marks. */
    PathStyle strokeStyle = new PathStyle( new Color(92, 92, 160) );

    /** Style of the labels. */
    StringStyle labelStyle = new StringStyle( new Color(92, 92, 128) );

    /** Whether ticks/labels are shown on the plot. */
    private boolean ticksVisible = true;


    //
    //
    // CONSTRUCTORS AND STATIC FACTORY METHODS
    //
    //

    /** Construct using defaults. */
    public LineAxes() {
    }

    /**
     * <b>FACTORY METHOD</b>
     * @return instance of the class with specified parameters.
     */
    public static LineAxes instance(String xLabel) {
        LineAxes result = new LineAxes();
        result.xLabel = xLabel;
        return result;
    }


    //
    //
    // BEAN PATTERNS
    //
    //

    /** Returns horizontal label. */
    public String getXLabel() {
        return xLabel;
    }

    /** Sets horizontal label. */
    public void setXLabel(String xLabel) {
        this.xLabel = xLabel;
    }

    public PathStyle getStrokeStyle() {
        return strokeStyle;
    }

    public void setStrokeStyle(PathStyle style) {
        this.strokeStyle = style;
    }

    /** @return true if ticks are visible. */
    public boolean isTicksVisible() {
        return ticksVisible;
    }

    /** Sets tick style. */
    public void setTicksVisible(boolean value) {
        this.ticksVisible = value;
    }

    public StringStyle getLabelStyle() {
        return labelStyle;
    }

    public void setLabelStyle(StringStyle labelStyle) {
        this.labelStyle = labelStyle;
    }
    
    //
    //
    // PAINT METHODS
    //
    //

    transient double[] xValues;

    public void visometryChanged(Visometry vis, VisometryGraphics canvas) {
        if (vis instanceof LineVisometry) {
            double min = (Double) vis.getMinPointVisible();
            double max = (Double) vis.getMaxPointVisible();

            xValues = NiceRangeGenerator.STANDARD.niceRange(min, max, ((LineVisometry) vis).getIdealStepForPixelSpacing(PIXEL_SPACING));
        }
    }

    /** Pixel height of a tick. */
    private static final int TICK_HEIGHT = 5;

    /** Offset for the axis label. */
    private static final Point XOFFSET = new Point(-10, 17);

    /** Format for axis number labels. */
    private static final DecimalFormat nf = new DecimalFormat("##0.##");

    /** Uses currently selected axis style to draw the axes.
     *
     * @param canvas the visometry graphics object used for painting
     */
    @Override
    public void paintComponent(VisometryGraphics<Double> canvas) {
        canvas.setPathStyle(strokeStyle);
        canvas.setStringStyle(labelStyle);
        if (xValues == null) {
            return;
        }
        if (ticksVisible) {
            for (int i = 0; i < xValues.length; i++) {
                canvas.drawVTick(xValues[i], TICK_HEIGHT);
                canvas.drawString(nf.format(xValues[i]), xValues[i], XOFFSET.x, XOFFSET.y);
            }
        }
        canvas.drawVTick(0.0, TICK_HEIGHT * 2);
        canvas.drawHLine(0.0);
        canvas.drawString(xLabel, canvas.getMaximumVisible(), -8, -7);
    }

    public boolean isClickablyCloseTo(VisometryMouseEvent<Double> coordinate) {
        return false;
    }
}
