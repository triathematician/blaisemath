/**
 * DelegatingPointSetGraphic.java
 * Created Jan 22, 2011
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

import java.awt.geom.Point2D;
import java.util.Map;
import java.util.Set;
import com.googlecode.blaisemath.style.ObjectStyler;
import com.googlecode.blaisemath.style.Renderer;
import com.googlecode.blaisemath.util.Edge;
import com.googlecode.blaisemath.util.coordinate.CoordinateManager;
import com.googlecode.blaisemath.util.geom.LabeledPoint;
import java.awt.Shape;
import javax.annotation.Nullable;

/**
 * A graph with fully-customizable points, edges, and tooltips. The styles and
 * point values are computed at runtime. Edges are maintained as a set of {@link Edge}s.
 *
 * @param <S> source object type
 * @param <E> edge type
 * 
 * @author Elisha Peterson
 */
public class DelegatingNodeLinkGraphic<S,E extends Edge<S>,G> extends GraphicComposite<G> {

    /** Point graphics */
    private final DelegatingPointSetGraphic<S,G> pointGraphics;
    /** Edge graphics */
    private final DelegatingEdgeSetGraphic<S,E,G> edgeGraphics;

    /**
     * Construct with no points
     */
    public DelegatingNodeLinkGraphic() {
        this(new CoordinateManager<S, Point2D>(), null, null, null);
    }
    
    /**
     * Construct with no points
     * @param nodeRenderer how nodes will be rendered
     * @param labelRenderer how node labels will be rendered
     * @param edgeRenderer how edges will be rendered
     */
    public DelegatingNodeLinkGraphic(@Nullable Renderer<Point2D,G> nodeRenderer,
            @Nullable Renderer<LabeledPoint, G> labelRenderer,
            @Nullable Renderer<Shape, G> edgeRenderer) {
        this(new CoordinateManager<S, Point2D>(), nodeRenderer, labelRenderer, edgeRenderer);
    }
    
    /**
     * Construct with specified coordinate manager
     * @param crdManager in charge of node locations
     * @param nodeRenderer draws the nodes
     * @param labelRenderer
     * @param edgeRenderer
     */
    public DelegatingNodeLinkGraphic(CoordinateManager<S,Point2D> crdManager,
            @Nullable Renderer<Point2D,G> nodeRenderer,
            @Nullable Renderer<LabeledPoint, G> labelRenderer,
            @Nullable Renderer<Shape, G> edgeRenderer) {
        pointGraphics = new DelegatingPointSetGraphic<S,G>(crdManager, nodeRenderer, labelRenderer);
        edgeGraphics = new DelegatingEdgeSetGraphic<S,E,G>(crdManager, edgeRenderer);
        addGraphic(edgeGraphics);
        addGraphic(pointGraphics);
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //

    public DelegatingPointSetGraphic<S,G> getPointGraphic() {
        return pointGraphics;
    }

    public DelegatingEdgeSetGraphic<S,E,G> getEdgeGraphic() {
        return edgeGraphics;
    }

    public CoordinateManager<S, Point2D> getCoordinateManager() {
        return pointGraphics.getCoordinateManager();
    }

    public void setCoordinateManager(CoordinateManager ptMgr) {
        pointGraphics.setCoordinateManager(ptMgr);
        edgeGraphics.setCoordinateManager(ptMgr);
    }

    public Set<? extends S> getNodeSet() {
        return pointGraphics.getObjects();
    }
    
    public Map<S, Point2D> getNodeLocations() {
        return pointGraphics.getCoordinateManager().getCoordinates();
    }

    public void setNodeLocations(Map<S, Point2D> pts) {
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

    public Renderer<LabeledPoint, G> getLabelRenderer() {
        return pointGraphics.getLabelRenderer();
    }

    public void setLabelRenderer(Renderer<LabeledPoint, G> renderer) {
        pointGraphics.setLabelRenderer(renderer);
    }

    public boolean isDragEnabled() {
        return pointGraphics.isDragEnabled();
    }

    public void setDragEnabled(boolean val) {
        pointGraphics.setDragEnabled(val);
    }

    public Set<? extends E> getEdgeSet() {
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
    
    //</editor-fold>

}
