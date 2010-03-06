/*
 * MAddIntTest.java
 * JUnit 4.x based test
 *
 * Created on June 25, 2007, 2:46 PM
 */

package scio.algebra.polynomial;

import junit.framework.TestCase;

/**
 *
 * @author ae3263
 */
public class MAddIntTest extends TestCase {
    
    public MAddIntTest() {
    }
    
    
    public void setUp() throws Exception {
    }

    public void testGet(){System.out.println("get: TRIVIAL");}

    public void testGetIdentity(){
        System.out.println("getIdentity");
        assertEquals("1",MAddInt.getIdentity().toString());
    }

    public void testGetInverse() {
        System.out.println("getInverse");
        int[] n={1,2,3};
        assertEquals("x^-1 y^-2 z^-3",new MAddInt(n).getInverse().toString());
    }

    public void testActLeft() {
        System.out.println("actLeft");
        int[] na={1,1};
        int[] nb={-2,-1};
        assertEquals("x^-1",new MAddInt(na).actLeft(new MAddInt(nb)).toString());
    }

    public void testIsCommutative() {
        System.out.println("isCommutative");
        assertEquals(true,new MAddInt().isCommutative());
    }

    public void testCompareTo() {
        System.out.println("compareTo");
        int[] na={1,1};
        int[] nb={1,4};
        int[] nc={2,4};
        int[] nd={0,1};
        assertEquals("a","-3",Integer.toString(new MAddInt(na).compareTo(new MAddInt(nb))));
        assertEquals("b","-1",Integer.toString(new MAddInt(nb).compareTo(new MAddInt(nc))));
        assertEquals("c","2",Integer.toString(new MAddInt(nc).compareTo(new MAddInt(nd))));
    }

    public void testToString() {
        System.out.println("toString");
        int[] nb={-2,-1};
        assertEquals("x^-2 y^-1",new MAddInt(nb).toString());
    }
    
}
