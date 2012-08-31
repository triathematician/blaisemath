/**
 * DelegatingEdgeSetGraphic.java
 * Created Aug 28, 2012
 */

package org.blaise.graphics;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.*;
import javax.swing.JPopupMenu;
import org.blaise.style.ObjectStyler;
import org.blaise.style.PathStyle;
import org.blaise.util.CoordinateChangeEvent;
import org.blaise.util.CoordinateListener;
import org.blaise.util.CoordinateManager;
import org.blaise.util.Edge;

/**
 * <p>
 *  Groups together a collection of edges backed by a common set of points.
 * </p>
 * @author elisha
 */
public class DelegatingEdgeSetGraphic<Src,EdgeType extends Edge<Src>> extends GraphicComposite implements CoordinateListener {

    /** The edges in the graphic. */
    protected final Map<EdgeType,DelegatingShapeGraphic<EdgeType>> edges = new HashMap<EdgeType,DelegatingShapeGraphic<EdgeType>>();
    /** Styler for edges */
    protected ObjectStyler<EdgeType,PathStyle> edgeStyler = new ObjectStyler<EdgeType,PathStyle>();

    /** Point manager. Maintains objects and their locations, and enables mouse dragging. */
    protected CoordinateManager<Src, Point2D> pointManager;

    /** Initialize without arguments */
    public DelegatingEdgeSetGraphic() {}

    public void coordinatesChanged(CoordinateChangeEvent evt) {
        update(pointManager.getCoordinates(), new ArrayList<Graphic>());
    }

    private void update(Map<Src,Point2D> locs, List<Graphic> removeMe) {
        List<Graphic> addMe = new ArrayList<Graphic>();
        if (edges != null) {
            for (EdgeType edge : edges.keySet()) {
                DelegatingShapeGraphic<EdgeType> dsg = edges.get(edge);
                Point2D p1 = locs.get(edge.getNode1());
                Point2D p2 = locs.get(edge.getNode2());
                if (p1 == null || p2 == null) {
                    if (dsg != null) {
                        removeMe.add(dsg);
                        edges.put(edge, null);
                    }
                } else {
                    Line2D.Double line = new Line2D.Double(p1, p2);
                    if (dsg == null) {
                        edges.put(edge, dsg = new DelegatingShapeGraphic<EdgeType>(edge, line, true));
                        dsg.setStyler(edgeStyler);
                        addMe.add(dsg);
                    } else {
                        dsg.setPrimitive(line);
                    }
                }
            }
        }
        replaceGraphics(removeMe, addMe);
    }

    //
    // PROPERTIES
    //

    /**
     * Return map describing graph's edges
     * @return edges
     */
    public Set<EdgeType> getEdges() {
        return edges.keySet();
    }

    /**
     * Sets map describing graphs edges.
     * Also updates the set of objects to be the nodes within the edges
     * @param ee new edges to put
     */
    public final void setEdges(Set<? extends EdgeType> ee) {
        Set<EdgeType> addMe = new HashSet<EdgeType>();
        Set<EdgeType> removeMe = new HashSet<EdgeType>();
        for (EdgeType e : ee) {
            if (!edges.containsKey(e)) {
                addMe.add(e);
            }
        }
        for (EdgeType e : edges.keySet()) {
            if (!ee.contains(e)) {
                removeMe.add(e);
            }
        }
        if (removeMe.size() > 0 || addMe.size() > 0) {
            List<Graphic> remove = new ArrayList<Graphic>();
            for (EdgeType e : removeMe) {
                remove.add(edges.remove(e));
            }
            for (EdgeType e : addMe) {
                edges.put(e, null);
            }
            update(pointManager.getCoordinates(), remove);
        }
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
            for (DelegatingShapeGraphic<EdgeType> dsg : edges.values()) {
                if (dsg != null) {
                    dsg.setStyler(styler);
                }
            }
            fireGraphicChanged();
        }
    }

    public CoordinateManager<Src, Point2D> getPointManager() {
        return pointManager;
    }

    public void setPointManager(CoordinateManager<Src, Point2D> pointManager) {
        if (this.pointManager != pointManager) {
            if (this.pointManager != null) {
                this.pointManager.removeCoordinateListener(this);
            }
            this.pointManager = pointManager;
            update(pointManager.getCoordinates(), new ArrayList<Graphic>());
            if (this.pointManager != null) {
                this.pointManager.addCoordinateListener(this);
            }
        }
    }

    @Override
    public void initialize(JPopupMenu menu, Point point, Object focus, Set<Graphic> selection) {
        // provide additional info for context menu
        Graphic gfc = graphicAt(point);
        super.initialize(menu, point, gfc instanceof DelegatingShapeGraphic
                ? ((DelegatingShapeGraphic)gfc).getSourceObject() : focus, selection);
    }

}
