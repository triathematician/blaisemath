/*
 * DegreeDistributionGraphSupplier.java
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

import com.google.common.base.Supplier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.SparseGraph;

/**
 * Provides a random-graph model based on a degree sequence. If the
     * graph is directed, the degree sequence is for the outdegrees. If the
     * graph is undirected, the degree sequence is as usual (and must sum to an
     * even number).
 *
 * @author elisha
 */
public final class DegreeDistributionGraphSupplier implements Supplier<Graph<Integer>> {

    private boolean directed = false;
    private int[] degSequence;

    public DegreeDistributionGraphSupplier() {
    }

    /**
     * Returns graph with random number of connections between vertices. If the
     * graph is directed, the degree sequence is for the outdegrees. If the
     * graph is undirected, the degree sequence is as usual (and must sum to an
     * even number).
     *
     * @param directed whether graph is directed
     * @param degs degree sequence, where deg[i] is the # of nodes of degree i
     *    (should sum to an even number)
     */
    public DegreeDistributionGraphSupplier(boolean directed, int[] degs) {
        setDirected(directed);
        setDegreeSequence(degs);
    }

    public boolean isDirected() {
        return directed;
    }

    public void setDirected(boolean directed) {
        this.directed = directed;
    }

    public int[] getDegreeSequence() {
        return degSequence;
    }

    public void setDegreeSequence(int[] degs) {
        this.degSequence = Arrays.copyOf(degs, degs.length);
    }



    public Graph<Integer> get() {
        return directed ? getDirectedInstance(degSequence) : getUndirectedInstance(degSequence);
    }

    /**
     * Generate a random (directed) graph with specified outdegrees.
     * @param deg the outdegree distribution of the graph
     * @return directed instance of this type of random graph
     */
    public static Graph<Integer> getDirectedInstance(int[] deg) {
        int n = sum(deg);
        if (deg.length > n) {
            throw new IllegalArgumentException("Maximum degree of sequence " + Arrays.toString(deg) + " is too large!");
        }
        Set<Integer[]> edges = new TreeSet<Integer[]>(EdgeCountGraphSupplier.PAIR_COMPARE);
        int i = 0;
        for (int iDeg = 0; iDeg < deg.length; iDeg++) {
            for (int nDegI = 0; nDegI < deg[iDeg]; nDegI++) {
                // each iteration here is a separate vertex of degree iDeg
                // need to generate this many edges at random
                int[] subset = randomSubset(n, iDeg, i);
                for (int i2 : subset) {
                    edges.add(new Integer[]{i, i2});
                }
                i++;
            }
        }
        return SparseGraph.createFromArrayEdges(true, GraphSuppliers.intList(n), edges);
    }

    /**
     * @param deg a specified degree sequence
     * @return undirected instance of this type of random graph; not guaranteed
     * to have the actual degree sum
     * @throws IllegalArgumentException if the degree sequence is not good (i.e.
     * sums to an odd total degree, or the maximum degree is too large)
     */
    public static Graph<Integer> getUndirectedInstance(int[] deg) {
        int n = sum(deg);
        if (deg.length > n) {
            throw new IllegalArgumentException("Maximum degree of sequence " + Arrays.toString(deg) + " is too large!");
        }
        int degSum = 0;
        for (int i = 0; i < deg.length; i++) {
            degSum += i * deg[i];
        }
        if (degSum % 2 != 0) {
            throw new IllegalArgumentException("Degree sequence " + Arrays.toString(deg) + " has odd total degree!");
        }

        // stores the mapping of nodes to desired degrees
        Map<Integer, Integer> vxLeft = new TreeMap<Integer, Integer>();
        int i = 0;
        for (int iDeg = 1; iDeg < deg.length; iDeg++) // ignore the degree 0 vertices
        {
            for (int nDegI = 0; nDegI < deg[iDeg]; nDegI++) {
                vxLeft.put(i++, iDeg);
            }
        }

        // stores the edges in the resulting graph
        Set<Integer[]> edges = new TreeSet<Integer[]>(EdgeCountGraphSupplier.PAIR_COMPARE_UNDIRECTED);

        while (vxLeft.size() > 1) {
            Set<Integer> vv = vxLeft.keySet();
            Integer[] edge = new Integer[]{0, 0};
            while (edge[0] == edge[1]) {
                edge = new Integer[]{random(vv), random(vv)};
            }

            int attempts = 1;

            // attempt to find new edge at random
            while ((edges.contains(edge) || edge[0] == edge[1]) && attempts < 20) {
                edge = new Integer[]{random(vv), random(vv)};
                attempts++;
            }

            // if it takes too long, brute-force check to ensure edges are not there
            if (edges.contains(edge) || edge[0] == edge[1]) {
                Set<Integer[]> edgesLeft = new TreeSet<Integer[]>(EdgeCountGraphSupplier.PAIR_COMPARE_UNDIRECTED);
                for (Integer i1 : vv) {
                    for (Integer i2 : vv) {
                        if (i1 != i2) {
                            Integer[] e = new Integer[]{i1, i2};
                            if (vxLeft.containsKey(i1) && vxLeft.containsKey(i2) && !edges.contains(e)) {
                                edgesLeft.add(e);
                            }
                        }
                    }
                }
                if (edgesLeft.isEmpty()) {
                    break;
                } else {
                    edge = random(edgesLeft);
                }
            }

            if (edges.contains(edge)) {
                throw new IllegalStateException();
            } else {
                edges.add(edge);
                if (vxLeft.get(edge[0]) == 1) {
                    vxLeft.remove(edge[0]);
                } else {
                    vxLeft.put(edge[0], vxLeft.get(edge[0]) - 1);
                }
                if (vxLeft.get(edge[1]) == 1) {
                    vxLeft.remove(edge[1]);
                } else {
                    vxLeft.put(edge[1], vxLeft.get(edge[1]) - 1);
                }
            }
        }
        if (vxLeft.size() > 0) {
            Logger.getLogger(DegreeDistributionGraphSupplier.class.getName()).log(Level.WARNING, 
                    "Unable to find edges for all vertices. Remaining list=" + vxLeft);
        }
        return SparseGraph.createFromArrayEdges(false, GraphSuppliers.intList(n), edges);
    }

    /**
     * @return random value in given set
     */
    private static <V> V random(Set<V> set) {
        List<V> list = new ArrayList<V>(set);
        int n = list.size();
        int i = (int) Math.floor(n * Math.random());
        return list.get(i);
    }

    /**
     * Generate a random subset of the integers 0,...,n-1, possibly with the
     * exclusion of a specific value.
     *
     * @param n the overall set size
     * @param k the subset size
     * @param omit an integer value to omit from the sequence; if outside the
     * range 0,...,n-1, it is ignored
     * @return random subset of integers 0,...,n-1 of given size
     * @throw IllegalArgumentException if k is not in the range 0,...,n (if omit
     * is in the sequence), or the range 0,...,n-1 (if omit is not in the
     * sequence)
     */
    private static int[] randomSubset(int n, int k, int omit) {
        if (k < 0 || k > n || (k == n && omit >= 0 && omit <= n - 1)) {
            throw new IllegalArgumentException("Cannot construct subset of size " + k + " from " + n + " values omitting " + omit);
        }
        int[] result = new int[k];
        Set<Integer> left = new TreeSet<Integer>();
        for (int i = 0; i < n; i++) {
            left.add(i);
        }
        left.remove(omit); // will be ignored if omit is outside of range

        for (int i = 0; i < k; i++) {
            Integer value = random(left);
            result[i] = value;
            left.remove(value);
        }

        return result;
    }

    /**
     * @return sum of array
     */
    private static int sum(int[] arr) {
        int sum = 0;
        for (int i : arr) {
            sum += i;
        }
        return sum;
    }
}
