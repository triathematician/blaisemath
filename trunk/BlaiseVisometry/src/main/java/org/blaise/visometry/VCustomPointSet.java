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
 * A set of draggable points defined in local coordinates. The local coordinates are updated
 * when the user drags the points in the window.
 *
 * @param <C> the local coordinate
 * @param <Src> the type of object being displayed
 *
 * @author elisha
 */
public class VCustomPointSet<C, Src> extends VGraphicSupport<C> implements PropertyChangeListener {

    /** Local coordinates of the points */
    protected final Map<Src, C> localCoords = new HashMap<Src, C>();
    /** The window entry */
    protected DelegatingPointSetGraphic<Src> window = new DelegatingPointSetGraphic<Src>();

    /**
     * Initialize set
     * @param loc initial locations of points
     */
    public VCustomPointSet(Map<Src, ? extends C> loc) {
        addObjects(loc);
        window.getPointManager().addPropertyChangeListener(this);
    }

    public synchronized void propertyChange(PropertyChangeEvent evt) {
        // user has dragged points around
        if (evt.getSource() == window.getPointManager()) {
            Visometry<C> vis = parent.getVisometry();
            if ("add".equals(evt.getPropertyName())) {
                Map<Src,Point2D> added = (Map<Src,Point2D>) evt.getNewValue();
                for (Src s : added.keySet()) {
                    localCoords.put(s, vis.toLocal(added.get(s)));
                }
            } else if ("cache".equals(evt.getPropertyName())) {
                Map<Src,Point2D> removed = (Map<Src,Point2D>) evt.getNewValue();
                for (Src s : removed.keySet()) {
                    localCoords.remove(s);
                }
            } else if ("remove".equals(evt.getPropertyName())) {
                Set<Src> removed = (Set<Src>) evt.getNewValue();
                for (Src s : removed) {
                    localCoords.remove(s);
                }
            }
        }
    }



    //
    // PROPERTIES
    //

    public Graphic getWindowEntry() {
        return window;
    }

    public synchronized Set<? extends Src> getObjects() {
        return window.getObjects();
    }

    public synchronized void addObjects(Map<Src, ? extends C> loc) {
        localCoords.putAll(loc);
        setUnconverted(true);
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
        Map<Src,Point2D> winmap = new HashMap<Src,Point2D>();
        for (Src s : localCoords.keySet()) {
            winmap.put(s, processor.convert(localCoords.get(s), vis));
        }
        window.getPointManager().update(winmap);
    }

}
