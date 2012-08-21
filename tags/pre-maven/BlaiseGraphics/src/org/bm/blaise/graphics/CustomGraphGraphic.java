/**
 * CustomPointSetGraphic.java
 * Created Jan 22, 2011
 */
package org.bm.blaise.graphics;

import org.bm.blaise.style.PointStyle;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.bm.blaise.style.ObjectStyler;
import org.bm.blaise.style.PathStyle;
import org.bm.util.Delegator;

/**
 * A graph with fully-customizable points, edges, and tooltips. The styles and
 * point values are computed at runtime.
 * 
 * @see PointStyle
 * 
 * @author Elisha Peterson
 */
public class CustomGraphGraphic<Src> extends CustomPointSetGraphic<Src> {
    
    /** The edge map */
    protected Map<? extends Src,? extends Set<? extends Src>> edges;
    /** Styler for edges */
    protected ObjectStyler<Object[], PathStyle> edgeStyler = new ObjectStyler<Object[], PathStyle>();
    
    //
    // CONSTRUCTORS
    //
    
    /**
     * Construct with no points
     */
    public CustomGraphGraphic() {
        this((Map) Collections.emptyMap(), null);
    }

    /** 
     * Construct with no style (will use the default) 
     * @param edges the source objects
     * @param delegate used for point conversion
     */
    public CustomGraphGraphic(Map<? extends Src,? extends Set<? extends Src>> edges, Delegator<Src, Point2D> delegate) { 
        super(new ArrayList<Src>(edges.keySet()), delegate);
        this.edges = edges;                 
    }

    @Override
    public String toString() {
        return "Graph";
    }

    //
    // PROPERTIES
    //

    /**
     * Return map describing graph's edges
     * @return edges
     */
    public Map<? extends Src, ? extends Set<? extends Src>> getEdges() {
        return edges;
    }

    /**
     * Sets map describing graphs edges.
     * Also updates the set of objects to be the key-set of the map.
     * @param edges 
     */
    public synchronized void setEdges(Map<? extends Src, ? extends Set<? extends Src>> edges) {
        this.edges = edges;
        setObjects(new ArrayList<Src>(edges.keySet()));
    }

    /**
     * Returns the current style styler
     * @return style styler
     */
    public ObjectStyler<Object[], PathStyle> getEdgeStyler() {
        return edgeStyler;
    }

    /**
     * Sets the current style styler. If null, will use the default style
     * provided by the parent.
     * @param styler used for custom edge styles 
     */
    public void setEdgeStyler(ObjectStyler<Object[], PathStyle> styler) {
        if (this.edgeStyler != styler) {
            this.edgeStyler = styler;
            fireAppearanceChanged();
        }
    }   
    

    //
    // DRAW METHODS
    //
    
    /** 
     * Return the actual style used for drawing a particular object.
     * @param source the source object used to render
     * @return the style... if not provided by the styler, defaults to the parent style
     */
    private PathStyle drawStyle(Src... source) {
        PathStyle rend = edgeStyler.getStyleDelegate() == null ? null : edgeStyler.getStyleDelegate().of(source);
        return rend == null ? parent.getStyleProvider().getPathStyle() : rend;
    }

    @Override
    public synchronized void draw(Graphics2D canvas) {
        synchronized(edges) {
            synchronized(pointManager) {
                for (Src o1 : edges.keySet()) {
                    Point2D pt1 = pointManager.of(o1);
                    for (Src o2 : edges.get(o1)) {
                        Point2D pt2 = pointManager.of(o2);
                        drawStyle(o1, o2).draw(new Line2D.Double(pt1, pt2), canvas, visibility);
                    }
                }
                super.draw(canvas);
            }
        }
    }

    @Override
    public synchronized boolean contains(Point p) {
        if (super.contains(p))
            return true;
//        for (Src o1 : edges.keySet()) {
//            Point2D pt1 = pointManager.of(o1);
//            for (Src o2 : edges.get(o1)) {
//                Point2D pt2 = pointManager.of(o2);
//                if (BasicShapeGraphic.CONTAINS_STROKE.createStrokedShape(new Line2D.Double(pt1, pt2)).contains(p))
//                    return true;
//            }
//        }
        return false;
    }

    @Override
    public synchronized String getTooltip(Point p) {
        String pt = super.getTooltip(p);
        if (pt != null)
            return pt;
        for (Src o1 : edges.keySet()) {
            Point2D pt1 = pointManager.of(o1);
            for (Src o2 : edges.get(o1)) {
                Point2D pt2 = pointManager.of(o2);
                if (BasicShapeGraphic.CONTAINS_STROKE.createStrokedShape(new Line2D.Double(pt1, pt2)).contains(p))
                    return edgeStyler.getTipDelegate() == null ? Arrays.asList(o1, o2).toString()
                            : edgeStyler.getTipDelegate().of((Src[]) new Object[]{o1, o2});
            }
        }
        return null;
    }
    
}
