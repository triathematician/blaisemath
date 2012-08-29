/**
 * DelegatingPointSetGraphic.java
 * Created Jan 22, 2011
 */
package org.blaise.graphics;

import java.awt.geom.Point2D;
import java.util.Map;
import java.util.Set;
import org.blaise.style.ObjectStyler;
import org.blaise.style.PathStyle;
import org.blaise.style.PointStyle;
import org.blaise.util.Edge;
import org.blaise.util.PointManager;

/**
 * A graph with fully-customizable points, edges, and tooltips. The styles and
 * point values are computed at runtime. Edges are maintained as a set of {@link Edge}s.
 * 
 * @see PointStyle
 * 
 * @author Elisha Peterson
 */
public class DelegatingNodeLinkGraphic<Src,EdgeType extends Edge<Src>> extends GraphicComposite {

    /** Point set */
    private final DelegatingPointSetGraphic<Src> points;
    /** Edges */
    private final DelegatingEdgeSetGraphic<Src,EdgeType> edges;
    
    /**
     * Construct with no points
     */
    public DelegatingNodeLinkGraphic() {
        addGraphic(edges = new DelegatingEdgeSetGraphic<Src,EdgeType>());
        addGraphic(points = new DelegatingPointSetGraphic<Src>());
        edges.setPointManager(points.getPointManager());
    }

    @Override
    public String toString() {
        return "Graph";
    }

    public DelegatingPointSetGraphic<Src> getPointGraphic() {
        return points;
    }

    public DelegatingEdgeSetGraphic<Src, EdgeType> getEdgeGraphic() {
        return edges;
    }
    
    //
    // POINTS
    //

    public PointManager<Src, Point2D> getPointManager() {
        return points.getPointManager();
    }

    public Set<? extends Src> getObjects() {
        return points.getObjects();
    }
    
    public void setPoints(Map<Src, Point2D> pts) {
        points.getPointManager().add(pts);
    }

    public ObjectStyler<Src, PointStyle> getStyler() {
        return points.getStyler();
    }

    public void setStyler(ObjectStyler<Src, PointStyle> styler) {
        points.setStyler(styler);
    }
    
    //
    // EDGES
    //

    public Set<EdgeType> getEdges() {
        return edges.getEdges();
    }

    public void setEdges(Set<EdgeType> edges) {
        this.edges.setEdges(edges);
    }

    public ObjectStyler<EdgeType, PathStyle> getEdgeStyler() {
        return edges.getEdgeStyler();
    }

    public void setEdgeStyler(ObjectStyler<EdgeType, PathStyle> styler) {
        edges.setEdgeStyler(styler);
    }
    
}
