/*
 * SpaceRendered.java
 * Created on Oct 22, 2009
 */

package org.bm.blaise.specto.space;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.RectangularShape;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.SampleModel;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.TreeMap;
import org.bm.blaise.specto.primitive.*;
import scio.coordinate.Point3D;

/**
 * <p>
 *   This class holds 3-dimensional objects of a variety of types, together with their
 *   styles, and controls ordering of these objects.
 * </p>
 * <p>
 *   The standard data type is an array of 3d points (<code>P3D</code>s). These are stored
 *   as a sorted collection (sorted with respect to distance from camera). An array should
 *   contain 1 or more points. Single points are rendered as dots; double points are rendered
 *   as segments; three or more points are rendered as polygons, using the normal vector
 *   computed by the first three coordinates.
 * </p>
 * @author Elisha Peterson
 */
public class SpaceScene {

    //
    // PROPERTIES
    //

    /** 
     * Stores the objects to be drawn, in sorted order by average z-distance from camera.
     * The values are style objects.
     */
    TreeMap<Point3D[], PrimitiveStyle> objects;
    
    /** Projection used to display the objects. */
    SpaceProjection proj;
    
    /** Stores opacity of displayed fill objects. */
    float opacity;

    //
    // CONSTRUCTORS
    //

    /** Construct the scene with a specified projection. */
    public SpaceScene(SpaceProjection proj) {
        this.proj = proj;
        objects = new TreeMap<Point3D[], PrimitiveStyle>(proj.getPolygonZOrderComparator());
    }

    //
    // GETTER & SETTER PROPERTIES
    //

    public float getOpacity() {
        return opacity;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    //
    // COMPOSITIONAL METHODS
    //

    /** Removes all objects from the scene. */
    public void clear() {
        objects.clear();
    }

    /** Adds an object to be displayed with the specified style. */
    public void addObject(Point3D pt, PrimitiveStyle style) {
        objects.put(new Point3D[]{pt}, style);
    }

    /** Adds an object to be displayed with the specified style. */
    public void addObject(Point3D[] arr, PrimitiveStyle style) {
        objects.put(arr, style);
    }

    /** Adds multiple objects to be displayed with the specified style. */
    public void addObjects(Collection<Point3D[]> arr, PrimitiveStyle style) {
        for (Point3D[] a : arr)
            objects.put(a, style);
    }

    /** Adds multiple objects to be displayed with the specified style. */
    public void addObjects(Point3D[][] arr, PrimitiveStyle style) {
        for (int i = 0; i < arr.length; i++)
            objects.put(arr[i], style);
    }

    //
    // DRAW METHODS
    //

    /**
     * Paint the scene on a graphics object.
     * @param gr the graphics object to draw on
     * @param anaglyph flag describing whether the object is an anaglyph
     */
    public void draw(Graphics2D gr, Color background, boolean anaglyph) {
        if (anaglyph) {
            if (leftImage == null || this.width != proj.getWinBounds().getWidth() || this.height != proj.getWinBounds().getHeight())
                initAnaglyph(width, height);
            proj.useLeftCamera();
            draw(leftImage.createGraphics(), background);
            proj.useRightCamera();
            draw(rightImage.createGraphics(), background);
            gr.drawImage(combineImages(leftImage, rightImage, width, height), 
                    (int) proj.getWinBounds().getX(), (int) proj.getWinBounds().getY(),
                    null);
        } else {
            proj.useCenterCamera();
            draw(gr, background);
        }
    }

    /**
     * Paint the scene on a graphics object.
     * @param gr the graphics object to draw on
     * @param background the color of the background (if null, will render as non-opaque)
     */
    void draw(Graphics2D gr, Color background) {
        if (background != null) {
            gr.setColor(background);
            RectangularShape r = proj.getWinBounds();
            gr.fillRect((int) r.getX(), (int) r.getY(), (int) r.getWidth(), (int) r.getHeight());
        }
        draw(gr);
    }

    /**
     * Draws the scene on the specified graphics component, using the current
     * projection settings in proj.
     */
    void draw(Graphics2D gr) {
        gr.setStroke(new BasicStroke(0.5f));
        int r = 5;
        double dist;
        for (Entry<Point3D[], PrimitiveStyle> entry : objects.entrySet()) {
            dist = proj.getAverageDist(entry.getKey());
            if (dist < proj.clipDist)
                continue;
            Object style = entry.getValue();
            if (style instanceof PointStyle) {
                for (Point3D pt : entry.getKey())
                    drawPoint(gr, pt, (PointStyle) style, dist);
            } else if (style instanceof ArrowStyle && entry.getKey().length == 2) {
                drawArrow(gr, entry.getKey()[0], entry.getKey()[1], (ArrowStyle) style, dist);
            } else if (style instanceof PathStyle) {
                drawPath(gr, entry.getKey(), (PathStyle) style, dist);
            } else if (style instanceof ShapeStyle) {
                drawShape(gr, entry.getKey(), (ShapeStyle) style, dist);
            } else {
                System.out.println("Unsupported 3d graphics draw command: " + style + ", " + entry.getKey());
            }
        }
    }
    
    //
    // PRIMITIVE DRAWING ROUTINES
    //

    public static final Point3D LIGHT = new Point3D(3, -5, 6).normalized();
    public static final Color BASE_FILL = new Color(200, 200, 255);

    /**
     * @param poly a polygon (only the first 3 coords are used)
     * @param dist determines opacity level
     * @return a fill color associated with the given polygon and distance settings.
     */

    Color shadedFillColor(Point3D[] poly, double dist) {
        Point3D n = (poly[2].minus(poly[0])).crossProduct(poly[2].minus(poly[1])).normalized();
        float costh = (float) Math.abs(n.dotProduct(LIGHT));
        costh = .5f + .5f * costh * costh;
        float opacityMultiplier = dist < proj.viewDist ? 1f
                                   : dist < 3 * proj.viewDist ? (float) (1 - .5 * (dist - proj.viewDist) / proj.viewDist)
                                   : 0f;
        return new Color(
                costh * BASE_FILL.getRed() * costh / 255,
                costh * BASE_FILL.getGreen() / 255,
                costh * BASE_FILL.getBlue() / 255,
                opacity * opacityMultiplier);
    }

    /** Draws a point on the canvas at specified distance from camera. */
    void drawPoint(Graphics2D gr, Point3D point, PointStyle style, double distance) {
        int oldRad = style.getRadius();
        style.setRadius((int) (oldRad * proj.viewDist / distance));
        style.draw(gr, proj.getWindowPointOf(point), false);
        style.setRadius(oldRad);
    }

    /** Draws an arrow on the canvas w/ specified distance setting. */
    void drawArrow(Graphics2D gr, Point3D anchor, Point3D head, ArrowStyle style, double distance) {
        style.draw(gr,
                new Point2D[]{ proj.getWindowPointOf(anchor), proj.getWindowPointOf(head) },
                false);
    }

    /** Draws a segment on the canvas w/ specified distance setting. */
    public void drawSegment(Graphics2D gr, Point3D coord1, Point3D coord2, PathStyle style) {
        Point3D[] clipped = P3DUtils.clipSegment(proj.clipPoint, proj.tDir, new Point3D[]{coord1, coord2});
        if (clipped != null)
            style.draw(gr, new Line2D.Double(proj.getWindowPointOf(coord1), proj.getWindowPointOf(coord2)), false);
    }

    /** Draws a path on the canvas w/ specified distance setting. */
    public void drawPath(Graphics2D gr, Point3D[] points, PathStyle style, double dist) {
        if (P3DUtils.clips(proj.clipPoint, proj.tDir, points))
            return;
        style.setColor(shadedFillColor(points, dist));
        if (points.length <= 0)
            return;
        Point2D nextPt = proj.getWindowPointOf(points[0]);
        GeneralPath path = new GeneralPath();
        path.moveTo((float) nextPt.getX(), (float) nextPt.getY());
        for (int i = 1; i < points.length; i++) {
            nextPt = proj.getWindowPointOf(points[i]);
            path.lineTo((float) nextPt.getX(), (float) nextPt.getY());
        }
        style.draw(gr, path, false);
    }

    /** Draws a shape on the canvas w/ specified distance setting. */
    public void drawShape(Graphics2D gr, Point3D[] points, ShapeStyle style, double dist) {
        if (P3DUtils.clips(proj.clipPoint, proj.tDir, points))
            return;
        style.setFillColor(shadedFillColor(points, dist));
        if (points.length <= 0)
            return;
        Point2D nextPt = proj.getWindowPointOf(points[0]);
        GeneralPath path = new GeneralPath();
        path.moveTo((float) nextPt.getX(), (float) nextPt.getY());
        for (int i = 1; i < points.length; i++) {
            nextPt = proj.getWindowPointOf(points[i]);
            path.lineTo((float) nextPt.getX(), (float) nextPt.getY());
        }
        path.closePath();
        style.draw(gr, path, false);
    }

    //
    // ANAGLYPH RENDERING UTILITIES
    //
    
    // left = red, right = cyan
    BufferedImage leftImage; SampleModel leftModel; DataBufferByte leftBuffer;
    BufferedImage rightImage; SampleModel rightModel; DataBufferInt rightBuffer;
    BufferedImage viewImage;

    int width, height;
    int chunk; // how many rows are copied at a time
    int[] leftSamples; // stores samples from left (grayscale)
    int[] rightSamplesGreen; // stores samples from right (green)
    int[] rightSamplesBlue; // stores samples from right (blue)
    int[] viewInts; // stores data used for the viewing image

    void initAnaglyph(int width, int height) {
        this.width = width;
        this.height = height;
        leftImage = null; leftModel = null; leftBuffer = null; leftSamples = null;
        rightImage = null; rightModel = null; rightBuffer = null; rightSamplesGreen = null; rightSamplesBlue = null;
        viewImage = null; viewInts = null;

        chunk = 10000 / width;
        if (chunk == 0)
            chunk = 1;

        leftImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        leftModel = leftImage.getRaster().getSampleModel();
        leftBuffer = (DataBufferByte) leftImage.getRaster().getDataBuffer();
        leftSamples = new int[chunk*width];

        rightImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        rightModel = rightImage.getRaster().getSampleModel();
        rightBuffer = (DataBufferInt) rightImage.getRaster().getDataBuffer();
        rightSamplesGreen = new int[chunk*width];
        rightSamplesBlue = new int[chunk*width];

        viewImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        viewInts = ((DataBufferInt) viewImage.getRaster().getDataBuffer()).getData();
    }

    BufferedImage combineImages(BufferedImage leftImage, BufferedImage rightImage, int width, int height) {
        int row = 0;
        while (row < height) {
            int nRows = chunk;
            if (row + nRows > height)
                nRows = height - row;
            int nSamples = nRows * width;
            int i0 = row * width;
            leftModel.getSamples(0, row, width, nRows, 0, leftSamples, leftBuffer);
            rightModel.getSamples(0, row, width, nRows, 1, rightSamplesGreen, rightBuffer);
            rightModel.getSamples(0, row, width, nRows, 2, rightSamplesBlue, rightBuffer);
            for (int i = 0; i < nSamples; i++)
                viewInts[i + i0] =
                        (leftSamples[i] << 16)
                        + (rightSamplesGreen[i] << 8)
                        + (rightSamplesBlue[i]);
            row += chunk;
        }
        return viewImage;
    }
}
