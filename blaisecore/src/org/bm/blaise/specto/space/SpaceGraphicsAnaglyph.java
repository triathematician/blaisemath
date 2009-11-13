/*
 * SpaceGraphicsAnaglyph.java
 * Created on Nov 4, 2009
 */

package org.bm.blaise.specto.space;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import org.bm.blaise.specto.primitive.GraphicArrow;
import org.bm.blaise.specto.primitive.GraphicString;
import org.bm.blaise.specto.visometry.Visometry;
import scio.coordinate.P3D;

/**
 * <p>
 *  This class provides algorithm for drawing anaglyphic objects in 3d space
 * </p>
 * @author Elisha Peterson
 */
public class SpaceGraphicsAnaglyph extends SpaceGraphics {

    /** Constructs with a visometry. */
    public SpaceGraphicsAnaglyph(Visometry<P3D> vis) {
        super(vis);
        proj = ((SpaceVisometry) vis).getProj();
        rend = new SpaceRendered(proj);
    }

    /** Filters a color for viewing by the left lens (red) */
    Color leftFilter(Color color) {
        return new Color(0, 255, 255, 255);
    }

    /** Filters a color for viewing with the right lens (cyan) */
    Color rightFilter(Color color) {
        return new Color(255, 0, 0, 255);
    }

    final static AlphaComposite COMP1 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
    final static AlphaComposite COMP2 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f);

    @Override
    public void drawPoint(P3D coordinate) {
        Color s1 = pointStyle.getStrokeColor();
        Color f1 = pointStyle.getFillColor();
        Point2D[] winC = proj.getDoubleWindowPointOf(coordinate);

        gr.setComposite(COMP1);
        
        pointStyle.setStrokeColor(leftFilter(s1));
        pointStyle.setFillColor(leftFilter(f1));
        pointStyle.draw(winC[0], gr);

        pointStyle.setStrokeColor(rightFilter(s1));
        pointStyle.setFillColor(rightFilter(f1));
        pointStyle.draw(winC[1], gr);

        gr.setComposite(AlphaComposite.SrcOver);

        pointStyle.setStrokeColor(s1);
        pointStyle.setFillColor(f1);
    }

    @Override
    public void drawSegment(P3D coord1, P3D coord2) {
        P3D[] clipped = P3DUtils.clipSegment(proj.clipPoint, proj.tDir, new P3D[]{coord1, coord2});
        if (clipped != null) {
            Color s1 = pathStyle.getColor();
            Point2D[] wp1 = proj.getDoubleWindowPointOf(coord1);
            Point2D[] wp2 = proj.getDoubleWindowPointOf(coord2);

            gr.setComposite(COMP1);

            pathStyle.setColor(leftFilter(s1));
            pathStyle.draw(new Line2D.Double(wp1[0], wp2[0]), gr);

            pathStyle.setColor(rightFilter(s1));
            pathStyle.draw(new Line2D.Double(wp1[1], wp2[1]), gr);

            gr.setComposite(AlphaComposite.SrcOver);

            pathStyle.setColor(s1);
        }
    }

    @Override
    public void drawArrow(P3D coord1, P3D coord2) {
        P3D[] clipped = P3DUtils.clipSegment(proj.clipPoint, proj.tDir, new P3D[]{coord1, coord2});
        if (clipped != null) {
            Color s1 = arrowStyle.getColor();
            Point2D[] wp1 = proj.getDoubleWindowPointOf(coord1);
            Point2D[] wp2 = proj.getDoubleWindowPointOf(coord2);

            gr.setComposite(COMP1);

            arrowStyle.setColor(leftFilter(s1));
            arrowStyle.draw(new GraphicArrow(wp1[0], wp2[0]), gr);

            arrowStyle.setColor(rightFilter(s1));
            arrowStyle.draw(new GraphicArrow(wp1[1], wp2[1]), gr);

            gr.setComposite(AlphaComposite.SrcOver);

            arrowStyle.setColor(s1);
        }
    }

    @Override
    public void drawClosedPath(P3D[] p) {
        if (p == null || p.length <= 1 || P3DUtils.clips(proj.clipPoint, proj.tDir, p)) {
            return;
        }
        Color s1 = shapeStyle.getStrokeColor();
        Color f1 = getFillColor(p);

        Point2D[] nextPt = proj.getDoubleWindowPointOf(p[0]);
        GeneralPath p1 = new GeneralPath();
        GeneralPath p2 = new GeneralPath();

        p1.moveTo((float) nextPt[0].getX(), (float) nextPt[0].getY());
        p2.moveTo((float) nextPt[1].getX(), (float) nextPt[1].getY());
        for (int i = 1; i < p.length; i++) {
            nextPt = proj.getDoubleWindowPointOf(p[i]);
            p1.lineTo((float) nextPt[0].getX(), (float) nextPt[0].getY());
            p2.lineTo((float) nextPt[1].getX(), (float) nextPt[1].getY());
        }
        p1.closePath();
        p2.closePath();

        gr.setComposite(COMP2);

        shapeStyle.setStrokeColor(leftFilter(s1));
        shapeStyle.setFillColor(leftFilter(f1));
        shapeStyle.draw(p1, gr);

        shapeStyle.setStrokeColor(rightFilter(s1));
        shapeStyle.setFillColor(rightFilter(f1));
        shapeStyle.draw(p2, gr);

        gr.setComposite(AlphaComposite.SrcOver);

        shapeStyle.setStrokeColor(s1);
    }

    @Override
    public void drawString(String str, P3D anchor, int shiftX, int shiftY) {
        Point2D wp[] = proj.getDoubleWindowPointOf(anchor);
        GraphicString gs1 = new GraphicString(wp[0].getX() + shiftX, wp[0].getY() + shiftY, str);
        GraphicString gs2 = new GraphicString(wp[1].getX() + shiftX, wp[1].getY() + shiftY, str);

        Color s1 = stringStyle.getColor();
        gr.setComposite(COMP1);

        stringStyle.setColor(leftFilter(s1));
        stringStyle.draw(gs1, gr);

        stringStyle.setColor(rightFilter(s1));
        stringStyle.draw(gs2, gr);

        gr.setComposite(AlphaComposite.SrcOver);

        stringStyle.setColor(s1);
    }


}
