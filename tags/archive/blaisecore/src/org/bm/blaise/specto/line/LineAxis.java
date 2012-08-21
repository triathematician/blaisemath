/*
 * LineAxes.java
 * Created on Sep 19, 2009
 */
package org.bm.blaise.specto.line;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import org.bm.blaise.specto.primitive.PathStyle;
import org.bm.blaise.specto.primitive.StringStyle;
import org.bm.blaise.specto.visometry.Plottable;
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
public class LineAxis extends Plottable<Double> implements VisometryChangeListener {

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
    // CONSTRUCTORS
    //

    /** Construct using defaults. */
    public LineAxis() {}

    /** Construct with x label. */
    public LineAxis(String xLabel) {
        setXLabel(xLabel);
    }


    //
    // BEAN PATTERNS
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
    // PAINT METHODS
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
    public void draw(VisometryGraphics<Double> canvas) {
        canvas.setStringStyle(labelStyle);
        if (xValues == null)
            return;
        if (ticksVisible)
            for (double x : xValues) {
                drawVTick(canvas, x, TICK_HEIGHT, strokeStyle);
                canvas.drawString(nf.format(x), x, XOFFSET.x, XOFFSET.y, labelStyle);
            }
        drawVTick(canvas, 0.0, 2 * TICK_HEIGHT, strokeStyle);

        canvas.drawSegment(canvas.getMinCoord(), canvas.getMaxCoord(), strokeStyle);
        canvas.drawString(xLabel, canvas.getMaxCoord(), -8, -7);
    }

    void drawVTick(VisometryGraphics<Double> canvas, double x, int height, PathStyle style) {
        Point2D.Double winX = canvas.getWindowPointOf(x);
        canvas.setPathStyle(style);
        canvas.drawWinSegment(winX.x, winX.y - height, winX.x, winX.y + height);
    }
    
}
