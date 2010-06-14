/*
 * SimpleGraphIO.java
 * Created May 12, 2010
 */

package org.bm.blaise.scio.graph.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.Graphs;
import org.bm.blaise.scio.graph.NodeValueGraphWrapper;
import org.bm.blaise.scio.graph.WeightedGraphWrapper;
import org.bm.blaise.scio.graph.WeightedNodeValueGraphWrapper;

/**
 * <p>
 *  Provides input/output for text files describing graphs via their vertices and edges.
 *  Each line in the file represents a vertex, an edge, or a descriptive text.
 *  Syntax requirements:
 *  <ul>
 *   <li>Four input modes are supported: "description", "vertices", "edges", and "arcs".</li>
 *   <li>File switches between input modes using a line beginning with a star:
 *      "<code>*DESCRIPTION</code>", "<code>*VERTICES</code>", etc.</li>
 *   <li>A line beginning with "<code>%</code>" represents a comment and is ignored.</li>
 *   <li>Lines in the <i>description mode</i> are currently ignored.</li>
 *   <li>In <i>vertex mode</i>, each line has an integer representing the vertex,
 *      optionally followed by a string representing the nodes name (separated by a space).</li>
 *   <li>The <i>edge mode</i> and <i>arc mode</i> currently have identical behavior.
 *      Each line contains two entries (separated by a space) indicating the integer values
 *      of the ends of the edge, and possibly a third indicating the weight of the edge.</li>
 *  </ul>
 *  The vertices are maintained in sorted order when a file is read.
 * </p>
 * @author Elisha Peterson
 */
public final class SimpleGraphIO {

    /** Specifies expected mode for next input line. */
    private enum ImportMode {
        /** Descriptive info about the graph */
        DESCRIPTION("DESCRIPTION"),
        /** A vertex, possibly with a name. */
        VERTICES("VERTICES"),
        /** An arc */
        ARCS("ARCS"),
        /** An edge (possibly weighted) */
        EDGES("EDGES");

        String header;
        /** Construct with specified header, used to identify the mode in text files. */
        ImportMode(String header) { this.header = header; }
    }

    /** 
     * Checks for a line matching one of the specified headers for an import mode,
     * returning the mode if it does.
     */
    private static ImportMode parseInputMode(String line) {
        for (ImportMode im : ImportMode.values())
            if (line.startsWith(im.header))
                return im;
        return null;
    }


    /** 
     * Reads in and returns a graph file
     * @param file file containing information of the graph 
     * @return the graph data structure
     */
    public static Graph<Integer> importGraph(URL file) { return importGraph(new File(file.getFile())); }

    /*
     * Reads in and returns a graph file
     * @param file file containing information of the graph
     * @return the graph data structure, possibly with node labels attached to the vertices
     */
    public static Graph<Integer> importGraph(File file) {
        ImportMode mode = ImportMode.DESCRIPTION;
        TreeSet<Integer> vertices = new TreeSet<Integer>();
        TreeMap<Integer,String> names = new TreeMap<Integer,String>();
        HashMap<Integer[],Double> weights = new HashMap<Integer[],Double>();
        ArrayList<Integer[]> edges = new ArrayList<Integer[]>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;
            String[] split;
            try {
                int lineNumber = 0;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.startsWith("*")) {
                        ImportMode newMode = parseInputMode(line.substring(1).trim().toUpperCase());
                        if (newMode == null)
                            System.out.println("importSimpleGraph: unable to import line " + lineNumber + ": " + line);
                        else
                            mode = newMode;
                    } else if (line.startsWith("%")) {
                        // ignore this as a comment
                    } else {
                        split = line.split("\\s+");
                        try {
                            switch (mode) {
                                case DESCRIPTION:
                                    break;
                                case VERTICES:
                                    importVertex(vertices, names, split);
                                    break;
                                case EDGES:
                                case ARCS:
                                    importEdge(vertices, edges, weights, split);
                                    break;
                            }
                        } catch (Exception ex) {
                            System.out.println("Unable to import line " + lineNumber + " in mode " + mode + ": " + Arrays.toString(split));
                        }
                    }
                    lineNumber++;
                }
            } finally {
                reader.close();
            }
        } catch (FileNotFoundException ex) {
            System.err.println(ex);
        } catch (IOException ex) {
            System.err.println(ex);
        }
        Graph<Integer> result = Graphs.getInstance(false, vertices, edges);
        if (names.size() > 0 && weights.size() > 0) {
            WeightedNodeValueGraphWrapper<Integer, Double, String> resultGraph = new WeightedNodeValueGraphWrapper<Integer, Double, String>(result);
            for (Entry<Integer, String> en : names.entrySet())
                resultGraph.setValue(en.getKey(), en.getValue());
            for (Entry<Integer[], Double> en : weights.entrySet())
                resultGraph.setWeight(en.getKey()[0], en.getKey()[1], en.getValue());
            return resultGraph;
        } else if (names.size() > 0) {
            NodeValueGraphWrapper<Integer, String> resultGraph = new NodeValueGraphWrapper<Integer, String>(result);
            for (Entry<Integer, String> en : names.entrySet())
                resultGraph.setValue(en.getKey(), en.getValue());
            return resultGraph;
        } else if (weights.size() > 0) {
            WeightedGraphWrapper<Integer, Double> resultGraph = new WeightedGraphWrapper<Integer, Double>(result);
            for (Entry<Integer[], Double> en : weights.entrySet())
                resultGraph.setWeight(en.getKey()[0], en.getKey()[1], en.getValue());
            return resultGraph;
            
        } else {
            return result;
        }
    }

    /**
     * Imports a vertex into the graph. The first argument is the vertex #, the second is the name.
     * The name is optional, and may or may not be surrounded by quotes ("...")
     * @param vertices the list object containing the current vertices in the graph
     * @param names a map associating integer vertices to string labels of the vertices
     * @param vertex the vertex to import... first argument is vertex #, second is name (optional)
     * @throws NumberFormatException if unable to decode integer representation of vertex #
     */
    private static void importVertex(Set<Integer> vertices, Map<Integer, String> names, String[] vertex) throws NumberFormatException {
        if (vertex == null || vertex.length < 1)
            throw new IllegalArgumentException("importVertex: too few arguments in array " + Arrays.toString(vertex));
        Integer vx = Integer.decode(vertex[0]);
        vertices.add(vx);
        if (vertex.length > 1) {
            String name = vertex[1].trim();
            if (name.startsWith("\"") && name.endsWith("\""))
                name = name.substring(1, name.length()-1);
            names.put(vx, name);
        }
        if (vertex.length > 2)
            System.out.println("WARNING -- importVertex: ignoring all but the first two entries @ input " + Arrays.toString(vertex));
    }

    /**
     * Import an edge into a graph. If the specified edge contains vertices that are not
     * already on the list of vertices, it adds them to the collection.
     * @param vertices the list object containing the current vertices in the graph
     * @param edges the list object containing the current edges in the graph
     * @param weights mapping of edges to weights
     * @param edge the edge to import
     * @throws NumberFormatException if unable to decode integer representation of vertex #
     */
    private static void importEdge(Set<Integer> vertices, List<Integer[]> edges, Map<Integer[],Double> weights, String[] edge) throws NumberFormatException {
        if (edge == null || edge.length < 2)
            throw new IllegalArgumentException("importEdge: too few arguments in array " + Arrays.toString(edge));
        Integer v1 = Integer.decode(edge[0]);
        Integer v2 = Integer.decode(edge[1]);
        if (!vertices.contains(v1))
            vertices.add(v1);
        if (!vertices.contains(v2))
            vertices.add(v2);
        Integer[] e = {v1, v2};
        edges.add(e);
        if (edge.length > 2) {
            try {
                Double weight = Double.valueOf(edge[2]);
                weights.put(e, weight);
            } catch (NumberFormatException ex) {
                System.out.println("WARNING -- importEdge: third value on line is not a properly formatted double value representing weight: " + edge[2]);
            }
        }
        if (edge.length > 3)
            System.out.println("WARNING -- importEdge: ignoring all but the first three entries @ input " + Arrays.toString(edge));
    }    

}