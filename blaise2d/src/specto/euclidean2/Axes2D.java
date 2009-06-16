/*
 * NewGrid2D.java
 * Created on Mar 22, 2008
 */
package specto.euclidean2;

import sequor.model.PointRangeModel;
import specto.NiceRangeGenerator;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.Vector;
import sequor.control.Ruler;
import sequor.style.LineStyle;
import specto.DynamicPlottable;

/**
 * Axes2D represents the second generation of axes drawing, with support for multiple drawing modes, labels, etc. The Grid2D class separately
 * maintains the grid. The spacing of the two classes should be adjusted separately.
 * 
 * @author Elisha Peterson
 */
public class Axes2D extends DynamicPlottable<Euclidean2> {

    // PROPERTIES
    /** Label for the horizontal axis. */
    private String xLabel = "x";
    /** Label for the vertical axis. */
    private String yLabel = "y";


    // CONSTRUCTOR
    /** Cosntruct with gray, semi-transparent axes. */
    public Axes2D() {
        setColor(new Color(128, 128, 128, 128));
    }

    // BEAN PATTERNS
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
    // CONSTANTS USED IN DRAWING THE AXES
    /** Pixel height of a tick. */
    private static final int TICK_HEIGHT = 5;
    /** Ideal spacing betwen ticks. */
    private static final int IDEAL_TICK_SPACE = 40;
    /** Offset for the horizontal axis label. */
    private static final Point2D XOFFSET = new Point2D.Double(5, -3);
    /** Offset for the vertical axis label. */
    private static final Point2D YOFFSET = new Point2D.Double(10, -5);
    /** Format for axis number labels. */
    private static final DecimalFormat nf = new DecimalFormat("##0.#");

    /** Customized paint method. */
    @Override
    public void paintComponent(Graphics2D g, Euclidean2 v) {
        NiceRangeGenerator spacing = new NiceRangeGenerator.StandardRange();
        Vector<Double> xGrid = spacing.niceRange(
                v.getActualMin().x, v.getActualMax().x,
                (double) IDEAL_TICK_SPACE * v.getDrawWidth() / v.getWindowWidth());
        Vector<Double> yGrid = spacing.niceRange(
                v.getActualMin().y, v.getActualMax().y,
                (double) IDEAL_TICK_SPACE * v.getDrawHeight() / v.getWindowHeight());
        Vector<String> xLabels = new Vector<String>();
        for (Double d : xGrid) {
            xLabels.add(nf.format(d));
        }
        Vector<String> yLabels = new Vector<String>();
        for (Double d : yGrid) {
            yLabels.add(nf.format(d));
        }
        xGrid = v.toWindowX(xGrid);
        yGrid = v.toWindowY(yGrid);
        g.setColor(getColor());
        g.setStroke(LineStyle.STROKES[LineStyle.THICK]);

        Point2D.Double origin = v.toWindow(0, 0);
        origin.x = Math.min(Math.max(origin.x, 0), v.getWindowWidth());
        origin.y = Math.min(Math.max(origin.y, 0), v.getWindowHeight());

        switch (style.getValue()) {
            case AXES_CROSS:
                g.draw(new Line2D.Double(0, origin.y, v.getWindowWidth(), origin.y));
                g.draw(new Line2D.Double(origin.x, 0, origin.x, v.getWindowHeight()));
                Ruler.drawLabeledHorizontalLines(g, origin.x - TICK_HEIGHT, yGrid, 2 * TICK_HEIGHT, yLabels, YOFFSET);
                Ruler.drawLabeledVerticalLines(g, xGrid, origin.y - TICK_HEIGHT, 2 * TICK_HEIGHT, xLabels, XOFFSET);
                break;
            case AXES_L:
                g.draw(new Line2D.Double(origin.x, origin.y, v.getWindowWidth(), origin.y));
                g.draw(new Line2D.Double(origin.x, origin.y, origin.x, 0));
                Ruler.drawLabeledVerticalLines(g, xGrid, origin.y, TICK_HEIGHT, xLabels, XOFFSET);
                Ruler.drawLabeledHorizontalLines(g, origin.x, yGrid, TICK_HEIGHT, yLabels, YOFFSET);
                break;
            case AXES_T:
                g.draw(new Line2D.Double(0, origin.y, v.getWindowWidth(), origin.y));
                g.draw(new Line2D.Double(origin.x, origin.y, origin.x, 0));
                Ruler.drawLabeledVerticalLines(g, xGrid, origin.y, TICK_HEIGHT, xLabels, XOFFSET);
                Ruler.drawLabeledHorizontalLines(g, origin.x - TICK_HEIGHT, yGrid, 2 * TICK_HEIGHT, yLabels, YOFFSET);
                break;
            case AXES_BOX:
                g.draw(new Rectangle2D.Double(0, 0, v.getWindowWidth(), v.getWindowHeight()));
                Ruler.drawLabeledVerticalLines(g, xGrid, 0, TICK_HEIGHT, xLabels, XOFFSET);
                Ruler.drawLabeledVerticalLines(g, xGrid, v.getWindowHeight(), -TICK_HEIGHT, xLabels, XOFFSET);
                Ruler.drawLabeledHorizontalLines(g, 0, yGrid, TICK_HEIGHT, yLabels, YOFFSET);
                Ruler.drawLabeledHorizontalLines(g, v.getWindowWidth(), yGrid, -TICK_HEIGHT, yLabels, YOFFSET);
                break;
        }

        Point2D.Float xLabelPos = new Point2D.Float(v.getWindowWidth() - 10, (float) origin.y + 12);
        Point2D.Float yLabelPos = new Point2D.Float((float) origin.x - 12, 15);
        g.drawString(xLabel, xLabelPos.x, xLabelPos.y);
        g.drawString(yLabel, yLabelPos.x, yLabelPos.y);
    }

    // CONSTRAINT MODELS
    /** Returns point model constrained to the horizontal axis. */
    public static PointRangeModel getXAxisModel() {
        return new PointRangeModel() {

            @Override
            public void setTo(double x0, double y0) {
                super.setTo(x0, 0);
            }
        };
    }

    /** Returns point model constrained to the vertical axis. */
    public static PointRangeModel getYAxisModel() {
        return new PointRangeModel() {

            @Override
            public void setTo(double x0, double y0) {
                super.setTo(0, y0);
            }
        };
    }

    // STYLE SETTINGS
    /** Customized name of the class. */
    @Override
    public String toString() {
        return "Axes";
    }
    /** Displays axes with x and y axes. */
    public static final int AXES_CROSS = 0;
    /** Displays axes on outer boundary. */
    public static final int AXES_BOX = 1;
    /** Displays the positive axes only. */
    public static final int AXES_L = 2;
    /** Displays the x-axis and the positive y-axis. */
    public static final int AXES_T = 3;
    /** String descriptors of the available styles. */
    final static String[] styleStrings = {"Cross-Style Axes", "Box-Style Axes", "L-Style Axes", "Inverted T-Style Axes"};

    /** Override style description. */
    @Override
    public String[] getStyleStrings() {
        return styleStrings;
    }
}
