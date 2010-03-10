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
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;
import org.bm.blaise.specto.primitive.PrimitiveStyle;
import org.bm.blaise.specto.visometry.Visometry;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import org.bm.utils.SpaceGridSampleSet;
import scio.coordinate.Point3D;
import scio.coordinate.sample.SampleCoordinateSetGenerator;

/**
 * <p>
 *  This class provides algorithm for drawing objects in three-dimensional space.
 * </p>
 * @author Elisha Peterson
 */
public class SpaceGraphics extends VisometryGraphics<Point3D> {

    /** Stores rendered objects. */
    SpaceScene rend;
    /** Stores a copy of the visometry's projection. */
    SpaceProjection proj;
    /** Stores opacity of displayed fill objects. */
    float opacity;

    /** Constructs with a visometry. */
    public SpaceGraphics(Visometry<Point3D> vis) {
        super(vis);
        proj = ((SpaceVisometry) vis).getProj();
        rend = new SpaceScene(proj);
        setOpacity(.95f);
    }

    /** Adds a point to a scene. */
    public void addPointToScene(Point3D point) {
        rend.addObject(new Point3D[]{point});
    }
    /** Adds a point to a scene. */
    public void addPointToScene(Point3D point, PrimitiveStyle style) {
        rend.addObject(new Point3D[]{point}, style);
    }

    /** Adds an arbitrary object to a scene; the length of array determines the object. */
    public void addToScene(Point3D[] object) {
        rend.addObject(object);
    }

    /** Adds several objects to a scene; the length of each array determines the object. */
    public void addToScene(Point3D[][] objects) {
        rend.addObjects(objects);
    }

    /** Adds several objects to a scene; the length of each array determines the object. */
    public void addToScene(Collection<Point3D[]> objects) {
        rend.addObjects(objects);
    }

    /** Adds several objects to a scene using a given style; the length of each array determines the object. */
    public void addToScene(Point3D[][] objects, PrimitiveStyle style) {
        rend.addObjects(objects, style);
    }

    public void clearScene() {
        rend.clear();
    }

    public void drawScene(Graphics2D g) {
        this.gr = g;
        rend.draw(this);
    }

    @Override
    public void setVisometry(Visometry<Point3D> vis) {
        super.setVisometry(vis);
        proj = ((SpaceVisometry) vis).getProj();
        rend = new SpaceScene(proj);
    }

    public float getOpacity() {
        return opacity;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
        shapeStyle.setFillOpacity(opacity);
    }

    @Override
    public void drawSegment(Point3D coord1, Point3D coord2) {
        Point3D[] clipped = P3DUtils.clipSegment(proj.clipPoint, proj.tDir, new Point3D[]{coord1, coord2});//        System.out.println("clipped: " + Arrays.toString(clipped));
        if (clipped != null) {
            Point2D wp1 = vis.getWindowPointOf(coord1);
            Point2D wp2 = vis.getWindowPointOf(coord2);
            pathStyle.draw(gr, new Line2D.Double(wp1, wp2), selected);
        }
    }

    public static final Point3D LIGHT = new Point3D(3, -5, 6).normalized();
    public static final Color BASE_FILL = new Color(200, 200, 255);

    Color shadedFillColor(Point3D[] poly) {
        Point3D n = (poly[2].minus(poly[0])).crossProduct(poly[2].minus(poly[1])).normalized();
        float costh = (float) Math.abs(n.dotProduct(LIGHT));
        costh = .5f + .5f * costh * costh;
        return new Color(
                costh*BASE_FILL.getRed()*costh/255,
                costh*BASE_FILL.getGreen()/255,
                costh*BASE_FILL.getBlue()/255,
                opacity);
    }

    @Override
    public void drawShape(Point3D[] p) {
        if (P3DUtils.clips(proj.clipPoint, proj.tDir, p))
            return;
        shapeStyle.setFillColor(shadedFillColor(p));
        super.drawShape(p);
    }



    public void drawPolygons(Point3D[][] polys) {
        // sort polygons by average z-order from projection
        Arrays.sort(polys, proj.getPolygonZOrderComparator());
        for (int i = 0; i < polys.length; i++)
            drawShape(polys[i]);
    }

    public void drawPolygons(List<Point3D[]> polys) {
        // sort polygons by average z-order from projection
        TreeSet<Point3D[]> sorted = new TreeSet<Point3D[]>(proj.getPolygonZOrderComparator());
        sorted.addAll(polys);        
        for (Point3D[] p : sorted)
            drawShape(p);
    }

    transient SpaceGridSampleSet pgss;

    public SampleCoordinateSetGenerator<Point3D> getSampleSetGenerator() {
        SpaceVisometry pv = (SpaceVisometry) this.vis;
        if (pgss == null) {
            pgss = new SpaceGridSampleSet(pv.minPoint, pv.maxPoint);
        } else {
            pgss.setMin(pv.minPoint);
            pgss.setMax(pv.maxPoint);
        }
        // TODO - figure out how to make this work better (i.e. should it depend on screen size??)
        return pgss;
    }
}
