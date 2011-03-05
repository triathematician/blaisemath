/**
 * ArrowEntry.java
 * Created Jan 23, 2011
 */
package org.bm.blaise.graphics.compound;

import org.bm.blaise.graphics.AbstractGraphicEntry;
import org.bm.blaise.graphics.BasicPointEntry;
import org.bm.blaise.graphics.BasicShapeEntry;
import org.bm.blaise.graphics.renderer.BasicPointRenderer;
import org.bm.blaise.graphics.renderer.GraphicRendererProvider;
import org.bm.blaise.graphics.renderer.ShapeLibrary;
import org.bm.blaise.graphics.renderer.ShapeRenderer;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * Displays an arrow between two points.
 *
 * TODO - write delegate so that start/end renderers do not have full point render functionality
 *
 * @author Elisha
 */
public class ArrowEntry extends AbstractGraphicEntry {

    /** Point at start of arrow */
    BasicPointEntry pEntryStart;
    /** Point renderer at start of arrow */
    BasicPointRenderer pRendStart;
    /** Point at end of arrow */
    BasicPointEntry pEntryEnd;
    /** Point renderer at end of arrow */
    BasicPointRenderer pRendEnd;

    /** Entry with the line */
    BasicShapeEntry lineEntry;

    /** Construct arrow between specified points */
    public ArrowEntry(Point2D.Double start, Point2D.Double end) {
        pRendStart = null;
//        pRendStart = new BasicPointRenderer(); pRendStart.setShape(ShapeLibrary.ARROWHEAD);
        pEntryStart = new BasicPointEntry(start, pRendStart);
        pRendEnd = new BasicPointRenderer(); pRendEnd.setShape(ShapeLibrary.ARROWHEAD);
        pEntryEnd = new BasicPointEntry(end, pRendEnd);
        updateAngle();
        lineEntry = new BasicShapeEntry(new Line2D.Double(start, end), true);
    }

    private void updateAngle() {
        double angle = Math.atan2(pEntryEnd.getPoint().getY() - pEntryStart.getPoint().getY(), pEntryEnd.getPoint().getX() - pEntryStart.getPoint().getX());
        pEntryStart.setAngle(angle+Math.PI);
        pEntryEnd.setAngle(angle);
    }

    public Point2D getStartPoint() { return pEntryStart.getPoint(); }
    public void setStartPoint(Point2D p) { if (!pEntryStart.getPoint().equals(p)) { pEntryStart.setPoint(p); updateAngle(); fireStateChanged(); } }

    public Point2D getEndPoint() { return pEntryStart.getPoint(); }
    public void setEndPoint(Point2D p) { if (!pEntryStart.getPoint().equals(p)) { pEntryStart.setPoint(p); updateAngle(); fireStateChanged(); } }

    public BasicPointRenderer getStartPointRenderer() { return pRendStart; }
    public void setStartPointRenderer(BasicPointRenderer r) { if (pRendStart != r) { pRendStart = r; pEntryStart.setRenderer(r); fireStateChanged(); } }

    public BasicPointRenderer getEndPointRenderer() { return pRendEnd; }
    public void setEndPointRenderer(BasicPointRenderer r) { if (pRendEnd != r) { pRendEnd = r; pEntryEnd.setRenderer(r); fireStateChanged(); } }

    public ShapeRenderer getLineRenderer() { return lineEntry.getRenderer(); }
    public void setLineRenderer(ShapeRenderer r) { if (lineEntry.getRenderer() != r) { lineEntry.setRenderer(r); fireStateChanged(); } }

    public void draw(Graphics2D canvas, GraphicRendererProvider factory) {
        lineEntry.draw(canvas, factory);
        if (pRendStart != null)
            pEntryStart.draw(canvas, factory);
        if (pRendEnd != null)
            pEntryEnd.draw(canvas, factory);

    }

    public boolean contains(Point point, GraphicRendererProvider provider) {
        if ((pRendStart != null && pEntryStart.contains(point, provider))
                || (pRendEnd != null && pEntryEnd.contains(point, provider)))
            return true;
        return false;
    }
}
