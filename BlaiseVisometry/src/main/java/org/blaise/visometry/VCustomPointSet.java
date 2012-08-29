/*
 * VCustomPointSet.java
 * Created Oct 5, 2011
 */
package org.blaise.visometry;

import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.blaise.graphics.DelegatingPointSetGraphic;
import org.blaise.graphics.Graphic;
import org.blaise.style.ObjectStyler;
import org.blaise.style.PointStyle;

/**
 * A set of draggable points defined in local coordinates. Properties of the objects
 * (including style, tooltips, locations, etc.) are managed by delegates.
 *
 * @param <C> the local coordinate
 * @param <Src> the type of object being displayed
 *
 * @author elisha
 */
public class VCustomPointSet<C, Src> extends VGraphicSupport<C> implements PropertyChangeListener {

    /** Local coordinates of the points */
    protected Map<Src, C> localCoords = new HashMap<Src, C>();
    /** The window entry */
    protected DelegatingPointSetGraphic<Src> window = new DelegatingPointSetGraphic<Src>();

    /**
     * Initialize set
     */
    public VCustomPointSet(C[] initialPoint) {
        window.getPointManager().addPropertyChangeListener(this);
    }

    //
    // PROPERTIES
    //

    public Graphic getWindowEntry() {
        return window;
    }

    public Set<? extends Src> getObjects() {
        return window.getObjects();
    }

    public ObjectStyler<Src, PointStyle> getStyler() {
        return window.getStyler();
    }

    public synchronized void setStyler(ObjectStyler<Src, PointStyle> styler) {
        window.setStyler(styler);
    }

    //
    // CONVERSION
    //

    public synchronized void convert(final Visometry<C> vis, VisometryProcessor<C> processor) {
        window.
        if (draggedPoint) {
            Point2D[] arr = window.getPointManager().getLocationArray();
            for (int i = 0; i < point.length; i++) {
                point[i] = vis.toLocal(arr[i]);
            } 
        } else {
            Point2D[] p = processor.convertToArray(point, vis);
            window.getPointManager().setLocationArray(p);
        }
        draggedPoint = false;
        setUnconverted(false);
    }

}
