/*
 * WattsStrogatzGraphSupplier.java
 * Created Aug 6, 2010
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.modules.suppliers.GraphSuppliers.GraphSupplierSupport;
import com.googlecode.blaisemath.graph.SparseGraph;
import java.util.Random;

/**
 * Provides methods for generating a Watts-Strogatz Random Graph
 *
 * @author Elisha Peterson
 */
public final class WattsStrogatzGraphSupplier extends GraphSupplierSupport<Integer> {

    private static Random RANDOM = new Random();
    
    private int deg = 4;
    private float rewire = .5f;

    public WattsStrogatzGraphSupplier() {
    }

    public WattsStrogatzGraphSupplier(boolean directed, int nodes, int deg, float rewiring) {
        super(directed, nodes);
        if (deg < 0 || deg > nodes - 1) {
            throw new IllegalArgumentException("Degree outside of range [0, " + (nodes - 1) + "]");
        }
        if (rewiring < 0 || rewiring > 1) {
            throw new IllegalArgumentException("Invalid rewiring parameter = " + rewiring + " (should be between 0 and 1)");
        }
        if (deg % 2 != 0) {
            Logger.getLogger(WattsStrogatzGraphSupplier.class.getName()).log(Level.WARNING, 
                    "Degree must be an even integer: changing from {0} to {1}", new Object[]{deg, deg - 1});
            this.deg = deg-1;
        } else {
            this.deg = deg;
        }
        this.rewire = rewiring;
    }

    @Override
    public String toString() {
        return "WattsStrogatzGraphSupplier{" + "deg=" + deg + ", rewire=" + rewire + '}';
    }

    public WattsStrogatzGraphSupplier randomGenerator(Random random) {
        this.RANDOM = random;
        return this;
    }

    public int getInitialDegree() {
        return deg;
    }

    public void setInitialDegree(int deg) {
        this.deg = deg;
    }

    public float getRewiringProbability() {
        return rewire;
    }

    public void setRewiringProbability(float rewire) {
        this.rewire = rewire;
    }

    public Graph<Integer> get() {
        List<Integer[]> edges = new ArrayList<Integer[]>();
        for (int i = 0; i < nodes; i++) {
            for (int off = 1; off <= (deg / 2); off++) {
                edges.add(new Integer[]{i, (i + off) % nodes});
            }
        }
        // could stop here for a regular ring lattice graph

        // generate list of edges to rewire
        for (Integer[] e : edges) {
            if (RANDOM.nextDouble() < rewire) {
                randomlyRewire(edges, e, nodes);
            }
        }

        return SparseGraph.createFromArrayEdges(false, GraphSuppliers.intList(nodes), edges);
    }

    /**
     * Randomly rewires the specified edge, by randomly moving one of the edge's
     * endpoints, provided the resulting edge does not already exist in the set.
     *
     * @param edges current list of edges
     * @param e the edge to rewire
     * @param n total # of vertices
     * @return new edge.
     */
    private static void randomlyRewire(List<Integer[]> edges, Integer[] e, int n) {
        Integer[] potential = new Integer[]{e[0], e[1]};
        Set<Integer[]> edgeTree = new TreeSet<Integer[]>(EdgeCountGraphSupplier.PAIR_COMPARE_UNDIRECTED);
        edgeTree.addAll(edges);
        while (edgeTree.contains(potential)) {
            if (RANDOM.nextBoolean()) {
                potential = new Integer[]{e[0], randomNot(e[0], n)};
            } else {
                potential = new Integer[]{randomNot(e[1], n), e[1]};
            }
        }
        e[0] = potential[0];
        e[1] = potential[1];
    }

    /**
     * @returns a random value between 0 and n-1, not including exclude
     */
    private static int randomNot(int exclude, int n) {
        int result;
        do {
            result = RANDOM.nextInt(n);
        } while (result == exclude);
        return result;
    }
}
