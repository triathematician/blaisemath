/*
 * Axes3D.java
 * Created on Mar 22, 2008
 */
package specto.euclidean3;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import scio.coordinate.R3;
import specto.DynamicPlottable;

/**
 * Axes2D represents the second generation of axes drawing, with support for multiple drawing modes, labels, etc. The Grid2D class separately
 * maintains the grid. The spacing of the two classes should be adjusted separately.
 * 
 * @author Elisha Peterson
 */
public class Axes3D extends DynamicPlottable<Euclidean3> {

    // PROPERTIES
    /** Label for the x axis. */
    private String xLabel = "x";
    /** Label for the y axis. */
    private String yLabel = "y";
    /** Label for the vertical axis. */
    private String zLabel = "z";

    // CONSTRUCTOR
    public Axes3D() {
        /** Cosntruct with gray, semi-transparent axes. */
        setColor(new Color(128, 128, 128, 128));
        style.setValue(AXES_BOX);
    }

    // BEAN PATTERNS
    /** Returns x axis label. */
    public String getXLabel() {
        return xLabel;
    }

    /** Sets x axis label. */
    public void setXLabel(String xLabel) {
        this.xLabel = xLabel;
    }

    /** Returns y axis label. */
    public String getYLabel() {
        return yLabel;
    }

    /** Sets y axis label. */
    public void setYLabel(String yLabel) {
        this.yLabel = yLabel;
    }

    /** Returns vertical axis label. */
    public String getZLabel() {
        return zLabel;
    }

    /** Sets vertical axis label. */
    public void setZLabel(String zLabel) {
        this.zLabel = zLabel;
    }

    // PAINT METHODS
    /** Pixel height of a tick. */
    private static final int TICK_HEIGHT = 5;
    /** Ideal spacing betwen ticks. */
    private static final int IDEAL_TICK_SPACE = 40;
    /** Offset for the x axis label. */
    private static final Point2D XOFFSET = new Point2D.Double(5, -3);
    /** Offset for the y axis label. */
    private static final Point2D YOFFSET = new Point2D.Double(10, -5);
    /** Offset for the vertical axis label. */
    private static final Point2D ZOFFSET = new Point2D.Double(10, -5);
    /** Format for axis number labels. */
    private static final DecimalFormat nf = new DecimalFormat("##0.#");

    /** Customized paint method. */
    @Override
    public void paintComponent(Graphics2D g, Euclidean3 v) {
        R3 center = v.getCenter();
        double wid = v.getSceneSize();
        if (style.getValue() != AXES_BOX) {
            wid *= 1.1;
        }
        R3[] axes = {center.plus(wid, 0, 0), center.plus(0, wid, 0), center.plus(0, 0, wid)};
        R3[] axes2 = {center.plus(-wid, 0, 0), center.plus(0, -wid, 0), center.plus(0, 0, -wid)};
        switch (style.getValue()) {
            case AXES_BOX:
                R3[] box = {center.plus(-wid, -wid, -wid), center.plus(-wid, -wid, wid), center.plus(-wid, wid, -wid), center.plus(-wid, wid, wid),
                    center.plus(wid, -wid, -wid), center.plus(wid, -wid, wid), center.plus(wid, wid, -wid), center.plus(wid, wid, wid)};
                v.drawLineSegment(g, box[0], box[1]);
                v.drawLineSegment(g, box[0], box[2]);
                v.drawLineSegment(g, box[0], box[4]);
                v.drawLineSegment(g, box[1], box[3]);
                v.drawLineSegment(g, box[1], box[5]);
                v.drawLineSegment(g, box[2], box[3]);
                v.drawLineSegment(g, box[2], box[6]);
                v.drawLineSegment(g, box[4], box[5]);
                v.drawLineSegment(g, box[4], box[6]);
                v.drawLineSegment(g, box[3], box[7]);
                v.drawLineSegment(g, box[5], box[7]);
                v.drawLineSegment(g, box[6], box[7]);
                break;
            case AXES_OCTANT:
                v.drawLineSegment(g, center, axes[0]);
                v.drawLineSegment(g, center, axes[1]);
                v.drawLineSegment(g, center, axes[2]);
                break;
            case AXES_TOPHALF:
                v.drawLineSegment(g, axes2[0], axes[0]);
                v.drawLineSegment(g, axes2[1], axes[1]);
                v.drawLineSegment(g, center, axes[2]);
                break;
            case AXES_STANDARD:
            default:
                v.drawLineSegment(g, axes2[0], axes[0]);
                v.drawLineSegment(g, axes2[1], axes[1]);
                v.drawLineSegment(g, axes2[2], axes[2]);
                break;
        }
        v.drawString(g, axes[0], xLabel);
        v.drawString(g, axes[1], yLabel);
        v.drawString(g, axes[2], zLabel);
        v.fillDot(g, axes[0], 3);
        v.fillDot(g, axes[1], 3);
        v.fillDot(g, axes[2], 3);
    }

    // STYLE SETTINGS    
    /** Customized name of the class. */
    @Override
    public String toString() {
        return "Axes";
    }
    /** Displays axes with x and y and z axes. */
    public static final int AXES_STANDARD = 0;
    /** Displays axes on outer boundary. */
    public static final int AXES_BOX = 1;
    /** Displays the positive axes only. */
    public static final int AXES_OCTANT = 2;
    /** Displays the x and y axes and the positive z-axis. */
    public static final int AXES_TOPHALF = 3;
    /** String descriptors of the available styles. */
    public static final String[] styleStrings = {"Standard", "Box", "First Octant", "Upper Half Space"};

    /** Override style description. */
    @Override
    public String[] getStyleStrings() {
        return styleStrings;
    }
}
