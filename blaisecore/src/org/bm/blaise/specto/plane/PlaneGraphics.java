/**
 * PlaneGraphics.java
 * Created on Jul 30, 2009
 */
package org.bm.blaise.specto.plane;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import org.bm.blaise.specto.primitive.*;
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


    // ************************************************************** //
    //                                                                //
    //         NEW VISOMETRY DELEGATORS                               //
    //                                                                //
    // ************************************************************** //

    public double getScaleX() {
        return Math.abs(((PlaneVisometry)visometry).getScaleX());
    }

    public double getScaleY() {
        return Math.abs(((PlaneVisometry)visometry).getScaleY());
    }



    // ************************************************************** //
    //                                                                //
    //         NEW ROUTINES TO DRAW IN LOCAL COORDINATES              //
    //                                                                //
    // ************************************************************** //

    //
    // PATH ROUTINES
    //

    /**
     * Draws line through specified points (in local coordinates)
     * @param x1 first x value
     * @param y1 first y value
     * @param x2 second x value
     * @param y2 second y value
     * @param style a custom style
     */
    public void drawSegment(double x1, double y1, double x2, double y2, PathStyle style) {
        super.drawSegment(new Point2D.Double(x1, y1), new Point2D.Double(x2, y2), style);
    }

    /**
     * Draws line through specified points (in local coordinates)
     * @param x1 first x value
     * @param y1 first y value
     * @param x2 second x value
     * @param y2 second y value
     */
    public void drawSegment(double x1, double y1, double x2, double y2) {
        super.drawSegment(new Point2D.Double(x1, y1), new Point2D.Double(x2, y2));
    }

    /**
     * Returns horizontal line at the given y value
     * @param y the y value
     * @param style the style to draw with
     */
    public void drawHorizontalLine(double y, PathStyle style) {
        drawSegment(getMinCoord().x, y, getMaxCoord().x, y, style);
    }

    /**
     * Returns horizontal line at the given y value
     * @param y the y value
     */
    public void drawHorizontalLine(double y) {
        drawSegment(getMinCoord().x, y, getMaxCoord().x, y);
    }

    /** 
     * Returns vertical line at the given x value
     * @param x the x value
     * @param style the style to draw with
     */
    public void drawVerticalLine(double x, PathStyle style) {
        drawSegment(x, getMinCoord().y, x, getMaxCoord().y, style);
    }

    /**
     * Returns vertical line at the given x value.
     * @param x the x value
     */
    public void drawVerticalLine(double x) {
        drawSegment(x, getMinCoord().y, x, getMaxCoord().y);
    }

    /**
     * Transforms a path from local coords to window coordinates.
     * @param path the path to transform
     * @return the resulting path
     */
    public GeneralPath toWindow(GeneralPath path) {
        path.transform(((PlaneVisometry)visometry).at);
        return path;
    }

    /**
     * Draws a path specified in LOCAL coordinates with custom style
     * @param path the path in LOCAL coordinates
     * @param style style to draw with
     */
    public void drawPath(GeneralPath path, PathStyle style) {
        GeneralPath pathClone = (GeneralPath) path.clone();
        pathClone.transform(((PlaneVisometry)visometry).at);
        style.draw(gr, pathClone, selected);
    }

    /**
     * Draws a path specified in LOCAL coordinates, using default <code>pathStyle</code>
     * @param path the path in LOCAL coordinates
     */
    public void drawPath(GeneralPath path) {
        drawPath(path, pathStyle);
    }

    //
    // DRAW ROUTINES: SHAPES
    //

    /**
     * Draws circle about specified point (in local coords)
     * @param x x-coord of center
     * @param y y-coord of center
     * @param r radius
     * @param style custom style for drawing
     */
    public void drawCircle(double x, double y, double r, ShapeStyle style) {
        super.drawEllipse(new Point2D.Double(x - r, y - r), new Point2D.Double(x + r, y + r), style);
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

    //
    // DRAW ROUTINES: ARROWS & VECTORS
    //

   /**
    * Draws vector at given point
    * @param x starting location x value
    * @param y starting location y value
    * @param vx vector x component
    * @param vy vector y component
    * @param style arrow style to use for drawing
    */
    public void drawVector(double x, double y, double vx, double vy, ArrowStyle style) {
        super.drawArrow(new Point2D.Double(x, y), new Point2D.Double(x + vx, y + vy), style);
    }

   /**
    * Draws vector at given point
    * @param x starting location x value
    * @param y starting location y value
    * @param vx vector x component
    * @param vy vector y component
    * @param style arrow style to use for drawing
    */
    public void drawVector(double x, double y, double vx, double vy) {
        super.drawArrow(new Point2D.Double(x, y), new Point2D.Double(x + vx, y + vy));
    }

    //
    // SAMPLING METHODS
    //

    /**
     * Computes and returns the step size corresponding to a given number of pixels in the x-direction.
     * @param pixelSpacing # of pixels
     * @return coordinate range for specified number of pixels.
     */
    public double getIdealHStepForPixelSpacing(double pixelSpacing) {
        return Math.abs(pixelSpacing / ((PlaneVisometry) visometry).getScaleX());
    }

    /**
     * Computes and returns the step size corresponding to a given number of pixels in the y-direction.
     * @param pixelSpacing # of pixels
     * @return coordinate range for specified number of pixels.
     */
    public double getIdealVStepForPixelSpacing(double pixelSpacing) {
        return Math.abs(pixelSpacing / ((PlaneVisometry) visometry).getScaleY());
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
