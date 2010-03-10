/**
 * VisometryGraphics.java
 * Created on Jul 30, 2009
 */

package org.bm.blaise.specto.visometry;

import java.awt.Graphics2D;
import org.bm.blaise.specto.primitive.*;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;

/**
 * <p>
 *  This class is repsonsible for using the visometry to draw objects on a graphics
 *  object. Plottables and other objects should use the drawing methods provided
 *  within this class, and may also set/retrieve the styles within this graphics object.
 * </p>
 * <p>
 *  Drawing is accomplished using customized style classes, which are stored by
 *  this class and may be altered externally. This class first converts
 *  the passed in elements into <i>graphics primitives</i>, and then uses the appropriate
 *  <i>style</i> (if not provided) to draw the element on the <code>Graphics2D</code> canvas.
 *  The style itself will use the current value of the <i>selected</i> boolean to
 *  determine the precise way to draw the object.
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
    // DEFAULT STYLES
    //

    // TODO - add default styles here

    /** Determines whether the object being drawn is currently "selected" or not.*/
    protected boolean selected = false;
    
    protected PointStyle pointStyle = new PointStyle();
    protected PathStyle pathStyle = new PathStyle();
    protected ShapeStyle shapeStyle = new ShapeStyle();
    protected StringStyle stringStyle = new StringStyle();
    protected ArrowStyle arrowStyle = new ArrowStyle();

    //
    // CONSTRUCTORS
    //
    
    /**
     * Construct with specified elements.
     * @param vis visometry (converts to/from window geometry)
     */
    protected VisometryGraphics(Visometry<C> vis) {
        this.vis = vis;
    }

    //
    // BIG BEANS
    //

    /** @return the underlying screen graphics being used. */
    public Graphics2D getScreenGraphics() {
        return gr;
    }

    /**
     * Sets the underlying screen graphics to paint on.
     * @param gr the graphics
     */
    public void setScreenGraphics(Graphics2D gr) {
        this.gr = gr;
        gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    /** @return the underlying visometry. */
    public Visometry<C> getVisometry() {
        return vis;
    }
    
    /**
     * Sets the underlying visometry.
     * @param vis the visometry
     */
    public void setVisometry(Visometry<C> vis) {
        this.vis = vis;
    }

    //
    // STYLE BEANS
    //
    
    public boolean isSelectedStyle() {
        return selected;
    }

    public void setSelectedStyle(boolean sel) {
        this.selected = sel;
    }

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
     * @param style an arrow, path, point, shape, or string style
     */
    public void setPrimitiveStyle(PrimitiveStyle style) {
        if (style instanceof ArrowStyle)
            setArrowStyle((ArrowStyle) style);
        else if (style instanceof PathStyle)
            setPathStyle((PathStyle) style);
        else if (style instanceof PointStyle)
            setPointStyle((PointStyle) style);
        else if (style instanceof ShapeStyle)
            setShapeStyle((ShapeStyle) style);
        else if (style instanceof StringStyle)
            setStringStyle((StringStyle) style);
        else
            System.out.println("Unable to set style");
    }

    //
    // WINDOW ATTRIBUTE BEANS
    //

    /**
     * @return minimum coordinate of the visometry.
     */
    public C getMinCoord() {
        return vis.getMinPointVisible();
    }

    /**
     * @return minimum coordinate of the visometry.
     */
    public C getMaxCoord() {
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
    // VISOMETRY ACCESSOR METHODS
    //

    public Point2D.Double getWindowPointOf(C coordinate) {
        return vis.getWindowPointOf(coordinate);
    }

    public C getCoordinateOf(Point2D.Double point) {
        return vis.getCoordinateOf(point);
    }


    // ************************************************************** //
    //                                                                //
    //           ROUTINES TO DRAW IN LOCAL COORDINATES                //
    //                                                                //
    // ************************************************************** //

    
    //
    // DRAWING ROUTINES: POINTS
    //

    /**
     * Draws a coordinate with a given style class.
     * @param coordinate the coordinate
     * @param style the style used to draw
     */
    public void drawPoint(C coordinate, PrimitiveStyle<Point2D> style) {
        style.draw(gr, vis.getWindowPointOf(coordinate), selected);
    }

    /**
     * Draws a point at specified coordinate, using default <code>pointStyle</code>
     * @param coordinate point in local coordinates
     */
    public void drawPoint(C coordinate) {
        drawPoint(coordinate, pointStyle);
    }

    /**
     * Draws a series of points with a given style class.
     * @param coords the points in local coordinates
     * @param style the style used to draw
     */
    public void drawPoints(C[] coords, PrimitiveStyle<Point2D> style) {
        if (coords == null || coords.length <= 0)
            return;
        for (C c : coords)
            style.draw(gr, vis.getWindowPointOf(c), selected);
    }

    /**
     * Draws a series of points, using default <code>pointStyle</code>
     * @param coords the points in local coordinates
     */
    public void drawPoints(C[] coords) {
        if (coords.length <= 0)
            return;
        for (int i = 0; i < coords.length; i++)
            drawPoint(coords[i]);
    }

    //
    // DRAWING ROUTINES: PATHS (SEGMENTS, ARROWS, LINES, etc.)
    //

    /**
     * Draws a straight segment (relative to the window) between specified
     * coordinates with a given style class
     * @param coord1 point in local coordinates
     * @param coord2 point in local coordinates
     * @param style the style used to draw
     */
    public void drawSegment(C coord1, C coord2, PathStyle style) {
        style.draw(gr, new Line2D.Double(vis.getWindowPointOf(coord1), vis.getWindowPointOf(coord2)), selected);
    }

    /**
     * Draws a straight segment between specified coordinates, using default <code>pathStyle</code>
     * @param coord1 point in local coordinates
     * @param coord2 point in local coordinates
     */
    public void drawSegment(C coord1, C coord2) {
        drawSegment(coord1, coord2, pathStyle);
    }

    /**
     * Draws straight segments (relative to the window) between specified
     * coordinates with a given style class
     * @param coords an array of arrays of length two
     * @param style the style used to draw
     */
    public void drawSegments(C[][] coords, PathStyle style) {
        if (coords == null || coords.length <= 0)
            return;
        if (coords[0].length != 2)
            throw new IllegalArgumentException("drawSegments should be called with length 2 arrays: " + coords[0].length);
        for (C[] segment : coords)
            drawSegment(segment[0], segment[1], style);
    }

    /**
     * Draws straight segments between specified coordinates, using default <code>pathStyle</code>
     * @param coords an array of arrays of length two
     * @param coords2 point in local coordinates
     */
    public void drawSegments(C[][] coords) {
        drawSegments(coords, pathStyle);
    }

    /**
     * Draws a path from the first supplied point to the last using specified style
     * @param coords the points of the path in local coordinates
     * @param style the custom style
     */
    public void drawPath(C[] coords, int iMin, int iMax, PathStyle style) {
        if (coords.length <= 0)
            return;
        Point2D nextPt = vis.getWindowPointOf(coords[iMin]);
        GeneralPath path = new GeneralPath();
        path.moveTo((float) nextPt.getX(), (float) nextPt.getY());
        for (int i = iMin+1; i <= Math.min(coords.length-1, iMax); i++) {
            nextPt = vis.getWindowPointOf(coords[i]);
            path.lineTo((float) nextPt.getX(), (float) nextPt.getY());
        }
        style.draw(gr, path, selected);
    }

    /**
     * Draws a path from the first supplied point to the last, using default <code>pathStyle</code>
     * @param coords the points of the path in local coordinates
     */
    public void drawPath(C[] coords, int iMin, int iMax) {
        drawPath(coords, iMin, iMax, pathStyle);
    }

    /**
     * Draws a path from the first supplied point to the last in a custom style
     * @param coords the points of the path in local coordinates
     * @param style the custom style
     */
    public void drawPath(C[] coords, PathStyle style) {
        drawPath(coords, 0, coords.length, style);
    }

    /**
     * Draws a path from the first supplied point to the last, using default <code>pathStyle</code>
     * @param coords the points of the path in local coordinates
     */
    public void drawPath(C[] coords) {
        drawPath(coords, 0, coords.length);
    }

    /**
     * Draws multiple paths specified by arrays of points, using default <code>pathStyle</code>
     * @param paths the paths as an array of arrays, in local coordinates
     * @param style the custom style
     */
    public void drawPaths(C[][] paths, PathStyle style) {
        for (C[] path : paths)
            drawPath(path, 0, path.length, style);
    }

    /**
     * Draws multiple paths specified by arrays of points, using default <code>pathStyle</code>
     * @param paths the paths as an array of arrays, in local coordinates
     */
    public void drawPaths(C[][] paths) {
        for (int i = 0; i < paths.length; i++)
            drawPath(paths[i]);
    }

    //
    // DRAW COMMANDS: SHAPES
    //

    /**
     * Utility method to return a rectangle between specified corners
     * @param corner1 first corner of the rectangle, in local coordinates
     * @param corner2 second corner of the rectangle, in local coordinates
     */
    Rectangle2D.Double getRectangle(C corner1, C corner2) {
        Point2D wp1 = vis.getWindowPointOf(corner1);
        Point2D wp2 = vis.getWindowPointOf(corner2);
        return new Rectangle2D.Double(
                Math.min(wp1.getX(), wp2.getX()), Math.min(wp1.getY(), wp2.getY()),
                Math.abs(wp2.getX() - wp1.getX()), Math.abs(wp2.getY() - wp1.getY()));
    }

    /**
     * Draws rectangle between specified corners using specified style.
     * Provided coordinates may be in any relative direction.
     * @param corner1 first corner of the rectangle, in local coordinates
     * @param corner2 second corner of the rectangle, in local coordinates
     * @param style the custom style
     */
    public void drawRectangle(C corner1, C corner2, ShapeStyle style) {
        style.draw(gr, getRectangle(corner1, corner2), selected);
    }

    /**
     * Draws rectangle between specified corners, using default <code>shapeStyle</code>
     * Provided coordinates may be in any relative direction.
     * @param corner1 first corner of the rectangle, in local coordinates
     * @param corner2 second corner of the rectangle, in local coordinates
     */
    public void drawRectangle(C corner1, C corner2) {
        drawRectangle(corner1, corner2, shapeStyle);
    }

    /**
     * Draws ellipse between specified corners using custom style
     * @param corner1 first corner of the rectangle, in local coordinates
     * @param corner2 second corner of the rectangle, in local coordinates
     * @param style the custom style
     */
    public void drawEllipse(C corner1, C corner2, ShapeStyle style) {
        Point2D wp1 = vis.getWindowPointOf(corner1);
        Point2D wp2 = vis.getWindowPointOf(corner2);
        shapeStyle.draw(gr, new Ellipse2D.Double(
                Math.min(wp1.getX(), wp2.getX()), Math.min(wp1.getY(), wp2.getY()),
                Math.abs(wp2.getX() - wp1.getX()), Math.abs(wp2.getY() - wp1.getY())
                ), selected);
    }

    /**
     * Draws ellipse between specified corners, using default <code>shapeStyle</code>
     * @param corner1 first corner of the rectangle, in local coordinates
     * @param corner2 second corner of the rectangle, in local coordinates
     */
    public void drawEllipse(C corner1, C corner2) {
        drawEllipse(corner1, corner2, shapeStyle);
    }

    /**
     * Draws a shape through supplied points, using default <code>shapeStyle</code>.
     * The path is automatically closed.
     * @param coords the points of the path in local coordinates
     * @param style the custom style
     */
    public void drawShape(C[] coords, ShapeStyle style) {
        if (coords.length <= 0)
            return;
        Point2D nextPt = vis.getWindowPointOf(coords[0]);
        GeneralPath path = new GeneralPath();
        path.moveTo((float) nextPt.getX(), (float) nextPt.getY());
        for (int i = 1; i < coords.length; i++) {
            nextPt = vis.getWindowPointOf(coords[i]);
            path.lineTo((float) nextPt.getX(), (float) nextPt.getY());
        }
        path.closePath();
        style.draw(gr, path, selected);
    }

    /**
     * Draws a shape through supplied points, using default <code>shapeStyle</code>.
     * The path is automatically closed.
     * @param coords the points of the path in local coordinates
     */
    public void drawShape(C[] coords) {
        drawShape(coords, shapeStyle);
    }

    //
    // DRAW COMMANDS: TEXT
    //

    /**
     * Draws a string at specified point, using specified style
     * @param str the string
     * @param coord the coordinate anchoring the string
     * @param shiftX # of pixels to shift in x direction
     * @param shiftY # of pixels to shift in y direction
     * @param anchor orientation specifies centering of the string
     * @param style the custom style
     */
    public void drawString(String str, C coord, int shiftX, int shiftY, int anchor, PrimitiveStyle<GraphicString> style) {
        Point2D wp = vis.getWindowPointOf(coord);
        GraphicString gs = new GraphicString(str, wp.getX() + shiftX, wp.getY() + shiftY, anchor);
        style.draw(gr, gs, selected);
    }

    /**
     * Draws a string at specified point, using specified style
     * @param str the string
     * @param coord the anchor point for the string, in local coordinates
     * @param shiftX # of pixels to shift in x direction
     * @param shiftY # of pixels to shift in y direction
     * @param style the custom style
     */
    public void drawString(String str, C coord, int shiftX, int shiftY, PrimitiveStyle<GraphicString> style) {
        Point2D wp = vis.getWindowPointOf(coord);
        GraphicString gs = new GraphicString(str, wp.getX() + shiftX, wp.getY() + shiftY);
        style.draw(gr, gs, selected);
    }

    /**
     * Draws a string at specified point, using default <code>stringStyle</code>
     * @param str the string
     * @param coord the anchor point for the string, in local coordinates
     * @param shiftX # of pixels to shift in x direction
     * @param shiftY # of pixels to shift in y direction
     * @param anchor orientation specifies centering of the string
     */
    public void drawString(String str, C coord, int shiftX, int shiftY, int anchor) {
        drawString(str, coord, shiftX, shiftY, anchor, stringStyle);
    }

    /**
     * Draws a string at specified point, using default <code>stringStyle</code>
     * @param str the string
     * @param coord the anchor point for the string, in local coordinates
     * @param shiftX # of pixels to shift in x direction
     * @param shiftY # of pixels to shift in y direction
     */
    public void drawString(String str, C coord, int shiftX, int shiftY) {
        drawString(str, coord, shiftX, shiftY, stringStyle);
    }

    //
    // DRAW COMMANDS: ARROWS
    //

    /**
     * Draws an arrow between specified coordinates with custom style
     * @param anchor starting point of arrow in local coordinates
     * @param head end point of arrow in local coordinates
     * @param style the custom style
     */
    public void drawArrow(C anchor, C head, PrimitiveStyle<GraphicArrow> style) {
        GraphicArrow ga = new GraphicArrow(vis.getWindowPointOf(anchor), vis.getWindowPointOf(head));
        style.draw(gr, ga, selected);
    }

    /**
     * Draws an arrow between specified coordinates, using default <code>arrowStyle</code>
     * @param anchor starting point of arrow in local coordinates
     * @param head end point of arrow in local coordinates
     */
    public void drawArrow(C anchor, C head) {
        drawArrow(anchor, head, arrowStyle);
    }

    
    // ************************************************************** //
    //                                                                //
    //           ROUTINES TO DRAW IN WINDOW COORDINATES                //
    //                                                                //
    // ************************************************************** //

    //
    // OBJECT CREATION...
    //

    /**
     * Draws a segment given in WINDOW coordinates on the window using a custom style
     * @param style the custom style
     */
    public void drawWinSegment(double x1, double y1, double x2, double y2, PrimitiveStyle<Shape> style) {
        style.draw(gr, new Line2D.Double(x1, y1, x2, y2), selected);
    }

    /**
     * Draws a segment on the window
     */
    public void drawWinSegment(double x1, double y1, double x2, double y2) {
        drawWinSegment(x1, y1, x2, y2, pathStyle);
    }

    /**
     * Draws border around the outside borders of the WINDOW using custom path style
     * @param style the custom style
     */
    public void drawWinBorder(PrimitiveStyle<Shape> style) {
        style.draw(gr, new Rectangle2D.Double(
                getWindowMinX(), getWindowMinY(),
                getWindowWidth() - 1, getWindowHeight() - 1)
                , selected);
    }

    /**
     * Draws border around the outside borders of the WINDOW, using default <code>pathStyle</code>
     */
    public void drawWinBorder() {
        drawWinBorder(pathStyle);
    }

    //
    // PRIMITIVE DRAWING
    //

    /**
     * Draws an arbitrary primitive, in window coordinates
     * @param primitive the primitive object
     * @param style custom style for the object
     */
    public <P> void drawWinPrimitive(P primitive, PrimitiveStyle<P> style) {
        style.draw(gr, primitive, selected);
    }

    /**
     * Draws arbitrary primitives, in window coordinates
     * @param primitives the primitives
     * @param style custom style for the object
     */
    public <P> void drawWinPrimitives(P[] primitives, PrimitiveStyle<P> style) {
        style.draw(gr, primitives, selected);
    }

    /**
     * Draws a path in WINDOW coordinates, using default <code>pathStyle</code>
     * @param path the path in WINDOW coordinates
     */
    public void drawWinPath(GeneralPath path) {
        drawWinPrimitive(path, pathStyle);
    }

    /**
     * Draws a shape in WINDOW coordinates with default style
     * @param shape the WINDOW shape
     */
    public void drawWinShape(Shape shape) {
        drawWinPrimitive(shape, shapeStyle);
    }

    /**
     * Draws arrow in WINDOW coordinates with provided WINDOW coordinates.
     * @param vec the arrow
     */
    public void drawWinArrow(GraphicArrow vec) {
        drawWinPrimitive(vec, arrowStyle);
    }
    
}
