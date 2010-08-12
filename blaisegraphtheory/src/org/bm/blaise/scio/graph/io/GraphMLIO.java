/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.scio.graph.io;

import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.ValuedGraph;
import org.bm.blaise.scio.graph.WeightedGraph;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import util.FileNameExtensionFilter;

/**
 * Basic support for the <i>graphML</i> XML file structure.
 * @author elisha
 */
public class GraphMLIO extends AbstractGraphIO {

    /** Used to construct the document */
    DocumentBuilder builder = null;

    // no instantiation
    private GraphMLIO() { }

    private static final GraphMLIO INSTANCE = new GraphMLIO();
    private static final FileNameExtensionFilter FILTER = new FileNameExtensionFilter("GraphML files (*.xml, *.graphml)", "xml", "graphml");

    /** Factory method @return instance of this IO class */
    public static GraphMLIO getInstance() { return INSTANCE; }

    public FileFilter getFileFilter() { return FILTER; }

    @Override
    public Object importGraph(Map<Integer, double[]> locations, File file, GraphType type) {
        // whether resulting graph is directed
        boolean directed = false;

        try {
            if (builder == null) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setValidating(true);
                factory.setNamespaceAware(false);
                builder = factory.newDocumentBuilder();
            }
        } catch (ParserConfigurationException e) {
            System.out.println("The underlying parser does not support the requested features.");
        } catch (FactoryConfigurationError e) {
            System.out.println("Error occurred obtaining Document Builder Factory.");
        }
        try {
            Document doc = builder.parse(file);
            NodeList nl = doc.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++)
                if (nl.item(i).getNodeName().equals("graphml"))
                    return importGraph(locations, nl.item(i), type);
            return null;
        } catch (SAXException ex) {
            Logger.getLogger(GraphMLIO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GraphMLIO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    Object importGraph(Map<Integer, double[]> locations, Node mlNode, GraphType type) {
        // stores the vertices and associated names
        TreeMap<Integer,String> vertices = new TreeMap<Integer,String>();
        // stores the vertices and associated images
        TreeMap<Integer,String> images = new TreeMap<Integer,String>();
        // stores points corresponding to vertices
        locations.clear();
        // stores times corresponding to vertices
        TreeMap<Integer,List<double[]>> times = new TreeMap<Integer,List<double[]>>();

        // stores the edges/arcs
        ArrayList<Integer[]> edges = new ArrayList<Integer[]>();
        // stores the weights of edges, in the same order as the edges list
        ArrayList<Double> weights = new ArrayList<Double>();
        // stores the times corresponding to edges
        ArrayList<List<double[]>> eTimes = new ArrayList<List<double[]>>();

        // graph name
        String graphName = null;
        // directed status
        boolean directed = false;

        NodeList docNodes = null;
        Node curNode = mlNode;
        NamedNodeMap attributes = null;

        docNodes = mlNode.getChildNodes();
        int iGraph = -1;
        for (int i = 0; i < docNodes.getLength(); i++) {
            curNode = docNodes.item(i);
            if (curNode.getNodeName().equals("key")) {
                System.out.println("found key: " + curNode);
            } else if (curNode.getNodeName().equals("graph")) {
                attributes = curNode.getAttributes();
                Node gn = attributes.getNamedItem("id");
                if (gn != null) graphName = gn.getNodeValue();
                Node ed = attributes.getNamedItem("edgedefault");
                if (ed != null) {
                    String eds = ed.getNodeValue();
                    directed = eds.equals("directed");
                }
                iGraph = i;
            }
        }
        docNodes = docNodes.item(iGraph).getChildNodes();

        int vIndex = 0;
        for (int i = 0; i < docNodes.getLength(); i++) {
            curNode = docNodes.item(i);
            if (curNode.getNodeType() == Node.ELEMENT_NODE) {
                attributes = curNode.getAttributes();
                if (curNode.getNodeName().equals("node")) {
                    vIndex = add(attributes.getNamedItem("id").getNodeValue(), vertices, vIndex);
                } else if (curNode.getNodeName().equals("edge")) {
                    String src = attributes.getNamedItem("source").getNodeValue();
                      Integer iSrc = lookup(src, vertices);
                      if (iSrc == null) iSrc = vIndex = add(src, vertices, vIndex);
                    String tgt = attributes.getNamedItem("target").getNodeValue();
                      Integer iTgt = lookup(tgt, vertices);
                      if (iTgt == null) iTgt = vIndex = add(src, vertices, vIndex);
                    edges.add(new Integer[]{iSrc, iTgt});
                    weights.add(1.0);
                }
            } else if (curNode.getNodeType() == Node.TEXT_NODE) {
            }
        }

        if (times.size() == 0 && eTimes.size() == 0) {
            type = GraphType.REGULAR;
            return images.size() == 0
                    ? buildGraph(vertices, edges, weights, directed)
                    : buildGraph(vertices, images, edges, weights, directed);
        } else {
            type = GraphType.LONGITUDINAL;
            return buildGraph(vertices, times, edges, weights, eTimes, directed);
        }
    }

    /**
     * Adds specified vertex to map w/ next available index
     * @return index of entry
     */
    private int add(String id, Map<Integer,String> table, int start) {
        while (table.containsKey(start)) start++;
        table.put(start, id);
        return start;
    }

    /** @return first key index of specified string in table, or null if the id is not in the table */
    private static Integer lookup(String id, Map<Integer,String> table) {
        for (Entry<Integer,String> en : table.entrySet())
            if (en.getValue().equals(id))
                return en.getKey();
        return null;
    }

    public GraphType saveGraph(Object oGraph, Map<Object, Point2D.Double> positions, File file) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            String indent = "";
            try {
                Graph graph = (Graph) oGraph;
                ValuedGraph vg = oGraph instanceof ValuedGraph ? (ValuedGraph) oGraph : null;

                // header content
                writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                writer.newLine();
                writer.write("<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\"  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\">");
                writer.newLine();
                indent = "  ";
                writer.write(indent + "<graph id=\"G\" edgedefault=\""
                        + (((Graph)graph).isDirected() ? "directed" : "undirected")
                        + "\">");
                writer.newLine();

                // write the vertices
                indent = "    ";
                List<Integer> nodes = graph.nodes();
                for (Integer i : nodes) {
                    writer.write(indent + "<node id=\""
                            + (vg == null ? i.toString() : vg.getValue(i).toString())
                            + "\"/>");
                    writer.newLine();
                }

                // write the edges
                for (Integer i1 : nodes)
                    for (Integer i2 : nodes) {
                        if (!graph.isDirected() && i2 < i1) continue;
                        if (!graph.adjacent(i1, i2)) continue;
                        writer.write(indent + "<edge source=\""
                                + (vg == null ? i1.toString() : vg.getValue(i1).toString())
                                + "\" target=\""
                                + (vg == null ? i2.toString() : vg.getValue(i2).toString())
                                + "\"/>");
                        writer.newLine();
                    }
                // final content
                indent = "  ";
                writer.write(indent + "</graph>");
                writer.newLine();
                writer.write("</graphml>");
                writer.newLine();
            } finally {
                writer.close();
            }
        } catch (IOException ex) {
            System.err.println(ex);
            return GraphType.UNKNOWN;
        }
        return GraphType.REGULAR;
    } // saveGraph

}
