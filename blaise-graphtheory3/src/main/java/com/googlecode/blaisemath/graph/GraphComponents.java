/*
 * GraphComponents.java
 * Created Nov 4, 2011
 */
package com.googlecode.blaisemath.graph;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2019 Elisha Peterson
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

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import javax.annotation.concurrent.Immutable;

/**
 * Provides additional knowledge about the components of a graph, which may be
 * useful in a variety of circumstances. Since this information may or may not be
 * easily obtained from the basic representation of the graph, this class provides
 * a decoupled cache and access to this info.
 *
 * @author elisha
 * 
 * @param <N> node type
 */
@Immutable
public final class GraphComponents<N> {

    /** What this class is modifying/describing. */
    private final Graph<N> graph;
    /** The connected components of the graph. */
    private final Collection<Set<N>> components;
    /** Stored components as graphs. */
    private final Set<Graph<N>> componentGraphs;

    /**
     * Construct components for specified graph.
     * @param graph graph
     * @param components the graph's components
     */
    public GraphComponents(Graph<N> graph, Collection<Set<N>> components) {
        this.graph = graph;
        this.components = components;
        if (components.size() == 1) {
            componentGraphs = Collections.singleton(graph);
        } else {
            this.componentGraphs = Sets.newHashSet();
            for (Set<N> compt : components) {
                componentGraphs.add(GraphUtils.copySubgraph(graph, compt));
            }
        }
    }

    /**
     * Return source graph
     * @return graph
     */
    public Graph<N> getGraph() {
        return graph;
    }

    /**
     * The connected components of the graph, copied into new subgraph representations.
     * @return subcomponents
     */
    public Set<Graph<N>> getComponentGraphs() {
        return Collections.unmodifiableSet(componentGraphs);
    }

    /**
     * The graph's connected components.
     * @return connected components
     */
    public Collection<Set<N>> getComponents() {
        return Collections.unmodifiableCollection(components);
    }

    /**
     * Returns the number of components in the graph.
     * @return number of components
     */
    public int getComponentCount() {
        return components.size();
    }

}
