/*
 * VCustomPointSet.java
 * Created Oct 5, 2011
 */
package org.blaise.visometry;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.blaise.graphics.DelegatingNodeLinkGraphic;
import org.blaise.graphics.Graphic;
import org.blaise.style.ObjectStyler;
import org.blaise.style.PathStyle;
import org.blaise.util.CoordinateManager;
import org.blaise.util.Edge;

/**
 * A set of draggable points defined in local coordinates. Properties of the objects
 * (including style, tooltips, locations, etc.) are managed by delegates.
 *
 * @param <C> the local coordinate
 * @param <S> the type of object being displayed
 * @param <E> object type of edge
 *
 * @author elisha
 */
public class VCustomGraph<C,S,E extends Edge<S>> extends VCustomPointSet<C, S> {

    /** Maintains collection of edges */
    protected final DelegatingNodeLinkGraphic<S,E> gwindow;

    /**
     * Initialize without any points or edges
     */
    public VCustomGraph() {
        this(Collections.EMPTY_MAP);
    }

    /**
     * Initialize with specified coordinate manager
     * @param mgr coordinate manager
     */
    public VCustomGraph(CoordinateManager<S, C> mgr) {
        super(mgr);
        window = null;
        gwindow = new DelegatingNodeLinkGraphic<S,E>();
        
        // change the window graphic from default specified by parent class
        window.getCoordinateManager().removeCoordinateListener(coordinateListener);
        window = gwindow.getPointGraphic();
        window.getCoordinateManager().addCoordinateListener(coordinateListener);
    }

    /**
     * Construct point set with specified objects.
     * @param loc initial locations of points
     */
    public VCustomGraph(Map<S,? extends C> loc) {
        super(loc);
        window = null;
        gwindow = new DelegatingNodeLinkGraphic<S,E>();
        
        // change the window graphic from default specified by parent class
        window.getCoordinateManager().removeCoordinateListener(coordinateListener);
        window = gwindow.getPointGraphic();
        window.getCoordinateManager().addCoordinateListener(coordinateListener);
    }

    //
    // PROPERTIES
    //

    @Override
    public Graphic getWindowGraphic() {
        return gwindow;
    }

    public Set<? extends E> getEdges() {
        return gwindow.getEdgeSet();
    }

    public void setEdges(Set<? extends E> edges) {
        // make a copy to prevent errors in updating edges
        gwindow.setEdgeSet(new LinkedHashSet<E>(edges));
    }

    public void setEdgeStyler(ObjectStyler<E, PathStyle> styler) {
        gwindow.setEdgeStyler(styler);
    }

    public ObjectStyler<E, PathStyle> getEdgeStyler() {
        return gwindow.getEdgeStyler();
    }

}
