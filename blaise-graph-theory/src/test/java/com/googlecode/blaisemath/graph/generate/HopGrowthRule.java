package com.googlecode.blaisemath.graph.generate;

/*
 * #%L
 * BlaiseGraphTheory
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

import com.google.common.annotations.Beta;
import com.google.common.graph.Graph;
import com.googlecode.blaisemath.graph.GraphUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Expands subset of a graph by adding connected nodes, looking out a specified number of hops.
 *
 * @author Elisha Peterson
 */
@Beta
@SuppressWarnings({"SameParameterValue", "UnstableApiUsage", "unused"})
public class HopGrowthRule implements GraphGrowthRule {

    private int n;
    private boolean directed;

    public HopGrowthRule() {
        this(2);
    }

    private HopGrowthRule(int n) {
        this.n = n;
    }

    @Override
    public String getName() {
        return n + "-Hop";
    }

    //region PROPERTIES

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = Math.max(0, n);
    }

    public boolean isDirected() {
        return directed;
    }

    public void setDirected(boolean directed) {
        this.directed = directed;
    }

    //endregion

    @Override
    public <N> Set<N> grow(Graph<N> graph, Set<N> seed) {
        return grow(directed || !graph.isDirected() ? graph : GraphUtils.copyUndirected(graph), seed, n);
    }

    /**
     * Grows the seed set by n hops.
     * @param <N> node type
     * @param graph graph
     * @param seed seed nodes
     * @param n # of steps to grow
     * @return nodes in grown set
     */
    private static <N> Set<N> grow(Graph<N> graph, Set<N> seed, int n) {
        if (n == 0) {
            return seed;
        }
        Set<N> grown = grow1(graph, seed);
        if (grown.containsAll(seed) && seed.containsAll(grown)) {
            return seed;
        } else {
            return grow(graph, grown, n - 1);
        }
    }

    /**
     * Grows the seed set by 1 hop.
     * @param graph graph
     * @param seed seed nodes
     * @return nodes in grown set
     */
    private static <N> Set<N> grow1(Graph<N> graph, Set<N> seed) {
        Set<N> result = new HashSet<>(seed);
        seed.forEach(o -> result.addAll(graph.adjacentNodes(o)));
        return result;
    }
    
}
