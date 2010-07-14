/*
 * PajekGraphIO.java
 * Created Jul 9, 2010
 */

package org.bm.blaise.scio.graph.io;

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
import org.bm.blaise.scio.graph.LongitudinalGraph;
import org.bm.blaise.scio.graph.ValuedGraph;
import org.bm.blaise.scio.graph.WeightedGraph;

/**
 * <p>
 *  Provides input/output for text files describing graphs in a simplified version of the pajek .NET format.
 *  Some features here are not in the <b>strict</b> Pajek format, and are indicated in <font style="color:red">red</font>.
 *  The modes supported are summarized below. Each mode is preceded by a header line (such as <code>*Vertices 10</code>)
 *  that switches the mode, and after the header several lines describing the vertices, arcs, etc. of the graph.
 *  The supported line formats are described within the list below:
 *   <li><font style="color:red">
 *      Description (<code>*Description</code>)
 *      <br/>Any lines input in this mode are ignored. It is intended to annotate the data source, etc. within the file.
 *   </font></li>
 *  <li>Vertices (section denoted by <code>*Vertices n</code>, where <code>n</code> is the number of nodes)
 *      <br/>Input lines have the form <code>vn label</code>, where <code>vn</code> is the (integer) index of the vertex and
 *          <code>label</code> is its label. Labels may either be enclosed in quotes or, if not, are parsed up to the space.
 *      <br/>For example, <code>1 ABC</code> and <code>1 "A B C"</code> are both valid vertex input lines.
 *  </li>
 *  <li>Arcs = directed edges (section denoted by <code>*Arcs</code>)
 *      <br/>Input lines have the form <code>v1 v2 value</code>, where <code>v1</code> is the first vertex of the arc,
 *          <code>v2</code> is the second, and <code>value</code> is a numeric value associated with the arc (e.g. a weight).
 *      <br/>For example, <code>1 2 2.35</code> is a valid arc input line. The value may be any real number, possibly negative.
 * </li>
 *  <li>Edges = undirected edges (section denoted by <code>*Edges</code>)
 *      <br/>Input lines have the same format as for arcs here.
 * </li>
 *  <li>Matrix = adjacency matrix <i>or</i> weighted matrix, of either a directed or an undirected graph (section denoted by <code>*Matrix</code>)
 *      <br/>The mode line must be followed by <code>n</code> lines, each with <code>n</code> space-separated entries describing the
 *          adjacency matrix. The <i>i</i>th row corresponds to the arcs pointing away from vertex <code>i</code>. Values may be
 *          restricted to just 0 and 1 (giving an unweighted graph), or may be weighted.
 * </li>
 *  <li>Arcs list = list of vertices adjacent to each vertex (section denoted by <code>*Arcslist</code>)
 *      <br/>Input lines have the form <code>v1 w1 w2 w3 w4 ...</code>, where <code>v1</code> is the first vertex in the set
 *          and <code>w1, w2, ...</code> are all vertices that are connected to <code>v1</code>. Thus, the arcs encoded
 *          are <code>v1->w1</code>, <code>v1->w2</code>, etc.
 *  </li>
 *  <li>Edges list = list of vertices adjacent to each vertex, undirected graph only (section denoted by <code>*Edgeslist</code>)
 *      <br/>Input lines have the same format as for "arcs list".
 *  </li>
 * </ul>
 * </p>
 * <p>
 *  In addition to the above strict modes, the following features are noted:
 *  <ul>
 *      <li>Case in section headers is ignored: <code>*VERTICES</code> works just as well as <code>*Vertices</code>.</li>
 *      <li>Only the first section describing a collection of edges or arcs is used; the remainder are ignored.</li>
 *      <li>Any additional options used by <i>Pajek</i> to describe appearance may be given at the end of an input line, but are ignored.</li>
 *      <li><font style="color:red">Any line beginning with "<code>%</code>" represents a comment and is ignored.</font></li>
 *      <li>Empty lines or lines consisting of just spaces in the file are ignored.</li>
 *  </ul>
 * </p>
 * <p>
 *  Once a file is parsed, the methods here create a graph (directed or undirected, weighted or unweighted edges) that
 *  reflects the underlying data and returns it to the user.
 * </p>
 *
 * @author Elisha Peterson
 */
public final class PajekGraphIO extends GraphIO {

    /** Global flag telling whether to warn when an undeclared vertex is read from file */
    static boolean WARN_UNDECLARED_VERTEX = false;
    /** Flag describing whether "extended" features are allowed, e.g. comments in file, *DESCRIPTION mode, etc. */
    boolean extendedMode;

    // no instantiation
    private PajekGraphIO(boolean x) { this.extendedMode = x; }

    // single instance
    private static final PajekGraphIO INSTANCE = new PajekGraphIO(false);
    private static final PajekGraphIO INSTANCE_X = new PajekGraphIO(true);

    /** Factory method @return instance of this IO class */
    public static PajekGraphIO getInstance() { return INSTANCE; }
    /** Factory method @return extended version of this IO class */
    public static PajekGraphIO getExtendedInstance() { return INSTANCE_X; }


    /** Specifies expected mode for next input line. */
    enum ImportMode {
        /** Description. */
        DESCRIPTION(true, "Description", false, false),
        /** A vertex, possibly with a name. */
        VERTICES(false, "Vertices", false, false),
        /** An arc */
        ARCS(false, "Arcs", true, true),
        /** An edge (possibly weighted) */
        EDGES(false, "Edges", true, false),
        /** Adjacency matrix mode */
        MATRIX(false, "Matrix", true, true),
        /** A list of arcs on each line */
        ARCSLIST(false, "Arcslist", true, true),
        /** A list of edges on each line */
        EDGELIST(false, "Edgeslist", true, false),
        /** Unknown mode */
        UNKNOWN(false, "Unknown", false, false);

        boolean requiresXMode;
        boolean edgeMode;
        boolean directedMode;
        String header;
        /**
         * Construct with specified header, used to identify the mode in text files.
         * @param requiresXMode whether the mode is an "extended mode" only (true indicates yes, false indicates its always okay to use)
         * @param header the code used to denote the mode
         * @param edgeMode whether the mode imports edges
         * @param directedMode whether the mode (if importing edges) imports <i>directed</i> edges
         */
        ImportMode(boolean requiresXMode, String header, boolean edgeMode, boolean directedMode) {
            this.requiresXMode = requiresXMode;
            this.header = header;
            this.edgeMode = edgeMode;
            this.directedMode = directedMode;
        }
    }

    public Graph<Integer> importGraph(File file) {        
        // stores the vertices and associated names
        TreeMap<Integer,String> vertices = new TreeMap<Integer,String>();
        // stores the edges/arcs
        ArrayList<Integer[]> edges = new ArrayList<Integer[]>();
        // stores the weights of edges, in the same order as the edges list
        ArrayList<Double> weights = new ArrayList<Double>();
        // whether resulting graph is directed
        boolean directed = false;

        // tracks the current input mode
        ImportMode mode = ImportMode.UNKNOWN;
        // logged when an edge mode has been used
        ImportMode edgeModeUsed = null;
        // tracks the line in the adjacency matrix for that mode
        int matrixLine = 1;
        // tracks the expected number of vertices from the "*Vertices" line
        int expectedOrder = -1;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;
            try {
                int lineNumber = 0;
                while ((line = reader.readLine()) != null) {
                    if (line.matches("\\s*") || (extendedMode && line.trim().startsWith("%"))) {
                        // ignore blank lines or comment lines in extendedMode
                        lineNumber++;
                        continue;
                    }
                    // ignore spaces on either side of line
                    line = line.trim();
                    if (line.startsWith("*")) {
                        // change modes
                        ImportMode newMode = parseInputMode(line, extendedMode);
                        if (newMode == null || newMode == ImportMode.UNKNOWN) {
                            System.out.println("importSimpleGraph: unable to import line " + lineNumber + ": " + line);
                            newMode = ImportMode.UNKNOWN;
                        }
                        // only use first case of an edge mode, unless it is empty
                        // subsequent cases switch to unknown import mode and lines are ignored
                        if (mode.edgeMode && edges.size() > 0)
                            edgeModeUsed = mode;
                        mode = (edgeModeUsed != null && newMode.edgeMode) ? ImportMode.UNKNOWN : newMode;
                        // initialize settings for particular modes
                        if (mode == ImportMode.VERTICES)
                            expectedOrder = parseVerticesModeNumber(line);
                        else if (mode == ImportMode.MATRIX)
                            matrixLine = 1;
                    } else {
                        // each line is space-delimited, so automatically split it by spaces
                        try {
                            switch (mode) {
                                case DESCRIPTION:
                                case UNKNOWN:
                                    // ignore the line
                                    break;
                                case VERTICES:
                                    importLine_vertex(lineNumber, line, vertices);
                                    break;
                                case ARCS:
                                case EDGES:
                                    importLine_edge(lineNumber, line, vertices, edges, weights);
                                    break;
                                case ARCSLIST:
                                case EDGELIST:
                                    importLine_edgelist(lineNumber, line, vertices, edges, weights);
                                    break;
                                case MATRIX:
                                    importLine_matrix(lineNumber, line, matrixLine, vertices, edges, weights);
                                    matrixLine++;
                                    break;
                            }
                        } catch (Exception ex) {
                            notifyImportFailure(lineNumber, mode, line, ex);
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

        if (expectedOrder != -1 && vertices.size() != expectedOrder) {
            System.out.println("WARNING -- expected " + expectedOrder + " vertices but only found " + vertices.size());
        }
        if (edgeModeUsed == null)
            edgeModeUsed = mode;

        directed = edgeModeUsed == ImportMode.MATRIX ? !symmetricMatrix(edges, weights) : edgeModeUsed.directedMode;
        return buildGraph(vertices, edges, weights, directed);
    }

    @Override
    public void saveGraph(Graph<Integer> graph, File file) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            try {
                // write the vertices
                List<Integer> nodes = graph.nodes();
                writer.write("*Vertices " + graph.order() + "\n");
                if (graph instanceof ValuedGraph) {
                    ValuedGraph nvg = (ValuedGraph) graph;
                    for (Integer i : nodes) {
                        Object value = nvg.getValue(i);
                        writer.write(i + (value == null ? "" : " \"" + value.toString()) + "\"\n");
                    }
                } else {
                    for (Integer i : graph.nodes())
                        writer.write(i + "\n");
                }
                // write the edges; uses one edge per line by default
                writer.write((graph.isDirected() ? "*Arcs" : "*Edges") + "\n");
                if (graph instanceof WeightedGraph) {
                    WeightedGraph wg = (WeightedGraph) graph;
                    for (Integer i1 : nodes)
                        for (Integer i2 : nodes) {
                            if (!graph.isDirected() && i2 < i1) continue;
                            if (!wg.adjacent(i1, i2)) continue;
                            Object weight = wg.getWeight(i1, i2);
                            writer.write(i1 + " " + i2 + (weight == null ? "" : " " + weight.toString()) + "\n");
                        }
                } else {
                    for (Integer i1 : nodes)
                        for (Integer i2 : nodes) {
                            if (!graph.isDirected() && i2 < i1) continue;
                            if (!graph.adjacent(i1, i2)) continue;
                            writer.write(i1 + " " + i2 + "\n");
                        }
                }
            } finally {
                writer.close();
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    public LongitudinalGraph<Integer> importLongitudinalGraph(File file) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void saveLongitudinalGraph(LongitudinalGraph<Integer> graph, File file) {
        throw new UnsupportedOperationException("Not supported yet.");
    }




    /**
     * Notifies the user of a failure to import a line.
     * @param lineNumber the line number in the file
     * @param mode the import mode
     * @param line the string corresponding to the import line
     * @param exception the exception causing the faiulre
     */
    private static void notifyImportFailure(int lineNumber, ImportMode mode, String line, Exception ex) {
        System.out.println("Unable to import line " + lineNumber + " in mode " + mode + " due to exception " + ex + ": " + line);
    }

    /**
     * Checks for a line matching one of the specified headers for an import mode,
     * returning the mode if it does. Otherwise, returns the <code>UNKNOWN</code> input mode.
     * @param line import line starting with a "*"
     */
    static ImportMode parseInputMode(String line, boolean inXMode) {
        String code = line.substring(1).trim().toLowerCase().split("\\s+", 2)[0];
        for (ImportMode im : ImportMode.values())
            if (code.equals(im.header.toLowerCase())) {
                ImportMode found = (im.requiresXMode && !inXMode) ? ImportMode.UNKNOWN : im;
                return found;
            }
        return ImportMode.UNKNOWN;
    }

    /**
     * Checks for the number of vertices in a header line. Uses the <code>Integer.decode</code>
     * method to parse the number.
     * @return number of vertices specified in the line, or -1 if no number is found
     * @throws NumberFormatException if the number is improperly formatted
     */
    static int parseVerticesModeNumber(String line) throws NumberFormatException {
        line = line.substring(1).trim().toLowerCase();
        String[] split = line.split("\\s+");
        for (int i = 0; i < split.length; i++)
            if (split[i].startsWith(ImportMode.VERTICES.header.toLowerCase())) {
                if (i == split.length - 1)
                    return -1;
                return Integer.decode(split[i+1]);
            }
        throw new IllegalStateException("parseVerticesModeNumber called for non-vertex-mode-line: " + line);
    }

    /**
     * Imports a vertex into the graph. The first argument is the vertex #, the second is the name.
     * The name is optional, and may or may not be surrounded by quotes ("...")
     * @param lineNumber number of line in import file
     * @param line the line to import... first argument is vertex #, second is name (optional)
     * @param vertices the map associating integer vertices to string labels of the vertices
     * @throws NumberFormatException if unable to decode integer representation of vertex #
     */
    static void importLine_vertex(int lineNumber, String line, Map<Integer, String> vertices)
            throws NumberFormatException {
        Integer index = null;
        String name = null;
        String[] split = line.split("\\s+", 2);
        index = Integer.decode(split[0]);
        if (split.length > 1) {
            if (split[1].startsWith("\"")) {
                int i2 = split[1].indexOf("\"", 1);
                if (i2 != -1)
                    name = split[1].substring(1, i2);
                else
                    System.out.println("WARNING -- line " + lineNumber + " has improperly formatted vertex label: " + line);
            } else
                name = split[1].split("\\s+", 2)[0];
        }
        vertices.put(index, name);
    }

    /**
     * Checks to see if specified vertex is contained in map; if not contained, adds it and
     * prints a warning.
     * @param vertex vertex to check
     * @param vertices the map of vertices
     * @param lineNumber number of line in import file
     * @param line the line to import... first argument is vertex #, second is name (optional)
     */
    private static void checkForVertex(int vertex, Map<Integer,String> vertices, int lineNumber, String line) {
        if (!vertices.containsKey(vertex)) {
            vertices.put(vertex, null);
            if (WARN_UNDECLARED_VERTEX)
                System.out.println("WARNING -- found undeclared vertex " + vertex + " on line " + lineNumber + ": " + line);
        }
    }

    /**
     * Import an edge into a graph. If the specified edge contains vertices that are not
     * already on the list of vertices, it adds them to the collection.
     * @param lineNumber number of line in import file
     * @param line the line to import... should be in the format v0 v1 weight
     * @param vertices the map associating integer vertices to string labels of the vertices
     * @param edges list object containing the current edges in the graph
     * @param weights list object describing weights of edges
     * @throws NumberFormatException if unable to decode integer representation of a vertex #
     */
    static void importLine_edge(int lineNumber, String line, Map<Integer,String> vertices, List<Integer[]> edges, List<Double> weights)
            throws NumberFormatException {
        // expect this to be completely space delimited... we are only interested in the first 3 entries
        String[] split = line.split("\\s+", 4);
        Integer v1 = Integer.decode(split[0]);
        Integer v2 = Integer.decode(split[1]);
        checkForVertex(v1, vertices, lineNumber, line);
        checkForVertex(v2, vertices, lineNumber, line);
        edges.add(new Integer[]{v1, v2});
        try {
            weights.add(split.length > 2 ? Double.valueOf(split[2]) : 1.0);
        } catch (NumberFormatException ex) {
            System.out.println("WARNING -- found improperly formatted weight " + split[2] + " on line " + lineNumber + ": " + line);
            weights.add(1.0);
        }
    }
    
    /**
     * Import an edge into a graph. If the specified edge contains vertices that are not
     * already on the list of vertices, it adds them to the collection.
     * @param lineNumber number of line in import file
     * @param line the line to import... should be in the format v0 v v v v v (all edges pointing from v0 elsewhere)
     * @param vertices the map associating integer vertices to string labels of the vertices
     * @param edges list object containing the current edges in the graph
     * @param weights list object describing weights of edges
     * @throws NumberFormatException if unable to decode integer representation of a vertex #
     */
    static void importLine_edgelist(int lineNumber, String line, Map<Integer,String> vertices, List<Integer[]> edges, List<Double> weights)
            throws NumberFormatException {
        // should be completely space-delimited, each entry a vertex
        String[] split = line.split("\\s+");
        int v0 = Integer.decode(split[0]);
        checkForVertex(v0, vertices, lineNumber, line);
        for (int i=1; i < split.length; i++) {
            int v = Integer.decode(split[i]);
            checkForVertex(v, vertices, lineNumber, line);
            edges.add(new Integer[]{v0, v});
            weights.add(1.0);
        }
    }

    /**
     * Import a line of an adjacency matrix into a graph. The order of vertices
     * is assumed to correspond to the order of vertices created earlier (if longer,
     * vertices are automatically added). Edges are created for each <i>non-zero</i>
     * value in the matrix, and added to the current list of edges. The corresponding
     * non-zero entry is added to the mapping of edge weights.
     * @param lineNumber number of line in import file
     * @param line the line to import... should be in the format # # # # # ... #
     * @param mxLine the line # in the adjacency matrix, which should start at 1
     * @param vertices the map associating integer vertices to string labels of the vertices
     * @param edges list object containing the current edges in the graph
     * @param weights list object describing weights of edges
     * @throws NumberFormatException if unable to decode number representation within the matrix
     */
    static void importLine_matrix(int lineNumber, String line, int mxLine, Map<Integer, String> vertices, List<Integer[]> edges, List<Double> weights)
            throws NumberFormatException {
        String[] split = line.split("\\s+");
        checkForVertex(mxLine, vertices, lineNumber, line);
        for (int i = 0; i < split.length; i++) {
            double weight = Double.valueOf(split[i]);
            checkForVertex(i+1, vertices, lineNumber, line);
            if (weight != 0.0) {
                edges.add(new Integer[] { mxLine, i+1 } );
                weights.add(weight);
            }
        }
    }
}