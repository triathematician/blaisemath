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
    private final CoordinateManager<Src,C> localCoords = new CoordinateManager<Src,C>();
    /** The window entry */
    protected DelegatingPointSetGraphic<Src> window = new DelegatingPointSetGraphic<Src>();

    public VCustomPointSet() {
        this(Collections.EMPTY_MAP);
    }

    /**
     * Initialize set
     * @param loc initial locations of points
     */
    public VCustomPointSet(Map<Src, ? extends C> loc) {
        addObjects(loc);
        window.getCoordinateManager().addCoordinateListener(this);
    }

    public synchronized void coordinatesAdded(Map added) {
        Visometry<C> vis = parent.getVisometry();
        Map<Src,Point2D> add = (Map<Src,Point2D>) added;
        Map<Src,C> local = new HashMap<Src,C>();
        for (Src s : add.keySet()) {
            local.put(s, vis.toLocal(add.get(s)));
        }
        localCoords.putAll(local);
    }

    public synchronized void coordinatesRemoved(Set removed) {
        localCoords.removeObjects(removed);
    }

    //
    // PROPERTIES
    //

    public Graphic getWindowEntry() {
        return window;
    }

    public CoordinateManager<Src, C> getCoordinateManager() {
        return localCoords;
    }

    public synchronized Set<? extends Src> getObjects() {
        return window.getObjects();
    }

    /**
     * Add a collection of objects to the view
     * @param loc objects to put
     */
    public synchronized void addObjects(Map<Src, ? extends C> loc) {
        localCoords.putAll(loc);
        setUnconverted(true);
    }

    /**
     * Replaces the current objects in the set with a new set
     * @param loc new objects
     */
    public synchronized void setObjects(Map<Src, ? extends C> loc) {
        localCoords.setCoordinateMap(loc);
        setUnconverted(true);
    }

    public synchronized void removeObjects(Set<Src> obj) {
        localCoords.removeObjects(obj);
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
        for (Src s : localCoords.getObjects()) {
            winmap.put(s, processor.convert(localCoords.of(s), vis));
        }
        window.getCoordinateManager().putAll(winmap);
        setUnconverted(false);
    }

}
