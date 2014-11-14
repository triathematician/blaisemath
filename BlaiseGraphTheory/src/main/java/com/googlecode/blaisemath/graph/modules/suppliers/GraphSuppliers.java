/**
 * GraphBuilders.java
 * Created Aug 18, 2012
 */

package com.googlecode.blaisemath.graph.modules.suppliers;

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

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.SparseGraph;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 *  Provides a number of simplistic graph builders.
 * </p>
 * @author elisha
 */
public class GraphSuppliers {

    /** A completely empty graph */
    public static final Graph EMPTY_GRAPH = SparseGraph.createFromEdges(false, Collections.EMPTY_SET, Collections.EMPTY_SET);

    // utility class
    private GraphSuppliers() {
    }
    
    /** 
     * Helper class with fields for directed/undirected and number of nodes
     * @param <V> graph vertex type
     */
    public abstract static class GraphSupplierSupport<V> implements Supplier<Graph<V>> {
        protected boolean directed = false;
        protected int nodes = 1;

        public GraphSupplierSupport() {
        }
        
        public GraphSupplierSupport(boolean directed, int nodes) {
            if (nodes < 0) {
                throw new IllegalArgumentException("Graphs must have a non-negative number of nodes: " + nodes);
            }
            this.directed = directed;
            this.nodes = nodes;
        }        

        public boolean isDirected() {
            return directed;
        }

        public void setDirected(boolean directed) {
            this.directed = directed;
        }

        public int getNodes() {
            return nodes;
        }

        public void setNodes(int nodes) {
            this.nodes = nodes;
        }
    }

    /** Constructs graph with n vertices */
    public static class EmptyGraphSupplier extends GraphSupplierSupport<Integer> {
        public EmptyGraphSupplier() {
        }
        public EmptyGraphSupplier(boolean directed, int nodes) { 
            super(directed, nodes); 
        }
        @Override
        public String toString() {
            return "Empty graph supplier";
        }
        public Graph<Integer> get() {
            if (nodes < 0) {
                throw new IllegalArgumentException("Numbers must be nonnegative! n=" + nodes);
            }
            return SparseGraph.createFromArrayEdges(directed, intList(nodes), Collections.<Integer[]>emptyList());
        }
    }

    /** Constructs complete graph with n vertices */
    public static class CompleteGraphSupplier extends GraphSupplierSupport<Integer> {
        public CompleteGraphSupplier() {
        }
        public CompleteGraphSupplier(boolean directed, int nodes) { 
            super(directed, nodes); 
        }
        @Override
        public String toString() {
            return "Complete graph supplier";
        }
        public Graph<Integer> get() {
            if (nodes < 0) {
                throw new IllegalArgumentException("Numbers must be nonnegative! n=" + nodes);
            }
            List<Integer[]> edges = new ArrayList<Integer[]>();
            for (int i = 0; i < nodes; i++) {
                for (int j = i + 1; j < nodes; j++) {
                    edges.add(new Integer[]{i, j});
                    if (directed) {
                        edges.add(new Integer[]{j, i});
                    }
                }
            }
            return SparseGraph.createFromArrayEdges(directed, intList(nodes), edges);
        }
    }

    /** Constructs cycle graph with n vertices */
    public static class CycleGraphSupplier extends GraphSupplierSupport<Integer> {
        public CycleGraphSupplier() {
        }
        public CycleGraphSupplier(boolean directed, int nodes) { 
            super(directed, nodes); 
        }
        @Override
        public String toString() {
            return "Cycle graph supplier";
        }
        public Graph<Integer> get() {
            if (nodes < 0) {
                throw new IllegalArgumentException("Numbers must be nonnegative! n=" + nodes);
            }
            return SparseGraph.createFromArrayEdges(directed, intList(nodes),
                    new AbstractList<Integer[]>() {
                        @Override
                        public Integer[] get(int index) {
                            return new Integer[]{index, (index + 1) % nodes};
                        }

                        @Override
                        public int size() {
                            return nodes;
                        }
                    });
        }
    }

    /** 
     * Constructs star graph with n vertices; all vertices are connected to a central hub. 
     */
    public static class StarGraphSupplier extends GraphSupplierSupport<Integer> {
        public StarGraphSupplier() {
        }
        public StarGraphSupplier(boolean directed, int nodes) { 
            super(directed, nodes); 
            checkArgument(nodes >= 0, "Positive number of nodes required.");
        }
        @Override
        public String toString() {
            return "Star graph supplier";
        }
        public Graph<Integer> get() {
            return SparseGraph.createFromArrayEdges(directed, intList(nodes),
                    new AbstractList<Integer[]>() {
                        @Override
                        public Integer[] get(int index) {
                            return new Integer[]{0, index + 1};
                        }

                        @Override
                        public int size() {
                            return nodes == 0 ? 0 : nodes - 1;
                        }
                    });
        }
    }

    /**
     * Constructs wheel graph with n vertices.
     * All vertices are connected to a central hub, and all non-central
     * vertices connected in a cyclic fashion.
     */
    public static class WheelGraphSupplier extends GraphSupplierSupport<Integer> {
        public WheelGraphSupplier() {
        }
        public WheelGraphSupplier(boolean directed, int nodes) { 
            super(directed, nodes); 
        }
        @Override
        public String toString() {
            return "Wheel graph supplier";
        }
        public Graph<Integer> get() {
            if (nodes < 0) {
                throw new IllegalArgumentException("Numbers must be nonnegative! n=" + nodes);
            }
            ArrayList<Integer[]> edges = Lists.newArrayList();
            for (int i = 1; i < nodes; i++) {
                edges.add(new Integer[]{0, i});
            }
            for (int i = 1; i < nodes - 1; i++) {
                edges.add(new Integer[]{i, i + 1});
                if (directed) {
                    edges.add(new Integer[]{i + 1, i});
                }
            }
            edges.add(new Integer[]{nodes - 1, 1});
            if (directed) {
                edges.add(new Integer[]{1, nodes - 1});
            }
            return SparseGraph.createFromArrayEdges(directed, intList(nodes), edges);
        }
    }

    //
    // UTILITY METHODS
    //

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
