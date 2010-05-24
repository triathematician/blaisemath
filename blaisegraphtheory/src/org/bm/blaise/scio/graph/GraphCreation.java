/**
 * GraphCreation.java
 * Created on Oct 14, 2009
 */
package org.bm.blaise.scio.graph;

import java.util.ArrayList;
import org.bm.blaise.scio.graph.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
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

        SimpleGraph g = generateSparseRandomGraph(7, 15, false);
        System.out.println(g);
        System.out.println(g.getEdges().size() + " edges");

        long t2 = System.currentTimeMillis();
        System.out.println("--DegreeMap/Distribution-- " + (t2-t) + "ms");
        t = t2;

        System.out.println(GraphMetrics.DEGREE.getValues(g));
        System.out.println(Arrays.toString(GraphUtils.degreeDistribution(g)));

        t2 = System.currentTimeMillis();
        System.out.println("--Neighborhood and Clique Counts-- " + (t2-t) + "ms");
        t = t2;

        System.out.println(GraphUtils.neighborhood(0, g));
        System.out.println(GraphMetrics.CLIQUE_COUNT.getValue(g, 0));

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

        System.out.println("Subgraph: " + deriveSubgraph(g, Arrays.asList((Object) 0, 1, 2, 3, 4)));
        System.out.println("Contracted: " + deriveContractedGraph(g, Arrays.asList(0, 1, 2, 3, 4), "*"));

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

    /**
     * Utility to return a random vertex in a graph, weighted by degree.
     * @param graph the graph
     * @return index of a randomly chosen vertex
     */
    static int weightedRandomVertex(Graph2 graph) {
        List<Integer> degrees = GraphMetrics.DEGREE.getValues(graph);
        int totalDegree = graph.getEdges().size() * 2;
        double random = Math.random() * totalDegree;
        double sumDegree = 0;
        for ( int i = 0; i < degrees.size(); i++ ) {
            sumDegree += degrees.get(i);
            if (sumDegree > random)
                return i;
        }
        throw new IllegalStateException("Should not be here since sum random is less than total degree");
    }
    
    /**
     * Returns random graph generated via preferential attachment.
     * @param seedVertices # of vertices to use in seed graph (0 or more)
     * @param seedProbability probability of each edge in seed graph
     * @param nVertices number of vertices in final graph
     * @param nEdgesPerStep number of edges to attach at each step
     */
    public static SimpleGraph generatePreferentialAttachmentGraph(
            int seedVertices, double seedProbability,
            int nVertices, int nEdgesPerStep) {
        SimpleGraph seedGraph = generateRandomGraph(seedVertices, seedProbability, false);
        int[] newEdges = new int[nEdgesPerStep];
        int cur;
        while (seedGraph.size() < nVertices) {
            cur = seedGraph.size();
            for (int i = 0; i < newEdges.length; i++)
                newEdges[i] = weightedRandomVertex(seedGraph);
            for (int i = 0; i < newEdges.length; i++)
                seedGraph.addEdge(cur, newEdges[i]);
        }
        return seedGraph;
    }
    
    /** @return index of a randomly chosen # in provided array of probabilities */
    static int sampleRandom(double[] probs) {
        double rand = Math.random();
        float sum = 0f;
        for (int i = 0; i < probs.length; i++) {
            sum += probs[i];
            if (sum > rand) return i;            
        }
        return -1;
    }

    /**
     * Returns random graph generated via preferential attachment.
     * @param seedVertices # of vertices to use in seed graph (0 or more)
     * @param seedProbability probability of each edge in seed graph
     * @param nVertices number of vertices in final graph
     * @param connectionProbs probabilities of initial #s of connections
     */
    public static SimpleGraph generatePreferentialAttachmentGraph(
            int seedVertices, double seedProbability,
            int nVertices, double[] connectionProbs) {
        SimpleGraph seedGraph = generateRandomGraph(seedVertices, seedProbability, false);
        int cur;
        while (seedGraph.size() < nVertices) {
            int degree = sampleRandom(connectionProbs);
            int[] newEdges = new int[degree];
            cur = seedGraph.size();
            for (int i = 0; i < degree; i++)
                newEdges[i] = weightedRandomVertex(seedGraph);
            for (int i = 0; i < degree; i++)
                seedGraph.addEdge(cur, newEdges[i]);
        }
        return seedGraph;
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
    public static SimpleGraph buildCircleGraph(int nVertices) {
        SimpleGraph result = buildEmptyGraph(nVertices);
        for (int i = 0; i < nVertices; i++)
            result.addEdge(i, (i + 1) % nVertices);
        return result;
    }

    /** Returns undirected graph with single central vertex. */
    public static SimpleGraph buildStarGraph(int nVertices) {
        SimpleGraph result = buildEmptyGraph(nVertices);
        for (int i = 1; i < nVertices; i++)
            result.addEdge(0, i);
        return result;
    }

    /** Returns undirected graph with single central vertex and circular connections around the outside. */
    public static SimpleGraph buildWheelGraph(int nVertices) {
        SimpleGraph result = buildEmptyGraph(nVertices);
        for (int i = 1; i < nVertices; i++) {
            result.addEdge(0, i);
            result.addEdge(i, i < nVertices-1 ? i+1 : 1);
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
     * @param label label for contracted vertex
     * @return the resulting graph
     */
    public static SimpleGraph deriveContractedGraph(SimpleGraph g, Collection<Integer> vertices, String label) {
        int max = 0;

        SimpleGraph result = new SimpleGraph(); // object value of vertices will represent old index
        List<Integer> oldIndices = new ArrayList<Integer>(); // will store old indices
        int pos = 0;
        for (int i = 0; i < g.size(); i++)
            if (!vertices.contains(i)) {
                result.addVertex(pos++);
                oldIndices.add(i);
                max = Math.max(max, i);
            }
        int newVertex = result.size();
        result.addVertex(newVertex, label);
//        System.out.println("graph: "  + result);

        // add edges
        for (Edge e : g.getEdges()) {
            if (vertices.contains(e.getSource()) && vertices.contains(e.getSink()))
                continue;
            else if (vertices.contains(e.getSource()))
                result.addEdge((int) newVertex, oldIndices.indexOf(e.getSink()));
            else if (vertices.contains(e.getSink()))
                result.addEdge(oldIndices.indexOf(e.getSource()), (int) newVertex);
            else
                result.addEdge(oldIndices.indexOf(e.getSource()), oldIndices.indexOf(e.getSink()));
//            System.out.println("graph: " + result);
        }
        return result;
    }
}
