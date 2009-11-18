/*
 * SpaceGraphics.java
 * Created on Oct 21, 2009
 */

package org.bm.blaise.specto.space;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;
import org.bm.blaise.specto.primitive.PrimitiveStyle;
import org.bm.blaise.specto.visometry.Visometry;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import org.bm.utils.SpaceGridSampleSet;
import org.bm.utils.Spacing;
import scio.coordinate.P3D;
import scio.coordinate.sample.SampleCoordinateSetGenerator;

/**
 * <p>
 *  This class provides algorithm for drawing objects in three-dimensional space.
 * </p>
 * @author Elisha Peterson
 */
public class SpaceGraphics extends VisometryGraphics<P3D> {

    /** Stores rendered objects. */
    SpaceRendered rend;

    /** Stores a copy of the visometry's projection. */
    SpaceProjection proj;

    /** Constructs with a visometry. */
    public SpaceGraphics(Visometry<P3D> vis) {
        super(vis);
        proj = ((SpaceVisometry) vis).getProj();
        rend = new SpaceRendered(proj);
    }

    public void addToScene(P3D[] object) {
        rend.addObject(object);
    }

    public void addToScene(P3D[][] objects) {
        rend.addObjects(objects);
    }

    public void addToScene(Collection<P3D[]> objects) {
        rend.addObjects(objects);
    }

    public void addToScene(P3D[][] objects, PrimitiveStyle style) {
        rend.addObjects(objects, style);
    }

    public void clearScene() {
        rend.clear();
    }

    public void drawScene() {
        rend.draw(this);
    }

    @Override
    public void setVisometry(Visometry<P3D> vis) {
        super.setVisometry(vis);
        proj = ((SpaceVisometry) vis).getProj();
        rend = new SpaceRendered(proj);
    }

    @Override
    public void drawSegment(P3D coord1, P3D coord2) {
//        System.out.println("segment: " + coord1 + " " + coord2);
        P3D[] clipped = P3DUtils.clipSegment(proj.clipPoint, proj.tDir, new P3D[]{coord1, coord2});
//        System.out.println("clipped: " + Arrays.toString(clipped));
        if (clipped != null) {
            super.drawSegment(clipped[0], clipped[1]);
        }
    }

    P3D LIGHT = new P3D(3, -5, 6).normalized();
    Color fill = new Color(200, 200, 255);

    public Color getFillColor(P3D[] poly) {
        P3D n = (poly[2].minus(poly[0])).crossProduct(poly[2].minus(poly[1])).normalized();
        float costh = (float) Math.abs(n.dotProduct(LIGHT));
        costh = .5f + .5f * costh * costh;
        return new Color(costh*fill.getRed()*costh/255, costh*fill.getGreen()/255, costh*fill.getBlue()/255);
    }

    @Override
    public void drawClosedPath(P3D[] p) {
        if (P3DUtils.clips(proj.clipPoint, proj.tDir, p)) {
            return;
        }
        shapeStyle.setFillColor(getFillColor(p));
        super.drawClosedPath(p);
    }



    public void drawPolygons(P3D[][] polys) {
        // sort polygons by average z-order from projection
        Arrays.sort(polys, proj.getPolyComparator());

        for (int i = 0; i < polys.length; i++) {
            drawClosedPath(polys[i]);
        }
    }

    public void drawPolygons(List<P3D[]> polys) {
        // sort polygons by average z-order from projection
        TreeSet<P3D[]> sorted = new TreeSet<P3D[]>(proj.getPolyComparator());
        sorted.addAll(polys);
        
        for (P3D[] p : sorted) {
            drawClosedPath(p);
        }
    }

    transient SpaceGridSampleSet pgss;

    public SampleCoordinateSetGenerator<P3D> getSampleSetGenerator(Spacing s) {
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
