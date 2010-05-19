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
import java.util.Arrays;
import org.bm.blaise.scio.graph.SimpleGraph;

/**
 * <p>
 *   This class loads in a simple graph w/ two integer vertices per line, separated by a space.
 * </p>
 * @author Elisha Peterson
 */
public class SimpleGraphIO {

    /** Specifies expected mode for next input line. */
    enum ImportMode {
        /** Descriptive info about the graph */
        DESCRIPTION,
        /** A vertex, possibly with a name. */
        VERTICES,
        /** An arc */
        ARCS,
        /** An edge (possibly weighted) */
        EDGES;
    }

    /** 
     * Reads in and returns a graph file
     * @param file file containing information of the graph 
     * @return the graph data structure
     */
    public static SimpleGraph importSimpleGraph(URL file) {
        return importSimpleGraph(new File(file.getFile()));
    }
    /*
     * Reads in and returns a graph file
     * @param file file containing information of the graph
     * @return the graph data structure
     */
    public static SimpleGraph importSimpleGraph(File file) {
        ImportMode mode = ImportMode.EDGES;
        SimpleGraph result = new SimpleGraph();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;
            String[] split;
            try {
                int lineNumber = 0;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.startsWith("*")) {
                        String adjusted = line.substring(1).toUpperCase();
                        if (adjusted.startsWith("DESCRIPTION"))
                            mode = ImportMode.DESCRIPTION;
                        else if (adjusted.startsWith("VERTICES"))
                            mode = ImportMode.VERTICES;
                        else if (adjusted.startsWith("ARCS"))
                            mode = ImportMode.ARCS;
                        else if (adjusted.startsWith("EDGES"))
                            mode = ImportMode.EDGES;
                        else
                            System.out.println("importSimpleGraph: unable to import line " + lineNumber + ": " + line);
                    } else if (line.startsWith("%")) {
                        // ignore this as a comment
                    } else {
                        split = line.split("\\s+");
                        try {
                            switch (mode) {
                                case DESCRIPTION:
                                    break;
                                case VERTICES:
                                    importVertex(result, split);
                                    break;
                                case EDGES:
                                case ARCS:
                                    importEdge(result, split);
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
        return result;
    }

    /**
     * Imports a vertex into the graph. The first argument is the vertex #, the second is the name.
     * The name is optional, and may or may not be surrounded by quotes ("...")
     * @param graph the graph to import into
     * @param vertex the vertex to import... first argument is vertex #, second is name (optional)
     * @throws NumberFormatException if unable to decode integer representation of vertex #
     */
    static void importVertex(SimpleGraph graph, String[] vertex) throws NumberFormatException {
        if (vertex == null || vertex.length < 1)
            throw new IllegalArgumentException("importVertex: too few arguments in array " + Arrays.toString(vertex));
        if (vertex.length == 1)
            graph.addVertex(Integer.decode(vertex[0]));
        else {
            String name = vertex[1].trim();
            if (name.startsWith("\"") && name.endsWith("\""))
                name = name.substring(1, name.length()-1);
            graph.addVertex(Integer.decode(vertex[0]), name);
        }
    }

    /**
     * Import an edge into a graph
     * @param graph the graph to import into
     * @param edge the edge to import
     * @throws NumberFormatException if unable to decode integer representation of vertex #
     */
    static void importEdge(SimpleGraph graph, String[] edge) throws NumberFormatException {
        if (edge == null || edge.length < 2)
            throw new IllegalArgumentException("importEdge: too few arguments in array " + Arrays.toString(edge));
        graph.addEdge(Integer.decode(edge[0]), Integer.decode(edge[1]));
    }    

}