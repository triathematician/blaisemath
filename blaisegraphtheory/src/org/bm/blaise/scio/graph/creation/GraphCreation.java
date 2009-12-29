/**
 * GraphCreation.java
 * Created on Oct 14, 2009
 */
package org.bm.blaise.scio.graph.creation;

import org.bm.blaise.scio.graph.metrics.GraphUtils;
import org.bm.blaise.scio.graph.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>
 *   <code>GraphCreation</code> defines utilities for creating graphs of specified types.
 * </p>
 *
 * @author Elisha Peterson
 */
public class GraphCreation {

    public static void main(String[] args) {
        Graph g = getRandomGraph(10, 0.3, false);
        System.out.println(g);
        System.out.println("====");
        System.out.println(GraphUtils.getDegreeMap(g));
        System.out.println(Arrays.toString(GraphUtils.getDegreeDistribution(g)));
        System.out.println("----");
        System.out.println(GraphUtils.getNeighborhood(g, 0));
        System.out.println(GraphUtils.getCliqueCount(g, 0));
        System.out.println(GraphUtils.getCliqueCountMap(g));
        System.out.println("----");
        System.out.println(GraphUtils.getDistance(g, 0, 1));
        System.out.println(GraphUtils.getDistance(g, 0, 2));
        System.out.println(GraphUtils.getDistance(g, 1, 2));
        System.out.println("----");
        System.out.println(GraphUtils.getNeighborhood(g, 0, 0));
        System.out.println(GraphUtils.getNeighborhood(g, 0, 1));
        System.out.println(GraphUtils.getNeighborhood(g, 0, 2));
        System.out.println(GraphUtils.getNeighborhood(g, 0, 3));
        System.out.println("----");
        System.out.println(getSubEdges(g, (Collection<Object>) Arrays.asList((Object) 0, 1, 2, 3, 4)));
        System.out.println(getSubgraph(g, (Collection<Object>) Arrays.asList((Object) 0, 1, 2, 3, 4)));
        System.out.println("----");
        System.out.println(getContractedEdges(g, (Collection<Object>) Arrays.asList((Object) 0, 1, 2, 3, 4), 0));
        System.out.println(getContractedGraph(g, (Collection<Object>) Arrays.asList((Object) 0, 1, 2, 3, 4), 0));
        System.out.println("----");
    }

    /**
     * Constructs a graph with provided array of vertices and array of edges.
     */
    public static Graph getGraph(Object[] vertices, Object[][] edges) {
        Graph result = new Graph();
        for (int i = 0; i < vertices.length; i++) {
            result.addVertex(vertices[i]);
        }
        for (int i = 0; i < edges.length; i++) {
            result.addEdge(edges[i][0], edges[i][1]);
        }
        return result;
    }

    /** Returns graph with specified number of vertices and edge probability. */
    public static Graph getRandomGraph(int nVertices, double pEdge, boolean directed) {
        Graph result = getEmptyGraph(nVertices);
        for (int i = 0; i < nVertices; i++) {
            for (int j = 0; j < nVertices; j++) {
                if ((!directed && j > i) || (directed && j != i)) {
                    if (Math.random() < pEdge) {
                        result.addEdge(i, j);
                    }
                }
            }
        }
        return result;
    }

    /** Returns graph with all possible edges. */
    public static Graph getCompleteGraph(int nVertices, boolean directed) {
        Graph result = getEmptyGraph(nVertices);
        for (int i = 0; i < nVertices; i++) {
            for (int j = 0; j < nVertices; j++) {
                if ((!directed && j > i) || (directed && j != i)) {
                    result.addEdge(i, j);
                }
            }
        }
        return result;
    }

    /** Returns undirected graph with cyclic edges. */
    public static Graph getLoopGraph(int nVertices) {
        Graph result = getEmptyGraph(nVertices);
        for (int i = 0; i < nVertices; i++) {
            result.addEdge(i, (i + 1) % nVertices);
        }
        return result;
    }

    /** Returns undirected graph with single central vertex. */
    public static Graph getWheelGraph(int nVertices) {
        Graph result = getEmptyGraph(nVertices);
        for (int i = 1; i < nVertices; i++) {
            result.addEdge(0, i);
        }
        return result;
    }

    /** Returns graph with specified number of vertices. */
    public static Graph<Integer> getEmptyGraph(int nVertices) {
        Graph<Integer> result = new Graph<Integer>();
        for (int i = 0; i < nVertices; i++) {
            result.addVertex(i);
        }
        return result;
    }


    /** Returns list of edges between specified vertices. */
    public static <V> Set<EdgeInterface<V>> getSubEdges(Graph<V> g, Collection<V> vertices) {
        Set<EdgeInterface<V>> result = new HashSet<EdgeInterface<V>>();
        for(EdgeInterface<V> e : g.getEdges()) {
            if (vertices.contains(e.getSource()) && vertices.contains(e.getSink())) {
                result.add(e);
            }
        }
        return result;
    }

    /** Returns subgraph for given set of vertices. */
    public static <V> Graph<V> getSubgraph(Graph<V> g, Collection<V> vertices) {
        Graph<V> result = (Graph<V>) g.clone();
        result.clear();
        for (V v : vertices) {
            result.addVertex(v);
        }
        result.setEdges(getSubEdges(g, vertices));
        return result;
    }

    /** Returns list of edges with specified vertices contracted, and replaced by the specified new vertex. */
    public static <V> Set<EdgeInterface<V>> getContractedEdges(Graph<V> g, Collection<V> vertices, V newVertex) {
        Set<EdgeInterface<V>> result = new HashSet<EdgeInterface<V>>();
        for(EdgeInterface<V> e : g.getEdges()) {
            if (vertices.contains(e.getSource())) {
                if (vertices.contains(e.getSink())) {
                    result.add(new Edge<V>(newVertex, newVertex));
                } else {
                    result.add(new Edge<V>(newVertex, e.getSink()));
                }
            } else {
                if (vertices.contains(e.getSink())) {
                    result.add(new Edge<V>(e.getSource(), newVertex));
                } else {
                    result.add(new Edge<V>(e.getSource(), e.getSink()));
                }
            }
        }
        return result;
    }

    /** Returns subgraph with specified vertices contracted. */
    public static <V> Graph<V> getContractedGraph(Graph<V> g, Collection<V> vertices, V newVertex) {
        Graph<V> result = (Graph<V>) g.clone();
        for (V v : vertices) {
            result.removeVertex(v);
        }
        result.addVertex(newVertex);
        result.setEdges(getContractedEdges(g, vertices, newVertex));
        return result;
    }
}
