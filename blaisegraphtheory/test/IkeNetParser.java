
import java.io.File;
import java.util.ArrayList;
import java.util.TreeMap;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.ListLongitudinalGraph;
import org.bm.blaise.scio.graph.io.AbstractGraphIO;
import org.bm.blaise.scio.graph.io.DynetMLGraphIO;
import org.bm.blaise.scio.graph.io.PajekLongGraphIO;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import util.XMLUtils;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author elisha
 */
public class IkeNetParser {

    public static Graph<Integer> importGraphByID(File file, String id) {
        Document doc = XMLUtils.constructDocument(file);
        if (doc == null) { System.out.println("File error: problem constructing org.w3c.dom.Document from file " + file); return null; }
        Node dnNode = XMLUtils.descendent(doc, "DynamicNetwork");
        if (dnNode == null) { System.out.println("DyNetML input error: no DynamicNetwork node found in XML file!"); return null; }
        Node metaNode = XMLUtils.descendent(dnNode, "MetaNetwork");
        if (metaNode == null) { System.out.println("DyNetML input error: no MetaNetwork found in XML file!"); return null; }
        Node nodeNode = XMLUtils.descendent(metaNode, "nodes", "nodeclass");
        Node networkNode = XMLUtils.attributeDescendent("network", "id", id, metaNode, "networks");

        String graphName = networkNode == null ? XMLUtils.attribute(metaNode, "id") : XMLUtils.attribute(networkNode, "id");

        TreeMap<Integer,String> vertices = new TreeMap<Integer,String>();
        ArrayList<Integer[]> edges = new ArrayList<Integer[]>();
        ArrayList<Double> weights = new ArrayList<Double>();
        DynetMLGraphIO.importNodes(nodeNode, vertices);
        DynetMLGraphIO.importNetworkEdges(networkNode, vertices, edges, weights);

        return AbstractGraphIO.buildGraph(vertices, edges, weights, true);
    }

    public static void main(String[] args) {
        ListLongitudinalGraph<Integer> lGraph = new ListLongitudinalGraph<Integer>();

        for (int i = 1; i <= 20; i++) {
            String fileName = String.format("ELDPWk%02d.xml", i);
            File f = new File(fileName);
            Graph<Integer> slice = importGraphByID(f, "TimeSpent");
            lGraph.addGraph(slice, i);
        }

        PajekLongGraphIO.getInstance().saveGraph(lGraph, null, new File("IkeNet-time-long.netl"));

        System.out.println(lGraph);
    }

}
