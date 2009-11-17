/*
 * PlaneAxes.java
 * Created on Mar 22, 2008
 */
package org.bm.blaise.specto.plane;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import org.bm.blaise.specto.primitive.BlaisePalette;
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
public class PlaneAxes extends AbstractDynamicPlottable<Point2D.Double> implements VisometryChangeListener {

    private static final int PIXEL_SPACING = 40;

    //
    //
    // PROPERTIES
    //
    //

    /** Label for the horizontal axis. */
    private String xLabel = "x";

    /** Label for the vertical axis. */
    private String yLabel = "y";

    /** Type of axis to display. */
    private AxisStyle axisStyle = AxisStyle.CROSS;

    /** Style of the axis and tick marks. */
    PathStyle strokeStyle = new PathStyle( BlaisePalette.STANDARD.axis() );

    /** Style of the labels. */
    StringStyle labelStyle = new StringStyle( BlaisePalette.STANDARD.axisLabel(), 14 );

    /** Whether ticks/labels are shown on the plot. */
    private boolean ticksVisible = true;


    //
    //
    // CONSTRUCTORS AND STATIC FACTORY METHODS
    //
    //

    /** Construct using defaults. */
    public PlaneAxes() {
    }

    /**
     * <b>FACTORY METHOD</b>
     * @return instance of the class with specified parameters.
     */
    public static PlaneAxes instance(String xLabel, String yLabel) {
        PlaneAxes result = new PlaneAxes();
        result.xLabel = xLabel;
        result.yLabel = yLabel;
        return result;
    }

    /**
     * <b>FACTORY METHOD</b>
     * @return instance of the class with specified parameters.
     */
    public static PlaneAxes instance(AxisStyle style, String xLabel, String yLabel) {
        PlaneAxes result = new PlaneAxes();
        result.axisStyle = style;
        result.xLabel = xLabel;
        result.yLabel = yLabel;
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

    /** Returns vertical label. */
    public String getYLabel() {
        return yLabel;
    }

    /** Sets vertical label. */
    public void setYLabel(String yLabel) {
        this.yLabel = yLabel;
    }

    /** Return style. */
    public AxisStyle getStyle() {
        return axisStyle;
    }

    /** Sets style. */
    public void setStyle(AxisStyle style) {
        this.axisStyle = style;
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
    transient double[] yValues;

    public void visometryChanged(Visometry vis, VisometryGraphics canvas) {
        if (canvas instanceof PlaneGraphics) {
            PlaneGraphics pg = ((PlaneGraphics) canvas);
            Point2D.Double min = pg.getMinimumVisible();
            Point2D.Double max = pg.getMaximumVisible();
            xValues = NiceRangeGenerator.STANDARD.niceRange(min.x, max.x, pg.getIdealHStepForPixelSpacing(PIXEL_SPACING));
            yValues = NiceRangeGenerator.STANDARD.niceRange(min.y, max.y, pg.getIdealVStepForPixelSpacing(PIXEL_SPACING));
        }
    }

    /** Uses currently selected axis style to draw the axes.
     *
     * @param canvas the visometry graphics object used for painting
     */
    @Override
    public void paintComponent(VisometryGraphics<Point2D.Double> canvas) {
        canvas.setPathStyle(strokeStyle);
        canvas.setStringStyle(labelStyle);
        axisStyle.draw(((PlaneGraphics) canvas), xValues, yValues, xLabel, yLabel, ticksVisible);
    }

    public boolean isClickablyCloseTo(VisometryMouseEvent<Point2D.Double> coordinate) {
        return false;
    }

    @Override
    public String toString() {
        return "Axes ["+xLabel + "," + yLabel + "]";
    }

    //
    //
    // DRAWING METHODS: SUPPORT VARIOUS STYLES OF DRAW
    //
    //

    /** Pixel height of a tick. */
    private static final int TICK_HEIGHT = 5;

    /** Offset for the horizontal axis label. */
    private static final Point XOFFSET = new Point(-10, -7);

    /** Offset for the vertical axis label. */
    private static final Point YOFFSET = new Point(7, 10);

    /** Format for axis number labels. */
    private static final DecimalFormat nf = new DecimalFormat("##0.##");

    /**
     * Options for the type of axis.
     */
    public enum AxisStyle {

        CROSS("Cross-Style Axes") {
            @Override
            public void draw(PlaneGraphics pg, double[] xValues, double[] yValues, String xLabel, String yLabel, boolean ticksVisible) {
                if (ticksVisible && xValues != null) {
                    ArrayList<String> xLab = new ArrayList<String>();
                    for (int i = 0; i < xValues.length; i++) {
                        xLab.add(nf.format(xValues[i]));
                    }
                    ArrayList<String> yLab = new ArrayList<String>();
                    for (int i = 0; i < yValues.length; i++) {
                        yLab.add(nf.format(yValues[i]));
                    }
                    pg.drawLabeledHorizontalLines(0.0, yValues, TICK_HEIGHT, yLab, YOFFSET);
                    pg.drawLabeledVerticalLines(xValues, 0.0, TICK_HEIGHT, xLab, XOFFSET);
                }

                pg.drawHorizontalLine(0.0);
                pg.drawVerticalLine(0.0);
                pg.drawString(xLabel, new Point2D.Double(pg.getMaximumVisible().x, 0.0), -8, 12);
                pg.drawString(yLabel, new Point2D.Double(0.0, pg.getMaximumVisible().y), -10, 12);

            }
        },

        ELL("L-Style Axes") {
            @Override
            public void draw(PlaneGraphics pg, double[] xValues, double[] yValues, String xLabel, String yLabel, boolean ticksVisible) {
                if (ticksVisible) {
                    ArrayList<String> xLab = new ArrayList<String>();
                    for (int i = 0; i < xValues.length; i++) {
                        xLab.add(nf.format(xValues[i]));
                    }
                    ArrayList<String> yLab = new ArrayList<String>();
                    for (int i = 0; i < yValues.length; i++) {
                        yLab.add(nf.format(yValues[i]));
                    }
                    pg.drawLabeledHorizontalLines(0.0, yValues, TICK_HEIGHT, yLab, YOFFSET);
                    pg.drawLabeledVerticalLines(xValues, 0.0, TICK_HEIGHT, xLab, XOFFSET);
                }

                pg.drawLine(0.0, 0.0, pg.getMaximumVisible().x, 0.0);
                pg.drawLine(0.0, 0.0, 0.0, pg.getMaximumVisible().y);
                pg.drawString(xLabel, new Point2D.Double(pg.getMaximumVisible().x, 0.0), -10, 15);
                pg.drawString(yLabel, new Point2D.Double(0.0, pg.getMaximumVisible().y), -10, 15);
            }
        },

        INVERTED_T("Inverted T-Style Axes") {
            @Override
            public void draw(PlaneGraphics pg, double[] xValues, double[] yValues, String xLabel, String yLabel, boolean ticksVisible) {
                if (ticksVisible) {
                    ArrayList<String> xLab = new ArrayList<String>();
                    for (int i = 0; i < xValues.length; i++) {
                        xLab.add(nf.format(xValues[i]));
                    }
                    ArrayList<String> yLab = new ArrayList<String>();
                    for (int i = 0; i < yValues.length; i++) {
                        yLab.add(nf.format(yValues[i]));
                    }
                    pg.drawLabeledHorizontalLines(0.0, yValues, TICK_HEIGHT, yLab, YOFFSET);
                    pg.drawLabeledVerticalLines(xValues, 0.0, TICK_HEIGHT, xLab, XOFFSET);
                }

                pg.drawHorizontalLine(0.0);
                pg.drawString(xLabel, new Point2D.Double(pg.getMaximumVisible().x, 0.0), XOFFSET.x, XOFFSET.y);
                pg.drawLine(0.0, 0.0, 0.0, pg.getMaximumVisible().y);
                pg.drawString(yLabel, new Point2D.Double(0.0, pg.getMaximumVisible().y), YOFFSET.x, YOFFSET.y);
            }
        },

        BOX("Box-Style Axes") {
            @Override
            public void draw(PlaneGraphics pg, double[] xValues, double[] yValues, String xLabel, String yLabel, boolean ticksVisible) {
                Point2D.Double min = pg.getMinimumVisible();
                Point2D.Double max = pg.getMaximumVisible();

                if (ticksVisible) {
                    ArrayList<String> xLab = new ArrayList<String>();
                    for (int i = 0; i < xValues.length; i++) {
                        xLab.add(nf.format(xValues[i]));
                    }
                    ArrayList<String> yLab = new ArrayList<String>();
                    for (int i = 0; i < yValues.length; i++) {
                        yLab.add(nf.format(yValues[i]));
                    }
                    pg.drawLabeledHorizontalLines(min.x, xValues, TICK_HEIGHT, yLab, YOFFSET);
                    pg.drawLabeledHorizontalLines(max.x, xValues, TICK_HEIGHT, yLab, YOFFSET);
                    pg.drawLabeledVerticalLines(xValues, min.y, TICK_HEIGHT, xLab, XOFFSET);
                    pg.drawLabeledVerticalLines(xValues, max.y, TICK_HEIGHT, xLab, XOFFSET);
                }
                
                pg.drawWinBorder();
                pg.drawString(xLabel, new Point2D.Double(max.x, 0.0), XOFFSET.x, XOFFSET.y);
                pg.drawString(yLabel, new Point2D.Double(0.0, max.y), YOFFSET.x, YOFFSET.y);
            }
        };

        String name;

        AxisStyle(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

        abstract public void draw(PlaneGraphics pg, double[] xValues, double[] yValues, String xLabel, String yLabel, boolean ticksVisible);
    }
}
