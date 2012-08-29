/*
 * VCustomPointSet.java
 * Created Oct 5, 2011
 */
package org.blaise.visometry;

import java.util.Set;
import org.blaise.graphics.DelegatingNodeLinkGraphic;
import org.blaise.graphics.Graphic;
import org.blaise.util.Edge;
import org.blaise.style.ObjectStyler;
import org.blaise.style.PathStyle;

/**
 * A set of draggable points defined in local coordinates. Properties of the objects
 * (including style, tooltips, locations, etc.) are managed by delegates.
 *
 * @param <C> the local coordinate
 * @param <Src> the type of object being displayed
 *
 * @author elisha
 */
public class VCustomGraph<C,Src,EdgeType extends Edge<Src>> extends VCustomPointSet<C, Src> {

    DelegatingNodeLinkGraphic<Src,EdgeType> gwindow;

    /** Construct point set with specified objects. */
    public VCustomGraph(C[] initialPoint) {
        super(initialPoint);
        gwindow = new DelegatingNodeLinkGraphic<Src,EdgeType>();
        window = gwindow.getPointGraphic();
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

    public void setEdges(Set<EdgeType> edges) {
        gwindow.setEdges(edges);
    }

    public void setEdgeStyler(ObjectStyler<EdgeType, PathStyle> styler) {
        gwindow.setEdgeStyler(styler);
    }

    public ObjectStyler<EdgeType, PathStyle> getEdgeStyler() {
        return gwindow.getEdgeStyler();
    }

}
