/*
 * GroupAlgebraElementTest.java
 * JUnit based test
 *
 * Created on June 21, 2007, 2:54 PM
 */

package scio.algebra;

import junit.framework.*;
import java.util.TreeSet;
import scio.algebra.polynomial.AddInt;
import scio.algebra.polynomial.Polynomial;

/**
 *
 * @author ae3263
 */
public class GroupAlgebraElementTest extends TestCase {
    
    public GroupAlgebraElementTest(String testName) {
        super(testName);
    }
    
    public static Test suite(){return new TestSuite(GroupAlgebraElementTest.class);}
            
    Polynomial p1,p2,p3;
    protected void setUp() throws Exception {
        p1=new Polynomial();p1.appendTerm(1,1);p1.appendTerm(-1,0);
        p2=new Polynomial();p2.appendTerm(1,1);p2.appendTerm(1,0);
        p3=new Polynomial();p3.appendTerm(.5f,1);p3.appendTerm(-.7f,-1);
    }

    /**
     * Test of appendTerm method, of class scio.algebra.GroupAlgebraElement.
     */
    public void testAppendTerm() {
        System.out.println("appendTerm");
        p1.appendTerm(7,1);
        assertEquals("+8x-1",p1.toString());
    }

    /**
     * Test of append method, of class scio.algebra.GroupAlgebraElement.
     */
    public void testAppend() {
        System.out.println("append");
        p1.append(p2);
        assertEquals("+2x",p1.toString());
        p2.append(p3);
        assertEquals("+1.5x+1-0.7x^-1",p2.toString());
    }

    /**
     * Test of getTermList method, of class scio.algebra.GroupAlgebraElement.
     */
    public void testGetTermList() {
        System.out.println("getTermList: ASSUME VALID");
    }

    /**
     * Test of getIdentity method, of class scio.algebra.GroupAlgebraElement.
     */
    public void testGetIdentity() {
        System.out.println("getIdentity");
        assertEquals("0",GroupAlgebraElement.getIdentity().toString());
    }

    /**
     * Test of isIdentity method, of class scio.algebra.GroupAlgebraElement.
     */
    public void testIsIdentity() {
        System.out.println("isIdentity");
        Polynomial p5=new Polynomial();
        p5.appendTerm(5,3);p5.appendTerm(-5,3);
        assertEquals(true,p5.isIdentity());
    }

    /**
     * Test of getInverse method, of class scio.algebra.GroupAlgebraElement.
     */
    public void testGetInverse() {
        System.out.println("getInverse");
        assertEquals(null,p1.getInverse());
        p1.append(p2);
        assertEquals("+0.5x^-1",p1.getInverse().toString());
    }

    /**
     * Test of actLeft method, of class scio.algebra.GroupAlgebraElement.
     */
    public void testActLeft() {
        System.out.println("actLeft");
        Polynomial r1=(Polynomial)p1.clone().actLeft(p2);
        Polynomial r2=(Polynomial)p1.clone().actLeft(p3);
        Polynomial r3=(Polynomial)p1.clone().actLeft(p2).actLeft(p3);
        assertEquals("+x^2-1",r1.toString());
        assertEquals("+0.5x^2-0.5x-0.7+0.7x^-1",r2.toString());
        assertEquals("+0.5x^3-1.2x+0.7x^-1",r3.toString());
    }

    /**
     * Test of toString method, of class scio.algebra.GroupAlgebraElement.
     */
    public void testToString() {
        System.out.println("toString: INADEQUATE TEST!!");
        assertEquals("+x-1",p1.toString());
        assertEquals("+x+1",p2.toString());
        assertEquals("+0.5x-0.7x^-1",p3.toString());
    }

    /**
     * Test of compareTo method, of class scio.algebra.GroupAlgebraElement.
     */
    public void testCompareTo() {
        System.out.println("compareTo: INADEQUATE TEST!!");
        assertEquals(0,new GroupAlgebraElement<AddInt>().compareTo(new GroupAlgebraElement<AddInt>()));
    }
    
}
