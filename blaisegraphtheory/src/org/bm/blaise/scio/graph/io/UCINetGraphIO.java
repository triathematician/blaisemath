/*
 * UCINetGraphIO.java
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
import java.util.Map.Entry;
import java.util.TreeMap;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.ValuedGraph;
import org.bm.blaise.scio.graph.WeightedGraph;
import util.FileNameExtensionFilter;

/**
 * <p>
 *  Basic support for UCINet graph format.
 * </p>
 *
 * @author Elisha Peterson
 */
public final class UCINetGraphIO extends AbstractGraphIO {

    // no instantiation
    private UCINetGraphIO() { }

    // single instance
    private static final UCINetGraphIO INSTANCE = new UCINetGraphIO();

    /** Factory method @return instance of this IO class */
    public static UCINetGraphIO getInstance() { return INSTANCE; }

    private static final FileNameExtensionFilter FILTER = new FileNameExtensionFilter("UCINet files (*.dat)", "dat");
    public javax.swing.filechooser.FileFilter getFileFilter() { return FILTER; }

    /** Specifies mode for data. */
    enum DataFormat {
        FULLMATRIX("fullmatrix"),
        EDGELIST1("edgelist1"),
        NODELIST1("nodelist1"),
        UNKNOWN("unknown");

        public String outputLine() { return "FORMAT = " + header.toUpperCase() + (this == FULLMATRIX ? " DIAGONAL PRESENT" : ""); }

        String header;
        /**
         * Construct with specified header, used to identify the mode in text files.
         * @param header the code used to denote the mode
         */
        DataFormat(String header) { this.header = header; }
    }

    @Override
    public Object importGraph(Map<Integer,double[]> locations, File file, GraphType type) {

        // stores the vertices and associated names
        TreeMap<Integer,String> vertices = new TreeMap<Integer,String>();

        // stores the edges/arcs
        ArrayList<Integer[]> edges = new ArrayList<Integer[]>();
        // stores the weights of edges, in the same order as the edges list
        ArrayList<Double> weights = new ArrayList<Double>();

        // whether resulting graph is directed
        boolean directed = false;

        // whether the header line "dl" has been found
        boolean foundDL = false;
        // tracks whether currently in the header
        boolean inHeader = true;
        // whether labels are currently being read
        boolean labelMode = false;
        // whether labels are embedded
        boolean labelsEmbedded = false;
        // tracks the current input mode
        DataFormat format = null;
        // tracks the line in the adjacency matrix for that mode
        int matrixLine = 1;
        // tracks the expected number of vertices from the "DL N=*" line
        int expectedOrder = -1;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;
            try {
                int lineNumber = 0;
                while ((line = reader.readLine()) != null) {
                    if (line.matches("\\s*")) {
                        // ignore blank lines or comment lines in extendedMode
                        lineNumber++;
                        continue;
                    }
                    // ignore spaces on either side of line
                    line = line.trim();
                    if (inHeader) {
                        String lineLower = line.toLowerCase();
                        labelMode = labelMode && line.matches("[_,A-za-z0-9\\s]*"); // labels are space or comma-delimited
                        if (labelMode) {
                            String[] split = line.split("[\\s,]+");
                            for (String s : split)
                                vertices.put(vertices.size(), s);
                            continue;
                        }
                        
                        if (lineLower.startsWith("dl"))
                            foundDL = true;

                        if (lineLower.matches("dl\\s+n\\s*=\\s*[0-9]+") || lineLower.matches("n\\s*=\\s*[0-9]+") ) {
                            int i1 = line.indexOf("=");
                            try {
                                expectedOrder = Integer.decode(line.substring(i1 + 1).trim());
                            } catch (Exception ex) {
                                notifyImportFailure(lineNumber, null, line, ex);
                            }
                        } else if (lineLower.startsWith("format")) {
                            format = parseDataFormat(lineLower);
                        } else if (lineLower.startsWith("labels")) {
                            if (lineLower.matches("labels\\s*:")) {
                                labelMode = true;
                            } else if (lineLower.matches("labels\\s*embedded\\s*:")) {
                                labelsEmbedded = true;
                            } else
                                notifyImportFailure(lineNumber, null, line, null);
                        } else if (lineLower.startsWith("data")) {
                            if (lineLower.matches("data\\s*:"))
                                inHeader = false;
                            else
                                notifyImportFailure(lineNumber, null, line, null);
                        }
                    } else {                        
                        try {
                            switch (format) {
                                case UNKNOWN:
                                    // ignore the line
                                    break;
                                case FULLMATRIX:
                                    if (!importLine_matrix(lineNumber, line, matrixLine, vertices, edges, weights, labelsEmbedded))
                                        matrixLine++;
                                    break;
                                case EDGELIST1:
                                    importLine_edge(lineNumber, line, vertices, edges, weights, labelsEmbedded);
                                    break;
                                case NODELIST1:
                                    importLine_edgelist(lineNumber, line, vertices, edges, weights, labelsEmbedded);
                                    break;
                            }
                        } catch (Exception ex) {
                            notifyImportFailure(lineNumber, format, line, ex);
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

        if (!foundDL)
            System.out.println("WARNING -- UCINet file imported without expected \"DL\" header line");
        if (expectedOrder != -1 && vertices.size() != expectedOrder)
            System.out.println("WARNING -- expected " + expectedOrder + " vertices but only found " + vertices.size());

        return buildGraph(vertices, edges, weights, directed);
    }

    /**
     * Notifies the user of a failure to import a line.
     * @param lineNumber the line number in the file
     * @param mode the import mode
     * @param line the string corresponding to the import line
     * @param exception the exception causing the failure
     */
    private static void notifyImportFailure(int lineNumber, DataFormat mode, String line, Exception ex) {
        System.out.println("Unable to import line " + lineNumber + " in mode " + mode + " due to exception " + ex + ": " + line);
    }

    /**
     * Checks a format line for a known data format, returning the data format if it does.
     * Otherwise, returns the <code>UNKNOWN</code> data format.
     * @param line <b>lowercase</b> line starting with "format = "
     */
    static DataFormat parseDataFormat(String line) {
        int i0 = line.indexOf("=");
        String code = line.substring(i0 + 1).trim();
        for (DataFormat df : DataFormat.values())
            if (code.equals(df.header.toLowerCase()))
                return df;
        if (code.matches("fullmatrix\\s+diagonal\\s+present"))
            return DataFormat.FULLMATRIX;
        return DataFormat.UNKNOWN;
    }

    /**
     * Checks to see if specified vertex is contained in map; if not contained, adds it.
     * @param vertex vertex to check
     * @param vertices the map of vertices
     */
    private static void checkForVertex(int vertex, Map<Integer,String> vertices) {
        if (!vertices.containsKey(vertex))
            vertices.put(vertex, null);
    }

    /**
     * Checks to see if specified vertex is contained in map; if not contained, adds it and
     * prints a warning.
     * @param vertex vertex to check
     * @param vertices the map of vertices
     * @return index of vertex
     */
    private static int checkForVertex(String vertex, Map<Integer,String> vertices) {
        if (vertices.containsValue(vertex)) {
            for (Entry<Integer,String> en : vertices.entrySet())
                if (vertex.equals(en.getValue()))
                    return en.getKey();
        } else {
            int i = 1;
            while (vertices.containsKey(i)) i++;
            vertices.put(i, vertex);
            return i;
        }
        throw new IllegalStateException("Should never be here!");
    }

    /**
     * Tests for a string representing a vertex... it should either be a number, indicating the index,
     * or a string label indicating the vertex's value.
     * @param s the vertex string
     * @param vertices the table of vertices
     * @param labelsOK whether labels are OK
     * @return index of the vertex
     */
    protected static int checkVertex(String s, Map<Integer, String> vertices, boolean labelsOK)
            throws NumberFormatException {
        s = s.trim();
        if (!labelsOK || s.matches("[0-9]+")) {
            int result = Integer.decode(s);
            checkForVertex(result, vertices);
            return result;
        } else if (s.matches("[_A-za-z][_A-za-z0-9]*"))
            return checkForVertex(s, vertices);
        return -1;
    }

    /**
     * Import an edge into a graph. If the specified edge contains vertices that are not
     * already on the list of vertices, it adds them to the collection.
     * @param lineNumber number of line in import file
     * @param line the line to import... should be in the format v0 v1 weight
     * @param vertices the map associating integer vertices to string labels of the vertices
     * @param edges list object containing the current edges in the graph
     * @param weights list object describing weights of edges
     * @param embeddedLabels flag describing whether labels are embedded
     * @throws NumberFormatException if unable to decode integer representation of a vertex #
     */
    static void importLine_edge(int lineNumber, String line,
                Map<Integer,String> vertices,
                List<Integer[]> edges,
                List<Double> weights,
                boolean embeddedLabels) {
        // expect this to be completely space delimited... we are only interested in the first 3 entries for now
        String[] split = line.split("\\s+", 4);

        // read vertices
        try {
            int v1 = checkVertex(split[0], vertices, embeddedLabels);
            int v2 = checkVertex(split[1], vertices, embeddedLabels);
            edges.add(new Integer[]{v1, v2});
        } catch (NumberFormatException ex) {
            notifyImportFailure(lineNumber, DataFormat.EDGELIST1, line, ex);
            return;
        }

        // read weights
        try {
            weights.add(split.length > 2 ? Double.valueOf(split[2]) : 1.0);
        } catch (NumberFormatException ex) {
            notifyImportFailure(lineNumber, DataFormat.EDGELIST1, line, ex);
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
     * @param weights list object describing weights of edges (a "1.0" will be added for each edge)
     * @param embeddedLabels flag describing whether labels are embedded
     * @throws NumberFormatException if unable to decode integer representation of a vertex #
     */
    static void importLine_edgelist(int lineNumber, String line,
            Map<Integer,String> vertices,
            List<Integer[]> edges,
            List<Double> weights,
            boolean embeddedLabels) {
        // should be completely space-delimited, each entry a vertex
        String[] split = line.split("\\s+");
        try {
            int v0 = checkVertex(split[0], vertices, embeddedLabels);
            for (int i=1; i < split.length; i++) {
                int v = checkVertex(split[i], vertices, embeddedLabels);
                if (v == -1) {
                    notifyImportFailure(lineNumber, DataFormat.NODELIST1, line, null);
                   return;
                }
                edges.add(new Integer[]{v0, v});
                weights.add(1.0);
            }
        } catch (NumberFormatException ex) {
            notifyImportFailure(lineNumber, DataFormat.NODELIST1, line, ex);
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
     * @param embeddedLabels flag describing whether labels are embedded
     * @return true if the imported line was a header line (list of labels), otherwise false
     * @throws NumberFormatException if unable to decode number representation within the matrix
     */
    static boolean importLine_matrix(int lineNumber, String line, int mxLine,
            Map<Integer, String> vertices,
            List<Integer[]> edges,
            List<Double> weights,
            boolean embeddedLabels)
            throws NumberFormatException {
        if (mxLine == 1 && embeddedLabels && line.matches("[_A-za-z][_A-za-z0-9]*([,\\s]+[_A-za-z][_A-za-z0-9]*)*")) {
            String[] split = line.split("[,\\s]+");
            for (String s : split)
                checkVertex(s, vertices, embeddedLabels);
            return true;
        }
        String[] split = line.split("\\s+");
        boolean start1 = false;
        int startLine = mxLine;
        if (embeddedLabels && split[0].matches("[_A-za-z][_A-za-z0-9]*")) {
            mxLine = checkVertex(split[0], vertices, embeddedLabels);
            if (mxLine == -1)
                notifyImportFailure(lineNumber, DataFormat.FULLMATRIX, line, null);
            start1 = true;
        } else {
            checkForVertex(mxLine, vertices);
            start1 = false;
        }
        for (int i = (start1 ? 1 : 0); i < split.length; i++) {
            int v2 = start1 ? i : i+1;
            double weight = Double.valueOf(split[i]);
            checkForVertex(v2, vertices);
            if (weight != 0.0) {
                edges.add(new Integer[] { mxLine, v2 } );
                weights.add(weight);
            }
        }
        return false;
    }

    @Override
    public GraphType saveGraph(Object go, Map<Object,Point2D.Double> positions, File file) {
        Graph<Integer> graph = null;
        try {
            graph = (Graph<Integer>) go;
        } catch (ClassCastException ex) {
            return null;
        }
        DataFormat format = DataFormat.FULLMATRIX;
        List<Integer> nodes = graph.nodes();
        int n = nodes.size();
        boolean weighted = graph instanceof WeightedGraph;
        WeightedGraph wg = weighted ? ((WeightedGraph) graph) : null;

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            try {
                // header
                writer.write("DL");
                writer.newLine();
                writer.write("N=" + n);
                writer.newLine();
                writer.write(format.outputLine());
                writer.newLine();

                // vertex labels (1 per line)
                if (graph instanceof ValuedGraph) {
                    writer.write("LABELS:");
                    writer.newLine();
                    ValuedGraph nvg = (ValuedGraph) graph;
                    for (int i = 0; i < n; i++) {
                        writer.write(nvg.getValue(nodes.get(i)).toString());
                        writer.newLine();
                    }
                }

                // write the edges; uses one edge per line by default
                writer.write("DATA:");
                writer.newLine();
                switch (format) {
                    case FULLMATRIX:
                        for (Integer i1 : nodes) {
                            for (Integer j1 : nodes) {
                                writer.write(" ");
                                writer.write( graph.adjacent(i1, j1)
                                        ? "" + (weighted ? wg.getWeight(i1, j1) : 1)
                                        : "0" );
                            }
                            writer.newLine();
                        }
                        break;

                    case EDGELIST1:
                        for (Integer i1 : nodes) {
                            for (Integer j1 : nodes) {
                                if (!graph.isDirected() && j1 < i1) continue;
                                if (graph.adjacent(i1, j1)) {
                                    writer.write(i1 + " " + j1 +
                                            (weighted ? (" " + wg.getWeight(i1, j1)) : ""));
                                    writer.newLine();
                                }
                            }
                        }
                        break;

                    case NODELIST1:
                        // assume graph is unweighted
                        if (weighted)
                            System.out.println("WARNING -- saving weighted graph using node list format (weights will be removed)");
                        for (Integer i1 : nodes) {
                            StringBuilder line = new StringBuilder(i1);
                            for (Integer j1 : nodes) {
                                if (graph.adjacent(i1, j1))
                                    line.append(" ").append(j1);
                            }
                            writer.append(line);
                            writer.newLine();
                        }
                        break;
                }
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