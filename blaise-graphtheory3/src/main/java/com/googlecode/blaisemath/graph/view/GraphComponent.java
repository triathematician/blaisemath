package com.googlecode.blaisemath.graph.view;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2021 Elisha Peterson
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
import com.google.common.graph.Graph;
import com.googlecode.blaisemath.coordinate.CoordinateManager;
import com.googlecode.blaisemath.graph.layout.GraphLayoutManager;
import com.googlecode.blaisemath.graphics.core.DelegatingNodeLinkGraphic;
import com.googlecode.blaisemath.graphics.core.DelegatingPrimitiveGraphic;
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent;
import com.googlecode.blaisemath.graphics.swing.JGraphics;
import com.googlecode.blaisemath.graphics.swing.PanAndZoomHandler;
import com.googlecode.blaisemath.style.ObjectStyler;
import com.googlecode.blaisemath.util.swing.ContextMenuInitializer;

import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.stream.Collectors.toSet;

/**
 * Provides a view of a graph, using a {@link GraphLayoutManager} for positions/layout and a {@link VisualGraph} for appearance.
 * The layout manager supports executing long-running layout algorithms in a background thread, and the visual graph
 * shares a {@link CoordinateManager} that is used for updating locations from the layout manager. The coordinate manager is thread-safe.
 *
 * @author Elisha Peterson
 */
public class GraphComponent extends JGraphicComponent {

    private static final Logger LOG = Logger.getLogger(GraphComponent.class.getName());

    public static final String MENU_KEY_GRAPH = "graph";
    public static final String MENU_KEY_EDGE = "edge";
    public static final String MENU_KEY_NODE = "node";

    /** This mechanism is used to create the initial view graph for swing components, by setting up appropriate renderers. */
    private static final Supplier<DelegatingNodeLinkGraphic<Object, EndpointPair<Object>, Graphics2D>> SWING_GRAPH_SUPPLIER = () -> {
        DelegatingNodeLinkGraphic<Object, EndpointPair<Object>, Graphics2D> res = JGraphics.nodeLink();
        res.getNodeStyler().setStyle(VisualGraph.DEFAULT_NODE_STYLE);
        return res;
    };

    /** Manages the visual elements of the underlying graph */
    protected final VisualGraph<Graphics2D> adapter;

    /**
     * Construct with an empty graph.
     */
    public GraphComponent() {
        this(new GraphLayoutManager());
    }

    /**
     * Construct with specified graph.
     * @param graph the graph to initialize with
     */
    public GraphComponent(Graph graph) {
        this(GraphLayoutManager.create(graph));
    }

    /**
     * Construct with specified graph manager (contains graph and positions).
     * @param gm graph manager to initialize with
     */
    public GraphComponent(GraphLayoutManager gm) {
        adapter = new VisualGraph<>(gm, SWING_GRAPH_SUPPLIER);
        addGraphic(adapter.getViewGraph());
        setPreferredSize(new java.awt.Dimension(400, 400));

        setSelectionEnabled(true);
        PanAndZoomHandler.install(this);

        // turn off animation if component hierarchy changes
        addHierarchyListener(e -> {
            if (e.getChangeFlags() == HierarchyEvent.PARENT_CHANGED) {
                setLayoutTaskActive(false);
            }
        });
    }
    
    //region PROPERTIES

    /**
     * Return the adapter that contains the graph manager and the graph, responsible for handling the visual appearance.
     * @return the adapter
     */
    public VisualGraph getAdapter() {
        return adapter;
    }
    
    //endregion

    //region DELEGATING PROPERTIES

    public ObjectStyler<EndpointPair<Object>> getEdgeStyler() {
        return adapter.getEdgeStyler();
    }

    public void setEdgeStyler(ObjectStyler<EndpointPair<Object>> edgeStyler) {
        adapter.setEdgeStyler(edgeStyler);
    }

    public ObjectStyler<Object> getNodeStyler() {
        return adapter.getNodeStyler();
    }

    public void setNodeStyler(ObjectStyler<Object> nodeStyler) {
        adapter.setNodeStyler(nodeStyler);
    }

    /**
     * Return the graph manager underlying the component, responsible for handling the graph and node locations.
     * @return the manager
     */
    public GraphLayoutManager getLayoutManager() {
        return adapter.getLayoutManager();
    }

    public void setLayoutManager(GraphLayoutManager gm) {
        adapter.setLayoutManager(gm);
    }

    public Graph getGraph() {
        return adapter.getGraph();
    }

    public void setGraph(Graph graph) {
        adapter.setGraph(graph);
    }

    public boolean isLayoutTaskActive() {
        return getLayoutManager().isLayoutTaskActive();
    }

    public void setLayoutTaskActive(boolean val) {
        getLayoutManager().setLayoutTaskActive(val);
    }

    public Predicate<Object> getNodeLabelFilter() {
        return getNodeStyler().getLabelFilter();
    }

    public void setNodeLabelFilter(Predicate<Object> nodeLabelFilter) {
        Object old = getNodeStyler().getLabelFilter();
        if (old != nodeLabelFilter) {
            getNodeStyler().setLabelFilter(nodeLabelFilter);
            repaint();
        }
    }

    public Function<?, String> getNodeLabelDelegate() {
        return adapter.getNodeStyler().getLabelDelegate();
    }

    public void setNodeLabelDelegate(Function<Object, String> labeler) {
        Object old = getNodeStyler().getLabelDelegate();
        if (old != labeler) {
            getNodeStyler().setLabelDelegate(labeler);
            repaint();
        }
    }

    public Set getSelectedNodes() {
        return getSelectionModel().getSelection().stream()
                .filter(s -> s instanceof DelegatingPrimitiveGraphic)
                .map(s -> ((DelegatingPrimitiveGraphic) s).getSourceObject())
                .collect(toSet());
    }
    
    public void setSelectedNodes(Collection nodes) {
        Set newSelection = (Set) adapter.getViewGraph().getPointGraphic().getGraphics().stream()
                .filter(g -> nodes.contains(((DelegatingPrimitiveGraphic)g).getSourceObject()))
                .collect(toSet());
        selector.getSelectionModel().setSelection(newSelection);
    }

    //endregion

    /**
     * Adds context menu element to specified object.
     * @param key either "graph", "node", or "edge"
     * @param init used to initialize the context menu
     */
    public void addContextMenuInitializer(String key, ContextMenuInitializer init) {
        DelegatingNodeLinkGraphic win = adapter.getViewGraph();
        if (MENU_KEY_GRAPH.equalsIgnoreCase(key)) {
            getGraphicRoot().addContextMenuInitializer(init);
        } else if (MENU_KEY_NODE.equalsIgnoreCase(key)) {
            win.getPointGraphic().addContextMenuInitializer(init);
        } else if (MENU_KEY_EDGE.equalsIgnoreCase(key)) {
            win.getEdgeGraphic().addContextMenuInitializer(init);
        } else {
            LOG.log(Level.WARNING, "Unsupported context menu key: {0}", key);
        }
    }

    /**
     * Removes context menu element from specified object
     * @param key either "graph", "node", or "edge"
     * @param init used to initialize the context menu
     */
    public void removeContextMenuInitializer(String key, ContextMenuInitializer init) {
        DelegatingNodeLinkGraphic win = adapter.getViewGraph();
        if (MENU_KEY_GRAPH.equals(key)) {
            getGraphicRoot().removeContextMenuInitializer(init);
        } else if (MENU_KEY_NODE.equals(key)) {
            win.getPointGraphic().removeContextMenuInitializer(init);
        } else if (MENU_KEY_EDGE.equals(key)) {
            win.getEdgeGraphic().removeContextMenuInitializer(init);
        } else {
            LOG.log(Level.WARNING, "Unsupported context menu key: {0}", key);
        }
    }

}
