/*
 * MPolynomialTest.java
 * JUnit 4.x based test
 *
 * Created on June 25, 2007, 2:53 PM
 */

package scio.algebra.polynomial;

import junit.framework.TestCase;

/**
 *
 * @author ae3263
 */
public class MPolynomialTest extends TestCase {
    
    public MPolynomialTest() {
    }
    
    MPolynomial p0,p1,p2;
    
    @Override
    public void setUp() throws Exception {
        p0=new MPolynomial();
        int[] t={1,1};
        p0.appendTerm(1,t);
        p1=new MPolynomial();
        int[][] ts={{2,0},{1,1},{0,2}};
        p1.appendTerm(2,ts[0]);
        p1.appendTerm(-1,ts[1]);
        p1.appendTerm(.5f,ts[2]);
        
        p2=new MPolynomial();
        int[][] ts2={{1,3,4},{-1,2,0},{0,0,1},{0,0,0}};
        p2.appendTerm(1,ts2[0]);
        p2.appendTerm(.5f,ts2[1]);
        p2.appendTerm(.33f,ts2[2]);
        p2.appendTerm(-101,ts2[3]);
    }
    
    public void testIsCommutative() {
        System.out.println("isCommutative");
        assertEquals(true,p1.isCommutative());
    }

    public void testAppendTerm() {
        System.out.println("appendTerm");
        assertEquals("+x y",p0.toString());
        assertEquals("+2x^2-x y+0.5y^2",p1.toString());
    }

    public void testGetInverse() {
        System.out.println("getInverse");
        assertEquals("+x^-1 y^-1",p0.getInverse().toString());
        assertEquals(null,p1.getInverse());
    }

    public void testToString() {
        System.out.println("toString");
        assertEquals("+x y^3 z^4+0.33z-101+0.5x^-1 y^2",p2.toString());
    }
    
    public void testActLeft() {
        System.out.println("actLeft");
        assertEquals("+2x^3 y-x^2 y^2+0.5x y^3",p0.actLeft(p1).toString());
    }

    public void testClone() {
        System.out.println("clone");
        assertEquals(p1.toString(),p1.clone().toString());
    }
    
}
