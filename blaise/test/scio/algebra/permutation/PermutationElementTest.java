/*
 * PermutationElementTest.java
 * JUnit based test
 *
 * Created on June 6, 2007, 3:03 PM
 */

package scio.algebra;

import junit.framework.*;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.HashSet;

/**
 *
 * @author ae3263
 */
public class PermutationElementTest extends TestCase {
    
    public PermutationElementTest(String testName) {
        super(testName);
    }
    
    public static Test suite(){return new TestSuite(PermutationElementTest.class);}
            
    int[] p2={0,1,3,4,5,2,6};
    int[] p3={0,5,3,6,4,2,1};
    int[] p4={0,5,4,3,2,1};
    PermutationElement instance1,instance2,instance3,instance4;
    
    protected void setUp() throws Exception {
        instance1=new PermutationElement(6);
        instance2=new PermutationElement(p2);
        instance3=new PermutationElement(p3);
        instance4=new PermutationElement(p4);
    }
    
    /**
     * Test of setToIdentity method, of class scio.algebra.PermutationElement.
     */
    public void testSetToIdentity() {
        System.out.println("setToIdentity");
        PermutationElement instance=new PermutationElement();
        instance.setToIdentity(5);
        assertEquals("(1 2 3 4 5)",instance.toString());
    }
    
    /**
     * Test of setP method, of class scio.algebra.PermutationElement.
     */
    public void testSetP() {
        System.out.println("setP");
        int[] pp={0,1,3,4,5,2,6};
        PermutationElement instance=new PermutationElement();
        instance.setP(pp);
        assertEquals("(1 3 4 5 2 6)",instance.toString());
    }
    
    /**
     * Test of setTo method, of class scio.algebra.PermutationElement.
     */
    public void testSetTo() {
        System.out.println("setTo");
        PermutationElement instance=new PermutationElement();
        instance.setTo("(1 3 4 5 2 6)");
        assertEquals("(1 3 4 5 2 6)",instance.toString());
        assertEquals(false,instance.setTo("3(43.2)"));
    }
    
    /**
     * Test of setToCycle method, of class scio.algebra.PermutationElement.
     */
    public void testSetToCycle() {
        System.out.println("setToCycle");
        PermutationElement instance=new PermutationElement();
        instance.setToCycle("(1 4) (2 3)  (5  ) (6)");
        assertEquals("(4 3 2 1 5 6)",instance.toString());
        instance.setToCycle("(1 6) (2 3 )");
        assertEquals("(6 3 2 4 5 1)",instance.toString());
        assertEquals(false,instance.setToCycle("3(43.2)"));
    }
    
    /**
     * Test of getP method, of class scio.algebra.PermutationElement.
     */
    public void testGetP() {
        System.out.println("getP");
        assertEquals(instance2.toString(),new PermutationElement(instance2.getP()).toString());
    }
    
    /**
     * Test of getN method, of class scio.algebra.PermutationElement.
     */
    public void testGetN() {
        System.out.println("getN");
        assertEquals(6,instance1.getN());
    }
    
    /**
     * Test of get method, of class scio.algebra.PermutationElement.
     */
    public void testGet() {
        System.out.println("get");
        assertEquals(5,instance2.get(4));
        assertEquals(3,instance3.get(2));
        assertEquals(-1,instance1.get(7));
    }
    
    /**
     * Test of getCycle method, of class scio.algebra.PermutationElement.
     */
    public void testGetCycle() {
        System.out.println("getCycle");
        assertEquals("pe1","[2]",instance1.getCycle(2).toString());
        assertEquals("pe2","[2, 3, 4, 5]",instance2.getCycle(2).toString());
        assertEquals("pe3","[2, 3, 6, 1, 5]",instance3.getCycle(2).toString());
    }
    
    /**
     * Test of toString method, of class scio.algebra.PermutationElement.
     */
    public void testToString() {
        System.out.println("toString");
        assertEquals("(1 2 3 4 5 6)",instance1.toString());
    }
    
    /**
     * Test of toLongString method, of class scio.algebra.PermutationElement.
     */
    public void testToLongString() {
        System.out.println("toLongString");
        assertEquals("(1->1,2->2,3->3,4->4,5->5,6->6)",instance1.toLongString());
    }
    
    /**
     * Test of toCycleString method, of class scio.algebra.PermutationElement.
     */
    public void testToCycleString() {
        System.out.println("toCycleString");
        assertEquals("(1)(2 3 4 5)(6)",instance2.toCycleString());
    }
    
    /**
     * Test of getCycles method, of class scio.algebra.PermutationElement.
     */
    public void testGetCycles() {
        System.out.println("getCycles");
        assertEquals("[[1], [2], [3], [4], [5], [6]]",instance1.getCycles().toString());
        assertEquals("[[1], [2, 3, 4, 5], [6]]",instance2.getCycles().toString());
        assertEquals("[[1, 5, 2, 3, 6], [4]]",instance3.getCycles().toString());
    }
    
    /**
     * Test of getInverse method, of class scio.algebra.PermutationElement.
     */
    public void testGetInverse() {
        System.out.println("getInverse");
        assertEquals("pe1","(1 2 3 4 5 6)",instance1.getInverse().toString());
        assertEquals("pe2","(1 5 2 3 4 6)",instance2.getInverse().toString());
        assertEquals("pe3","(6 5 2 4 1 3)",instance3.getInverse().toString());
        assertEquals(instance2.toString(),instance2.getInverse().getInverse().toString());
    }
    
    /**
     * Test of actLeft method, of class scio.algebra.PermutationElement.
     */
    public void testActLeft() {
        System.out.println("actLeft");
        assertEquals("2o3","(1 2 4 5 3 6)",((PermutationElement)instance2.actLeft(instance3)).toCycleString());
        assertEquals("3o2","(1 5 3 4 2 6)",((PermutationElement)instance3.actLeft(instance2)).toCycleString());
        assertEquals("1o2",instance2.toString(),instance1.actLeft(instance2).toString());
        assertEquals("2o1",instance2.toString(),instance2.actLeft(instance1).toString());
    }
    
    /**
     * Test of isValid method, of class scio.algebra.PermutationElement.
     */
    public void testIsValid() {
        System.out.println("isValid");
        int[] pBad={0,4,3,0,1,2};
        assertEquals(false,instance2.isValid(pBad));
        assertEquals(true,instance2.isValid(p2));
    }
    
    /**
     * Test of isIdentity method, of class scio.algebra.PermutationElement.
     */
    public void testIsIdentity() {
        System.out.println("isIdentity");
        assertEquals(true,instance1.isIdentity());
        assertEquals(false,instance2.isIdentity());
    }
    
    /**
     * Test of getIdentity method, of class scio.algebra.PermutationElement.
     */
    public void testGetIdentity() {
        System.out.println("getIdentity");
        assertEquals("(1 2 3 4)",instance2.getIdentity(4).toString());
    }
    
    /**
     * Test of hasNext method
     */
    public void testHasNext(){
        System.out.println("hasNext");
        assertEquals("i1",true,instance1.hasNext());
        assertEquals("i2",true,instance2.hasNext());
        assertEquals("i3",true,instance3.hasNext());
        assertEquals("i4",false,instance4.hasNext());
    }
    
    /**
     * Test of next method
     */
    public void testNext(){
        System.out.println("next");
        //PermutationElement e=new PermutationElement(3);
        //while(e.hasNext()){System.out.println(e.toString());e=e.next();}System.out.println(e.toString());
        assertEquals("i1","(1 2 3 4 6 5)",instance1.next().toString());
        assertEquals("i2","(1 3 4 5 6 2)",instance2.next().toString());
        assertEquals("i3","(5 4 1 2 3 6)",instance3.next().toString());
        assertEquals("i4",null,instance4.next());
    }
    
    /**
     * Test of remove method
     */
    public void remove(){
        System.out.println("remove");
        System.out.println("NO TEST REQUIRED!");
    }
}
