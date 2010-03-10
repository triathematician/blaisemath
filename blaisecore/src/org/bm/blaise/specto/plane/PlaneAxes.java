/*
 * PlaneAxes.java
 * Created on Mar 22, 2008
 */
package org.bm.blaise.specto.plane;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.bm.blaise.specto.primitive.BlaisePalette;
import org.bm.blaise.specto.primitive.GraphicString;
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
 *      <code>PlaneAxes</code> represents the second generation of axes drawing, with support for multiple drawing modes, labels, etc.
 *      The <code>PlaneGrid</code> class separately maintains the grid.
 *      The spacing of the two classes should be adjusted separately.
 * </p>
 * 
 * TODO - moveable labels
 * TODO - arrow option for ends of cross
 * TODO - option for multiples of PI spacing/labels
 * 
 * @author Elisha Peterson
 */
public class PlaneAxes extends AbstractDynamicPlottable<Point2D.Double> implements Cloneable, VisometryChangeListener {

    /** Options for the type of axis. */
    public enum Style {
        CROSS,
        ELL,
        INVERTED_T,
        BOX
    }

    //
    // PROPERTIES
    //

    /** Label for the horizontal axis. */
    private String xLabel = "x";
    /** Label for the vertical axis. */
    private String yLabel = "y";

    /** Type of axis to display. */
    private Style axisStyle = Style.CROSS;

    /** Style of the axis and tick marks. */
    PathStyle strokeStyle = new PathStyle( BlaisePalette.STANDARD.axis() );
    /** Style of the labels. */
    StringStyle labelStyle = new StringStyle( BlaisePalette.STANDARD.axisLabel(), 14 );

    /** Whether ticks/labels are shown on the plot. */
    private boolean ticksVisible = true;


    //
    // CONSTRUCTORS
    //

    /** Construct using defaults. */
    public PlaneAxes() {
    }

    /** Construct with specified labels. */
    public PlaneAxes(String xLabel, String yLabel) {
        setXLabel(xLabel);
        setYLabel(yLabel);
    }

    /** Construct with specified labels. */
    public PlaneAxes(String xLabel, String yLabel, Style style) {
        setXLabel(xLabel);
        setYLabel(yLabel);
        setStyle(style);
    }
    
    //
    // OBJECT UTILITIES
    //

    @Override
    public PlaneAxes clone() {
        return new PlaneAxes(xLabel, yLabel, axisStyle);
    }

    @Override
    public String toString() {
        return "Axes ["+xLabel + "," + yLabel + "]";
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
        if (!this.xLabel.equals(xLabel)) {
            this.xLabel = xLabel;
            fireStateChanged();
        }
    }

    /** Returns vertical label. */
    public String getYLabel() {
        return yLabel;
    }

    /** Sets vertical label. */
    public void setYLabel(String yLabel) {
        if (!this.yLabel.equals(yLabel)) {
            this.yLabel = yLabel;
            fireStateChanged();
        }
    }

    /** Return style. */
    public Style getStyle() {
        return axisStyle;
    }

    /** Sets style. */
    public void setStyle(Style style) {
        if (this.axisStyle != style) {
            this.axisStyle = style;
            fireStateChanged();
        }
    }

    public PathStyle getStrokeStyle() {
        return strokeStyle;
    }

    public void setStrokeStyle(PathStyle style) {
        if (this.strokeStyle != style) {
            this.strokeStyle = style;
            fireStateChanged();
        }
    }

    public StringStyle getLabelStyle() {
        return labelStyle;
    }

    public void setLabelStyle(StringStyle labelStyle) {
        if (this.labelStyle != labelStyle) {
            this.labelStyle = labelStyle;
            fireStateChanged();
        }
    }

    /** @return true if ticks are visible. */
    public boolean isTicksVisible() {
        return ticksVisible;
    }

    /** Sets tick style. */
    public void setTicksVisible(boolean value) {
        if (this.ticksVisible != value) {
            this.ticksVisible = value;
            fireStateChanged();
        }
    }
    
    //
    // PAINT METHODS
    //

    transient Point2D.Double min;
    transient Point2D.Double max;
    transient double[] xValues;
    transient String[] xLabels;
    transient double[] yValues;
    transient String[] yLabels;
    
    /** Format for axis number labels. */
    private static final DecimalFormat nf = new DecimalFormat("##0.##");

    public void visometryChanged(Visometry vis, VisometryGraphics canvas) {
        if (! (canvas instanceof PlaneGraphics))
            throw new IllegalStateException("PlaneAxes only supports PlaneGraphics!");
        PlaneGraphics pg = ((PlaneGraphics) canvas);

        min = pg.getMinCoord();
        max = pg.getMaxCoord();
        if (axisStyle == Style.ELL) {
            min.x = 0;
            min.y = 0;
        } else if (axisStyle == Style.INVERTED_T)
            min.y = 0;

        xValues = NiceRangeGenerator.STANDARD.niceRange(min.x, max.x, pg.getIdealHStepForPixelSpacing(PIXEL_SPACING));
        xLabels = new String[xValues.length];
        for (int i = 0; i < xValues.length; i++)
            xLabels[i] = nf.format(xValues[i]);
        
        yValues = NiceRangeGenerator.STANDARD.niceRange(min.y, max.y, pg.getIdealVStepForPixelSpacing(PIXEL_SPACING));
        yLabels = new String[yValues.length];
        for (int i = 0; i < yValues.length; i++)
            yLabels[i] = nf.format(yValues[i]);
    }

    /** Pixel height of a tick. */
    private static final int TICK_HEIGHT = 5;
    /** Determines the "ideal" spacing between tick marks, in terms of pixels. */
    private static final int PIXEL_SPACING = 40;

    /**
     * Uses currently selected axis style to draw the axes.
     * @param canvas the visometry graphics object used for painting
     */
    @Override
    public void paintComponent(VisometryGraphics<Point2D.Double> canvas) {
        PlaneGraphics pg = (PlaneGraphics) canvas;
        Point2D.Double maxC = pg.getMaxCoord();
        Point2D.Double minC = pg.getMinCoord();

        // draw ticks
        if (ticksVisible) {
            switch (axisStyle) {
                case BOX:
                    drawLabeledHorizontalLines(pg, min.x, yValues, 0, TICK_HEIGHT, yLabels);
                    drawLabeledHorizontalLines(pg, max.x, yValues, -TICK_HEIGHT, 0, yLabels);
                    drawLabeledVerticalLines(pg, xValues, min.y, 0, TICK_HEIGHT, xLabels);
                    drawLabeledVerticalLines(pg, xValues, max.y, -TICK_HEIGHT, 0, xLabels);
                    break;
                case CROSS:
                case ELL:
                case INVERTED_T:
                default:
                    drawLabeledHorizontalLines(pg, 0.0, yValues, -TICK_HEIGHT, TICK_HEIGHT, yLabels);
                    drawLabeledVerticalLines(pg, xValues, 0.0, -TICK_HEIGHT, TICK_HEIGHT, xLabels);
                    break;
            }
        }

        // draw axes
        switch (axisStyle) {
            case BOX:
                pg.drawWinBorder(strokeStyle);
                break;
            case ELL:
                pg.drawSegment(0.0, 0.0, maxC.x, 0.0, strokeStyle);
                pg.drawSegment(0.0, 0.0, 0.0, maxC.y, strokeStyle);
                break;
            case INVERTED_T:
                pg.drawSegment(0.0, 0.0, 0.0, maxC.y, strokeStyle);
                pg.drawHorizontalLine(0.0, strokeStyle);
                break;
            case CROSS:
                pg.drawHorizontalLine(0.0, strokeStyle);
                pg.drawVerticalLine(0.0, strokeStyle);
            default:
                break;
        }

        // draw axis labels
        switch(axisStyle) {
            case BOX:
                pg.drawString(xLabel, new Point2D.Double(max.x, 0.0), 0, -2 * TICK_HEIGHT, GraphicString.RIGHT, labelStyle);
                pg.drawString(yLabel, new Point2D.Double(0.0, max.y), -2 * TICK_HEIGHT, 0, GraphicString.TOP, labelStyle);
                break;
            case ELL:
            case INVERTED_T:
            case CROSS:
            default:
                pg.drawString(xLabel, new Point2D.Double(maxC.x, 0.0), -TICK_HEIGHT, 2 * TICK_HEIGHT, GraphicString.RIGHT, labelStyle);
                pg.drawString(yLabel, new Point2D.Double(0.0, maxC.y), 2 * TICK_HEIGHT, -TICK_HEIGHT, GraphicString.TOP_LEFT, labelStyle);
                break;
        }
    }

    
    //
    // MOUSE HANDLING
    //

    public boolean isClickablyCloseTo(VisometryMouseEvent<Point2D.Double> coordinate) {
        return false;
    }

    //
    // UTILITY DRAWING METHODS
    //


    /** 
     * Draws a series of labeled horizontal lines with appropriate labels, e.g.<br><br>
     *    ---  2.0 <br>
     *    ---  1.0 <br>
     *    ---  0.0 <br>
     *    ---  -1.0 <br>
     *    ---  -2.0 <br>
     *
     * @param pg the graphics object to draw on
     *
     * @param x the x value at which to draw the lines
     * @param ys the set of y values
     * @param minusX pixels to shift in negative x direction
     * @param plusX pixels to shift in positive direction
     *
     * @param labels the set of labels for the y values
     */
    public void drawLabeledHorizontalLines(PlaneGraphics pg,
            double x, double[] ys, int minusX, int plusX,
            String[] labels) {

        assert ys != null && labels != null && ys.length == labels.length;

        Point2D.Double pt = new Point2D.Double(x, 0);
        Point2D.Double winPt;

        for (int i = 0; i < ys.length; i++) {
            pt.y = ys[i];
            winPt = pg.getWindowPointOf(pt);
            pg.drawWinSegment(winPt.x + minusX, winPt.y, winPt.x + plusX, winPt.y, strokeStyle);
            pg.drawString(labels[i], pt, plusX + 2, 0, GraphicString.LEFT, labelStyle);
        }
    }

    /**
     * Draws a series of labeled vertical lines with appropriate labels, e.g.<br><br>
     * <code>-2.0 . -1.0 .. 0.0 .. 1.0 .. 2.0<br>
     *       . | .... | .... | .... | .... |<br>
     *       . | .... | .... | .... | .... |<br>
     * </code>
     *
     * @param pg the graphics object to draw on
     *
     * @param xx the set of x values
     * @param y the y value where to draw the line
     * @param minusY pixels to shift in negative y direction
     * @param plusY pixels to shift in positive y direction
     *
     * @param labels the set of labels for the x values
     */
    public void drawLabeledVerticalLines(PlaneGraphics pg,
            double[] xs, double y, int minusY, int plusY,
            String[] labels) {

        assert xs != null && labels != null && xs.length == labels.length;

        Point2D.Double pt = new Point2D.Double(0, y);
        Point2D.Double winPt;

        for (int i = 0; i < xs.length; i++) {
            pt.x = xs[i];
            winPt = pg.getWindowPointOf(pt);
            pg.drawWinSegment(winPt.x, winPt.y - plusY, winPt.x, winPt.y - minusY, strokeStyle);
            pg.drawString(labels[i], pt, 0, plusY + 2, GraphicString.BOTTOM, labelStyle);
        }
    }

}
