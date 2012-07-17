/*
 * VCustomPointSet.java
 * Created Oct 5, 2011
 */
package org.bm.blaise.specto.graphics;

import java.util.Map;
import java.util.Set;
import org.bm.blaise.graphics.CustomGraphGraphic;
import org.bm.blaise.style.ObjectStyler;
import org.bm.blaise.style.PathStyle;

/**
 * A set of draggable points defined in local coordinates. Properties of the objects
 * (including style, tooltips, locations, etc.) are managed by delegates.
 *
 * @param <C> the local coordinate
 * @param <Src> the type of object being displayed
 *
 * @author elisha
 */
public class VCustomGraph<C, Src> extends VCustomPointSet<C, Src> {

    CustomGraphGraphic<Src> window;

    /** Construct point set with specified objects. */
    public VCustomGraph(C[] initialPoint) {
        super(initialPoint);
        super.window = window = new CustomGraphGraphic<Src>();
        window.clearMouseListeners();
        window.addMouseListener(new VGraphicIndexedPointDragger<C>(this).adapter());
    }

    //
    // PROPERTIES
    //

    @Override
    public CustomGraphGraphic<Src> getWindowEntry() {
        return (CustomGraphGraphic<Src>) window;
    }

    public Map<? extends Src, ? extends Set<? extends Src>> getEdges() {
        return window.getEdges();
    }

    public void setEdges(Map<? extends Src, ? extends Set<? extends Src>> edges) {
        window.setEdges(edges);
    }

    public void setEdgeStyler(ObjectStyler<Object[], PathStyle> styler) {
        window.setEdgeStyler(styler);
    }

    public ObjectStyler<Object[], PathStyle> getEdgeStyler() {
        return window.getEdgeStyler();
    }

}
