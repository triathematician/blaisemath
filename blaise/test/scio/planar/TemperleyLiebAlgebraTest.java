/*
 * TemperleyLiebAlgebraTest.java
 * JUnit based test
 *
 * Created on June 6, 2007, 2:12 PM
 */

package scio.planar;

import junit.framework.*;
import scio.algebra.GroupAlgebraElement;
import scio.algebra.GroupAlgebraTerm;
import scio.graph.Edge;
import java.util.ArrayList;

/**
 *
 * @author ae3263
 */
public class TemperleyLiebAlgebraTest extends TestCase {
    
    public static Test suite(){return new TestSuite(TemperleyLiebAlgebraTest.class);}
    
    public TemperleyLiebAlgebraTest(String testName) {
        super(testName);
    }
    
    TemperleyLiebAlgebra a0,a1,a2,a3,a4;
    protected void setUp() throws Exception {
        int[] s1={1,0};TemperleyLiebElement e1=new TemperleyLiebElement(s1);
        int[] s2={0,0};TemperleyLiebElement e2=new TemperleyLiebElement(s2);
        int[] s3={2,0,0};TemperleyLiebElement e3=new TemperleyLiebElement(s3);
        int[] s4={1,0,0};TemperleyLiebElement e4=new TemperleyLiebElement(s4);
        a0=new TemperleyLiebAlgebra(2);a0.appendTerm(e1);a0.appendTerm(e2);
        a1=new TemperleyLiebAlgebra(2);a1.appendTerm(e1);a1.appendTerm(-1,e2);
        a2=new TemperleyLiebAlgebra(3);a2.appendTerm(.5f,e3);a2.appendTerm(.2f,e4);
        a3=new TemperleyLiebAlgebra(3);
        TemperleyLiebElement e=new TemperleyLiebElement(3);int i=1;
        do{a3.appendTerm(i,e);e=e.next();i++;}while(e.hasNext());a3.appendTerm(i,e);
        TemperleyLiebElement e5=new TemperleyLiebElement(3);
        TemperleyLiebElement e6=new TemperleyLiebElement(3);e6.clear();e6.addEdge(1,6);e6.addEdge(2,4);e6.addEdge(3,5);
        TemperleyLiebElement e7=new TemperleyLiebElement(3);e7.clear();e7.addEdge(1,4);e7.addEdge(2,6);e7.addEdge(3,5);
        a4=new TemperleyLiebAlgebra(3);a4.appendTerm(e5);a4.appendTerm(-.5f,e6);a4.appendTerm(e7);
    }
    
    protected void tearDown() throws Exception {
    }
    
    /**
     * Tests appendTerm
     */
    public void testAppendTerm() {
        System.out.println("appendTerm");
        assertEquals("??",a0.toString());
        assertEquals("??",a1.toString());
        assertEquals("??",a2.toString());
        assertEquals("??",a3.toString());
        assertEquals("??",a4.toString());
    }
    
    /**
     * Tests actLeft
     */
    public void testActLeft() {
        System.out.println("actLeft");
        TemperleyLiebAlgebra a01=(TemperleyLiebAlgebra) a0.actLeft(a1);
        TemperleyLiebAlgebra a10=(TemperleyLiebAlgebra) a0.actRight(a1);
        TemperleyLiebAlgebra a34=(TemperleyLiebAlgebra) a3.actLeft(a4);
        assertEquals("01","??",a01.toString());
        assertEquals("10","??",a10.toString());
        assertEquals("34","??",a34.toString());
    }
    
    /**
     * Test of getSymmetrizer method, of class scio.planar.TemperleyLiebAlgebra.
     */
    public void testGetSymmetrizer() {
        System.out.println("getSymmetrizer");
        assertEquals("1","??",TemperleyLiebAlgebra.getSymmetrizer(1).toString());
        assertEquals("2","??",TemperleyLiebAlgebra.getSymmetrizer(2).toString());
        assertEquals("3","??",TemperleyLiebAlgebra.getSymmetrizer(3).toString());
        assertEquals("4","??",TemperleyLiebAlgebra.getSymmetrizer(4).toString());
    }
    
    /**
     * Test of removeCrossings method, of class scio.planar.TemperleyLiebAlgebra.
     */
    public void testRemoveCrossings() {
        System.out.println("removeCrossings");
        assertEquals("before","??",a4.toString());
        a4.removeCrossings();
        assertEquals("after","??",a4.toString());
    }
    
    /**
     * Test of getAntiSymmetrizer method, of class scio.planar.TemperleyLiebAlgebra.
     */
    public void testGetAntiSymmetrizer() {
        System.out.println("getAntiSymmetrizer");
        assertEquals("1","??",TemperleyLiebAlgebra.getAntiSymmetrizer(1).toString());
        assertEquals("2","??",TemperleyLiebAlgebra.getAntiSymmetrizer(2).toString());
        assertEquals("3","??",TemperleyLiebAlgebra.getAntiSymmetrizer(3).toString());
        assertEquals("4","??",TemperleyLiebAlgebra.getAntiSymmetrizer(4).toString());
    }
    
    /**
     * Test of factorial method, of class scio.planar.TemperleyLiebAlgebra.
     */
    public void testFactorial() {
        System.out.println("factorial");
        assertEquals(720,TemperleyLiebAlgebra.factorial(6));
    }
}
