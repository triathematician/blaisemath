/*
 * GraphComponent.java
 * Created Jan 31, 2011
 */

package com.googlecode.blaisemath.graph.view;

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


import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.layout.GraphLayoutManager;
import com.googlecode.blaisemath.util.ContextMenuInitializer;
import com.googlecode.blaisemath.graphics.DelegatingNodeLinkGraphic;
import com.googlecode.blaisemath.graphics.PanAndZoomHandler;
import com.googlecode.blaisemath.graphics.GraphicComponent;
import com.googlecode.blaisemath.style.ObjectStyler;
import com.googlecode.blaisemath.style.PathStyle;
import com.googlecode.blaisemath.style.PointStyle;
import com.googlecode.blaisemath.util.Edge;


/**
 * Provides a view of a graph, using a {@link GraphLayoutManager} for positions/layout
 * and a {@link PlaneGraphAdapter} for appearance.
 *
 * @author elisha
 */
public class GraphComponent extends GraphicComponent {

    /** Manages the visual elements of the underlying graph */
    protected final PlaneGraphAdapter adapter;
    /** Pan and zoom control */
    protected final PanAndZoomHandler zoomControl;


    //<editor-fold defaultstate="collapsed" desc="INITIALIZATION">
    //
    // INITIALIZATION
    //

    /**
     * Construct without a graph
     */
    public GraphComponent() {
        this(new GraphLayoutManager());
    }

    /**
     * Construct with specified graph
     * @param graph the graph to initialize with
     */
    public GraphComponent(Graph graph) {
        this(new GraphLayoutManager(graph));
    }

    /**
     * Construct with specified graph manager (contains graph and positions)
     * @param gm graph manager to initialize with
     */
    public GraphComponent(GraphLayoutManager gm) {
        adapter = new PlaneGraphAdapter(gm);
        addGraphic(adapter.getViewGraph());
        setPreferredSize(new java.awt.Dimension(400, 400));
        // TODO - graph bounding box
        // enable selection
        setSelectionEnabled(true);
        // enable zoom and drag
        zoomControl = new PanAndZoomHandler(this);
        // trun off animation if component hierarchy changes
        addHierarchyListener(new HierarchyListener(){
            public void hierarchyChanged(HierarchyEvent e) {
                if (e.getChangeFlags() == HierarchyEvent.PARENT_CHANGED) {
                    setLayoutAnimating(false);
                }
            }
        });
    }

    //</editor-fold>


    //<editor-fold defaultstate="collapsed" desc="DELEGATING PROPERTIES">
    //
    // DELEGATING PROPERTIES
    //

    /**
     * Return the adapter that contains the graph manager and the graph, responsible for handling the visual appearance.
     * @return the adapter
     */
    public PlaneGraphAdapter getAdapter() {
        return adapter;
    }

    public ObjectStyler<Edge<Object>, PathStyle> getEdgeStyler() {
        return adapter.getEdgeStyler();
    }

    public void setEdgeStyler(ObjectStyler<Edge<Object>, PathStyle> edgeStyler) {
        adapter.setEdgeStyler(edgeStyler);
    }

    public ObjectStyler<Object, PointStyle> getNodeStyler() {
        return adapter.getNodeStyler();
    }

    public void setNodeStyler(ObjectStyler<Object, PointStyle> nodeStyler) {
        adapter.setNodeStyler(nodeStyler);
    }

    /**
     * Return the graph manager underlying the component, responsible for handling the graph and node locations.
     * @return the manager
     */
    public GraphLayoutManager getGraphManager() {
        return adapter.getGraphManager();
    }

    public void setGraphManager(GraphLayoutManager gm) {
        adapter.setGraphManager(gm);
    }

    public synchronized Graph getGraph() {
        return adapter.getGraphManager().getGraph();
    }

    public synchronized void setGraph(Graph graph) {
        adapter.getGraphManager().setGraph(graph);
    }

    public boolean isLayoutAnimating() {
        return getGraphManager().isLayoutAnimating();
    }

    public void setLayoutAnimating(boolean val) {
        getGraphManager().setLayoutAnimating(val);
    }

    //</editor-fold>


    /**
     * Adds context menu element to specified object
     * @param key either "graph", "node", or "link"
     * @param init used to initialize the context menu
     */
    public void addContextMenuInitializer(String key, ContextMenuInitializer init) {
        DelegatingNodeLinkGraphic win = (DelegatingNodeLinkGraphic) adapter.getViewGraph();
        if ("graph".equalsIgnoreCase(key)) {
            getGraphicRoot().addContextMenuInitializer(init);
        } else if ("node".equalsIgnoreCase(key)) {
            win.getPointGraphic().addContextMenuInitializer(init);
        } else if ("link".equalsIgnoreCase(key)) {
            win.getEdgeGraphic().addContextMenuInitializer(init);
        }
    }

    /**
     * Removes context menu element from specified object
     * @param key either "graph", "node", or "link"
     * @param init used to initialize the context menu
     */
    public void removeContextMenuInitializer(String key, ContextMenuInitializer init) {
        DelegatingNodeLinkGraphic win = (DelegatingNodeLinkGraphic) adapter.getViewGraph();
        if ("graph".equals(key)) {
            getGraphicRoot().removeContextMenuInitializer(init);
        } else if ("node".equals(key)) {
            win.getPointGraphic().removeContextMenuInitializer(init);
        } else if ("link".equals(key)) {
            win.getEdgeGraphic().removeContextMenuInitializer(init);
        }
    }




}
