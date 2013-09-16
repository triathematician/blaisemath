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
 * @param <Src> the type of object being displayed
 * @param <EdgeType> object type of edge
 *
 * @author elisha
 */
public class VCustomGraph<C,Src,EdgeType extends Edge<Src>> extends VCustomPointSet<C, Src> {

    /** Maintains collection of edges */
    protected final DelegatingNodeLinkGraphic<Src,EdgeType> gwindow;

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
    public VCustomGraph(CoordinateManager<Src, C> mgr) {
        super(mgr);
        window = null;
        gwindow = new DelegatingNodeLinkGraphic<Src,EdgeType>();
        window = gwindow.getPointGraphic();
        window.getCoordinateManager().addCoordinateListener(this);
    }

    /**
     * Construct point set with specified objects.
     * @param loc initial locations of points
     */
    public VCustomGraph(Map<Src,? extends C> loc) {
        super(loc);
        window = null;
        gwindow = new DelegatingNodeLinkGraphic<Src,EdgeType>();
        window = gwindow.getPointGraphic();
        window.getCoordinateManager().addCoordinateListener(this);
    }

    //
    // PROPERTIES
    //

    @Override
    public Graphic getWindowEntry() {
        return gwindow;
    }

    public Set<EdgeType> getEdges() {
        return gwindow.getEdges();
    }

    public void setEdges(Set<? extends EdgeType> edges) {
        // make a copy to prevent errors in updating edges
        gwindow.setEdges(new LinkedHashSet<EdgeType>(edges));
    }

    public void setEdgeStyler(ObjectStyler<EdgeType, PathStyle> styler) {
        gwindow.setEdgeStyler(styler);
    }

    public ObjectStyler<EdgeType, PathStyle> getEdgeStyler() {
        return gwindow.getEdgeStyler();
    }

}