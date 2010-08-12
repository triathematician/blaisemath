/*
 * EdgeListGraphIO.java
 * Created Jul 9, 2010
 */

package org.bm.blaise.scio.graph.io;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.WeightedGraph;
import util.FileNameExtensionFilter;

/**
 * <p>
 *  Provides input/output for text files consisting of several lines each of which has
 *  two numbers describing the edges of a graph. The graph returned is directed.
 * </p>
 * <p>
 * <b>EXAMPLE:</b>
 * <br/>
 * <code>1 5<br/>2 3<br/>1 4<br/>3 4<br/>4 5</code>
 * </p>
 * <p>
 * Any blank lines are ignored, but any non-blank lines without two spaced (or tabbed)
 * integers results in an error.
 * </p>
 * <p>
 *  Once a file is parsed, the methods here create a direcetd graph that
 *  reflects the underlying data and returns it to the user.
 * </p>
 *
 * @author Elisha Peterson
 */
public final class EdgeListGraphIO extends AbstractGraphIO {

    // no instantiation
    private EdgeListGraphIO() { }

    // single instance
    private static final EdgeListGraphIO INSTANCE = new EdgeListGraphIO();
    
    /** Factory method @return instance of this IO class */
    public static EdgeListGraphIO getInstance() { return INSTANCE; }

    private static final FileNameExtensionFilter FILTER = new FileNameExtensionFilter("Edge list files (*.txt)", "txt", "");
    public javax.swing.filechooser.FileFilter getFileFilter() { return FILTER; }

    public Graph<Integer> importGraph(Map<Integer, double[]> locations, File file, GraphType type) {
        // stores the vertices and associated names
        TreeMap<Integer,String> vertices = new TreeMap<Integer,String>();
        // stores the edges/arcs
        ArrayList<Integer[]> edges = new ArrayList<Integer[]>();
        // stores the weights of edges, in the same order as the edges list
        ArrayList<Double> weights = new ArrayList<Double>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;
            try {
                int lineNumber = 0;
                while ((line = reader.readLine()) != null) {
                    if (line.matches("\\s*")) {
                        // ignore blank lines
                    } else {
                        // ignore spaces on either side of line
                        line = line.trim();
                        // expect this to be completely space delimited... we are only interested in the first 2 entries
                        String[] split = line.split("\\s+");
                        if (split.length > 2)
                            System.out.println("WARNING -- lines should not have more than 2 entries on line " + lineNumber + ": " + line);
                        Integer v1 = Integer.decode(split[0]);
                        Integer v2 = Integer.decode(split[1]);
                        if (!vertices.containsKey(v1)) vertices.put(v1, null);
                        if (!vertices.containsKey(v2)) vertices.put(v2, null);
                        edges.add(new Integer[]{v1, v2});
                        weights.add(1.0);
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
        return buildGraph(vertices, edges, weights, true);
    }

    @Override
    public GraphType saveGraph(Object go, Map<Object,Point2D.Double> positions, File file) {
        Graph<Integer> graph = null;
        try {
            graph = (Graph<Integer>) go;
        } catch (ClassCastException ex) {
            return null;
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            try {
                // write the edges; uses one edge per line
                if (graph instanceof WeightedGraph)
                    System.out.println("WARNING -- edge list format does not support edge weights");
                List<Integer> nodes = graph.nodes();
                for (Integer i1 : nodes)
                    for (Integer i2 : nodes)
                        if (graph.adjacent(i1, i2))
                            writer.write(i1 + " " + i2 + "\n");
            } finally {
                writer.close();
            }
        } catch (IOException ex) {
            System.err.println(ex);
            return null;
        }
        return GraphType.REGULAR;
    }
}