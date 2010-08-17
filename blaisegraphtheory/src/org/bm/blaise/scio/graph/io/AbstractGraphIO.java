/*
 * AbstractGraphIO.java
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
import java.util.TreeMap;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.GraphFactory;
import org.bm.blaise.scio.graph.LongitudinalGraph;
import org.bm.blaise.scio.graph.ValuedGraph;
import org.bm.blaise.scio.graph.ValuedGraphWrapper;
import org.bm.blaise.scio.graph.WeightedGraphWrapper;
import org.bm.blaise.scio.graph.WeightedValuedGraphWrapper;

/**
 * Provides a framework for graph IO support for a particular format.
 * @author Elisha Peterson
 */
public abstract class AbstractGraphIO {

    /** Used to describe whether saved and loaded graphs are of type "regular" or "longitudinal" */
    public enum GraphType {
        UNKNOWN, REGULAR, LONGITUDINAL;
    }

    //
    // REGULAR FILES
    //
    
    /** @return file filter that can be used for this particular file type */
    abstract public javax.swing.filechooser.FileFilter getFileFilter();

    /**
     * Reads in and returns a graph file
     * @param locations information about positions of nodes in the file
     * @param file file containing information of the graph
     * @param type a type pointer describing whether resulting graph should be regular or longitudinal;
     *   if impossible to support the specified option, the type pointer will switch to indicate
     *   a different return type
     * @return the graph data structure;
     *   or possibly return null if there is an error reading the specified file
     */
    public Object importGraph(Map<Integer, double[]> locations, URL file, GraphType type) {
        return importGraph(locations, new File(file.getFile()), type);
    }

    /*
     * Reads in and returns a graph file
     * @param locations information about positions of nodes in the file
     * @param file file containing information of the graph
     * @param type a type pointer describing whether resulting graph should be regular or longitudinal;
     *   if impossible to support the specified option, the type pointer will switch to indicate
     *   a different return type
     * @return the graph data structure, possibly with node labels attached to the vertices;
     *   or possibly return null if there is an error reading the specified file
     */
    public abstract Object importGraph(Map<Integer, double[]> locations, File file, GraphType type);

    /**
     * Saves a graph to a file.
     * @param graph the graph data structure, either regular or longitudinal
     * @param positions the positions of vertices in the graph
     * @param file the file to save the graph to
     * @return the type of graph that was saved to the file, or null if the save attempt failed
     */
    public abstract GraphType saveGraph(Object graph, Map<Object,Point2D.Double> positions, File file);
    
    //
    // UTILITIES
    //

    /**
     * Utility method. Returns a graph of the appropriate type, given the input data in the specified formats.
     * Returns a longitudinal graph. Each vertex/edge may have one or more intervals; if no interval is given,
     * it is assumed to last throughout the time period. The resulting time period lasts from the minimum
     * specified time value to the maximum time value.
     * 
     * @param vertices vertices, stored as a map from integer index to string label
     * @param times list of time intervals corresponding to the vertices
     * @param edges list of the edges or arcs
     * @param weights list of weights, in same order as edges
     * @param eTimes list of time intervals corresponding to the edges
     * @param directed whether resulting graph should be directed
     * @return a graph implementation with the desired properties/values
     */
    public static LongitudinalGraph<Integer> buildGraph(
            Map<Integer,String> vertices,
            Map<Integer,List<double[]>> times,
            ArrayList<Integer[]> edges,
            ArrayList<Double> weights,
            List<List<double[]>> eTimes,
            boolean directed) {

//        System.out.println(".buildGraph (longitudinal version): " + vertices.size() + " vertices, " + edges.size() + " edges");

        if (edges.size() != weights.size() || edges.size() != eTimes.size())
            throw new IllegalStateException("buildGraph: size of edges, weights, and eTimes lists should be equal!");

        // determine whether result should be weighted
        boolean weighted = false;
        for (Double d : weights)
            if (!(d==0.0 || d==1.0)) { weighted = true; break; }
        // determine whether result should have value nodes
        boolean valued = false;
        for (String s : vertices.values())
            if (s != null) { valued = true; break; }
        if (weighted || valued)
            System.out.println("WARNING -- longitudinal buildGraph does not currently support weighted or valued edges!");

        // add extra vertices to vertex map
        for (Integer v : vertices.keySet())
            if (!times.containsKey(v)) {
                ArrayList<double[]> ivs = new ArrayList<double[]>();
                ivs.add(new double[]{-Double.MAX_VALUE, Double.MAX_VALUE});
                times.put(v, ivs);
            }

        // create edge map
        TreeMap<Integer, Map<Integer, List<double[]>>> edgeMap = new TreeMap<Integer, Map<Integer, List<double[]>>>();
        for (int i = 0; i < edges.size(); i++) {
            Integer[] edge = edges.get(i);
            List<double[]> ivs = eTimes.get(i);
            if (ivs == null) {
                ivs = new ArrayList<double[]>();
                ivs.add(new double[]{-Double.MAX_VALUE, Double.MAX_VALUE});
            }
            if (!edgeMap.containsKey(edge[0]))
                edgeMap.put(edge[0], new TreeMap<Integer, List<double[]>>());
            edgeMap.get(edge[0]).put(edge[1], ivs);
        }

        return GraphFactory.getLongitudinalGraph(directed, times, edgeMap);
    }

    /**
     * Utility method. Returns a graph of the appropriate type, given the input data in the specified formats.
     * @param vertices vertices, stored as a map from integer index to string label
     * @param edges list of the edges or arcs
     * @param weights list of weights, in same order as edges
     * @param directed whether resulting graph should be directed
     * @return a graph implementation with the desired properties/values
     */
    public static Graph<Integer> buildGraph(
            Map<Integer,String> vertices,
            ArrayList<Integer[]> edges,
            ArrayList<Double> weights,
            boolean directed) {

//        System.out.println(".buildGraph: " + vertices.size() + " vertices, " + edges.size() + " edges");

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
    /**
     * Utility method. Returns a graph of the appropriate type, given the input data in the specified formats.
     * The nodes in the return graph have values of the form {string,image}.
     * @param vertices vertices, stored as a map from integer index to string label
     * @param images images associated to certain vertices (by file name only)
     * @param edges list of the edges or arcs
     * @param weights list of weights, in same order as edges
     * @param directed whether resulting graph should be directed
     * @return a graph implementation with the desired properties/values
     */
    public static ValuedGraph<Integer,Object[]> buildGraph(
            Map<Integer,String> vertices,
            Map<Integer,String> images,
            ArrayList<Integer[]> edges,
            ArrayList<Double> weights,
            boolean directed) {

//        System.out.println(".buildGraph: " + vertices.size() + " vertices, " + edges.size() + " edges");

        if (edges.size() != weights.size())
            throw new IllegalStateException("buildGraph: size of edges and weights lists should be equal!");

        // base graph
        Graph<Integer> result = GraphFactory.getGraph(directed, vertices.keySet(), edges);

        // determine whether result should be weighted
        boolean weighted = false;
        for (Double d : weights)
            if (!(d==0.0 || d==1.0)) { weighted = true; break; }

        if (weighted) {
            WeightedValuedGraphWrapper<Integer, Double, Object[]> resultGraph = new WeightedValuedGraphWrapper<Integer, Double, Object[]>(result);
            for (Integer i : vertices.keySet())
                resultGraph.setValue(i, new Object[]{vertices.get(i), images.get(i)});
            for (int i = 0; i < edges.size(); i++)
                resultGraph.setWeight(edges.get(i)[0], edges.get(i)[1], weights.get(i));
            return resultGraph;
        } else {
            ValuedGraphWrapper<Integer, Object[]> resultGraph = new ValuedGraphWrapper<Integer, Object[]>(result);
            for (Integer i : vertices.keySet())
                resultGraph.setValue(i, new Object[]{vertices.get(i), images.get(i)});
            return resultGraph;
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
