/*
 * VCustomPointSet.java
 * Created Oct 5, 2011
 */
package org.blaise.visometry;

import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.blaise.graphics.DelegatingPointSetGraphic;
import org.blaise.graphics.Graphic;
import org.blaise.style.ObjectStyler;
import org.blaise.style.PointStyle;
import org.blaise.util.CoordinateChangeEvent;
import org.blaise.util.CoordinateListener;
import org.blaise.util.CoordinateManager;

/**
 * A set of draggable points defined in local coordinates. The local coordinates are updated
 * when the user drags the points in the window.
 *
 * @param <C> the local coordinate
 * @param <S> the type of object being displayed
 *
 * @author elisha
 */
public class VCustomPointSet<C, S> extends VGraphicSupport<C> {

    /** The window entry */
    protected DelegatingPointSetGraphic<S> window = new DelegatingPointSetGraphic<S>();
    
    /** Local coordinates of the points */
    protected CoordinateManager<S,C> coordManager;
    /** Listens for updates to both window & local coordinate managers */
    protected final CoordinateListener coordinateListener;
    
    /**
     * Initialize with no points
     */
    public VCustomPointSet() {
        this(Collections.EMPTY_MAP);
    }

    /**
     * Initialize with specified coordinate manager.
     * @param mgr coordinate manager
     */
    public VCustomPointSet(CoordinateManager<S,C> mgr) {
        coordinateListener = new CoordinateListener(){
            public void coordinatesChanged(CoordinateChangeEvent evt) {
                handleCoordinatesChanged(evt);
            }
        };
        
        setCoordinateManager(mgr);
        window.getCoordinateManager().addCoordinateListener(coordinateListener);
    }

    /**
     * Initialize set
     * @param loc initial locations of points
     */
    public VCustomPointSet(Map<S, ? extends C> loc) {
        this(new CoordinateManager<S,C>());
        coordManager.putAll(loc);
    }

    private void handleCoordinatesChanged(CoordinateChangeEvent evt) {
        if (converting) {
            return;
        }
        if (evt.getSource() == coordManager) {
            // local coords changed
            setUnconverted(true);
        } else if (evt.getSource() == window.getCoordinateManager()) {
            // window coords changed (from dragging)
            synchronized (coordManager) {
                if (evt.isAddEvent()) {
                    Visometry<C> vis = parent.getVisometry();
                    Map<S,Point2D> add = (Map<S,Point2D>) evt.getAdded();
                    if (add != null) {
                        Map<S,C> local = new HashMap<S,C>();
                        for (S s : add.keySet()) {
                            local.put(s, vis.toLocal(add.get(s)));
                        }
                        coordManager.putAll(local);
                    }
                }
                if (evt.isRemoveEvent()) {
                    coordManager.removeObjects(evt.getRemoved());
                }
            }
        }
    }

    //
    // PROPERTIES
    //

    public Graphic getWindowGraphic() {
        return window;
    }

    public CoordinateManager<S, C> getCoordinateManager() {
        return coordManager;
    }

    /**
     * Initialize coordinate manager for this set.
     * @param mgr manager
     */
    public void setCoordinateManager(CoordinateManager mgr) {
        if (this.coordManager != mgr) {
            if (this.coordManager != null) {
                coordManager.removeCoordinateListener(coordinateListener);
            }
            coordManager = mgr;
            if (coordManager != null) {
                coordManager.addCoordinateListener(coordinateListener);
            }
            setUnconverted(true);
        }
    }

    public Set<? extends S> getObjects() {
        return window.getObjects();
    }

    /**
     * Add a collection of objects to the view
     * @param loc objects to put
     */
    public void addObjects(Map<S, ? extends C> loc) {
        coordManager.putAll(loc);
        setUnconverted(true);
    }

    /**
     * Replaces the current objects in the set with a new set
     * @param loc new objects
     */
    public void setObjects(Map<S, ? extends C> loc) {
        coordManager.setCoordinateMap(loc);
        setUnconverted(true);
    }

    public void removeObjects(Set<S> obj) {
        if (!obj.isEmpty()) {
            coordManager.removeObjects(obj);
            setUnconverted(true);
        }
    }

    public ObjectStyler<S, PointStyle> getStyler() {
        return window.getStyler();
    }

    public void setStyler(ObjectStyler<S, PointStyle> styler) {
        window.setStyler(styler);
    }

    //
    // CONVERSION
    //

    /** Ignore changes while converting */
    boolean converting = false;

    public void convert(final Visometry<C> vis, VisometryProcessor<C> processor) {
        // conversion should never occur while points are changing
        synchronized (coordManager) {
            converting = true;
            Map<S,Point2D> winmap = new HashMap<S,Point2D>();
            int n = coordManager.getObjects().size();
            for (S s : coordManager.getObjects()) {
                winmap.put(s, processor.convert(coordManager.apply(s), vis));
            }
            window.getCoordinateManager().setCoordinateMap(winmap);
            int n2 = window.getCoordinateManager().getObjects().size();
            if (n != n2) {
                throw new IllegalStateException("Object sizes do not match!");
            }
            setUnconverted(false);
            converting = false;
        }
    }

}
