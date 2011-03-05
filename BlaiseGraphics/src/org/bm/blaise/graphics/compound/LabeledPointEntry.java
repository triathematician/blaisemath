/*
 * LabeledPointEntry.java
 * Created Jan 22, 2011
 */

package org.bm.blaise.graphics.compound;

import org.bm.blaise.graphics.AbstractGraphicEntry;
import org.bm.blaise.graphics.BasicPointEntry;
import org.bm.blaise.graphics.BasicStringEntry;
import org.bm.blaise.graphics.GraphicMouseListener;
import org.bm.blaise.graphics.renderer.GraphicRendererProvider;
import org.bm.blaise.graphics.renderer.PointRenderer;
import org.bm.blaise.graphics.renderer.StringRenderer;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;

/**
 * Displays a point together with a label.
 * TODO - additional binding between compound types
 *
 * @author Elisha
 */
public class LabeledPointEntry extends AbstractGraphicEntry
        implements GraphicMouseListener.PointBean {

    BasicPointEntry pEntry;
    BasicStringEntry sEntry;

    //
    // CONSTRUCTORS
    //

    /** Construct labeled point with given primitive and default renderer */
    public LabeledPointEntry(Point2D.Double p, String s) { this(p, s, null); }

    /** Construct with given primitive and renderer. */
    public LabeledPointEntry(Point2D.Double p, String s, PointRenderer renderer) {
        pEntry = new BasicPointEntry(p, renderer);
        sEntry = new BasicStringEntry(p, s);
        setMouseListener(new GraphicMouseListener.PointDragger(this));
    }

    //
    // PROPERTY PATTERNS
    //

    public Point2D getPoint() { return pEntry.getPoint(); }
    public void setPoint(Point2D p) { if (!pEntry.getPoint().equals(p)) { pEntry.setPoint(p); sEntry.setPoint(p); fireStateChanged(); } }

    public String getString() { return sEntry.getString(); }
    public void setString(String s) { if (!sEntry.getString().equals(s)) { sEntry.setString(s); fireStateChanged(); } }

    public StringRenderer getStringRenderer() { return sEntry.getRenderer(); }
    public void setStringRenderer(StringRenderer renderer) { if (sEntry.getRenderer() != renderer) { sEntry.setRenderer(renderer); fireStateChanged(); } }

    public PointRenderer getPointRenderer() { return pEntry.getRenderer(); }
    public void setPointRenderer(PointRenderer renderer) { if (pEntry.getRenderer() != renderer) { pEntry.setRenderer(renderer); fireStateChanged(); } }

    //
    // DRAW METHODS
    //

    public void draw(Graphics2D canvas, GraphicRendererProvider rend) {
        pEntry.draw(canvas, rend);
        sEntry.draw(canvas, rend);
    }
    
    public boolean contains(Point point, GraphicRendererProvider provider) {
        return pEntry.contains(point, provider) || sEntry.contains(point, provider);
    }

}
