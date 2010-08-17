/*
 * GraphMLIO.java
 * Created Aug 2010
 */

package org.bm.blaise.scio.graph.io;

import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.DocumentBuilder;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.ValuedGraph;
import org.bm.blaise.scio.graph.WeightedGraph;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import util.FileNameExtensionFilter;
import util.XMLUtils;

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
        Document doc = XMLUtils.constructDocument(file);
        if (doc == null) { System.out.println("File error: problem constructing org.w3c.dom.Document from file " + file); return null; }

        Node mlNode = XMLUtils.descendent(doc, "graphml");
        if (mlNode == null) { System.out.println("GraphML input error: no graphml node found in XML file!"); return null; }

        Node gNode = XMLUtils.descendent(mlNode, "graph");
        if (mlNode == null) { System.out.println("GraphML input error: no graph node found in XML file!"); return null; }

        String graphName = XMLUtils.attribute(gNode, "id");
        String dirString = XMLUtils.attribute(gNode, "edgedefault");
        boolean directed = dirString == null ? true : dirString.equalsIgnoreCase("directed");

        // stores the vertices and associated names
        TreeMap<Integer,String> vertices = new TreeMap<Integer,String>();
        // stores points corresponding to vertices
        locations.clear();
        // stores the edges/arcs
        ArrayList<Integer[]> edges = new ArrayList<Integer[]>();
        // stores the weights of edges, in the same order as the edges list
        ArrayList<Double> weights = new ArrayList<Double>();

        int vIndex = 0;
        NodeList graphNodes = gNode.getChildNodes();
        for (int i = 0; i < graphNodes.getLength(); i++) {
            Node cur = graphNodes.item(i);
            if (cur.getNodeType() == Node.ELEMENT_NODE && cur.getNodeName().equalsIgnoreCase("node")) {
                String id = XMLUtils.attribute(cur, "id");
                vIndex = add(id, vertices, vIndex);
            } else if (cur.getNodeType() == Node.ELEMENT_NODE && cur.getNodeName().equalsIgnoreCase("edge")) {
                String linkSource = XMLUtils.attribute(cur, "source");
                Integer iSrc = lookup(linkSource, vertices);
                if (iSrc == null)
                    iSrc = vIndex = add(linkSource, vertices, vIndex);

                String linkTarget = XMLUtils.attribute(cur, "target");
                Integer iTgt = lookup(linkTarget, vertices);
                if (iTgt == null)
                    iTgt = vIndex = add(linkTarget, vertices, vIndex);

                edges.add(new Integer[]{iSrc, iTgt});
                weights.add(1.0);
            }
        }

        type = GraphType.REGULAR;
        return buildGraph(vertices, edges, weights, directed);
    } // importGraph

    /**
     * Adds specified vertex to map w/ next available index
     * @return index of entry
     */
    private static int add(String id, Map<Integer,String> table, int start) {
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
        Graph graph = (Graph) oGraph;
        ValuedGraph vg = oGraph instanceof ValuedGraph ? (ValuedGraph) oGraph : null;
        WeightedGraph wg = oGraph instanceof WeightedGraph ? (WeightedGraph) oGraph : null;

        Document doc = XMLUtils.getDefaultDocument();

        Element mlEl = doc.createElement("grahml");
        doc.appendChild(mlEl);

        Element graphEl = doc.createElement("graph");
        graphEl.setAttribute("id", "G");
        graphEl.setAttribute("edgedefault", "directed");
        mlEl.appendChild(graphEl);

        List<Integer> nodes = graph.nodes();
        for (Integer i : nodes) {
            Element nodeEl = doc.createElement("node");
            nodeEl.setAttribute("id", vg == null ? i.toString() : vg.getValue(i).toString());
            graphEl.appendChild(nodeEl);
        }

        for (Integer i1 : nodes)
            for (Integer i2 : nodes) {
                if (!graph.isDirected() && i2 < i1) continue;
                if (!graph.adjacent(i1, i2)) continue;
                Element linkEl = doc.createElement("edge");
                linkEl.setAttribute("source", vg == null ? i1.toString() : vg.getValue(i1).toString());
                linkEl.setAttribute("target", vg == null ? i2.toString() : vg.getValue(i2).toString());
                graphEl.appendChild(linkEl);
            }

        XMLUtils.writeXMLDocument(doc, file);
        return GraphType.REGULAR;
    } // saveGraph

}
