/**
 * DelegatingEdgeSetGraphic.java
 * Created Aug 28, 2012
 */

package com.googlecode.blaisemath.graphics.core;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
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





import com.googlecode.blaisemath.graphics.core.DelegatingPrimitiveGraphic;
import com.googlecode.blaisemath.graphics.core.GraphicComposite;
import com.googlecode.blaisemath.graphics.core.Graphic;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Maps;
import com.googlecode.blaisemath.style.Renderer;
import com.googlecode.blaisemath.style.ObjectStyler;
import com.googlecode.blaisemath.util.coordinate.CoordinateChangeEvent;
import com.googlecode.blaisemath.util.coordinate.CoordinateListener;
import com.googlecode.blaisemath.util.coordinate.CoordinateManager;
import com.googlecode.blaisemath.util.Edge;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A collection of edges backed by a common set of points.
 * 
 * @param <S> source object type
 * @param <E> edge type
 * 
 * @author elisha
 */
public class DelegatingEdgeSetGraphic<S,E extends Edge<S>,G> extends GraphicComposite<G> {

    /** The edges in the graphic. */
    protected final Map<E,DelegatingPrimitiveGraphic<E,Shape,G>> edges = Maps.newHashMap();
    /** Styler for edges */
    protected ObjectStyler<E> edgeStyler = ObjectStyler.create();
    /** Renderer for edges */
    protected Renderer<Shape,G> edgeRenderer = null;
    
    /** Point manager. Maintains objects and their locations, and enables mouse dragging. */
    protected CoordinateManager<S, Point2D> pointManager;
    /** Listener for changes to coordinates */
    private final CoordinateListener coordListener;
    
    /**
     * Initialize with default coordinate manager.
     */
    public DelegatingEdgeSetGraphic() {
        this(new CoordinateManager<S,Point2D>());
    }
    
    /** 
     * Initialize with given coordinate manager.
     * @param mgr manages source object locations
     */
    public DelegatingEdgeSetGraphic(CoordinateManager<S,Point2D> mgr) {
        coordListener = new CoordinateListener(){
            @Override
            public void coordinatesChanged(CoordinateChangeEvent evt) {
                updateEdgeGraphics(pointManager.getCoordinates(), new ArrayList<Graphic>());
            }
        };
        
        setCoordinateManager(mgr);
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="coordinate updates">

    private void updateEdgeGraphics(Map<S,Point2D> locs, List<Graphic> removeMe) {
        List<Graphic> addMe = new ArrayList<Graphic>();
        for (E edge : edges.keySet()) {
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
                }
            }
        }
        replaceGraphics(removeMe, addMe);
    }

    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //
    public CoordinateManager<S, Point2D> getCoordinateManager() {
        return pointManager;
    }

    public final void setCoordinateManager(CoordinateManager<S, Point2D> pointManager) {
        if (this.pointManager != checkNotNull(pointManager)) {
            if (this.pointManager != null) {
                this.pointManager.removeCoordinateListener(coordListener);
            }
            this.pointManager = pointManager;
            this.pointManager.addCoordinateListener(coordListener);
            updateEdgeGraphics(pointManager.getCoordinates(), new ArrayList<Graphic>());
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
     * Sets map describing graphs edges.
     * Also updates the set of objects to be the nodes within the edges
     * @param ee new edges to put
     */
    public final void setEdges(Set<? extends E> ee) {
        Set<E> addMe = new LinkedHashSet<E>();
        Set<E> removeMe = new HashSet<E>();
        for (E e : ee) {
            if (!edges.containsKey(e)) {
                addMe.add(e);
            }
        }
        for (E e : edges.keySet()) {
            if (!ee.contains(e)) {
                removeMe.add(e);
            }
        }
        if (!removeMe.isEmpty() || !addMe.isEmpty()) {
            List<Graphic> remove = new ArrayList<Graphic>();
            for (E e : removeMe) {
                remove.add(edges.remove(e));
            }
            for (E e : addMe) {
                edges.put(e, null);
            }
            updateEdgeGraphics(pointManager.getCoordinates(), remove);
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
    
    //</editor-fold>

}