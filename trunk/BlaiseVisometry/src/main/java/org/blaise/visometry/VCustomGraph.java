/*
 * VCustomPointSet.java
 * Created Oct 5, 2011
 */
package org.blaise.visometry;

import java.util.Set;
import org.blaise.graphics.CustomGraphGraphic;
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

    CustomGraphGraphic<Src,EdgeType> window;

    /** Construct point set with specified objects. */
    public VCustomGraph(C[] initialPoint) {
        super(initialPoint);
        super.window = window = new CustomGraphGraphic<Src,EdgeType>();
        window.removeMouseListeners();
        window.removeMouseMotionListeners();
    }

    //
    // PROPERTIES
    //

    @Override
    public CustomGraphGraphic<Src,EdgeType> getWindowEntry() {
        return window;
    }

    public Set<EdgeType> getEdges() {
        return window.getEdges();
    }

    public void setEdges(Set<EdgeType> edges) {
        window.setEdges(edges);
    }

    public void setEdgeStyler(ObjectStyler<EdgeType, PathStyle> styler) {
        window.setEdgeStyler(styler);
    }

    public ObjectStyler<EdgeType, PathStyle> getEdgeStyler() {
        return window.getEdgeStyler();
    }

}
