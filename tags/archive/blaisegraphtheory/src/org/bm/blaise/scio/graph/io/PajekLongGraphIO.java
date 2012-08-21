/*
 * PajekLongGraphIO.java
 * Created Jul 6, 2010
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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.ListLongitudinalGraph;
import org.bm.blaise.scio.graph.LongitudinalGraph;
import org.bm.blaise.scio.graph.WeightedGraph;
import org.bm.blaise.scio.graph.io.PajekGraphIO.ImportMode;
import util.io.FileNameExtensionFilter;

/**
 * <p>
 * Extends the features of <code>PajekGraphIO</code> to support loading files with
 * multiple networks. These files are stored as longitudinal graphs, comprised of
 * pointers to multiple graphs. Here, the data is assumed to consist of a single set
 * of vertices, and multiple sets of edges/adjacencies.
 * </p>
 * <p>
 * Uses the same mode formats as <code>PajekGraphIO</code>... see the documentation there.
 * The exception is that the "edges", "arcs", and "adjacency" modes now assume that a
 * time flag is also specified, e.g. the line should be something like "*EDGES t=5"
 * </p>
 *
 * @see PajekGraphIO
 *
 * @author elisha
 */
public class PajekLongGraphIO extends AbstractGraphIO {

    // no instantiation
    private PajekLongGraphIO() {}

    // single instance
    private static final PajekLongGraphIO INSTANCE = new PajekLongGraphIO();

    /** Factory method @return instanceo of this IO class */
    public static PajekLongGraphIO getInstance() { return INSTANCE; }

    private static final FileNameExtensionFilter FILTER = new FileNameExtensionFilter("Extended/longitudinal Pajek file (*.net, *.netx, *.netl)", "net", "netx", "netl");
    public javax.swing.filechooser.FileFilter getFileFilter() { return FILTER; }
    
    /*
     * Reads in and returns a longitudinal graph file
     * @param file file containing information of the longitudinal graph
     * @return the longitudinal graph data structure, possibly with node labels attached to the vertices
     */
    @Override
    public Object importGraph(Map<Integer,double[]> locations, File file, GraphType type) {
        // stores the time associated with imported graphs
        Double curTime = Double.NaN;

        // stores the resulting list of graphs
        ListLongitudinalGraph<Integer> result = new ListLongitudinalGraph<Integer>();
        // stores the names of vertices
        TreeMap<Integer,String> vertices = new TreeMap<Integer,String>();
        // stores times corresponding to vertices
        TreeMap<Integer,List<double[]>> times = new TreeMap<Integer,List<double[]>>();

        // stores the edges
        ArrayList<Integer[]> edges = new ArrayList<Integer[]>();
        // stores the weights of edges
        ArrayList<Double> weights = new ArrayList<Double>();
        // stores the times corresponding to edges
        ArrayList<List<double[]>> eTimes = new ArrayList<List<double[]>>();

        // Tracks the current input mode
        ImportMode mode = ImportMode.UNKNOWN;
        // tracks the line in the adjacency matrix
        int matrixLine = 1;
        
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;
            try {
                int lineNumber = 0;
                while ((line = reader.readLine()) != null) {
                    if (line.matches("\\s*")) { // ignore blank lines
                        lineNumber++;
                        continue;
                    }
                    line = line.trim();
                    if (line.startsWith("*")) {
                        ImportMode newMode = PajekGraphIO.parseInputMode(line, true);
                        if (newMode == null)
                            System.out.println("LongitudinalGraphIO.importGraph: unable to import line " + lineNumber + ": " + line);
                        else
                            mode = newMode;
                        if (mode == ImportMode.MATRIX)
                            matrixLine = 1;
                        if (mode == ImportMode.ARCS || mode == ImportMode.EDGES || mode == ImportMode.MATRIX) {
                            // store the current graph
                            if (!curTime.isNaN())
                                result.addGraph(AbstractGraphIO.buildGraph(vertices, edges, weights, false), curTime);
                            weights.clear();
                            edges.clear();
                            // look for associated time in addition to the associated input
                            if (line.contains("=")) {
                                try {
                                    curTime = parseInputTime(line, mode);
                                } catch (NumberFormatException ex) {
                                    curTime = Double.NaN;
                                }
                            } else { 
                                curTime = Double.NaN;
                            }
                        }
                    } else if (line.startsWith("%")) {
                        // ignore this as a comment
                    } else {
                        try {
                            switch (mode) {
                                case DESCRIPTION:
                                case UNKNOWN:
                                    break;
                                case VERTICES:
                                    PajekGraphIO.importLine_vertex(lineNumber, line, vertices, locations, times);
                                    break;
                                case EDGES:
                                case ARCS:
                                    PajekGraphIO.importLine_edge(lineNumber, line, vertices, edges, weights, eTimes);
                                    break;
                                case MATRIX:
                                    PajekGraphIO.importLine_matrix(lineNumber, line, matrixLine, vertices, edges, weights);
                                    matrixLine++;
                                    break;
                            }
                        } catch (Exception ex) {
                            System.out.println("LongitudinalGraphIO.importGraph: Unable to import line " + lineNumber + " in mode " + mode + ": " + line);
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
        if (!curTime.isNaN())
            result.addGraph(AbstractGraphIO.buildGraph(vertices, edges, weights, false), curTime);
        return result;
    } // importGraph


    /**
     * Finds the time associated with a particular header within specified string.
     * It should occur as "*Edges t=5.5" for example.
     * @param mode the mode matching the header line
     * @return the time stored in the line, i.e. what goes along with "t=**"
     * @throws NumberFormatException if the time is not in the proper format.
     */
    static double parseInputTime(String line, ImportMode mode) throws NumberFormatException {
        if (!(mode == ImportMode.EDGES || mode == ImportMode.ARCS || mode == ImportMode.MATRIX))
            throw new UnsupportedOperationException("LongitudinalGraphIO.parseInputTime only supports EDGES, ARCS, and ADJACENCY modes.");
        if (!line.toLowerCase().contains(mode.header.toLowerCase()))
            throw new UnsupportedOperationException("LongitudinalGraphIO.parseInputTime only supports EDGES, ARCS, and ADJACENCY modes.");

        line = line.toLowerCase();
        line = line.substring(line.indexOf(mode.header.toLowerCase()) + mode.header.length()).trim();
        if (!line.contains("="))
            throw new UnsupportedOperationException("LongitudinalGraphIO.parseInputTime: expected an = on the input line!");
        String[] split = line.split("=");
        String timePart = split[0].trim().toLowerCase();
        split = split[1].split("\\s+"); // ignores everything after the first space after whatever follows the equals sign
        String numberPart = split[0].trim().toLowerCase();
        if (!(timePart.equals("t") || timePart.equals("time")))
            throw new UnsupportedOperationException("LongitudinalGraphIO.parseInputTime: expected 't' or 'time' on the time line (e.g. t=5.5)");
        return Double.valueOf(numberPart);
    } // parseInputTime


    @Override
    public GraphType saveGraph(Object go, Map<Object,Point2D.Double> positions, File file) {
        LongitudinalGraph<Integer> lg = null;
        try {
            lg = (LongitudinalGraph<Integer>) go;
        } catch (ClassCastException ex) {
            return null;
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            try {
                // write the vertices
                Collection<Integer> nodes = lg.getAllNodes();
                writer.write("*Vertices " + nodes.size());
                writer.newLine();
                int i = 0;
                for (Integer node : nodes) {
                    writer.write(node.toString() + " " + node.toString());
                    if (positions != null && positions.get(node) != null)
                        writer.write(" " + positions.get(node).x + " " + positions.get(node).y);
                    writer.newLine();
                    i++;
                }
                // write the edges; uses one edge per line by default
                Graph<Integer> graph = null;
                for (double t : lg.getTimes()) {
                    graph = lg.slice(t, true);
                    nodes = graph.nodes();
                    writer.write(graph.isDirected() ? "*Arcs" : "*Edges");
                    writer.write(" t=" + t);
                    writer.newLine();
                    if (graph instanceof WeightedGraph) {
                        WeightedGraph wg = (WeightedGraph) graph;
                        for (Integer i1 : nodes)
                            for (Integer i2 : nodes) {
                                if (!graph.isDirected() && i2 < i1) continue;
                                if (!wg.adjacent(i1, i2)) continue;
                                Object weight = wg.getWeight(i1, i2);
                                writer.write(i1 + " " + i2 + (weight == null ? "" : " " + weight.toString()));
                                writer.newLine();
                            }
                    } else {
                        for (Integer i1 : nodes)
                            for (Integer i2 : nodes) {
                                if (!graph.isDirected() && i2 < i1) continue;
                                if (!graph.adjacent(i1, i2)) continue;
                                writer.write(i1 + " " + i2);
                                writer.newLine();
                            }
                    }
                }
            } finally {
                writer.close();
            }
        } catch (IOException ex) {
            System.err.println(ex);
            return null;
        }
        return GraphType.LONGITUDINAL;
    } // saveGraph

}
