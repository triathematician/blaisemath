/*
 * PlaneGraphAdapter.java
 * Created Jan 31, 2011
 */
package com.googlecode.blaisemath.graph.view;

/*
 * #%L
 * BlaiseGraphTheory
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

import com.google.common.base.Functions;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.layout.GraphLayoutManager;
import com.googlecode.blaisemath.graphics.core.DelegatingNodeLinkGraphic;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.ObjectStyler;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.util.Edge;


/**
 * <p>
 *  Combines a {@link GraphLayoutManager} and a {@link DelegatingNodeLinkGraphic}
 *  to manage a graph and its node locations.
 * </p>
 *
 * @author Elisha Peterson
 */
public class VisualGraph<G> {

    /** Default graph edge style */
    public static final AttributeSet DEFAULT_EDGE_STYLE = Styles.strokeWidth(new Color(0, 128, 0, 128), .5f);
    
    /** Stores the visible graph */
    private DelegatingNodeLinkGraphic<Object,Edge<Object>,G> viewGraph;
    
    /** Manages graph and node locations */
    private GraphLayoutManager layoutManager;
    /** Listens for changes from the layout */
    public final PropertyChangeListener layoutListener;

    /**
     * Initialize adapter with an empty graph.
     */
    public VisualGraph() {
        this(new GraphLayoutManager());
    }

    /**
     * Construct adapter with the specified graph.
     * @param graph the graph to display
     */
    public VisualGraph(Graph graph) {
        this(new GraphLayoutManager(graph));
    }

    /**
     * Construct adapter with the specified manager.
     * @param manager a GraphLayoutManager with the graph to display
     */
    public VisualGraph(final GraphLayoutManager manager) {
        layoutListener = new PropertyChangeListener(){
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
        synchronized(layoutManager) {
            if (viewGraph == null) {
                // initialize the view graph with the same coordinate manager as the layout manager
                //   this ensures that layout changes will propagate to the view
                viewGraph = new DelegatingNodeLinkGraphic<Object,Edge<Object>,G>(layoutManager.getCoordinateManager());
            } else {
                viewGraph.setCoordinateManager(layoutManager.getCoordinateManager());
            }
            viewGraph.setEdgeSet(layoutManager.getGraph().edges());
        }
        if (viewGraph.getNodeStyler().getLabelDelegate() == null) {
            viewGraph.getNodeStyler().setLabelDelegate(Functions.toStringFunction());
        }
        if (viewGraph.getNodeStyler().getTipDelegate() == null) {
            viewGraph.getNodeStyler().setTipDelegate(Functions.toStringFunction());
        }
        if (viewGraph.getEdgeStyler().getStyleDelegate() == null) {
            viewGraph.getEdgeStyler().setStyleDelegate(Functions.constant(DEFAULT_EDGE_STYLE));
        }
    }

    //
    // EVENT HANDLERS
    //


    
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
                this.layoutManager.removePropertyChangeListener(GraphLayoutManager.PROP_GRAPH, layoutListener);
            }
            this.layoutManager = manager;
            initViewGraph();
            this.layoutManager.addPropertyChangeListener(GraphLayoutManager.PROP_GRAPH, layoutListener);
        }
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
     * @param styler
     */
    public <N> void setNodeStyler(ObjectStyler<Object> styler) {
        viewGraph.setNodeStyler(styler);
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

    //</editor-fold>

}
