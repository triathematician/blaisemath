/*
 * DynetMLIO.java
 * Created Aug 16, 2010
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
 * Basic support for the <i>DyNetML</i> XML file structure.
 * @author elisha
 */
public class DynetMLIO extends AbstractGraphIO {

    /** Used to construct the document */
    DocumentBuilder builder = null;

    // no instantiation
    private DynetMLIO() { }

    private static final DynetMLIO INSTANCE = new DynetMLIO();
    private static final FileNameExtensionFilter FILTER = new FileNameExtensionFilter("DyNetML files (*.xml)", "xml");

    /** Factory method @return instance of this IO class */
    public static DynetMLIO getInstance() { return INSTANCE; }

    public FileFilter getFileFilter() { return FILTER; }

    @Override
    public Object importGraph(Map<Integer, double[]> locations, File file, GraphType type) {
        Document doc = XMLUtils.constructDocument(file);
        if (doc == null) { System.out.println("File error: problem constructing org.w3c.dom.Document from file " + file); return null; }
        Node dnNode = XMLUtils.descendent(doc, "DynamicNetwork");
        if (dnNode == null) { System.out.println("DyNetML input error: no DynamicNetwork node found in XML file!"); return null; }
        Node metaNode = XMLUtils.descendent(dnNode, "MetaNetwork");
        if (metaNode == null) { System.out.println("DyNetML input error: no MetaNetwork found in XML file!"); return null; }
        Node nodeNode = XMLUtils.descendent(metaNode, "nodes", "nodeclass");
        Node networkNode = XMLUtils.descendent(metaNode, "networks", "network");

        String graphName = networkNode == null ? XMLUtils.attribute(metaNode, "id") : XMLUtils.attribute(networkNode, "id");
        locations.clear();
        type = GraphType.REGULAR;

        TreeMap<Integer,String> vertices = new TreeMap<Integer,String>();
        ArrayList<Integer[]> edges = new ArrayList<Integer[]>();
        ArrayList<Double> weights = new ArrayList<Double>();

        importNodes(nodeNode, vertices);
        importNetworkEdges(networkNode, vertices, edges, weights);

        return buildGraph(vertices, edges, weights, true);
    } // importGraph

    /** Imports a list of nodes */
    public static void importNodes(Node nodeNode, TreeMap<Integer,String> vertices) {
        int vIndex = 0;
        NodeList nodeNodes = nodeNode.getChildNodes();
        for (int i = 0; i < nodeNodes.getLength(); i++) {
            Node cur = nodeNodes.item(i);
            if (cur.getNodeType() == Node.ELEMENT_NODE && cur.getNodeName().equalsIgnoreCase("node")) {
                String id = XMLUtils.attribute(cur, "id");
                vIndex = add(id, vertices, vIndex);
            }
        }
    }

    /** Imports a network node, reading in the list of edges. */
    public static void importNetworkEdges(Node networkNode, TreeMap<Integer,String> vertices, ArrayList<Integer[]> edges, ArrayList<Double> weights) {
        int vIndex = 0;
        NodeList networkNL = networkNode.getChildNodes();
        for (int i = 0; i < networkNL.getLength(); i++) {
            Node cur = networkNL.item(i);
            if (cur.getNodeType() == Node.ELEMENT_NODE && cur.getNodeName().equalsIgnoreCase("link")) {
                String linkSource = XMLUtils.attribute(cur, "source");
                Integer iSrc = lookup(linkSource, vertices);
                if (iSrc == null)
                    iSrc = vIndex = add(linkSource, vertices, vIndex);

                String linkTarget = XMLUtils.attribute(cur, "target");
                Integer iTgt = lookup(linkTarget, vertices);
                if (iTgt == null)
                    iTgt = vIndex = add(linkTarget, vertices, vIndex);

                edges.add(new Integer[]{iSrc, iTgt});

                String linkType = XMLUtils.attribute(cur, "type");
                String linkValue = XMLUtils.attribute(cur, "value");
                double weight = 1.0;
                if (linkType != null && linkType.equals("double") && linkValue != null)
                    try { weight = Double.valueOf(linkValue); } catch (NumberFormatException ex) {}
                weights.add(weight);
            }
        }
    }


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
        
        Element dnEl = doc.createElement("DynamicNetwork");
        doc.appendChild(dnEl);

        Element mnEl = doc.createElement("MetaNetwork");
        mnEl.setAttribute("id", "G");
        dnEl.appendChild(mnEl);

        mnEl.appendChild(doc.createElement("documents"));

        Element nodesEl = doc.createElement("nodes");
        mnEl.appendChild(nodesEl);

        Element nodeclassEl = doc.createElement("nodeclass");
        nodeclassEl.setAttribute("id", "Agent");
        nodeclassEl.setAttribute("type", "Agent");
        nodesEl.appendChild(nodeclassEl);

        List<Integer> nodes = graph.nodes();
        for (Integer i : nodes) {
            Element nodeEl = doc.createElement("node");
            nodeEl.setAttribute("id", vg == null ? i.toString() : vg.getValue(i).toString());
            nodeclassEl.appendChild(nodeEl);
        }

        Element networksEl = doc.createElement("networks");
        mnEl.appendChild(networksEl);

        Element networkEl = doc.createElement("network");
        networkEl.setAttribute("sourceType", "Agent");
        networkEl.setAttribute("source", "Agent");
        networkEl.setAttribute("targetType", "Agent");
        networkEl.setAttribute("target", "Agent");
        networkEl.setAttribute("id", "Network");
        networksEl.appendChild(networkEl);

        for (Integer i1 : nodes)
            for (Integer i2 : nodes) {
                if (!graph.isDirected() && i2 < i1) continue;
                if (!graph.adjacent(i1, i2)) continue;
                Element linkEl = doc.createElement("link");
                linkEl.setAttribute("source", vg == null ? i1.toString() : vg.getValue(i1).toString());
                linkEl.setAttribute("target", vg == null ? i2.toString() : vg.getValue(i2).toString());
                linkEl.setAttribute("type", "double");
                linkEl.setAttribute("value", wg == null ? "1.0" : wg.getWeight(i1, i2).toString());
                networkEl.appendChild(linkEl);
            }

        XMLUtils.writeXMLDocument(doc, file);
        return GraphType.REGULAR;
    } // saveGraph

}
