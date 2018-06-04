/**
 * BasicGeneratorParameters.java
 * Created Mar 28, 2015
 */
package com.googlecode.blaisemath.graph.mod.generators;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2018 Elisha Peterson
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


import com.google.common.base.Preconditions;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.SparseGraph;
import java.util.AbstractList;
import java.util.List;

/**
 * Parameters including whether a graph is directed, and the number of nodes.
 * 
 * @author elisha
 */
public class DefaultGeneratorParameters {
    
    protected boolean directed = false;
    protected int nodeCount = 1;

    public DefaultGeneratorParameters() {
    }

    public DefaultGeneratorParameters(boolean directed, int nodes) {
        setDirected(directed);
        setNodeCount(nodes);
    }
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">

    public final boolean isDirected() {
        return directed;
    }

    public final void setDirected(boolean directed) {
        this.directed = directed;
    }

    public final int getNodeCount() {
        return nodeCount;
    }

    public final void setNodeCount(int nodes) {
        Preconditions.checkArgument(nodes >= 0);
        this.nodeCount = nodes;
    }
    
    //</editor-fold>

    
    //
    // UTILITY METHODS
    //

    /**
     * Generate graph with given set of edges.
     * @param parm the parameters
     * @param edges edges
     * @return created graph
     */
    public static Graph<Integer> createGraphWithEdges(
            DefaultGeneratorParameters parm, Iterable<Integer[]> edges) {
        return SparseGraph.createFromArrayEdges(parm.isDirected(),
                intList(parm.getNodeCount()), edges);
    }

    /**
     * Returns abstract list of integers 0,...,n-1
     * @param n # of items in list
     * @return list
     */
    public static List<Integer> intList(final int n) {
        return new AbstractList<Integer>() {
            @Override
            public Integer get(int index) {
                return index;
            }
            @Override
            public int size() {
                return n;
            }
        };
    }
}
