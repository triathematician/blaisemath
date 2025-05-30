package com.googlecode.blaisemath.graphics.impl;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2025 Elisha Peterson
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

import com.google.common.graph.EndpointPair;
import com.googlecode.blaisemath.graphics.GraphicComposite;
import com.googlecode.blaisemath.graphics.Renderer;
import com.googlecode.blaisemath.style.ObjectStyler;
import com.googlecode.blaisemath.coordinate.CoordinateManager;
import com.googlecode.blaisemath.primitive.AnchoredText;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.Map;
import java.util.Set;

/**
 * A graph with fully-customizable nodes, edges, and tooltips. The styles and
 * point values are computed at runtime. Edges are maintained as a set of {@link EndpointPair}s.
 *
 * @param <S> source object type
 * @param <E> edge type
 * @param <G> graphics canvas type
 * 
 * @author Elisha Peterson
 */
public class DelegatingNodeLinkGraphic<S,E extends EndpointPair<S>,G> extends GraphicComposite<G> {
    
    private static final int DEFAULT_NODE_CACHE_SIZE = 20000;

    /** Point graphics */
    private final DelegatingPointSetGraphic<S,G> pointGraphics;
    /** Edge graphics */
    private final DelegatingEdgeSetGraphic<S,E,G> edgeGraphics;

    //region CONSTRUCTORS

    /**
     * Construct with no points and default renderers.
     */
    public DelegatingNodeLinkGraphic() {
        this(null, null, null);
    }
    
    /**
     * Construct with no points.
     * @param nodeRenderer how nodes will be rendered
     * @param labelRenderer how node labels will be rendered
     * @param edgeRenderer how edges will be rendered
     */
    public DelegatingNodeLinkGraphic(@Nullable Renderer<Point2D,G> nodeRenderer,
            @Nullable Renderer<AnchoredText, G> labelRenderer,
            @Nullable Renderer<Shape, G> edgeRenderer) {
        this(CoordinateManager.create(DEFAULT_NODE_CACHE_SIZE), nodeRenderer, labelRenderer, edgeRenderer);
    }
    
    /**
     * Construct with specified coordinate manager.
     * @param crdManager in charge of node locations
     * @param nodeRenderer draws the nodes
     * @param labelRenderer draws labels
     * @param edgeRenderer draws edges
     */
    public DelegatingNodeLinkGraphic(CoordinateManager<S, Point2D.Double> crdManager,
            @Nullable Renderer<Point2D, G> nodeRenderer,
            @Nullable Renderer<AnchoredText, G> labelRenderer,
            @Nullable Renderer<Shape, G> edgeRenderer) {
        pointGraphics = new DelegatingPointSetGraphic<>(crdManager, nodeRenderer, labelRenderer);
        edgeGraphics = new DelegatingEdgeSetGraphic<>(crdManager, edgeRenderer);
        addGraphic(edgeGraphics);
        addGraphic(pointGraphics);
    }

    //endregion

    //region DELEGATES - POINTS

    public DelegatingPointSetGraphic<S, G> getPointGraphic() {
        return pointGraphics;
    }

    public CoordinateManager<S, Point2D.Double> getCoordinateManager() {
        return pointGraphics.getCoordinateManager();
    }

    public void setCoordinateManager(CoordinateManager<S, Point2D.Double> ptMgr) {
        pointGraphics.setCoordinateManager(ptMgr);
        edgeGraphics.setCoordinateManager(ptMgr);
    }

    public Set<S> getNodeSet() {
        return pointGraphics.getObjects();
    }
    
    public Map<S, Point2D.Double> getNodeLocations() {
        return pointGraphics.getCoordinateManager().getActiveLocationCopy();
    }

    public void setNodeLocations(Map<S, Point2D.Double> pts) {
        pointGraphics.getCoordinateManager().putAll(pts);
    }

    public ObjectStyler<S> getNodeStyler() {
        return pointGraphics.getStyler();
    }

    public void setNodeStyler(ObjectStyler<S> styler) {
        pointGraphics.setStyler(styler);
    }

    public Renderer<Point2D, G> getNodeRenderer() {
        return pointGraphics.getRenderer();
    }

    public void setNodeRenderer(Renderer<Point2D, G> renderer) {
        pointGraphics.setRenderer(renderer);
    }

    public Renderer<AnchoredText, G> getLabelRenderer() {
        return pointGraphics.getLabelRenderer();
    }

    public void setLabelRenderer(Renderer<AnchoredText, G> renderer) {
        pointGraphics.setLabelRenderer(renderer);
    }

    public boolean isDragEnabled() {
        return pointGraphics.isDragEnabled();
    }

    public void setDragEnabled(boolean val) {
        pointGraphics.setDragEnabled(val);
    }

    public boolean isPointSelectionEnabled() {
        return pointGraphics.isPointSelectionEnabled();
    }

    public void setPointSelectionEnabled(boolean val) {
        pointGraphics.setPointSelectionEnabled(val);
    }
    
    //endregion
    
    //region DELEGATES - EDGES

    public DelegatingEdgeSetGraphic<S,E,G> getEdgeGraphic() {
        return edgeGraphics;
    }

    public Set<E> getEdgeSet() {
        return edgeGraphics.getEdges();
    }

    public void setEdgeSet(Set<? extends E> edges) {
        this.edgeGraphics.setEdges(edges);
    }

    public ObjectStyler<E> getEdgeStyler() {
        return edgeGraphics.getEdgeStyler();
    }

    public void setEdgeStyler(ObjectStyler<E> styler) {
        edgeGraphics.setEdgeStyler(styler);
    }

    public Renderer<Shape, G> getEdgeRenderer() {
        return edgeGraphics.getEdgeRenderer();
    }

    public final void setEdgeRenderer(Renderer<Shape, G> renderer) {
        edgeGraphics.setEdgeRenderer(renderer);
    }
    
    //endregion

}
