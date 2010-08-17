/*
 * XMLUtils.java
 * Created Aug 16, 2010
 */

package util;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides utilities for working with XML files and the Document class.
 * @author Elisha Peterson
 */
public class XMLUtils {

    /** @return new instance of a document using the standard builder */
    public static javax.xml.parsers.DocumentBuilder getDefaultDocumentBuilder() 
            throws javax.xml.parsers.ParserConfigurationException, javax.xml.parsers.FactoryConfigurationError {
        javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(false);
        return factory.newDocumentBuilder();
    }

    /** @return new instance of a document using the standard builder */
    public static org.w3c.dom.Document getDefaultDocument() {
        try {
            javax.xml.parsers.DocumentBuilder builder = getDefaultDocumentBuilder();
            return builder.newDocument();
        } catch (javax.xml.parsers.ParserConfigurationException ex) {
            System.out.println("The underlying parser does not support the requested features.");
            Logger.getLogger(XMLUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (javax.xml.parsers.FactoryConfigurationError ex) {
            System.out.println("Error occurred obtaining Document Builder Factory.");
            Logger.getLogger(XMLUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Opens an XML file and loads it into a Document object, using the default
     * document builder class.
     * @param xmlFile the XML file
     * @return document class for the file, or null if there is a problem reading the document
     */
    public static org.w3c.dom.Document constructDocument(java.io.File xmlFile) {
        org.w3c.dom.Document result = null;
        try {
            javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
            factory.setValidating(true);
            factory.setNamespaceAware(false);
            javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
            result = builder.parse(xmlFile);
        } catch (javax.xml.parsers.ParserConfigurationException ex) {
            System.out.println("The underlying parser does not support the requested features.");
            Logger.getLogger(XMLUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (javax.xml.parsers.FactoryConfigurationError ex) {
            System.out.println("Error occurred obtaining Document Builder Factory.");
            Logger.getLogger(XMLUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (org.xml.sax.SAXException ex) {
            Logger.getLogger(XMLUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (java.io.IOException ex) {
            Logger.getLogger(XMLUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    /**
     * Outputs an XML document to a file
     * @param doc the document to output
     * @param xmlFile the file to output the document to
     */
    public static void writeXMLDocument(org.w3c.dom.Document doc, java.io.File file) {
        try {
            javax.xml.transform.Transformer transformer = javax.xml.transform.TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");

            //initialize StreamResult with File object to save to file
            javax.xml.transform.stream.StreamResult result = new javax.xml.transform.stream.StreamResult(file);
            javax.xml.transform.dom.DOMSource source = new javax.xml.transform.dom.DOMSource(doc);
            transformer.transform(source, result);
        } catch (javax.xml.transform.TransformerException ex) {
            Logger.getLogger(XMLUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (javax.xml.transform.TransformerFactoryConfigurationError ex) {
            Logger.getLogger(XMLUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /** @return descendent of specified start node, where descent tree is specified by the strings provided. */
    public static org.w3c.dom.Node descendent(org.w3c.dom.Node start, String... names) {
        org.w3c.dom.Node cur = start;
        org.w3c.dom.NodeList nl;
        if (names.length == 0)
            return start;
        for (int i = 0; i < names.length; i++) {
            nl = cur.getChildNodes();
            for (int j = 0; j < nl.getLength(); j++)
                if (nl.item(j).getNodeName().equals(names[i])) {
                    cur = nl.item(j);
                    break;
                }
            if (cur == start)
                return null;
        }
        return cur;
    }

    /** Looks for node w/ specified attribute & specified parents. Will look down
     * the tree for the specified name. Then looks for the first node of specified
     * name & specified attribute value.
     * @return first descendent with specified attribute */
    public static org.w3c.dom.Node attributeDescendent(String nodeName, String attr, String value, org.w3c.dom.Node start, String... parents) {
        org.w3c.dom.Node cur = start;
        org.w3c.dom.NodeList nl;

        // trace through tree
        for (int i = 0; i < parents.length; i++) {
            nl = cur.getChildNodes();
            for (int j = 0; j < nl.getLength(); j++)
                if (nl.item(j).getNodeName().equalsIgnoreCase(parents[i])) {
                    cur = nl.item(j);
                    break;
                }
        }
        
        // now look for special node
        nl = cur.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            org.w3c.dom.Node item = nl.item(i);
            if (item.getNodeName().equalsIgnoreCase(nodeName)) {
                org.w3c.dom.Node attribute = item.getAttributes().getNamedItem(attr);
                if (attribute != null && attribute.getNodeValue().equalsIgnoreCase(value))
                    return item;
            }
        }

        return null;
    }

    /** @return attribute value of specified node, or null if no such attribute exists. */
    public static String attribute(org.w3c.dom.Node node, String attrName) {
        org.w3c.dom.Node attrNode = node.getAttributes().getNamedItem(attrName);
        return attrNode == null ? null : attrNode.getNodeValue();
    }


}
