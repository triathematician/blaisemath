/**
 * PlaneGraphics.java
 * Created on Jul 30, 2009
 */
package org.bm.blaise.specto.plane;

import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.List;
import org.bm.blaise.specto.visometry.Visometry;
import org.bm.blaise.specto.visometry.VisometryGraphics;

/**
 * <p>
 *   <code>PlaneGraphics</code> handles special graphics for the plane visometry.
 * </p>
 *
 * @author Elisha Peterson
 */
public class PlaneGraphics extends VisometryGraphics<Point2D.Double> {

    public PlaneGraphics(Visometry<Point2D.Double> vis) {
        super(vis);
    }

    //
    //
    // DRAW METHODS
    //
    //

    /**
     * Draws line through specified points (in local coordinates)
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public void drawLine(double x1, double y1, double x2, double y2) {
        super.drawSegment(new Point2D.Double(x1, y1), new Point2D.Double(x2, y2));
    }

   /**
    * Draws vector at given point
    * @param x starting location x value
    * @param y starting location y value
    * @param vx vector x component
    * @param vy vector y component
    */
    public void drawVector(double x, double y, double vx, double vy) {
        super.drawArrow(new Point2D.Double(x, y), new Point2D.Double(x + vx, y + vy));
    }

    /** Returns horizontal line at the given y value. */
    public void drawHorizontalLine(double y) {
        drawLine(getMinimumVisible().x, y, getMaximumVisible().x, y);
    }

    /** Returns vertical line at the given x value. */
    public void drawVerticalLine(double x) {
        drawLine(x, getMinimumVisible().y, x, getMaximumVisible().y);
    }

    /** Draws labeled line with ticks.
     * @param x
     * @param ys
     * @param dist length of the line from center point (in pixels)
     * @param labels
     * @param labelOffset pixel distance from center of lines to label position
     */
    public void drawLabeledHorizontalLines(double x, double[] ys, double dist, List<String> labels, Point labelOffset) {
        Line2D.Double line = new Line2D.Double();
        for (int i = 0; i < Math.min(ys.length, labels.size()); i++) {
            Point2D winPt = vis.getWindowPointOf(new Point2D.Double(x, ys[i]));
            line.setLine(winPt.getX() - dist, winPt.getY(), winPt.getX() + dist, winPt.getY());
            pathStyle.draw(line, gr);
        }
        for (int i = 0; i < Math.min(ys.length, labels.size()); i++) {
            drawString(labels.get(i), new Point2D.Double(x, ys[i]), labelOffset.x, labelOffset.y);
        }
    }

    /** Draws labeled line with ticks.
     * @param xs
     * @param y
     * @param dist length of the line from center point (in pixels)
     * @param labels
     * @param labelOffset pixel distance from center of lines to label position
     */
    public void drawLabeledVerticalLines(double[] xs, double y, double dist, List<String> labels, Point labelOffset) {
        Line2D.Double line = new Line2D.Double();
        for (int i = 0; i < Math.min(xs.length, labels.size()); i++) {
            Point2D winPt = vis.getWindowPointOf(new Point2D.Double(xs[i], y));
            line.setLine(winPt.getX(), winPt.getY() - dist, winPt.getX(), winPt.getY() + dist);
            pathStyle.draw(line, gr);
        }
        for (int i = 0; i < Math.min(xs.length, labels.size()); i++) {
            drawString(labels.get(i), new Point2D.Double(xs[i], y), labelOffset.x, labelOffset.y);
        }
    }

    /**
     * Draws circle about specified point (in local coords)
     * @param x x-coord of center
     * @param y y-coord of center
     * @param r radius
     */
    public void drawCircle(double x, double y, double r) {
        super.drawEllipse(new Point2D.Double(x - r, y - r), new Point2D.Double(x + r, y + r));
    }

    /**
     * Draws a path in LOCAL coordinates, using default <code>pathStyle</code>
     * @param path the path in LOCAL coordinates
     */
    @Override
    public void drawPath(GeneralPath path) {
        super.drawPath((GeneralPath) path.clone(), ((PlaneVisometry)vis).at);
    }

    //
    //
    // SAMPLING METHODS
    //
    //

    /**
     * Computes and returns the step size corresponding to a given number of pixels in the x-direction.
     * @param pixelSpacing # of pixels
     * @return coordinate range for specified number of pixels.
     */
    public double getIdealHStepForPixelSpacing(double pixelSpacing) {
        return Math.abs(pixelSpacing / ((PlaneVisometry) vis).getScaleX());
    }

    /**
     * Computes and returns the step size corresponding to a given number of pixels in the y-direction.
     * @param pixelSpacing # of pixels
     * @return coordinate range for specified number of pixels.
     */
    public double getIdealVStepForPixelSpacing(double pixelSpacing) {
        return Math.abs(pixelSpacing / ((PlaneVisometry) vis).getScaleY());
    }

    /**
     * Computes and returns the step size corresponding to a given number of pixels in either x or y direction.
     * This assumes the aspectRatio is fairly close to 1.
     * @param pixelSpacing # of pixels
     * @return coordinate range for specified number of pixels (averages that for horizontal and vertical).
     */
    public double getIdealStepForPixelSpacing(double pixelSpacing) {
        double hStep = getIdealHStepForPixelSpacing(pixelSpacing);
        double vStep = getIdealVStepForPixelSpacing(pixelSpacing);
        return (hStep + vStep) / 2;
    }
}
