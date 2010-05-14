/**
 * GraphCreation.java
 * Created on Oct 14, 2009
 */
package org.bm.blaise.scio.graph.creation;

import org.bm.blaise.scio.graph.*;
import java.util.Arrays;
import java.util.Collection;
import org.bm.blaise.scio.graph.metrics.GraphMetrics;

/**
 * <p>
 *   <code>GraphCreation</code> defines utilities for creating graphs of specified types.
 * </p>
 *
 * @author Elisha Peterson
 */
public class GraphCreation {

    public static void main(String[] args) {
        long t0 = System.currentTimeMillis();
        
        long t = System.currentTimeMillis();
        System.out.println("== Graph == " + t0);

        SimpleGraph g = generateSparseRandomGraph(5000, 5000, false);
//        System.out.println(g);
        System.out.println(g.getEdges().size() + " edges");

        long t2 = System.currentTimeMillis();
        System.out.println("--DegreeMap/Distribution-- " + (t2-t) + "ms");
        t = t2;

//        System.out.println(GraphUtils.getDegreeMap(g));
        System.out.println(Arrays.toString(GraphUtils.degreeDistribution(g)));

        t2 = System.currentTimeMillis();
        System.out.println("--Neighborhood and Clique Counts-- " + (t2-t) + "ms");
        t = t2;

        System.out.println(GraphUtils.neighborhood(0, g));
        System.out.println(GraphMetrics.CLIQUE_COUNT.getValue(g, 0));
//        System.out.println(GraphUtils.getCliqueCountMap(g));

        t2 = System.currentTimeMillis();
        System.out.println("--Components-- " + (t2-t) + "ms");
        t = t2;

        for (Object s : GraphUtils.components(g))
            System.out.println(".." + s);

        t2 = System.currentTimeMillis();
        System.out.println("--Distance Calculations-- " + (t2-t) + "ms");
        t = t2;

        System.out.println(GraphUtils.distance(0, 1, g));
        System.out.println(GraphUtils.distance(0, 2, g));
        System.out.println(GraphUtils.distance(1, 2, g));

        t2 = System.currentTimeMillis();
        System.out.println("--Neighborhood Methods-- " + (t2-t) + "ms");
        t = t2;

        System.out.println(GraphUtils.neighborhood(0, g, 0));
        System.out.println(GraphUtils.neighborhood(0, g, 1));
        System.out.println(GraphUtils.neighborhood(0, g, 2));
        System.out.println(GraphUtils.neighborhood(0, g, 3));

        t2 = System.currentTimeMillis();
        System.out.println("--SubEdges & Subgraph-- " + (t2-t) + "ms");
        t = t2;

        System.out.println(deriveSubgraph(g, Arrays.asList((Object) 0, 1, 2, 3, 4)));
        System.out.println(deriveContractedGraph(g, Arrays.asList(0, 1, 2, 3, 4), 0));

        t2 = System.currentTimeMillis();
        System.out.println("--ContractedEdges & ContractedGraph-- " + (t2-t) + "ms");
        t = t2;

//        System.out.println(getContractedEdges(g, (Collection<Object>) Arrays.asList((Object) 0, 1, 2, 3, 4), 0));
//        System.out.println(deriveContractedGraph(g, (Collection<Object>) Arrays.asList((Object) 0, 1, 2, 3, 4), 0));

        t2 = System.currentTimeMillis();
        System.out.println("---- " + (t2-t) + "ms");
        System.out.println("==== total = " + (t2-t0) + "ms");
    }

    /**
     * Builds a graph with provided array of vertices and array of edges.
     * @param vertices array of vertices
     * @param edges array of vertex-pairings; each entry should be an array of length two
     */
    public static SimpleGraph buildGraph(Object[] vertices, Object[][] edges) {
        SimpleGraph result = new SimpleGraph();
        for (int i = 0; i < vertices.length; i++)
            result.addVertex(vertices[i]);
        for (int i = 0; i < edges.length; i++)
            result.addEdge(edges[i][0], edges[i][1]);
        return result;
    }

    /**
     * Returns random graph with specified number of vertices and edge probability. Every potential
     * link has an equal probability of existence.
     * @param nVertices number of vertices in the resulting graph
     * @param pEdge probability of each edge's existence
     * @param directed whether graph is directed or not
     */
    public static SimpleGraph generateRandomGraph(int nVertices, double pEdge, boolean directed) {
        SimpleGraph result = buildEmptyGraph(nVertices);
        for (int i = 0; i < nVertices; i++)
            for (int j = 0; j < nVertices; j++)
                if ((!directed && j > i) || (directed && j != i))
                    if (Math.random() < pEdge)
                        result.addEdge(i, j);
        return result;
    }

    /**
     * Returns random graph w/ specified number of vertices and edges. Useful for creating
     * large sparse random graphs
     * @param nVertices number of vertices in the resulting graph
     * @param nEdges number of edges in the resulting graph
     * @param directed whether graph is directed or not
     */
    public static SimpleGraph generateSparseRandomGraph(int nVertices, int nEdges, boolean directed) {
        SimpleGraph result = buildEmptyGraph(nVertices);
        Edge potential;
        for (int i = 0; i < nEdges; i++) {
//            do {
                potential = new Edge(
                    (int) Math.floor(nVertices * Math.random()),
                    (int) Math.floor(nVertices * Math.random())
                );                    
//            } while (result.containsEdge(potential));
            result.addEdge(potential);
        }
        return result;
    }

    /** Returns graph with all possible edges. */
    public static SimpleGraph buildCompleteGraph(int nVertices, boolean directed) {
        SimpleGraph result = buildEmptyGraph(nVertices);
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
    public static SimpleGraph buildLoopGraph(int nVertices) {
        SimpleGraph result = buildEmptyGraph(nVertices);
        for (int i = 0; i < nVertices; i++) {
            result.addEdge(i, (i + 1) % nVertices);
        }
        return result;
    }

    /** Returns undirected graph with single central vertex. */
    public static SimpleGraph buildWheelGraph(int nVertices) {
        SimpleGraph result = buildEmptyGraph(nVertices);
        for (int i = 1; i < nVertices; i++) {
            result.addEdge(0, i);
        }
        return result;
    }

    /** Returns graph with specified number of vertices. */
    public static SimpleGraph buildEmptyGraph(int nVertices) {
        SimpleGraph result = new SimpleGraph();
        for (int i = 0; i < nVertices; i++)
            result.addVertex(i);
        return result;
    }


    /**
     * Returns deriveSubgraph for given set of vertices.
     * @param g the starting graph
     * @param vertices the collection of vertices that will comprise the vertex set in the new graph
     * @return the resulting graph
     */
    public static SimpleGraph deriveSubgraph(SimpleGraph g, Collection<Object> vertices) {
        SimpleGraph result = new SimpleGraph();
        for (Object v : vertices)
            result.addVertex(v);
        for (Edge e : g.getEdges())
            if (vertices.contains(e.getSource()) && vertices.contains(e.getSink()))
                result.addEdge(result.indexOf(e.getSource()), result.indexOf(e.getSink()));
        return result;
    }


    /**
     * Returns graph in which the specified collection of vertices have been replaced by a single new vertex
     * @param g the starting graph
     * @param vertices the collection of vertices to be contracted
     * @param newVertex the index of the replacement vertex
     * @return the resulting graph
     */
    public static SimpleGraph deriveContractedGraph(SimpleGraph g, Collection<Integer> vertices, Integer newVertex) {
        SimpleGraph result = new SimpleGraph();
//        for (Integer v : g.getVertices())
//            if (!vertices.contains(v))
//                result.addVertex(v);
//        result.addVertex(newVertex);
//        for (Edge e : g.getEdges())
//            if (vertices.contains(e.getSource()) && vertices.contains(e.getSink()))
//                continue;
//            else if (vertices.contains(e.getSource()))
//                result.addEdge(newVertex, e.getSink());
//            else if (vertices.contains(e.getSink()))
//                result.addEdge(e.getSource(), newVertex);
//            else
//                result.addEdge(e.getSource(), e.getSink());
        return result;
    }
}
