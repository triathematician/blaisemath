/*
 * OptimizedGraphLayout.java
 * Created on Dec 14, 2011
 */
package org.bm.blaise.scio.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Utilities and classes for constructing an alternate/condensed representation
 * of a graph that ensures layout will run more quickly.
 *
 * @author petereb1
 */
public class GraphOptimizers {

    /**
     * Generate list of optimized graph components, sorted by size.
     * @param graph a non-optimized view of the graph
     * @return collection of optimized graph, by component
     */
    public static List<OptimizedGraph> getOptimizedComponentGraphs(Graph graph) {
        if (graph.isDirected())
            graph = GraphUtils.undirectedCopy(graph);
        List<Graph> components = GraphUtils.getComponentGraphs(graph);
        Collections.sort(components, GraphUtils.GRAPH_SIZE_DESCENDING);

        List<OptimizedGraph> result = new ArrayList<OptimizedGraph>();
        for (Graph g : components)
            result.add(new OptimizedGraph(g));
        return result;
    }

    /**
     * A component that is optimized for computing degrees and for iterating over edges.
     */
    public static class OptimizedGraph implements Graph {

        /** Nodes */
        List nodes = new ArrayList();
        /** Degrees */
        Map<Object,Integer> degrees = new HashMap<Object,Integer>();

        /** Isolate nodes (deg = 0) */
        Set isolates = new HashSet();
        /** Leaf nodes (deg = 1) */
        Set leafNodes = new HashSet();
        /** Non-leaf nodes (deg >= 2) */
        Set coreNodes = new HashSet();

        /** General objects adjacent to each node */
        Map<Object,Set> neighbors = new HashMap<Object,Set>();
        /** Leaf objects adjacent to each node. Values consist of objects that have degree 1 ONLY. */
        Map<Object,Set> leaves = new HashMap<Object,Set>();

        /**
         * Construct optimized graph from general graph
         * @param g
         */
        public OptimizedGraph(Graph g) {
            g = GraphUtils.undirectedCopy(g);

            // iterate through objects
            for (Object o : g.nodes()) {
                nodes.add(o);
                neighbors.put(o, new HashSet());
                leaves.put(o, new HashSet());

                int deg = g.degree(o);
                if (deg == 0)
                    isolates.add(o);
                else if (deg == 1)
                    leafNodes.add(o);
                else
                    coreNodes.add(o);
                degrees.put(o, deg);
            }

            // iterate through edges
            for (Object o1 : nodes)
                for (Object o2 : nodes)
                    if (g.adjacent(o1, o2)) {
                        if (degrees.get(o1) > 1)
                            neighbors.get(o2).add(o1);
                        else
                            leaves.get(o2).add(o1);
                        if (degrees.get(o2) > 1)
                            neighbors.get(o1).add(o2);
                        else
                            leaves.get(o1).add(o2);
                    }
        }

        public int order() { return nodes.size(); }
        public List nodes() { return nodes; }
        /**
         * Return degree 0 nodes
         * @return
         */
        public Set isolates() { return isolates; }
        /**
         * Return degree 1 nodes
         * @return
         */
        public Set leafNodes() { return leafNodes; }
        /**
         * Return degree 2+ nodes
         * @return
         */
        public Set generalNodes() { return coreNodes; }
        public Map<Object,Set> getLeaves() { return leaves; }
        public boolean contains(Object x) { return degrees.containsKey(x); } // use HashSet to improve performance
        public boolean isDirected() { return false; }
        public boolean adjacent(Object x, Object y) {
            return (neighbors.containsKey(x) && neighbors.get(x).contains(y))
                    || (leaves.containsKey(x) && leaves.get(x).contains(y));
        }
        public int degree(Object x) {
            return degrees.containsKey(x) ? degrees.get(x) : 0;
        }
        public Set neighbors(Object x) {
            Set result = new HashSet();
            if (neighbors.containsKey(x))
                result.addAll(neighbors.get(x));
            if (leaves.containsKey(x))
                result.addAll(leaves.get(x));
            return result;
        }
        public int edgeCount() {
            int degCount = 0;
            for (Object o : nodes)
                degCount += degree(o);
            return degCount/2;
        }

        /** Returns graph comprised of just the "core" nodes of this graph */
        public OptimizedGraphCore getCore() {
            return new OptimizedGraphCore(this);
        }
    }

    /**
     * Extension of the optimized graph component that restricts view of graph
     * to the "core" nodes (those with degree > 1).
     */
    public static class OptimizedGraphCore implements Graph {
        OptimizedGraph base;
        List coreNodeList = new ArrayList();
        public OptimizedGraphCore(OptimizedGraph og) {
            this.base = og;
            coreNodeList.addAll(og.coreNodes);
        }
        public OptimizedGraph getBase() { return base; }
        @Override
        public int order() { return coreNodeList.size(); }
        @Override
        public List nodes() { return coreNodeList; }
        @Override
        public boolean contains(Object x) { return base.coreNodes.contains(x); } // faster to use hashset than list
        @Override
        public boolean adjacent(Object x, Object y) {
            return base.neighbors.containsKey(x) && base.neighbors.get(x).contains(y);
        }
        @Override
        public int degree(Object x) {
            return base.neighbors.containsKey(x) ? base.neighbors.get(x).size() : 0;
        }
        @Override
        public Set neighbors(Object x) {
            return base.neighbors.containsKey(x) ? base.neighbors.get(x) : Collections.emptySet();
        }
        public boolean isDirected() {
            return false;
        }
        public int edgeCount() {
            int degCount = 0;
            for (Object o : coreNodeList)
                degCount += degree(o);
            return degCount/2;
        }
    }


    private GraphOptimizers() {}

}
