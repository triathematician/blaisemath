/**
 * VisometryGraphics.java
 * Created on Jul 30, 2009
 */

package org.bm.blaise.specto.visometry;

import org.bm.blaise.specto.primitive.*;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;

// TODO - rethink the approach here... is the drawing done within the plottable object, or is it
// done within here? or is it from the visometry? review the plottable's contract, in terms of
// what it delivers... should be the graphics primitive objects, converted to the screen geometry


/**
 * <p>
 *  This class is responsible for <b>ALL</b> drawing done by underyling plottables.
 * </p>
 * <p>
 *  Drawing is accomplished using customized style classes, which are stored by
 *  this class and may be altered externally. This class first converts
 *  the passed in elements into <i>graphics primitives</i>, and then uses the appropriate
 *  <i>style</i> to draw the element on the <code>Graphics2D</code> canvas.
 * </p>
 *
 * @param <C> class representing the underlying coordinate system
 *
 * @author Elisha Peterson
 */
public class VisometryGraphics<C> {

    /** Underlying graphics object. */
    protected Graphics2D gr = null;

    /** Visometry for point conversion. */
    protected Visometry<C> vis;

    //
    //
    // DEFAULT STYLE PROPERTIES
    //
    //

    protected ArrowStyle arrowStyle = new ArrowStyle();
    protected PathStyle pathStyle = new PathStyle();
    protected PointStyle pointStyle = new PointStyle();
    protected ShapeStyle shapeStyle = new ShapeStyle();
    protected StringStyle stringStyle = new StringStyle();


    //
    //
    // CONSTRUCTORS & FACTORY METHODS
    //
    //
    
    /**
     * Construct with specified elements.
     * @param vis visometry (converts to/from window geometry)
     */
    protected VisometryGraphics(Visometry<C> vis) {
        this.vis = vis;
    }

    /**
     * <b>FACTORY METHOD:</b> create and return a new instance of this class.
     * @return new instance of a graphics object for painting on specified visometry.
     */
    public static VisometryGraphics instance(Visometry vis) {
        return new VisometryGraphics(vis);
    }

    //
    //
    // BIG BEANS
    //
    //

    /**
     * Sets the underlying screen graphics to paint on.
     * @param gr the graphics
     */
    public void setScreenGraphics(Graphics2D gr) {
        this.gr = gr;
        gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }
    
    /**
     * Sets the underlying visometry.
     * @param vis the visometry
     */
    public void setVisometry(Visometry<C> vis) {
        this.vis = vis;
    }

    //
    //
    // STYLE BEANS
    //
    //

    public ArrowStyle getArrowStyle() {
        return arrowStyle;
    }

    public void setArrowStyle(ArrowStyle arrowStyle) {
        this.arrowStyle = arrowStyle;
    }

    public PathStyle getPathStyle() {
        return pathStyle;
    }

    public void setPathStyle(PathStyle pathStyle) {
        this.pathStyle = pathStyle;
    }

    public PointStyle getPointStyle() {
        return pointStyle;
    }

    public void setPointStyle(PointStyle pointStyle) {
        this.pointStyle = pointStyle;
    }

    public ShapeStyle getShapeStyle() {
        return shapeStyle;
    }

    public void setShapeStyle(ShapeStyle shapeStyle) {
        this.shapeStyle = shapeStyle;
    }

    public StringStyle getStringStyle() {
        return stringStyle;
    }

    public void setStringStyle(StringStyle stringStyle) {
        this.stringStyle = stringStyle;
    }

    /**
     * Generic command to set style.
     * 
     * @param style
     */
    public void setPrimitiveStyle(PrimitiveStyle style) {
        if (style instanceof ArrowStyle) {
            setArrowStyle((ArrowStyle) style);
        } else if (style instanceof PathStyle) {
            setPathStyle((PathStyle) style);
        } else if (style instanceof PointStyle) {
            setPointStyle((PointStyle) style);
        } else if (style instanceof ShapeStyle) {
            setShapeStyle((ShapeStyle) style);
        } else if (style instanceof StringStyle) {
            setStringStyle((StringStyle) style);
        } else {
            System.out.println("Unable to set style");
        }
    }



    //
    //
    // WINDOW ATTRIBUTE BEANS
    //
    //

    /**
     * @return minimum coordinate of the visometry.
     */
    public C getMinimumVisible() {
        return vis.getMinPointVisible();
    }

    /**
     * @return minimum coordinate of the visometry.
     */
    public C getMaximumVisible() {
        return vis.getMaxPointVisible();
    }

    /**
     * @return bounding box of the window
     */
    public RectangularShape getWindowBounds() {
        return vis.getWindowBounds();
    }

    /**
     * @return minimum x value in window coords
     */
    public double getWindowMinX() {
        return vis.getWindowBounds().getMinX();
    }

    /**
     * @return minimum x value in window coords
     */
    public double getWindowMaxX() {
        return vis.getWindowBounds().getMaxX();
    }

    /**
     * @return minimum x value in window coords
     */
    public double getWindowMinY() {
        return vis.getWindowBounds().getMinY();
    }

    /**
     * @return minimum x value in window coords
     */
    public double getWindowMaxY() {
        return vis.getWindowBounds().getMaxY();
    }
    
    /** @return width of window */
    public double getWindowWidth() {
        return vis.getWindowBounds().getWidth();
    }
    
    /** @return height of window */
    public double getWindowHeight() {
        return vis.getWindowBounds().getHeight();
    }

    //
    //
    // DRAW UTILITY LIBRARY
    //
    //

    /**
     * Draws a coordinate with a given style class.
     * @param coordinate the coordinate
     * @param style the style used to draw
     */
    public void drawWithStyle(C coordinate, PrimitiveStyle<Point2D> style) {
        style.draw(vis.getWindowPointOf(coordinate), gr);
    }

    /**
     * Draws a point at specified coordinate, using default <code>pointStyle</code>
     * @param coordinate point in local coordinates
     */
    public void drawPoint(C coordinate) {
        drawWithStyle(coordinate, pointStyle);
    }

    /**
     * Draws a series of points, using default <code>pointStyle</code>
     * @param coords the points in local coordinates
     */
    public void drawPoints(C[] coords) {
        if (coords.length <= 0) {
            return;
        }
        for (int i = 0; i < coords.length; i++) {
            drawPoint(coords[i]);
        }
    }

    /** Draws a vertical line at specified coordinate, using default pathStyle */
    public void drawVLine(C coord) {
        Point2D wp = vis.getWindowPointOf(coord);
        pathStyle.draw(new Line2D.Double(wp.getX(), vis.getWindowBounds().getMaxY(), wp.getX(), vis.getWindowBounds().getMinY()), gr);
    }

    /** Draws a horizontal line at specified coordinate, using default pathStyle */
    public void drawHLine(C coord) {
        Point2D wp = vis.getWindowPointOf(coord);
        pathStyle.draw(new Line2D.Double(vis.getWindowBounds().getMinX(), wp.getY(), vis.getWindowBounds().getMaxX(), wp.getY()), gr);
    }

    /** Draws a vertical tick mark at specified location, using default path style.
     * @param coord the local coordinate of the position
     * @param tickHeight height of the mark (in pixels)
     */
    public void drawVTick(C coord, int tickHeight) {
        Point2D wp = vis.getWindowPointOf(coord);
        pathStyle.draw(new Line2D.Double(wp.getX(), wp.getY() - tickHeight, wp.getX(), wp.getY() + tickHeight), gr);
    }

    /** Draws a horizontal tick mark at specified location, using default path style.
     * @param coord the local coordinate of the position
     * @param tickHeight width of the mark (in pixels)
     */
    public void drawHTick(C coord, int tickWidth) {
        Point2D wp = vis.getWindowPointOf(coord);
        pathStyle.draw(new Line2D.Double(wp.getX() - tickWidth, wp.getY(), wp.getX() - tickWidth, wp.getY()), gr);
    }

    /**
     * Draws a straight segment between specified coordinates, using default <code>pathStyle</code>
     * @param coord1 point in local coordinates
     * @param coord2 point in local coordinates
     */
    public void drawSegment(C coord1, C coord2) {
        Point2D wp1 = vis.getWindowPointOf(coord1);
        Point2D wp2 = vis.getWindowPointOf(coord2);
        pathStyle.draw(new Line2D.Double(wp1, wp2), gr);
    }

    /**
     * Draws an arrow between specified coordinates, using default <code>arrowStyle</code>
     * @param anchor starting point of arrow in local coordinates
     * @param head end point of arrow in local coordinates
     */
    public void drawArrow(C anchor, C head) {
        GraphicArrow ga = new GraphicArrow(vis.getWindowPointOf(anchor), vis.getWindowPointOf(head));
        arrowStyle.draw(ga, gr);
    }

    /**
     * Draws a path from the first supplied point to the last, using default <code>pathStyle</code>
     * @param coords the points of the path in local coordinates
     */
    public void drawPath(C[] coords) {
        if (coords.length <= 0) {
            return;
        }
        Point2D nextPt = vis.getWindowPointOf(coords[0]);
        GeneralPath path = new GeneralPath();
        path.moveTo((float) nextPt.getX(), (float) nextPt.getY());
        for (int i = 1; i < coords.length; i++) {
            nextPt = vis.getWindowPointOf(coords[i]);
            path.lineTo((float) nextPt.getX(), (float) nextPt.getY());
        }
        pathStyle.draw(path, gr);
    }

    /**
     * Draws multiple paths specified by arrays of points, using default <code>pathStyle</code>
     * @param paths the paths as an array of arrays, in local coordinates
     */
    public void drawPaths(C[][] paths) {
        for (int i = 0; i < paths.length; i++) {
            drawPath(paths[i]);
        }
    }

    /**
     * Draws a shape through supplied points, using default <code>shapeStyle</code>.
     * The path is automatically closed
     * @param coords the points of the path in local coordinates
     */
    public void drawClosedPath(C[] coords) {
        if (coords.length <= 0) {
            return;
        }
        Point2D nextPt = vis.getWindowPointOf(coords[0]);
        GeneralPath path = new GeneralPath();
        path.moveTo((float) nextPt.getX(), (float) nextPt.getY());
        for (int i = 1; i < coords.length; i++) {
            nextPt = vis.getWindowPointOf(coords[i]);
            path.lineTo((float) nextPt.getX(), (float) nextPt.getY());
        }
        path.closePath();
        shapeStyle.draw(path, gr);
    }

    /**
     * Draws rectangle between specified corners, using default <code>shapeStyle</code>
     * @param corner1 first corner of the rectangle, in local coordinates
     * @param corner2 second corner of the rectangle, in local coordinates
     */
    public void drawRectangle(C corner1, C corner2) {
        shapeStyle.draw(getRectangle(corner1, corner2), gr);
    }

    /**
     * Returns a rectangle between specified corners
     * @param corner1 first corner of the rectangle, in local coordinates
     * @param corner2 second corner of the rectangle, in local coordinates
     */
    public Rectangle2D.Double getRectangle(C corner1, C corner2) {
        Point2D wp1 = vis.getWindowPointOf(corner1);
        Point2D wp2 = vis.getWindowPointOf(corner2);
        return new Rectangle2D.Double(
                Math.min(wp1.getX(), wp2.getX()), Math.min(wp1.getY(), wp2.getY()),
                Math.abs(wp2.getX() - wp1.getX()), Math.abs(wp2.getY() - wp1.getY()));
    }

    /**
     * Draws ellipse between specified corners, using default <code>shapeStyle</code>
     * @param corner1 first corner of the rectangle, in local coordinates
     * @param corner2 second corner of the rectangle, in local coordinates
     */
    public void drawEllipse(C corner1, C corner2) {
        Point2D wp1 = vis.getWindowPointOf(corner1);
        Point2D wp2 = vis.getWindowPointOf(corner2);
        shapeStyle.draw(new Ellipse2D.Double(
                Math.min(wp1.getX(), wp2.getX()), Math.min(wp1.getY(), wp2.getY()),
                Math.abs(wp2.getX() - wp1.getX()), Math.abs(wp2.getY() - wp1.getY())), gr);
    }

    /**
     * Draws a string at specified point, using default <code>stringStyle</code>
     * @param str the string
     * @param anchor the anchor point for the string, in local coordinates
     * @param shiftX # of pixels to shift in x direction
     * @param shiftY # of pixels to shift in y direction
     */
    public void drawString(String str, C anchor, int shiftX, int shiftY) {
        Point2D wp = vis.getWindowPointOf(anchor);
        GraphicString gs = new GraphicString(wp.getX() + shiftX, wp.getY() + shiftY, str);
        stringStyle.draw(gs, gr);
    }

    //
    //
    // ARBITRARY COORDINATE COMMANDS
    //
    //

    /**
     * Draws a path after applying the specified affine transform, using default <code>pathStyle</code>
     * @param path the path
     * @param at the affine transform
     */
    public void drawPath(GeneralPath path, AffineTransform at) {
        path.transform(at);
        pathStyle.draw(path, gr);
    }

    //
    //
    // HYBRID COORDINATE COMMANDS
    //
    //

    /**
     * Draws a path in window coordinates, using default <code>pathStyle</code>
     * @param path the path in WINDOW coordinates
     */
    public void drawPath(GeneralPath path) {
        drawWinPath(path);
    }

    //
    //
    // WINDOW COORDINATE COMMANDS
    //
    //

    /**
     * Draws border around the outside borders of the window, using default <code>pathStyle</code>
     */
    public void drawWinBorder() {
        pathStyle.draw(new Rectangle2D.Double(getWindowMinX(), getWindowMinY(), getWindowWidth()-1, getWindowHeight()-1), gr);
    }

    /** Draws collection of arrows with provided window coordinates. */
    public void drawWinArrow(GraphicArrow vec) {
        arrowStyle.draw(vec, gr);
    }

    /**
     * Draws a path in window coordinates, using default <code>pathStyle</code>
     * @param path the path in WINDOW coordinates
     */
    public void drawWinPath(GeneralPath path) {
        pathStyle.draw(path, gr);
    }

    /**
     * Draws a shape in window coordinates.
     * @param shape the window shape
     */
    public void drawWinShape(Shape shape) {
        shapeStyle.draw(shape, gr);
    }

    /** Draws a generic collection of objects on the window. */
    public <P> void draw(P[] objects) {
        if (objects == null || objects.length == 0)
            return;
        if (objects[0] instanceof GraphicArrow) {
            arrowStyle.draw((GraphicArrow[]) objects, gr);
        } else if (objects[0] instanceof GraphicString) {
            stringStyle.draw((GraphicString[]) objects, gr);
        } else if (objects[0] instanceof Point2D) {
            pointStyle.draw((Point2D[]) objects, gr);
        } else if (objects[0] instanceof Shape) {
            shapeStyle.draw((Shape[]) objects, gr);
        } else if (objects[0] instanceof GeneralPath) {
            pathStyle.draw((Shape[]) objects, gr);
        } else {
            System.out.println("Unable to draw objects.");
        }
    }
    
}
