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


import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.layout.GraphLayoutManager;
import com.googlecode.blaisemath.graphics.core.DelegatingNodeLinkGraphic;
import com.googlecode.blaisemath.graphics.core.DelegatingPrimitiveGraphic;
import com.googlecode.blaisemath.graphics.core.Graphic;
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent;
import com.googlecode.blaisemath.graphics.swing.JGraphics;
import com.googlecode.blaisemath.graphics.swing.PanAndZoomHandler;
import com.googlecode.blaisemath.style.ObjectStyler;
import com.googlecode.blaisemath.util.Edge;
import com.googlecode.blaisemath.util.swing.ContextMenuInitializer;
import java.awt.Graphics2D;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.util.Collection;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Provides a view of a graph, using a {@link GraphLayoutManager} for positions/layout
 * and a {@link VisualGraph} for appearance.
 *
 * @author elisha
 * 
 * @todo as a swing component, should not synchronize based on this; instead, should
 *    be not thread safe, and should be managed only within the swing dispatch thread
 */
public class GraphComponent extends JGraphicComponent {
    
    public static final String MENU_KEY_GRAPH = "graph";
    public static final String MENU_KEY_LINK = "link";
    public static final String MENU_KEY_NODE = "node";
    
    /** This mechanism is used to create the initial view graph for swing components, by setting up appropriate renderers. */
    private static final Supplier<DelegatingNodeLinkGraphic<Object,Edge<Object>,Graphics2D>> SWING_GRAPH_SUPPLIER
            = new Supplier<DelegatingNodeLinkGraphic<Object,Edge<Object>,Graphics2D>>() {
        public DelegatingNodeLinkGraphic<Object, Edge<Object>, Graphics2D> get() {
            DelegatingNodeLinkGraphic<Object, Edge<Object>, Graphics2D> res = JGraphics.nodeLink();
            res.getNodeStyler().setStyleConstant(VisualGraph.DEFAULT_NODE_STYLE);
            return res;
        }
    };

    /** Manages the visual elements of the underlying graph */
    protected final VisualGraph<Graphics2D> adapter;
    /** Pan and zoom control */
    protected final PanAndZoomHandler zoomControl;

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
        adapter = new VisualGraph<Graphics2D>(gm, SWING_GRAPH_SUPPLIER);
        addGraphic(adapter.getViewGraph());
        setPreferredSize(new java.awt.Dimension(400, 400));
        // enable selection
        setSelectionEnabled(true);
        // enable zoom and drag
        zoomControl = new PanAndZoomHandler(this);
        // turn off animation if component hierarchy changes
        addHierarchyListener(new HierarchyListener(){
            public void hierarchyChanged(HierarchyEvent e) {
                if (e.getChangeFlags() == HierarchyEvent.PARENT_CHANGED) {
                    setLayoutAnimating(false);
                }
            }
        });
    }


    //<editor-fold defaultstate="collapsed" desc="DELEGATING PROPERTIES">
    //
    // DELEGATING PROPERTIES
    //

    /**
     * Return the adapter that contains the graph manager and the graph, responsible for handling the visual appearance.
     * @return the adapter
     */
    public VisualGraph getAdapter() {
        return adapter;
    }

    public ObjectStyler<Edge<Object>> getEdgeStyler() {
        return adapter.getEdgeStyler();
    }

    public void setEdgeStyler(ObjectStyler<Edge<Object>> edgeStyler) {
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

    public boolean isLayoutAnimating() {
        return getLayoutManager().isLayoutAnimating();
    }

    public void setLayoutAnimating(boolean val) {
        getLayoutManager().setLayoutAnimating(val);
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

    public Set<String> getSelectedNodes() {
        Set<String> selectedNodes = Sets.newLinkedHashSet();
        for (DelegatingPrimitiveGraphic dpg : Iterables.filter(getSelectionModel().getSelection(), DelegatingPrimitiveGraphic.class)) {
            if (dpg.getSourceObject() instanceof String) {
                selectedNodes.add((String) dpg.getSourceObject());
            }
        }
        return selectedNodes;
    }
    
    public void setSelectedNodes(Collection<String> nodes) {
        Set<Graphic> newSelection = Sets.newHashSet();
        for (Object g : adapter.getViewGraph().getPointGraphic().getGraphics()) {
            if (nodes.contains((String) ((DelegatingPrimitiveGraphic)g).getSourceObject())) {
                newSelection.add((Graphic) g);
            }
        }
        selector.getSelectionModel().setSelection(newSelection);
    }

    //</editor-fold>


    /**
     * Adds context menu element to specified object
     * @param key either "graph", "node", or "link"
     * @param init used to initialize the context menu
     */
    public void addContextMenuInitializer(String key, ContextMenuInitializer init) {
        DelegatingNodeLinkGraphic win = (DelegatingNodeLinkGraphic) adapter.getViewGraph();
        if (MENU_KEY_GRAPH.equalsIgnoreCase(key)) {
            getGraphicRoot().addContextMenuInitializer(init);
        } else if (MENU_KEY_NODE.equalsIgnoreCase(key)) {
            win.getPointGraphic().addContextMenuInitializer(init);
        } else if (MENU_KEY_LINK.equalsIgnoreCase(key)) {
            win.getEdgeGraphic().addContextMenuInitializer(init);
        } else {
            Logger.getLogger(GraphComponent.class.getName()).log(Level.WARNING,
                    "Unsupported context menu key: {0}", key);
        }
    }

    /**
     * Removes context menu element from specified object
     * @param key either "graph", "node", or "link"
     * @param init used to initialize the context menu
     */
    public void removeContextMenuInitializer(String key, ContextMenuInitializer init) {
        DelegatingNodeLinkGraphic win = (DelegatingNodeLinkGraphic) adapter.getViewGraph();
        if (MENU_KEY_GRAPH.equals(key)) {
            getGraphicRoot().removeContextMenuInitializer(init);
        } else if (MENU_KEY_NODE.equals(key)) {
            win.getPointGraphic().removeContextMenuInitializer(init);
        } else if (MENU_KEY_LINK.equals(key)) {
            win.getEdgeGraphic().removeContextMenuInitializer(init);
        } else {
            Logger.getLogger(GraphComponent.class.getName()).log(Level.WARNING,
                    "Unsupported context menu key: {0}", key);
        }
    }


}
