/**
 * DelegatingEdgeSetGraphic.java
 * Created Aug 28, 2012
 */

package org.blaise.graphics;

import static com.google.common.base.Preconditions.*;
import com.google.common.collect.Maps;
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
 * A collection of edges backed by a common set of points.
 * 
 * @param <S> source object type
 * @param <E> edge type
 * 
 * @author elisha
 */
public class DelegatingEdgeSetGraphic<S,E extends Edge<S>> extends GraphicComposite {

    /** The edges in the graphic. */
    protected final Map<E,DelegatingShapeGraphic<E>> edges = Maps.newHashMap();
    /** Styler for edges */
    protected ObjectStyler<E,PathStyle> edgeStyler = ObjectStyler.create();

    /** Point manager. Maintains objects and their locations, and enables mouse dragging. */
    protected CoordinateManager<S, Point2D> pointManager;
    /** Listener for changes to coordinates */
    private final CoordinateListener coordListener;
    
    /**
     * Initialize with default coordinate manager.
     */
    public DelegatingEdgeSetGraphic() {
        this(new CoordinateManager<S,Point2D>());
    }
    
    /** 
     * Initialize with given coordinate manager.
     * @param mgr manages source object locations
     */
    public DelegatingEdgeSetGraphic(CoordinateManager<S,Point2D> mgr) {
        setCoordinateManager(mgr);
        coordListener = new CoordinateListener(){
            public void coordinatesChanged(CoordinateChangeEvent evt) {
                updateEdgeGraphics(pointManager.getCoordinates(), new ArrayList<Graphic>());
            }
        };
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="coordinate updates">

    private void updateEdgeGraphics(Map<S,Point2D> locs, List<Graphic> removeMe) {
        List<Graphic> addMe = new ArrayList<Graphic>();
        for (E edge : edges.keySet()) {
            DelegatingShapeGraphic<E> dsg = edges.get(edge);
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
                    dsg = new DelegatingShapeGraphic<E>(edge, line, true);
                    edges.put(edge, dsg);
                    dsg.setStyler(edgeStyler);
                    addMe.add(dsg);
                } else {
                    dsg.setPrimitive(line);
                }
            }
        }
        replaceGraphics(removeMe, addMe);
    }

    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //
    public CoordinateManager<S, Point2D> getCoordinateManager() {
        return pointManager;
    }

    public final void setCoordinateManager(CoordinateManager<S, Point2D> pointManager) {
        if (this.pointManager != checkNotNull(pointManager)) {
            if (this.pointManager != null) {
                this.pointManager.removeCoordinateListener(coordListener);
            }
            this.pointManager = pointManager;
            this.pointManager.addCoordinateListener(coordListener);
            updateEdgeGraphics(pointManager.getCoordinates(), new ArrayList<Graphic>());
        }
    }

    /**
     * Return map describing graph's edges
     * @return edges
     */
    public Set<E> getEdges() {
        return edges.keySet();
    }

    /**
     * Sets map describing graphs edges.
     * Also updates the set of objects to be the nodes within the edges
     * @param ee new edges to put
     */
    public final void setEdges(Set<? extends E> ee) {
        Set<E> addMe = new LinkedHashSet<E>();
        Set<E> removeMe = new HashSet<E>();
        for (E e : ee) {
            if (!edges.containsKey(e)) {
                addMe.add(e);
            }
        }
        for (E e : edges.keySet()) {
            if (!ee.contains(e)) {
                removeMe.add(e);
            }
        }
        if (!removeMe.isEmpty() || !addMe.isEmpty()) {
            List<Graphic> remove = new ArrayList<Graphic>();
            for (E e : removeMe) {
                remove.add(edges.remove(e));
            }
            for (E e : addMe) {
                edges.put(e, null);
            }
            updateEdgeGraphics(pointManager.getCoordinates(), remove);
        }
    }

    /**
     * Returns the current style styler
     * @return style styler
     */
    public ObjectStyler<E, PathStyle> getEdgeStyler() {
        return edgeStyler;
    }

    /**
     * Sets the current style styler. If null, will use the default style
     * provided by the parent.
     * @param styler used for custom edge styles
     */
    public void setEdgeStyler(ObjectStyler<E, PathStyle> styler) {
        if (this.edgeStyler != styler) {
            this.edgeStyler = styler;
            for (DelegatingShapeGraphic<E> dsg : edges.values()) {
                if (dsg != null) {
                    dsg.setStyler(styler);
                }
            }
            fireGraphicChanged();
        }
    }
    
    //</editor-fold>
    

    @Override
    public void initContextMenu(JPopupMenu menu, Graphic src, Point2D point, Object focus, Set selection) {
        // use delegate's source object for focus instead of the graphic
        Graphic gfc = graphicAt(point);
        super.initContextMenu(menu, src, point, gfc instanceof DelegatingShapeGraphic
                ? ((DelegatingShapeGraphic)gfc).getSourceObject() : focus, selection);
    }

}
