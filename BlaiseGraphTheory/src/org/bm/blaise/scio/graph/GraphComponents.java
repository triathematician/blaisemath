/*
 * GraphComponents.java
 * Created Nov 4, 2011
 */
package org.bm.blaise.scio.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Provides additional knowledge about the components of a graph, which may be
 * useful in a variety of circumstances. Since this information may or may not be
 * easily obtained from the basic representation of the graph, this class provides
 * a decoupled cache and access to this info.
 *
 * @author elisha
 */
public class GraphComponents<V> {

    /** What this class is modifying/describing. */
    private final Graph<V> graph;
    /** The connected components of the graph. */
    private final Collection<Set<V>> components;
    /** Stored components as graphs. */
    private final List<Graph<V>> componentGraphs;

    /**
     * Construct components for specified graph.
     * @param source graph
     * @param components the graph's components
     */
    public GraphComponents(Graph<V> graph, Collection<Set<V>> components) {
        this.graph = graph;
        this.components = components;
        if (components.size() == 1)
            componentGraphs = Arrays.asList(graph);
        else {
            this.componentGraphs = new ArrayList<Graph<V>>();
            for (Set<V> compt : components)
                componentGraphs.add(GraphUtils.copyGraph(new Subgraph(graph, compt)));
        }
    }

    /**
     * Return source graph
     * @return graph
     */
    public Graph<V> getGraph() {
        return graph;
    }

    /**
     * The connected components of the graph, copied into new subgraph representations.
     * @return subcomponents
     */
    public List<Graph<V>> getComponentGraphs() {
        return componentGraphs;
    }

    /**
     * The graph's connected components.
     * @return connected components
     */
    public Collection<Set<V>> getComponents() {
        return components;
    }

    /**
     * Returns the number of components in the graph.
     * @return number of components
     */
    public int getComponentCount() {
        return components.size();
    }


    /**
     * The component of a particular node.
     * @param x a node
     * @return the component of a node
     */
    public Set<V> getComponentOf(V x) {
        for (Set<V> compt : components)
            if (compt.contains(x))
                return compt;
        return null;
    }

}
