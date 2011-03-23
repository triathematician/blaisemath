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
import org.bm.blaise.graphics.GraphicEntry;

/**
 * Displays a point together with a label.
 * TODO - additional binding between compound types
 *
 * @author Elisha
 */
public class LabeledPointEntry extends AbstractGraphicEntry
        implements GraphicMouseListener.PointBean {

    /** Stores the point */
    private final BasicPointEntry pEntry;
    /** Stores the string */
    private final BasicStringEntry sEntry;
    /** Whether label is visible */
    private boolean labelVisible = true;

    //
    // CONSTRUCTORS
    //

    /** Construct labeled point with given primitive and default renderer */
    public LabeledPointEntry(Point2D p, String s) { this(p, s, null); }

    /** Construct with given primitive and renderer. */
    public LabeledPointEntry(Point2D p, String s, PointRenderer renderer) {
        pEntry = new BasicPointEntry(p, renderer);
        pEntry.setParent(this);
        sEntry = new BasicStringEntry(p, s);
        sEntry.setParent(this);
        setMouseListener(new GraphicMouseListener.PointDragger(this));
    }

    //
    // PROPERTY PATTERNS
    //

    public Point2D getPoint() { return pEntry.getPoint(); }
    public void setPoint(Point2D p) { pEntry.setPoint(p); }

    public PointRenderer getPointRenderer() { return pEntry.getRenderer(); }
    public void setPointRenderer(PointRenderer renderer) { pEntry.setRenderer(renderer); }

    public String getString() { return sEntry.getString(); }
    public void setString(String s) { sEntry.setString(s); }

    public StringRenderer getStringRenderer() { return sEntry.getRenderer(); }
    public void setStringRenderer(StringRenderer renderer) { sEntry.setRenderer(renderer); }

    //
    // CHANGE LISTENING
    //

    boolean changing = false;

    @Override
    public void stateChanged(GraphicEntry e) {
        if (changing) return;
        changing = true;
        if (e == pEntry)
            sEntry.setPoint(pEntry.getPoint());
        else if (e == sEntry)
            pEntry.setPoint(sEntry.getPoint());

        fireStateChanged();
        changing = false;
    }

    //
    // DRAW METHODS
    //

    public void draw(Graphics2D canvas, GraphicRendererProvider rend) {
        pEntry.draw(canvas, rend);
        if (labelVisible)
            sEntry.draw(canvas, rend);
    }
    
    public boolean contains(Point point, GraphicRendererProvider provider) {
        return pEntry.contains(point, provider) || (labelVisible && sEntry.contains(point, provider));
    }

}
