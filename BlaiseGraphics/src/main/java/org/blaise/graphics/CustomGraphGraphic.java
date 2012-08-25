/**
 * CustomPointSetGraphic.java
 * Created Jan 22, 2011
 */
package org.blaise.graphics;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.blaise.style.ObjectStyler;
import org.blaise.style.PathStyle;
import org.blaise.style.PointStyle;
import org.blaise.util.Delegator;
import org.blaise.util.Edge;

/**
 * A graph with fully-customizable points, edges, and tooltips. The styles and
 * point values are computed at runtime. Edges are maintained as a set of {@link Edge}s.
 * 
 * @see PointStyle
 * 
 * @author Elisha Peterson
 */
public class CustomGraphGraphic<Src,EdgeType extends Edge<Src>> extends CustomPointSetGraphic<Src> {
    
    /** The edges in the graphic. */
    protected Set<EdgeType> edges;
    /** Styler for edges */
    protected ObjectStyler<EdgeType, PathStyle> edgeStyler = new ObjectStyler<EdgeType, PathStyle>();
    
    //
    // CONSTRUCTORS
    //
    
    /**
     * Construct with no points
     */
    public CustomGraphGraphic() {
        this(Collections.EMPTY_SET, null);
    }

    /** 
     * Construct with no style (will use the default) 
     * @param edges the source objects
     * @param delegate used for point conversion
     */
    public CustomGraphGraphic(Set<EdgeType> edges, Delegator<Src, Point2D> delegate) { 
        super(null, delegate);
        setEdges(edges);
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
    public Set<EdgeType> getEdges() {
        return edges;
    }

    /**
     * Sets map describing graphs edges.
     * Also updates the set of objects to be the nodes within the edges
     * @param edges 
     */
    public final synchronized void setEdges(Set<EdgeType> edges) {
        this.edges = edges;
        Set<Src> nodes = new HashSet<Src>();
        for (EdgeType e : edges) {
            nodes.add(e.getNode1());
            nodes.add(e.getNode2());
        }
        setObjects(nodes);
    }

    /**
     * Returns the current style styler
     * @return style styler
     */
    public ObjectStyler<EdgeType, PathStyle> getEdgeStyler() {
        return edgeStyler;
    }

    /**
     * Sets the current style styler. If null, will use the default style
     * provided by the parent.
     * @param styler used for custom edge styles 
     */
    public void setEdgeStyler(ObjectStyler<EdgeType, PathStyle> styler) {
        if (this.edgeStyler != styler) {
            this.edgeStyler = styler;
            fireGraphicChanged();
        }
    }   
    

    //
    // DRAW METHODS
    //
    
    /** 
     * Return the actual style used for drawing an edge
     * @param source the source object used to render
     * @return the style... if not provided by the styler, defaults to the parent style
     */
    private PathStyle drawStyle(EdgeType source) {
        PathStyle rend = edgeStyler.getStyleDelegate() == null ? null : edgeStyler.getStyleDelegate().of(source);
        return rend == null ? parent.getStyleProvider().getPathStyle(this) : rend;
    }

    @Override
    public synchronized void draw(Graphics2D canvas) {
        synchronized(edges) {
            synchronized(pointManager) {
                for (EdgeType e : edges) {
                    Point2D pt1 = pointManager.of(e.getNode1());
                    Point2D pt2 = pointManager.of(e.getNode2());
                    drawStyle(e).draw(new Line2D.Double(pt1, pt2), canvas, visibility);
                }
                // draw points after the edges
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
        if (pt != null) {
            return pt;
        }
        for (EdgeType e : edges) {
            Point2D pt1 = pointManager.of(e.getNode1());
            Point2D pt2 = pointManager.of(e.getNode2());
            if (BasicShapeGraphic.CONTAINS_STROKE.createStrokedShape(new Line2D.Double(pt1, pt2)).contains(p)) {
                return edgeStyler.getTipDelegate() == null ? e.toString()
                        : edgeStyler.getTipDelegate().of(e);
            }
        }
        return null;
    }
    
}
