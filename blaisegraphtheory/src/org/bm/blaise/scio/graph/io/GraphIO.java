/*
 * GraphIO.java
 * Created Jul 10, 2010
 */

package org.bm.blaise.scio.graph.io;

import java.awt.geom.Point2D;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.GraphFactory;
import org.bm.blaise.scio.graph.LongitudinalGraph;
import org.bm.blaise.scio.graph.ValuedGraphWrapper;
import org.bm.blaise.scio.graph.WeightedGraphWrapper;
import org.bm.blaise.scio.graph.WeightedValuedGraphWrapper;

/**
 * Provides a framework for graph IO support for a particular format.
 * @author Elisha Peterson
 */
public abstract class GraphIO {

    //
    // REGULAR FILES
    //
    
    /**
     * Reads in and returns a graph file
     * @param locations information about positions of nodes in the file
     * @param file file containing information of the graph
     * @return the graph data structure
     */
    public Graph<Integer> importGraph(Map<Integer, double[]> locations, URL file) {
        return importGraph(locations, new File(file.getFile()));
    }

    /*
     * Reads in and returns a graph file
     * @param locations information about positions of nodes in the file
     * @param file file containing information of the graph
     * @return the graph data structure, possibly with node labels attached to the vertices
     */
    public abstract Graph<Integer> importGraph(Map<Integer, double[]> locations, File file);

    /**
     * Saves a graph to a file.
     * @param graph the graph data structure
     * @param positions the positions of vertices in the graph, in the same order returned by <code>graph.nodes()</code>
     * @param file the file to save the graph to
     */
    public abstract void saveGraph(Graph<Integer> graph, Point2D.Double[] positions, File file);

    //
    // LONGITUDINAL FILES
    //

    /**
     * Reads in and returns a longitudinal graph file
     * @param file file containing information of the graph
     * @return the longitudinal graph data structure
     */
    public LongitudinalGraph<Integer> importLongitudinalGraph(URL file) {
        return importLongitudinalGraph(new File(file.getFile()));
    }

    /*
     * Reads in and returns a longitudinal graph file
     * @param file file containing information of the graph
     * @return the longitudinal graph data structure, possibly with node labels attached to the vertices
     */
    public abstract LongitudinalGraph<Integer> importLongitudinalGraph(File file);

    /**
     * Saves a longitudinal graph to a file.
     * @graph the graph data structure
     * @param file the file to save the graph to
     */
    public abstract void saveLongitudinalGraph(LongitudinalGraph<Integer> graph, File file);

    
    //
    // UTILITIES
    //

    /**
     * Utility method. Returns a graph of the appropriate type, given the input data in the specified formats.
     * @param vertices vertices, stored as a map from integer index to string label
     * @param edges list of the edges or arcs
     * @param weights list of weights, in same order as edges
     * @param directed whether resulting graph should be directed
     * @return a graph implementation with the desired properties/values
     */
    protected static Graph<Integer> buildGraph(
            Map<Integer,String> vertices,
            ArrayList<Integer[]> edges,
            ArrayList<Double> weights,
            boolean directed) {

        System.out.println(".buildGraph: " + vertices.size() + " vertices, " + edges.size() + " edges");

        if (edges.size() != weights.size())
            throw new IllegalStateException("buildGraph: size of edges and weights lists should be equal!");

        // base graph
        Graph<Integer> result = GraphFactory.getGraph(directed, vertices.keySet(), edges);

        // determine whether result should be weighted
        boolean weighted = false;
        for (Double d : weights)
            if (!(d==0.0 || d==1.0)) { weighted = true; break; }
        // determine whether result should have value nodes
        boolean valued = false;
        for (String s : vertices.values())
            if (s != null) { valued = true; break; }

        if (weighted && valued) {
            WeightedValuedGraphWrapper<Integer, Double, String> resultGraph = new WeightedValuedGraphWrapper<Integer, Double, String>(result);
            for (Entry<Integer, String> en : vertices.entrySet())
                resultGraph.setValue(en.getKey(), en.getValue());
            for (int i = 0; i < edges.size(); i++)
                resultGraph.setWeight(edges.get(i)[0], edges.get(i)[1], weights.get(i));
            return resultGraph;
        } else if (weighted && !valued) {
            WeightedGraphWrapper<Integer, Double> resultGraph = new WeightedGraphWrapper<Integer, Double>(result);
            for (int i = 0; i < edges.size(); i++)
                resultGraph.setWeight(edges.get(i)[0], edges.get(i)[1], weights.get(i));
            return resultGraph;
        } else if (!weighted && valued) {
            ValuedGraphWrapper<Integer, String> resultGraph = new ValuedGraphWrapper<Integer, String>(result);
            for (Entry<Integer, String> en : vertices.entrySet())
                resultGraph.setValue(en.getKey(), en.getValue());
            return resultGraph;
        } else {
            return result;
        }
    }

    /** Looks to see if matrix described by given data is symmetric. */
    protected static final boolean symmetricMatrix(List<Integer[]> edges, List<Double> weights) {
        for (int i=0; i < edges.size(); i++) {
            Integer[] ii = edges.get(i);
            // look to see if the transpose is identical
            boolean foundTranspose = false;
            for (int j=0; j < edges.size(); j++) {
                Integer[] ii2 = edges.get(j);
                if (ii2[0] == ii[1] && ii2[1] == ii[0]) {
                    foundTranspose = true;
                    if (!weights.get(i).equals(weights.get(j))) return false;
                }
            }
            if (!foundTranspose) return false;
        }
        return true;
    }
}
