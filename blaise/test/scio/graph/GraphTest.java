/*
 * GraphTest.java
 * JUnit based test
 *
 * Created on May 22, 2007, 10:27 AM
 */

package scio.graph;

import java.util.Set;
import junit.framework.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

/**
 *
 * @author ae3263
 */
public class GraphTest extends TestCase {
    
    public static Test suite(){return new TestSuite(GraphTest.class);}

    Graph instance;
    
    public GraphTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        instance=new Graph();
        instance.addEdge(1,2);
        instance.addEdge(1,2,4);
        instance.addEdge(1,2,-2);
        instance.addEdge(2,3);
        instance.addEdge(2,4);
        instance.addEdge(2,5);
        instance.addEdge(1,6);
        instance.addEdge(6,10);
        instance.addEdge(10,11);
        instance.addEdge(11,1);
        instance.addEdge(6,6);
        instance.addEdge(6,6);
        instance.addEdge(15,15);
        instance.addEdge(20,21);
        instance.addEdge(-1,-1);
    }

    /**
     * Test of addTrivialLoop method, of class scio.graph.Graph.
     */
    public void testAddTrivialEdge() {
        System.out.println("addTrivialEdge");        
        instance.addTrivialLoop();
        assertEquals("{(2)}",(instance.getNeighborhood(-1).toString()));
    }

    /**
     * Test of addEdge method, of class scio.graph.Graph. Assume works.
     */
    public void testAddEdge() {
        System.out.println("addEdge");
        assertEquals("{3(1->2)(1->6)(11->1)}",(instance.getNeighborhood(1).toString()));
    }

    /**
     * Test of getVertices method, of class scio.graph.Graph.
     */
    public void testGetVertices() {
        System.out.println("getVertices");
        
        TreeSet<Integer> expResult=new TreeSet<Integer>();
        for(int i=1;i<=6;i++){expResult.add(i);}
        expResult.add(10);expResult.add(11);expResult.add(15);expResult.add(20);expResult.add(21);
        TreeSet<Integer> result = instance.getVertices();
        assertEquals(expResult, result);
    }

    /**
     * Test of getNumVertices method, of class scio.graph.Graph.
     */
    public void testGetNumVertices() {
        System.out.println("getNumVertices");
        assertEquals(11,instance.getNumVertices());
    }

    /**
     * Test of getMaxVertex method, of class scio.graph.Graph.
     */
    public void testGetMaxVertex() {
        System.out.println("getMaxVertex");
        
        int expResult = 21;
        int result = instance.getMaxVertex();
        assertEquals(expResult, result);
    }

    /**
     * Test of toString method, of class scio.graph.Graph.
     */
    public void testToString() {
        System.out.println("toString: NO TEST REQUIRED");
    }

    /**
     * Test of relabelVertices method, of class scio.graph.Graph.
     */
    public void testRelabelVertices() {
        System.out.println("relabelVertices");        
        instance.relabelVertices();
        TreeSet<Integer> expResult=new TreeSet<Integer>();
        for(int i=1;i<=11;i++){expResult.add(i);}
        assertEquals(expResult,instance.getVertices());
    }

    /**
     * Test of addToLabels method, of class scio.graph.Graph.
     */
    public void testAddToLabels() {
        System.out.println("addToLabels");
        TreeSet<Integer> expResult=new TreeSet<Integer>();
        for(int i=11;i<=16;i++){expResult.add(i);}
        expResult.add(20);expResult.add(21);expResult.add(25);expResult.add(30);expResult.add(31);
        instance.addToLabels(10);
        assertEquals(expResult,instance.getVertices());
    }

    /**
     * Test of reverseEdges method, of class scio.graph.Graph.
     */
    public void testReverseEdges() {
        System.out.println("reverseEdges");        
        instance.reverseEdges();
        assertEquals("{(21->20)}", (instance.getNeighborhood(21).toString()));
    }

    /**
     * Test of removeVertex method, of class scio.graph.Graph.
     */
    public void testRemoveVertex() {
        System.out.println("removeVertex");
        instance.removeVertex(6);
        assertEquals("{3(1->2)(11->1)}", (instance.getNeighborhood(1).toString()));
        assertEquals("{(10->11)}", (instance.getNeighborhood(10).toString()));
    }

    /**
     * Test of glueVertices method, of class scio.graph.Graph.
     */
    public void testGlueVertices() {
        System.out.println("glueVertices");
        instance.glueVertices(3,5);
        assertEquals("3&5","{2(2->3)}",(instance.getNeighborhood(3).toString()));
        instance.glueVertices(10,11);
        assertEquals("10&11","{(6->10)(10->1)(10->10)}", (instance.getNeighborhood(10).toString()));
    }

    /**
     * Test of getLoopsAt method, of class scio.graph.Graph.
     */
    public void testGetLoopsAt() {
        System.out.println("getLoopsAt");
        
        assertEquals("v1",0,instance.getLoopsAt(1));
        assertEquals("v6",2,instance.getLoopsAt(6));
        assertEquals("v-1",1,instance.getLoopsAt(-1));
    }
    
    /**
     * Test of getValency method, of class scio.graph.Graph.
     */
    public void testGetValency() {
        System.out.println("getValency");
        
        assertEquals("v-1",0,instance.getValency(-1));
        assertEquals("v1",5,instance.getValency(1));
        assertEquals("v2",6,instance.getValency(2));
        assertEquals("v6",6,instance.getValency(6));
        assertEquals("v7",0,instance.getValency(7));
        assertEquals("v11",2,instance.getValency(11));
        assertEquals("v21",1,instance.getValency(21));
    }

    /**
     * Test of getNeighborhood method, of class scio.graph.Graph.
     */
    public void testGetNeighborhood() {
        System.out.println("getNeighborhood");
        
        assertEquals("v-1","{(1)}",(instance.getNeighborhood(-1).toString()));
        assertEquals("v1","{3(1->2)(1->6)(11->1)}",(instance.getNeighborhood(1).toString()));
        assertEquals("v6","{(1->6)2(6->6)(6->10)}",(instance.getNeighborhood(6).toString()));
        assertEquals("v7","{}",(instance.getNeighborhood(7).toString()));
    }

    /**
     * Test of removeEdge method, of class scio.graph.Graph.
     */
    public void testRemoveEdge() {
        System.out.println("removeEdge");
        instance.removeEdge(1,6);
        assertEquals("v6","{2(6->6)(6->10)}", (instance.getNeighborhood(6).toString()));
        instance.removeEdge(1,2,1);
        assertEquals("v1","{2(1->2)(11->1)}",(instance.getNeighborhood(1).toString()));
        instance.removeEdge(-1,-1);
        assertEquals("v-1","{}",(instance.getNeighborhood(-1).toString()));
    }

    /**
     * Test of contractEdge method, of class scio.graph.Graph.
     */
    public void testContractEdge() {
        System.out.println("removeEdge");
        instance.contractEdge(1,6);
        assertEquals("v1a","{2(1->1)3(1->2)(1->10)(11->1)}", (instance.getNeighborhood(1).toString()));
        instance.contractEdge(1,2);
        assertEquals("v1b","{2(1->1)(1->3)(1->4)(1->5)(1->10)(11->1)}",(instance.getNeighborhood(1).toString()));
    }

    /**
     * Test of contractVerticesByDegree method, of class scio.graph.Graph.
     */
    public void testContractDegreeTwo() {
        System.out.println("contractDegreeTwo");
        instance.contractDegreeTwo();
        assertEquals("{(2)3(1->2)(1->6)(2->3)(2->4)(2->5)(6->1)2(6->6)(20->21)}",instance.toString());
    }

    /**
     * Test of glueTo method, of class scio.graph.Graph.
     */
    public void testGlueTo() {
        System.out.println("glueTo");
        Graph instance2=new Graph();
        instance2.addEdge(3,1);
        instance2.addEdge(2,3);
        instance2.addEdge(3,4);
        instance2.addTrivialLoop();
        ArrayList<Integer> ag1=new ArrayList<Integer>();ag1.add(10);ag1.add(11);
        ArrayList<Integer> ag2=new ArrayList<Integer>();ag2.add(1);ag2.add(2);
        instance.glueTo(instance2,ag1,ag2);
        assertEquals("v-1",2,instance.getLoopsAt(-1));
        assertEquals("v10","{(6->10)(10->11)(24->10)}",(instance.getNeighborhood(10).toString()));
        assertEquals("v24","{(11->24)(24->10)(24->25)}",(instance.getNeighborhood(24).toString()));
    }

    /**
     * Test of getEdge method, of class scio.graph.Graph.
     */
    public void testGetEdge() {        
        System.out.println("getEdge: NO TESTING REQUIRED!");
    }

    /**
     * Test of edgeCount method, of class scio.graph.Graph.
     */
    public void testEdgeCount() {
        System.out.println("edgeCount");
        assertEquals(15,instance.edgeCount());
    }

    /**
     * Test of isConnected method, of class scio.graph.Graph.
     */
    public void testIsConnected() {
        System.out.println("isConnected");
        assertEquals("g connected",false,instance.isConnected());        
        assertEquals("n1 connected",true,instance.getNeighborhood(1).isConnected());
        Graph instance2=new Graph();
        instance2.addEdge(3,1);
        instance2.addEdge(2,3);
        instance2.addEdge(3,4);
        assertEquals("g2",true,instance2.isConnected());
    }

    /**
     * Test of getComponent method, of class scio.graph.Graph.
     */
    public void testGetComponent() {
        System.out.println("getComponent");
        assertEquals("n1a",12,instance.getComponent(1).edgeCount());
        instance.removeVertex(1);
        assertEquals("n10",4,instance.getComponent(10).edgeCount());
        assertEquals("n2",3,instance.getComponent(2).edgeCount());
        assertEquals("n20",1,instance.getComponent(20).edgeCount());
    }

 
    /**
     * Test of getAllComponents method, of class scio.graph.Graph.
     */
    public void testGetAllComponents() {
        System.out.println("getAllComponents");
        ArrayList<Integer> vertices=new ArrayList<Integer>();
        for(Graph g:instance.getAllComponents()){vertices.add(g.getNumVertices());}        
        assertEquals("[0, 8, 1, 2]",vertices.toString());
    }

    /**
     * Test of getVertexOfDegree method, of class scio.graph.Graph.
     */
    public void testGetVertexOfDegree() {
        System.out.println("getVertexOfDegree");
        assertEquals("d0",0,instance.getVertexOfDegree(0));
        assertEquals("d1",3,instance.getVertexOfDegree(1));
        assertEquals("d2",10,instance.getVertexOfDegree(2));
        assertEquals("d3",0,instance.getVertexOfDegree(3));
        assertEquals("d4",0,instance.getVertexOfDegree(4));
        assertEquals("d5",1,instance.getVertexOfDegree(5));
        assertEquals("d6",2,instance.getVertexOfDegree(6));
    }

    /**
     * Test of getAllAdjacencies method, of class scio.graph.Graph.
     */
    public void testGetAllAdjacencies() {
        System.out.println("getAllAdjacencies");
        assertEquals("{-1=[-1], 1=[2, 6, 11], 2=[1, 3, 4, 5], 3=[2], 4=[2], 5=[2], 6=[1, 6, 10], 10=[6, 11], 11=[1, 10], 15=[15], 20=[21], 21=[20]}",
                instance.getAllAdjacencies().toString());
    }










    /**
     * Test of getWeightedMatrix method, of class scio.graph.Graph.
     */
    public void testGetWeightedMatrix() {
        System.out.println("getWeightedMatrix TO BE IMPLEMENTED");
    }

    /**
     * Test of getOrderedMatrix method, of class scio.graph.Graph.
     */
    public void testGetOrderedMatrix() {
        System.out.println("getOrderedMatrix TO BE IMPLEMENTED");
    }

    /**
     * Test of getUnOrderedMatrix method, of class scio.graph.Graph.
     */
    public void testGetUnOrderedMatrix() {
        System.out.println("getUnOrderedMatrix TO BE IMPLEMENTED");
    }

    /**
     * Test of surgery method, of class scio.graph.Graph.
     */
    public void testSurgery() {
        System.out.println("surgery TO BE IMPLEMENTED");
    }

    /**
     * Test of closeOff method, of class scio.graph.Graph.
     */
    public void testCloseOff() {
        System.out.println("closeOff TO BE IMPLEMENTED");
    }

    /** Test of addTrivialLoop method, of class scio.graph.Graph. */
    public void testAddTrivialLoop(){System.out.println("addTrivialLoop: NO TEST REQUIRED!");}

    /** Test of getNumTrivialLoops method, of class scio.graph.Graph. */
    public void testGetNumTrivialLoops(){System.out.println("getNumTrivialLoops: NO TEST REQUIRED!");}

}
