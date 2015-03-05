/*
 * VCustomPointSet.java
 * Created Oct 5, 2011
 */
package com.googlecode.blaisemath.visometry;

/*
 * #%L
 * BlaiseVisometry
 * --
 * Copyright (C) 2009 - 2015 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.googlecode.blaisemath.annotation.InvokedFromThread;
import com.googlecode.blaisemath.graphics.core.DelegatingPointSetGraphic;
import com.googlecode.blaisemath.graphics.core.Graphic;
import com.googlecode.blaisemath.style.ObjectStyler;
import com.googlecode.blaisemath.util.coordinate.CoordinateChangeEvent;
import com.googlecode.blaisemath.util.coordinate.CoordinateListener;
import com.googlecode.blaisemath.util.coordinate.CoordinateManager;
import com.googlecode.blaisemath.util.swing.BSwingUtilities;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A set of draggable points defined in local coordinates. The local coordinates are updated
 * when the user drags the points in the window.
 *
 * @param <C> the local coordinate
 * @param <S> the type of object being displayed
 * @param <G> graphics canvas type
 *
 * @author elisha
 */
public class VCustomPointSet<C,S,G> extends VGraphicSupport<C,G> {
    
    private static final int NODE_CACHE_SIZE = 20000;

    /** The window entry */
    protected DelegatingPointSetGraphic<S,G> window = new DelegatingPointSetGraphic<S,G>();
    
    /** Local coordinates of the points */
    protected CoordinateManager<S,C> coordManager;
    /** Listens for updates to both window and local coordinate managers */
    protected final CoordinateListener coordinateListener;

    /** Ignore changes while converting */
    boolean converting = false;
    
    /**
     * Initialize with no points
     */
    public VCustomPointSet() {
        this(CoordinateManager.<S,C>create(NODE_CACHE_SIZE));
    }

    /**
     * Initialize with specified coordinate manager.
     * @param mgr coordinate manager
     */
    public VCustomPointSet(CoordinateManager<S,C> mgr) {
        coordinateListener = new CoordinateListener<S,C>(){
            @Override
            @InvokedFromThread("unknown")
            public void coordinatesChanged(CoordinateChangeEvent<S,C> evt) {
                handleCoordinatesChanged(evt);
            }
        };
        
        setCoordinateManager(mgr);
        window.getCoordinateManager().addCoordinateListener(coordinateListener);
    }

    @InvokedFromThread("unknown")
    private void handleCoordinatesChanged(final CoordinateChangeEvent<S,C> evt) {
        BSwingUtilities.invokeOnEventDispatchThread(new Runnable(){
            @Override
            public void run() {
                if (converting) {
                    return;
                }
                if (evt.getSource() == coordManager) {
                    // local coords changed
                    setUnconverted(true);
                } else if (evt.getSource() == window.getCoordinateManager()) {
                    CoordinateChangeEvent<S,Point2D> winEvt = (CoordinateChangeEvent<S,Point2D>) evt;
                    // window coords changed (from dragging)
                    if (evt.isAddEvent()) {
                        Visometry<C> vis = parent.getVisometry();
                        Map<S, ? extends Point2D> add = winEvt.getAdded();
                        Map<S,C> local = new HashMap<S,C>();
                        for (S s : add.keySet()) {
                            local.put(s, vis.toLocal(add.get(s)));
                        }
                        coordManager.putAll(local);
                    }
                    // possible for both add and remove, so this shouldn't be an else statement
                    if (evt.isRemoveEvent()) {
                        coordManager.forget(evt.getRemoved());
                    }
                }
            }
        });
    }

    //
    // PROPERTIES
    //

    @Override
    public Graphic getWindowGraphic() {
        return window;
    }

    public CoordinateManager<S,C> getCoordinateManager() {
        return coordManager;
    }

    /**
     * Initialize coordinate manager for this set.
     * @param mgr manager
     */
    public final void setCoordinateManager(CoordinateManager<S,C> mgr) {
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

    public Set<S> getObjects() {
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
            coordManager.forget(obj);
            setUnconverted(true);
        }
    }

    public ObjectStyler<S> getPointStyler() {
        return window.getStyler();
    }

    public void setPointStyler(ObjectStyler<S> styler) {
        window.setStyler(styler);
    }

    //
    // CONVERSION
    //

    @Override
    public void convert(final Visometry<C> vis, VisometryProcessor<C> processor) {
        // conversion should never occur while points are changing
        converting = true;
        Map<S, C> locs = coordManager.getActiveLocationCopy();
        Map<S,Point2D> winmap = new HashMap<S,Point2D>();
        for (S s : locs.keySet()) {
            winmap.put(s, processor.convert(locs.get(s), vis));
        }
        window.getCoordinateManager().setCoordinateMap(winmap);
        setUnconverted(false);
        converting = false;
    }

}
