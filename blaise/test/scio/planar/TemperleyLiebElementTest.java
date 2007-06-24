/*
 * TemperleyLiebElementTest.java
 * JUnit based test
 *
 * Created on May 22, 2007, 2:39 PM
 */

package scio.planar;

import junit.framework.*;
import java.util.TreeSet;
import scio.graph.Edge;
import scio.algebra.GroupElement;
import scio.algebra.GroupElementId;
import scio.graph.GraphGroupElement;
import java.awt.Adjustable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;
import scio.graph.Graph;
import java.util.Iterator;
import scio.algebra.PermutationElement;

/**
 *
 * @author ae3263
 */
public class TemperleyLiebElementTest extends TestCase {
    
    public static Test suite(){return new TestSuite(TemperleyLiebElementTest.class);}
        
    TemperleyLiebElement instance,instance2,instance3,instance4,instance5,instance6,instance7,instance8,instance9;
        
    public TemperleyLiebElementTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
        int[][] i1={{-1,-1},{-1,-1},{1,10},{2,3},{4,6},{7,5},{8,9}};
        instance=new TemperleyLiebElement(i1);
        // Basis Element: ( () ) ( () ) () = 10100
        int[][] i2={{1,4},{2,3},{5,8},{10,9},{7,6}};
        instance2=new TemperleyLiebElement(i2);
        // Basis Element: ( () ) () = 100
        int[][] i3={{1,4},{2,3},{5,6}};
        instance3=new TemperleyLiebElement(i3);
        // Basis Element: () () () () = 0000
        int[][] i4={{1,2},{3,4},{5,6},{7,8}};
        instance4=new TemperleyLiebElement(i4);
        // Basis Element: ( ( () ) ) ( () ) = 21010
        int[][] i5={{1,6},{2,5},{3,4},{7,10},{8,9}};
        instance5=new TemperleyLiebElement(i5);
        // First Basis Element ( ( ( ( () ) ) ) ) = 43210
        instance6=new TemperleyLiebElement(5);
        
        int[] i7={6,3,1,0,0,1,0,1,0};
        instance7=new TemperleyLiebElement(i7);
        
        int[] i8={2,1,0};
        instance8=new TemperleyLiebElement(i8);
        
        int[] i9={2,0,0,1,0,0};
        instance9=new TemperleyLiebElement(i9);
    }
    
    protected void tearDown() throws Exception {
    }
    
    
    
    /**
     * Test of setTo method, of class scio.planar.TemperleyLiebElement.
     */
    public void testSetTo() {
        System.out.println("setTo: WILL FAIL!");
        TemperleyLiebElement instance3=new TemperleyLiebElement();
        instance3.setTo("{(2)(1-10)(2-3)(4-6)(5-7)(8-9)}");
        assertEquals("{(2)(1-10)(2-3)(4-6)(5-7)(8-9)} in TL(5)",instance3.toString());
    }
    
    /**
     * Test of toString method, of class scio.planar.TemperleyLiebElement.
     */
    public void testToString() {
        System.out.println("toString");
        assertEquals("{(2)(1-10)(2-3)(4-6)(5-7)(8-9)} in TL(5)",instance.toString());
        assertEquals("{(1-4)(2-3)(5-8)(6-7)(9-10)} in TL(5)",instance2.toString());
    }
    
    /**
     * Test of useDefaultInputOutput method, of class scio.planar.TemperleyLiebElement.
     */
    public void testUseDefaultInputOutput() {
        System.out.println("useDefaultInputOutput: NO TESTS NECESSARY!");
    }
    
    /**
     * Test of actLeft method, of class scio.planar.TemperleyLiebElement.
     */
    public void testActLeft() {
        System.out.println("actLeft");
        assertEquals("2/1","{(3)(1-5)(2-3)(4-8)(6-7)(9-10)} in TL(5)",instance2.actLeft(instance).toString());
        assertEquals("1/2","{(2)(1-4)(2-3)(5-10)(6-7)(8-9)} in TL(5)",instance.actLeft(instance2).toString());
        assertEquals("1/1","{(5)(1-10)(2-3)(4-7)(5-6)(8-9)} in TL(5)",instance.actLeft(instance).toString());
        assertEquals("2/2","{(1-4)(2-3)(5-8)(6-7)(9-10)} in TL(5)",instance2.actLeft(instance2).toString());
        assertEquals("1/2/1","{(5)(1-5)(2-3)(4-10)(6-7)(8-9)} in TL(5)",instance.actLeft(instance2).actLeft(instance).toString());
    }
    
    /**
     * Test of validEdge method, of class scio.planar.TemperleyLiebElement.
     */
    public void testValidEdge(){
        System.out.println("validEdge: MAY WANT TO IMPLEMENT TEST");
    }
    
    /**
     * Test of getStrandFromAdjacency method, of class scio.planar.TemperleyLiebElement.
     */
    public void testGetStrandFromAdjacency() {
        System.out.println("getStrandFromAdjacency");
        Graph g=new Graph();
        g.addEdge(1,2);g.addEdge(3,8);g.addEdge(2,3);g.addEdge(5,8);
        g.addEdge(4,4);g.addEdge(-1,-1);
        assertEquals("[1, 2, 3, 8, 5]",
                TemperleyLiebElement.getStrandFromAdjacency(g,g.getAllAdjacencies(),5).toString());
        assertEquals("[4, 4]",
                TemperleyLiebElement.getStrandFromAdjacency(g,g.getAllAdjacencies(),4).toString());
        assertEquals("[-1]",
                TemperleyLiebElement.getStrandFromAdjacency(g,g.getAllAdjacencies(),-1).toString());
    }
    
    /**
     * Test of getAllStrandsFromAdjacency method, of class scio.planar.TemperleyLiebElement.
     */
    public void testGetAllStrandsFromAdjacency() {
        System.out.println("getStrandFromAdjacency");
        Graph g=new Graph();
        g.addEdge(1,2);g.addEdge(3,8);g.addEdge(2,3);g.addEdge(5,8);
        g.addEdge(4,4);g.addEdge(-1,-1);
        assertEquals("[[5, 8, 3, 2, 1], [4, 4]]",
                TemperleyLiebElement.getAllStrandsFromAdjacency(g,g.getAllAdjacencies()).toString());
    }
    
    /**
     * Test of kinkNumber method, of class scio.planar.TemperleyLiebElement.
     */
    public void testKinkNumber() {
        System.out.println("kinkNumber: TESTED in actLeft");
    }
    
    /**
     * Test of crossed method, of class scio.planar.TemperleyLiebElement.
     */
    public void testCrossed() {
        System.out.println("crossed");
        assertEquals(true,instance.crossed(4,5));
        assertEquals(false,instance.crossed(1,7));
        assertEquals(false,instance.crossed(2,3));
        assertEquals(false,instance.crossed(7,8));
        assertEquals(true,instance.crossed(6,7));
    }
    
    /**
     * Test of initPuts method, of class scio.planar.TemperleyLiebElement.
     */
    public void testInitPuts() {
        System.out.println("initPuts");
        TemperleyLiebElement instance = new TemperleyLiebElement();
        instance.initPuts(5);
        assertEquals("[1, 2, 3, 4, 5]",instance.inputs.toString());
        assertEquals("[10, 9, 8, 7, 6]",instance.outputs.toString());
    }
    
    /**
     * Test of setToParen method, of class scio.planar.TemperleyLiebElement.
     */
    public void testSetToParen() {
        System.out.println("setToParen");
        assertEquals("{(1-14)(2-9)(3-6)(4-5)(7-8)(10-13)(11-12)(15-18)(16-17)} in TL(9)",instance7.toString());
        assertEquals("{(1-6)(2-5)(3-4)} in TL(3)",instance8.toString());
        assertEquals("{(1-6)(2-3)(4-5)(7-10)(8-9)(11-12)} in TL(6)",instance9.toString());
    }
    
    /**
     * Test of getParen method, of class scio.planar.TemperleyLiebElement.
     */
    public void testGetParen() {
        System.out.println("getParen");
        assertEquals("40000",instance.getParenString());
        assertEquals("10100",instance2.getParenString());
        assertEquals("100",instance3.getParenString());
        assertEquals("0000",instance4.getParenString());
        assertEquals("21010",instance5.getParenString());
        assertEquals("43210",instance6.getParenString());
        assertEquals("631001010",instance7.getParenString());
        assertEquals("210",instance8.getParenString());
        assertEquals("200100",instance9.getParenString());
    }
    public void testGetParenString(){System.out.println("getParenString: see getParen test");}
    
    /**
     * Test of isBasisElement method, of class scio.planar.TemperleyLiebElement.
     */
    public void testIsBasisElement() {
        System.out.println("isBasisElement");
        assertEquals("1",false,instance.isBasisElement());
        assertEquals("2",true,instance2.isBasisElement());
        assertEquals("3",true,instance3.isBasisElement());
        assertEquals("4",true,instance4.isBasisElement());
        assertEquals("5",true,instance5.isBasisElement());
        assertEquals("6",true,instance6.isBasisElement());
        assertEquals("7",true,instance7.isBasisElement());
        assertEquals("8",true,instance8.isBasisElement());
        assertEquals("9",true,instance9.isBasisElement());
    }
    
    /**
     * Test of validParen method, of class scio.planar.TemperleyLiebElement.
     */
    public void testValidParen() {
        System.out.println("validParen");
        int[] p1={3,2,1,0};
        int[] p2={3,1,0,0};
        int[] p3={3,1,0,1};
        int[] p4={2,1,1,0};
        assertEquals("p1",true,TemperleyLiebElement.validParen(p1,0,3));
        assertEquals("p2",true,TemperleyLiebElement.validParen(p2,0,3));
        assertEquals("p3a",false,TemperleyLiebElement.validParen(p3,0,3));
        assertEquals("p3b",true,TemperleyLiebElement.validParen(p3,1,2));
        assertEquals("p4a",false,TemperleyLiebElement.validParen(p4,0,3));
        assertEquals("p4b",true,TemperleyLiebElement.validParen(p4,2,3));
    }
    
    /**
     * Test of getPermutationElement method, of class scio.planar.TemperleyLiebElement.
     */
    public void testGetPermutationElement() {
        int[] p2={0,1,3,4,5,2,6};
        int[] p3={0,5,3,6,4,2,1};
        PermutationElement instance1=new PermutationElement(6);
        PermutationElement instance2=new PermutationElement(p2);
        PermutationElement instance3=new PermutationElement(p3);
        assertEquals("{(1-12)(2-11)(3-10)(4-9)(5-8)(6-7)} in TL(6)",TemperleyLiebElement.getPermutationElement(instance1).toString());
        assertEquals("{(1-12)(2-10)(3-9)(4-8)(5-11)(6-7)} in TL(6)",TemperleyLiebElement.getPermutationElement(instance2).toString());
        assertEquals("{(1-8)(2-10)(3-7)(4-9)(5-11)(6-12)} in TL(6)",TemperleyLiebElement.getPermutationElement(instance3).toString());
    }
    
    /**
     * Test of hasNext method, of class scio.planar.TemperleyLiebElement.
     */
    public void testHasNext(){
        System.out.println("hasNext");
        assertEquals("1",false,instance.hasNext());
        assertEquals("2",true,instance2.hasNext());
        assertEquals("3",true,instance3.hasNext());
        assertEquals("4",false,instance4.hasNext());
        assertEquals("5",true,instance5.hasNext());
        assertEquals("6",true,instance6.hasNext());
        assertEquals("7",true,instance7.hasNext());
        assertEquals("8",true,instance8.hasNext());
        assertEquals("9",true,instance9.hasNext());
    }
    public void testHasParenAfter(){System.out.println("hasParenAfter: see hasNext test");}
    
    /**
     * Test of next method, of class scio.planar.TemperleyLiebElement.
     */
    public void testNext() {
        System.out.println("next");
        TemperleyLiebElement e=new TemperleyLiebElement(3);
        while(e.hasNext()){System.out.println(e.getParenString());e=e.next();}
        System.out.println(e.getParenString());
        assertEquals("1",null,instance.next());
        assertEquals("2","10010",instance2.next().getParenString());
        assertEquals("3","010",instance3.next().getParenString());
        assertEquals("4",null,instance4.next());
        assertEquals("5","21000",instance5.next().getParenString());
        assertEquals("6","43200",instance6.next().getParenString());
        assertEquals("7","631001000",instance7.next().getParenString());
        assertEquals("8","200",instance8.next().getParenString());
        assertEquals("9","200010",instance9.next().getParenString());
    }
    public void testParenAfter(){System.out.println("parenAfter: see next");}
    
    /**
     * Test of remove method, of class scio.planar.TemperleyLiebElement.
     */
    public void testRemove(){System.out.println("remove: NO CODE!");}
}
