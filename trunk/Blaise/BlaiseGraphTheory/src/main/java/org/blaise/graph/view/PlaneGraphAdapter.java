/*
 * PlaneGraphAdapter.java
 * Created Jan 31, 2011
 */
package org.blaise.graph.view;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
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
import org.blaise.graph.Graph;
import org.blaise.graph.layout.GraphLayoutManager;
import org.blaise.graphics.DelegatingNodeLinkGraphic;
import org.blaise.style.ObjectStyler;
import org.blaise.style.PathStyle;
import org.blaise.style.PointStyle;
import org.blaise.style.Styles;
import org.blaise.util.Edge;


/**
 * <p>
 *  Combines a {@link GraphLayoutManager} with a {@link VCustomGraph} to manage the positions of a displayed graph;
 *  also manages the visual appearance of the graph. The provided {@link VCustomGraph} may then be added
 *  to an arbitrary {@link PlanePlotComponent}.
 * </p>
 *
 * @author Elisha Peterson
 */
public class PlaneGraphAdapter {

    /** Manages graph and node locations */
    private GraphLayoutManager layoutManager;
    /** Stores the visible graph */
    private DelegatingNodeLinkGraphic viewGraph;

    /**
     * Initialize adapter with an empty graph.
     */
    public PlaneGraphAdapter() {
        this(new GraphLayoutManager());
    }

    /**
     * Construct adapter with the specified graph.
     * @param graph the graph to display
     */
    public PlaneGraphAdapter(Graph graph) {
        this(new GraphLayoutManager(graph));
    }

    /**
     * Construct adapter with the specified manager.
     * @param manager a GraphLayoutManager with the graph to display
     */
    public PlaneGraphAdapter(final GraphLayoutManager manager) {
        setGraphManager(manager);
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
                viewGraph = new DelegatingNodeLinkGraphic<Object,Edge<Object>>(layoutManager.getCoordinateManager());
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
            viewGraph.getEdgeStyler().setStyleDelegate(Functions.constant(Styles.strokeWidth(new Color(0, 128, 0, 128), .5f)));
        }
    }

    //
    // EVENT HANDLERS
    //
    
    public final PropertyChangeListener LAYOUT_LISTENER = new PropertyChangeListener(){
        public void propertyChange(PropertyChangeEvent evt) {
            initViewGraph();
        }
    };


    
    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    //
    // PROPERTIES
    //

    /**
     * Return the GraphLayoutManager with position data
     * @return GraphLayoutManager
     */
    public GraphLayoutManager getGraphManager() {
        return layoutManager;
    }

    public final void setGraphManager(GraphLayoutManager manager) {
        if (this.layoutManager != manager) {
            if (this.layoutManager != null) {
                this.layoutManager.removePropertyChangeListener("graph", LAYOUT_LISTENER);
            }
            this.layoutManager = manager;
            initViewGraph();
            this.layoutManager.addPropertyChangeListener("graph", LAYOUT_LISTENER);
        }
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
    public ObjectStyler<Object, PointStyle> getNodeStyler() {
        return viewGraph.getNodeStyler();
    }

    /**
     * Return node styler
     * @param styler
     */
    public void setNodeStyler(ObjectStyler<Object, PointStyle> styler) {
        viewGraph.setNodeStyler(styler);
    }

    /**
     * Return edge styler
     * @return edge styler
     */
    public ObjectStyler<Edge<Object>, PathStyle> getEdgeStyler() {
        return viewGraph.getEdgeStyler();
    }

    /**
     * Sets edge styler
     * @param styler edge styler
     */
    public void setEdgeStyler(ObjectStyler<Edge<Object>, PathStyle> styler) {
        viewGraph.setEdgeStyler(styler);
    }

    //</editor-fold>

}
