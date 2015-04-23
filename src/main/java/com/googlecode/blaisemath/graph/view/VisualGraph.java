/*
 * VisualGraph.java
 * Created Jan 31, 2011
 */
package com.googlecode.blaisemath.graph.view;

/*
 * #%L
 * BlaiseGraphTheory
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

import com.google.common.base.Functions;
import com.google.common.base.Supplier;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.GraphLayoutManager;
import com.googlecode.blaisemath.graph.mod.layout.SpringLayout;
import com.googlecode.blaisemath.graphics.core.DelegatingNodeLinkGraphic;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.ObjectStyler;
import com.googlecode.blaisemath.style.Renderer;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.util.AnchoredText;
import com.googlecode.blaisemath.util.Edge;
import com.googlecode.blaisemath.util.coordinate.CoordinateManager;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.annotation.Nullable;


/**
 * <p>
 *  Combines a {@link GraphLayoutManager} and a {@link DelegatingNodeLinkGraphic}
 *  to manage a graph and its node locations. The graph is maintained by the manager,
 *  and the visual elements by the graphic.
 * </p>
 *
 * @param <G> graphics canvas type
 * @author Elisha Peterson
 */
public class VisualGraph<G> {

    /** Default graph node style */
    public static final AttributeSet DEFAULT_NODE_STYLE = Styles.fillStroke(
            new Color(0, 0, 128, 128), new Color(0, 0, 128, 192), .5f)
            .and(Styles.MARKER_RADIUS, 3f);
    /** Default graph edge style */
    public static final AttributeSet DEFAULT_EDGE_STYLE = Styles.strokeWidth(
            new Color(0, 128, 0, 128), 1f);
    
    /** Responsible for instantiating the view graph */
    @Nullable
    private final Supplier<DelegatingNodeLinkGraphic<Object,Edge<Object>,G>> viewGraphSupplier;
    /** Stores the visible graph */
    @Nullable
    private DelegatingNodeLinkGraphic<Object,Edge<Object>,G> viewGraph;
    
    /** Manages graph and node locations */
    private GraphLayoutManager layoutManager;
    /** Listens for changes from the layout */
    public final PropertyChangeListener layoutListener;

    /**
     * Construct adapter with the specified graph.
     * @param graph the graph to display
     */
    public VisualGraph(Graph graph) {
        this(new GraphLayoutManager(graph, new SpringLayout()), null);
    }

    /**
     * Construct adapter with the specified manager.
     * @param manager a GraphLayoutManager with the graph to display
     * @param graphicSupplier optional, provides a way to override default creation of the view graph
     */
    public VisualGraph(GraphLayoutManager manager,
            @Nullable Supplier<DelegatingNodeLinkGraphic<Object,Edge<Object>,G>> graphicSupplier) {
        this.viewGraphSupplier = graphicSupplier;
        layoutListener = new PropertyChangeListener(){
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                initViewGraph();
            }
        };
        setLayoutManager(manager);
    }

    /**
     * Initializes view graph for the graph in the graph manager. This includes
     * setting up any styling appropriate for the graph, as well as updating
     * the nodes and edges in the view graph.
     */
    protected final void initViewGraph() {
        if (viewGraph == null) {
            if (viewGraphSupplier != null) {
                viewGraph = viewGraphSupplier.get();
            } else {
                viewGraph = new DelegatingNodeLinkGraphic<Object,Edge<Object>,G>(
                        layoutManager.getCoordinateManager(), null, null, null);
                viewGraph.getNodeStyler().setStyleConstant(DEFAULT_NODE_STYLE);
            }

            // set up default styles, in case the graph isn't visible by default
            if (viewGraph.getNodeStyler().getStyleDelegate() == null) {
                viewGraph.getNodeStyler().setStyleConstant(DEFAULT_NODE_STYLE);
            }
            if (viewGraph.getNodeStyler().getLabelDelegate() == null) {
                viewGraph.getNodeStyler().setLabelDelegate(Functions.toStringFunction());
            }
            if (viewGraph.getNodeStyler().getTipDelegate() == null) {
                viewGraph.getNodeStyler().setTipDelegate(Functions.toStringFunction());
            }
            if (viewGraph.getEdgeStyler().getStyleDelegate() == null) {
                viewGraph.getEdgeStyler().setStyleConstant(DEFAULT_EDGE_STYLE);
            }
        } else {
            viewGraph.setCoordinateManager(layoutManager.getCoordinateManager());
        }
        viewGraph.setEdgeSet(layoutManager.getGraph().edges());
    }

    
    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    //
    // PROPERTIES
    //

    /**
     * Return the GraphLayoutManager with position data
     * @return GraphLayoutManager
     */
    public GraphLayoutManager getLayoutManager() {
        return layoutManager;
    }

    public final void setLayoutManager(GraphLayoutManager manager) {
        if (this.layoutManager != manager) {
            if (this.layoutManager != null) {
                this.layoutManager.removePropertyChangeListener(GraphLayoutManager.GRAPH_PROP, layoutListener);
            }
            this.layoutManager = manager;
            initViewGraph();
            this.layoutManager.addPropertyChangeListener(GraphLayoutManager.GRAPH_PROP, layoutListener);
        }
    }

    public CoordinateManager getCoordinateManager() {
        return layoutManager.getCoordinateManager();
    }

    /**
     * Return the graph instance being visualized.
     * @return the graph
     */
    public Graph getGraph() {
        return layoutManager.getGraph();
    }

    /**
     * Set the graph instance being visualized.
     * @param g the graph
     */
    public void setGraph(Graph g) {
        layoutManager.setGraph(g);
    }

    /**
     * Return the VPointGraph for display
     * @return the VPointGraph
     */
    public DelegatingNodeLinkGraphic getViewGraph() {
        return viewGraph;
    }

    /**
     * Return node styler
     * @return styler
     */
    public ObjectStyler<Object> getNodeStyler() {
        return viewGraph.getNodeStyler();
    }

    /**
     * Return node styler
     * @param styler object styler for nodes
     */
    public void setNodeStyler(ObjectStyler<Object> styler) {
        viewGraph.setNodeStyler(styler);
    }

    public Renderer<Point2D, G> getNodeRenderer() {
        return viewGraph.getNodeRenderer();
    }

    public void setNodeRenderer(Renderer<Point2D, G> renderer) {
        viewGraph.setNodeRenderer(renderer);
    }

    public Renderer<AnchoredText, G> getLabelRenderer() {
        return viewGraph.getLabelRenderer();
    }
    
    /**
     * Return edge styler
     * @return edge styler
     */
    public ObjectStyler<Edge<Object>> getEdgeStyler() {
        return viewGraph.getEdgeStyler();
    }

    /**
     * Sets edge styler
     * @param styler edge styler
     */
    public void setEdgeStyler(ObjectStyler<Edge<Object>> styler) {
        viewGraph.setEdgeStyler(styler);
    }

    public Renderer<Shape, G> getEdgeRenderer() {
        return viewGraph.getEdgeRenderer();
    }

    public final void setEdgeRenderer(Renderer<Shape, G> renderer) {
        viewGraph.setEdgeRenderer(renderer);
    }

    //</editor-fold>

}
