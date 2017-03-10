/**
 * DelegatingEdgeSetGraphic.java
 * Created Aug 28, 2012
 */

package com.googlecode.blaisemath.graphics.core;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2017 Elisha Peterson
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

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.googlecode.blaisemath.annotation.InvokedFromThread;
import com.googlecode.blaisemath.style.ObjectStyler;
import com.googlecode.blaisemath.style.Renderer;
import com.googlecode.blaisemath.util.Edge;
import com.googlecode.blaisemath.util.coordinate.CoordinateChangeEvent;
import com.googlecode.blaisemath.util.coordinate.CoordinateListener;
import com.googlecode.blaisemath.util.coordinate.CoordinateManager;
import com.googlecode.blaisemath.util.swing.BSwingUtilities;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

/**
 * A collection of edges backed by a common set of points.
 * 
 * @param <S> source object type
 * @param <E> edge type
 * @param <G> type of graphics canvas to render to
 * 
 * @author elisha
 */
public class DelegatingEdgeSetGraphic<S,E extends Edge<S>,G> extends GraphicComposite<G> {

    private static final Logger LOG = Logger.getLogger(DelegatingEdgeSetGraphic.class.getName());
    public static final String EDGE_RENDERER_PROP = "edgeRenderer";

    /** The edges in the graphic. */
    protected final Map<E,DelegatingPrimitiveGraphic<E,Shape,G>> edges = Maps.newHashMap();
    /** Styler for edges */
    protected ObjectStyler<E> edgeStyler = ObjectStyler.create();
    /** Renderer for edges */
    protected Renderer<Shape,G> edgeRenderer;
    
    /** Point manager. Maintains objects and their locations, and enables mouse dragging. */
    protected CoordinateManager<S, Point2D> pointManager;
    /** Listener for changes to coordinates */
    private final CoordinateListener<S, Point2D> coordListener;
    /** Flag that indicates points are being updated, and no notification events should be sent. */
    protected boolean updating = false;
    /** Queue of updates to be processed */
    private final Queue<CoordinateChangeEvent> updateQueue = Queues.newConcurrentLinkedQueue();
    
    /**
     * Initialize with default coordinate manager.
     */
    public DelegatingEdgeSetGraphic() {
        this(CoordinateManager.<S,Point2D>create(5000), null);
    }
    
    /** 
     * Initialize with given coordinate manager.
     * @param mgr manages source object loc
     * @param edgeRenderer edge renderer
     */
    public DelegatingEdgeSetGraphic(CoordinateManager<S,Point2D> mgr,
            Renderer<Shape,G> edgeRenderer) {
        coordListener = new CoordinateListener<S,Point2D>(){
            @Override
            @InvokedFromThread("unknown")
            public void coordinatesChanged(CoordinateChangeEvent<S,Point2D> evt) {
                handleCoordinateChange(evt);
            }
        };
        
        setCoordinateManager(mgr);
        setEdgeRenderer(edgeRenderer);
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="EVENT HANDLERS">
    
    @InvokedFromThread("unknown")
    private void handleCoordinateChange(final CoordinateChangeEvent<S,Point2D> evt) {
        updateQueue.add(evt);
        BSwingUtilities.invokeOnEventDispatchThread(new Runnable(){
            @Override
            public void run() {
                processNextCoordinateChangeEvent();
            }
        });
    }
    
    @InvokedFromThread("EDT")
    private void processNextCoordinateChangeEvent() {
        if (!SwingUtilities.isEventDispatchThread()) {
            LOG.log(Level.WARNING, "processNextCoordinateChangeEvent() called from non-EDT");
        }
        CoordinateChangeEvent evt = updateQueue.poll();
        if (evt != null && evt.getSource() == pointManager) {
            updateEdgeGraphics(pointManager.getActiveLocationCopy(), Lists.<Graphic<G>>newArrayList(), true);
        }
    }
    
    @InvokedFromThread("EDT")
    private void clearPendingUpdates() {
        if (!SwingUtilities.isEventDispatchThread()) {
            LOG.log(Level.WARNING, "clearPendingUpdates() called from non-EDT");
        }
        updateQueue.clear();
    }
                
    @InvokedFromThread("EDT")
    private void updateEdgeGraphics(Map<S,Point2D> locs, List<Graphic<G>> removeMe, boolean notify) {
        if (!SwingUtilities.isEventDispatchThread()) {
            LOG.log(Level.WARNING, "updateEdgeGraphics() called from non-EDT");
        }
        updating = true;
        boolean change = false;
        List<Graphic<G>> addMe = Lists.newArrayList();
        for (E edge : Sets.newLinkedHashSet(edges.keySet())) {
            DelegatingPrimitiveGraphic<E,Shape,G> dsg = edges.get(edge);
            Point2D p1 = locs.get(edge.getNode1());
            Point2D p2 = locs.get(edge.getNode2());
            if (p1 == null || p2 == null) {
                if (dsg != null) {
                    removeMe.add(dsg);
                    edges.put(edge, null);
                }
            } else {
                Line2D.Double line = new Line2D.Double(p1, p2);
                if (dsg == null) {
                    dsg = new DelegatingPrimitiveGraphic<E,Shape,G>(edge, line,
                        edgeStyler, edgeRenderer);
                    edges.put(edge, dsg);
                    dsg.setObjectStyler(edgeStyler);
                    addMe.add(dsg);
                } else {
                    dsg.setPrimitive(line);
                    change = true;
                }
            }
        }
        change = replaceGraphics(removeMe, addMe) || change;
        updating = false;
        if (change && notify) {
            fireGraphicChanged();
        }
    }

    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //
    
    public CoordinateManager<S, Point2D> getCoordinateManager() {
        return pointManager;
    }

    /**
     * Set manager responsible for tracking point locations
     * @param mgr manager
     */
    public final void setCoordinateManager(CoordinateManager<S, Point2D> mgr) {
        if (this.pointManager != checkNotNull(mgr)) {
            if (this.pointManager != null) {
                this.pointManager.removeCoordinateListener(coordListener);
            }
            this.pointManager = null;
            clearPendingUpdates();
            
            // lock to ensure that no changes are made until after the listener
            // has been setup
            synchronized (mgr) {
                this.pointManager = mgr;
                updateEdgeGraphics(mgr.getActiveLocationCopy(), Lists.<Graphic<G>>newArrayList(), false);
                this.pointManager.addCoordinateListener(coordListener);
            }
            super.graphicChanged(this);
        }
    }

    /**
     * Return map describing graph's edges
     * @return edges
     */
    public Set<E> getEdges() {
        return edges.keySet();
    }

    /**
     * Sets map describing graphs edges. Also updates the set of objects to be
     * the nodes within the edges. Should be called from the EDT.
     * @param newEdges new edges to put
     */
    public final void setEdges(Set<? extends E> newEdges) {
        Set<E> addMe = Sets.newLinkedHashSet();
        Set<E> removeMe = Sets.newHashSet();
        for (E e : newEdges) {
            if (!edges.containsKey(e)) {
                addMe.add(e);
            }
        }
        for (E e : edges.keySet()) {
            if (!newEdges.contains(e)) {
                removeMe.add(e);
            }
        }
        if (!removeMe.isEmpty() || !addMe.isEmpty()) {
            List<Graphic<G>> remove = Lists.newArrayList();
            for (E e : removeMe) {
                remove.add(edges.remove(e));
            }
            for (E e : addMe) {
                edges.put(e, null);
            }
            updateEdgeGraphics(pointManager.getActiveLocationCopy(), remove, true);
        }
    }

    /**
     * Returns the current style styler
     * @return style styler
     */
    public ObjectStyler<E> getEdgeStyler() {
        return edgeStyler;
    }

    /**
     * Sets the current style styler. If null, will use the default style
     * provided by the parent.
     * @param styler used for custom edge styles
     */
    public void setEdgeStyler(ObjectStyler<E> styler) {
        if (this.edgeStyler != styler) {
            this.edgeStyler = styler;
            for (DelegatingPrimitiveGraphic<E,Shape,G> dsg : edges.values()) {
                if (dsg != null) {
                    dsg.setObjectStyler(styler);
                }
            }
            fireGraphicChanged();
        }
    }

    @Nullable 
    public Renderer<Shape, G> getEdgeRenderer() {
        return edgeRenderer;
    }

    public final void setEdgeRenderer(@Nullable Renderer<Shape, G> renderer) {
        if (this.edgeRenderer != renderer) {
            Object old = this.edgeRenderer;
            this.edgeRenderer = renderer;
            for (DelegatingPrimitiveGraphic<E,Shape,G> edge : edges.values()) {
                edge.setRenderer(renderer);
            }
            fireGraphicChanged();
            pcs.firePropertyChange(EDGE_RENDERER_PROP, old, renderer);
        }
    }
    
    //</editor-fold>


    @Override
    public void initContextMenu(JPopupMenu menu, Graphic<G> src, Point2D point, Object focus, Set selection) {
        // provide additional info for context menu
        Graphic<G> gfc = graphicAt(point);
        super.initContextMenu(menu, this, point, 
                gfc instanceof DelegatingPrimitiveGraphic ? ((DelegatingPrimitiveGraphic)gfc).getSourceObject() : focus, 
                selection);
    }
}
