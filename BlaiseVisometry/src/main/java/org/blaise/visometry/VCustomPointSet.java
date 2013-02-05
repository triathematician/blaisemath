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
 * @param <Src> the type of object being displayed
 *
 * @author elisha
 */
public class VCustomPointSet<C, Src> extends VGraphicSupport<C> implements CoordinateListener {

    /** Local coordinates of the points */
    private CoordinateManager<Src,C> coordManager;
    /** The window entry */
    protected DelegatingPointSetGraphic<Src> window = new DelegatingPointSetGraphic<Src>();

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
    public VCustomPointSet(CoordinateManager<Src,C> mgr) {
        setCoordinateManager(mgr);
        window.getCoordinateManager().addCoordinateListener(this);
    }

    /**
     * Initialize set
     * @param loc initial locations of points
     */
    public VCustomPointSet(Map<Src, ? extends C> loc) {
        this(new CoordinateManager<Src,C>());
        coordManager.putAll(loc);
    }

    public void coordinatesChanged(CoordinateChangeEvent evt) {
//        if (evt.getSource() == coordManager) {
//            System.err.println((converting?"VCPS: CM-CONV: ":"VCPS: CM-UNCO: ") + evt);
//        } else if (evt.getSource() == window.getCoordinateManager()) {
//            System.err.println((converting?"VCPS:WCM-CONV: ":"VCPS:WCM-UNCO: ") + evt);
//        } else {
//            System.err.println("VCPS-UNEXPECTED EVENT SOURCE");
//        }
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
                    Map<Src,Point2D> add = (Map<Src,Point2D>) evt.getAdded();
                    if (add != null) {
                        Map<Src,C> local = new HashMap<Src,C>();
                        for (Src s : add.keySet()) {
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

    public Graphic getWindowEntry() {
        return window;
    }

    public CoordinateManager<Src, C> getCoordinateManager() {
        return coordManager;
    }

    /**
     * Initialize coordinate manager for this set.
     * @param mgr manager
     */
    public void setCoordinateManager(CoordinateManager mgr) {
        if (this.coordManager != mgr) {
            if (this.coordManager != null) {
                coordManager.removeCoordinateListener(this);
            }
            coordManager = mgr;
            if (coordManager != null) {
                coordManager.addCoordinateListener(this);
            }
            setUnconverted(true);
        }
    }

    public Set<? extends Src> getObjects() {
        return window.getObjects();
    }

    /**
     * Add a collection of objects to the view
     * @param loc objects to put
     */
    public void addObjects(Map<Src, ? extends C> loc) {
        coordManager.putAll(loc);
        setUnconverted(true);
    }

    /**
     * Replaces the current objects in the set with a new set
     * @param loc new objects
     */
    public void setObjects(Map<Src, ? extends C> loc) {
        coordManager.setCoordinateMap(loc);
        setUnconverted(true);
    }

    public void removeObjects(Set<Src> obj) {
        if (obj.size() > 0) {
            coordManager.removeObjects(obj);
            setUnconverted(true);
        }
    }

    public ObjectStyler<Src, PointStyle> getStyler() {
        return window.getStyler();
    }

    public void setStyler(ObjectStyler<Src, PointStyle> styler) {
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
            Map<Src,Point2D> winmap = new HashMap<Src,Point2D>();
            int n = coordManager.getObjects().size();
            for (Src s : coordManager.getObjects()) {
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
