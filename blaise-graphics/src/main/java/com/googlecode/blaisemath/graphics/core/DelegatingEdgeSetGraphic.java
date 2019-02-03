package com.googlecode.blaisemath.graphics.core;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2019 Elisha Peterson
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
import static java.util.stream.Collectors.toCollection;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.graph.EndpointPair;
import com.googlecode.blaisemath.annotation.InvokedFromThread;
import com.googlecode.blaisemath.style.ObjectStyler;
import com.googlecode.blaisemath.style.Renderer;
import com.googlecode.blaisemath.coordinate.CoordinateChangeEvent;
import com.googlecode.blaisemath.coordinate.CoordinateListener;
import com.googlecode.blaisemath.coordinate.CoordinateManager;
import com.googlecode.blaisemath.util.swing.MoreSwingUtilities;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

/**
 * A collection of edges backed by a common set of points.
 * 
 * @param <S> source object type
 * @param <E> edge type
 * @param <G> type of graphics canvas to render to
 * 
 * @author Elisha Peterson
 */
public class DelegatingEdgeSetGraphic<S,E extends EndpointPair<S>,G> extends GraphicComposite<G> {

    private static final Logger LOG = Logger.getLogger(DelegatingEdgeSetGraphic.class.getName());

    public static final String P_EDGE_RENDERER = "edgeRenderer";
    public static final int DEFAULT_MAX_CACHE_SIZE = 5000;

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
        this(CoordinateManager.create(DEFAULT_MAX_CACHE_SIZE), null);
    }
    
    /** 
     * Initialize with given coordinate manager.
     * @param mgr manages source object loc
     * @param edgeRenderer edge renderer
     */
    public DelegatingEdgeSetGraphic(CoordinateManager<S,Point2D> mgr, Renderer<Shape,G> edgeRenderer) {
        coordListener = this::handleCoordinateChange;
        
        setCoordinateManager(mgr);
        setEdgeRenderer(edgeRenderer);
    }

    //region EVENTS
    
    @InvokedFromThread("unknown")
    private void handleCoordinateChange(final CoordinateChangeEvent<S,Point2D> evt) {
        updateQueue.add(evt);
        MoreSwingUtilities.invokeOnEventDispatchThread(this::processNextCoordinateChangeEvent);
    }
    
    @InvokedFromThread("EDT")
    private void processNextCoordinateChangeEvent() {
        if (!SwingUtilities.isEventDispatchThread()) {
            LOG.log(Level.WARNING, "processNextCoordinateChangeEvent() called from non-EDT");
        }
        CoordinateChangeEvent evt = updateQueue.poll();
        if (evt != null && evt.getSource() == pointManager) {
            updateEdgeGraphics(pointManager.getActiveLocationCopy(), Lists.newArrayList(), true);
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
    private void updateEdgeGraphics(Map<S, Point2D> locMap, List<Graphic<G>> removeMe, boolean notify) {
        if (!SwingUtilities.isEventDispatchThread()) {
            LOG.log(Level.WARNING, "updateEdgeGraphics() called from non-EDT");
        }
        updating = true;
        boolean change = false;
        List<Graphic<G>> addMe = Lists.newArrayList();
        for (E edge : Sets.newLinkedHashSet(edges.keySet())) {
            DelegatingPrimitiveGraphic<E,Shape,G> dsg = edges.get(edge);
            Point2D p1 = locMap.get(edge.nodeU());
            Point2D p2 = locMap.get(edge.nodeV());
            if (p1 == null || p2 == null) {
                if (dsg != null) {
                    removeMe.add(dsg);
                    edges.put(edge, null);
                }
            } else {
                Line2D.Double line = new Line2D.Double(p1, p2);
                if (dsg == null) {
                    dsg = new DelegatingPrimitiveGraphic<>(edge, line,
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

    //endregion

    //region PROPERTIES
    
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
            
            // lock to ensure that no changes are made until after the listener has been setup
            synchronized (mgr) {
                this.pointManager = mgr;
                updateEdgeGraphics(mgr.getActiveLocationCopy(), Lists.newArrayList(), false);
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
        Set<E> addMe = newEdges.stream().filter(e -> !edges.containsKey(e))
                .collect(toCollection(Sets::newLinkedHashSet));
        Set<E> removeMe = edges.keySet().stream().filter(e -> !newEdges.contains(e))
                .collect(Collectors.toSet());
        if (!removeMe.isEmpty() || !addMe.isEmpty()) {
            List<Graphic<G>> remove = removeMe.stream().map(edges::remove).collect(Collectors.toList());
            addMe.forEach(e -> edges.put(e, null));
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
            edges.values().stream().filter(Objects::nonNull).forEach(e -> e.setObjectStyler(styler));
            fireGraphicChanged();
        }
    }

    public @Nullable Renderer<Shape, G> getEdgeRenderer() {
        return edgeRenderer;
    }

    public final void setEdgeRenderer(@Nullable Renderer<Shape, G> renderer) {
        if (this.edgeRenderer != renderer) {
            Object old = this.edgeRenderer;
            this.edgeRenderer = renderer;
            edges.values().forEach(e -> e.setRenderer(renderer));
            fireGraphicChanged();
            pcs.firePropertyChange(P_EDGE_RENDERER, old, renderer);
        }
    }
    
    //endregion

    @Override
    public void initContextMenu(JPopupMenu menu, Graphic<G> src, Point2D point, Object focus, Set<Graphic<G>> selection, G canvas) {
        // provide additional info for context menu
        Graphic<G> gfc = graphicAt(point, canvas);
        super.initContextMenu(menu, this, point, 
                gfc instanceof DelegatingPrimitiveGraphic ? ((DelegatingPrimitiveGraphic)gfc).getSourceObject() : focus, 
                selection, canvas);
    }
}
