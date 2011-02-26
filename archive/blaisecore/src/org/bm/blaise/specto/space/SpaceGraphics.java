/*
 * SpaceGraphics.java
 * Created on Oct 21, 2009
 */

package org.bm.blaise.specto.space;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;
import org.bm.blaise.specto.primitive.GraphicString;
import org.bm.blaise.specto.primitive.PathStyle;
import org.bm.blaise.specto.primitive.PrimitiveStyle;
import org.bm.blaise.specto.primitive.ShapeStyle;
import org.bm.blaise.specto.visometry.Visometry;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import org.bm.utils.SpaceGridSampleSet;
import scio.coordinate.Point3D;
import scio.coordinate.sample.SampleGenerator;

/**
 * <p>
 *  This class provides algorithm for drawing objects in three-dimensional space.
 * </p>
 * @author Elisha Peterson
 */
public class SpaceGraphics extends VisometryGraphics<Point3D> {

    /** Stores rendered objects. */
    SpaceScene scene;
    /** Stores a copy of the visometry's projection. */
    SpaceProjection proj;

    /** Constructs with a visometry. */
    public SpaceGraphics(Visometry<Point3D> vis) {
        super(vis);
        proj = ((SpaceVisometry) vis).getProj();
        scene = new SpaceScene(proj);
        setOpacity(.95f);
    }

    //
    // SCENE METHODS
    //

    /** Clears the current scene. */
    public void clearScene() {
        scene.clear();
    }

    /**
     * Draws the current scene on the specified graphics object.
     * If the anaglyph flag is true, will render the scene as a 3D anaglyph.
     * @param g
     * @param anaglyph
     */
    public void drawScene(Graphics2D g, Color background, boolean anaglyph) {
        this.gr = g;
        scene.draw(g, background, anaglyph);
    }

    //
    // GETTERS & SETTERS
    //

    public float getOpacity() {
        return scene.opacity;
    }

    public void setOpacity(float opacity) {
        scene.opacity = opacity;
        shapeStyle.setFillOpacity(opacity);
    }

    //
    // DRAW METHODS
    //

    @Override
    public void drawArrow(Point3D anchor, Point3D head, PrimitiveStyle<Point2D[]> style) {
        scene.addObject(new Point3D[]{anchor, head}, style);
    }

    @Override
    public void drawEllipse(Point3D corner1, Point3D corner2, ShapeStyle style) {
        System.out.println("drawEllipse is not supported in 3D");
    }

    @Override
    public void drawPath(Point3D[] coords, int iMin, int iMax, PathStyle style) {
        Point3D[] subPath = new Point3D[iMax - iMin + 1];
        System.arraycopy(coords, iMin, subPath, 0, iMax - iMin + 1);
        scene.addObject(subPath, style);
    }

    @Override
    public void drawPath(Point3D[] coords, PathStyle style) {
        scene.addObject(coords, style);
    }

    @Override
    public void drawPaths(Point3D[][] paths, PathStyle style) {
        scene.addObjects(paths, style);
    }

    @Override
    public void drawPoint(Point3D coordinate, PrimitiveStyle<Point2D> style) {
        scene.addObject(coordinate, style);
    }

    @Override
    public void drawPoints(Point3D[] coords, PrimitiveStyle<Point2D> style) {
        for (Point3D pt : coords)
            scene.addObject(pt, style);
    }

    @Override
    public void drawRectangle(Point3D corner1, Point3D corner2, ShapeStyle style) {
        System.out.println("drawRectangle is not supported in 3D");
    }

    @Override
    public void drawSegment(Point3D coord1, Point3D coord2, PathStyle style) {
        scene.addObject(new Point3D[]{coord1, coord2}, style);
    }

    @Override
    public void drawSegments(Point3D[][] coords, PathStyle style) {
        scene.addObjects(coords, style);
    }

    @Override
    public void drawShape(Point3D[] coords, ShapeStyle style) {
        scene.addObject(coords, style);
    }

    @Override
    public void drawString(String str, Point3D coord, int shiftX, int shiftY, int orientation, PrimitiveStyle<GraphicString> style) {
        System.out.println("drawString is not supported in 3D");
    }

    @Override
    public void drawString(String str, Point3D coord, int shiftX, int shiftY, PrimitiveStyle<GraphicString> style) {
        System.out.println("drawString is not supported in 3D");
    }
}
