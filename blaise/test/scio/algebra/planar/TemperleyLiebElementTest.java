/*
 * TemperleyLiebAlgebraTest.java
 * JUnit based test
 *
 * Created on June 6, 2007, 2:12 PM
 */

package scio.algebra.planar;

import scio.algebra.planar.TemperleyLiebElement;
import junit.framework.*;

/**
 *
 * @author ae3263
 */
public class TemperleyLiebElementTest extends TestCase {
    
    public static Test suite(){return new TestSuite(TemperleyLiebElementTest.class);}
    
    public TemperleyLiebElementTest(String testName) {
        super(testName);
    }
    
    TemperleyLiebElement a0,a1,a2,a3,a4;
    
    protected void setUp() throws Exception {
        int[] s1={1,0};TemperleyLiebTerm e1=new TemperleyLiebTerm(s1);
        int[] s2={0,0};TemperleyLiebTerm e2=new TemperleyLiebTerm(s2);
        int[] s3={2,0,0};TemperleyLiebTerm e3=new TemperleyLiebTerm(s3);
        int[] s4={1,0,0};TemperleyLiebTerm e4=new TemperleyLiebTerm(s4);
        a0=new TemperleyLiebElement(2);a0.appendTerm(e1);a0.appendTerm(e2);
        a1=new TemperleyLiebElement(2);a1.appendTerm(e1);a1.appendTerm(-1,e2);
        a2=new TemperleyLiebElement(3);a2.appendTerm(.5f,e3);a2.appendTerm(.2f,e4);
        a3=new TemperleyLiebElement(3);
        TemperleyLiebTerm e=new TemperleyLiebTerm(3);int i=1;
        do{a3.appendTerm(i,e);e=e.next();i++;}while(e.hasNext());a3.appendTerm(i,e);
        TemperleyLiebTerm e5=new TemperleyLiebTerm(3);
        TemperleyLiebTerm e6=new TemperleyLiebTerm(3);e6.clear();e6.addEdge(1,6);e6.addEdge(2,4);e6.addEdge(3,5);
        TemperleyLiebTerm e7=new TemperleyLiebTerm(3);e7.clear();e7.addEdge(1,4);e7.addEdge(2,6);e7.addEdge(3,5);
        a4=new TemperleyLiebElement(3);a4.appendTerm(e5);a4.appendTerm(-.5f,e6);a4.appendTerm(2,e7);
    }
    
    protected void tearDown() throws Exception {
    }
    
    /**
     * Tests hasCrossings
     */
    public void testHasCrossings() {
        System.out.println("hasCrossings");
        assertEquals("0",false,a0.hasCrossings());
        assertEquals("1",false,a1.hasCrossings());
        assertEquals("2",false,a2.hasCrossings());
        assertEquals("3",false,a3.hasCrossings());
        assertEquals("4",true,a4.hasCrossings());
    }
    
    /**
     * Tests appendTerm
     */
    public void testAppendTerm() {
        System.out.println("appendTerm");
        assertEquals("0","+(10)+(00)",a0.toString());
        assertEquals("1","+(10)-(00)",a1.toString());
        assertEquals("2","+0.5(200)+0.2(100)",a2.toString());
        assertEquals("3","+(210)+2(200)+3(100)+4(010)+5(000)",a3.toString());
        assertEquals("4","+{(1-6)(2-5)(3-4)}-0.5{(1-6)(2-4)(3-5)}+2{(1-4)(2-6)(3-5)}",a4.toString());
    }
    
    /**
     * Tests actLeft
     */
    public void testActLeft() {
        System.out.println("actLeft");
        System.out.println("test1");
        assertEquals("01","+(10)-2(00)",a0.actLeft(a0,a1).toString());
        System.out.println("test2");
        assertEquals("10","+(10)-2(00)",a0.actRight(a1).toString());
        System.out.println("test3");
        TemperleyLiebElement a23=a2.actLeft(a2,a3);
        // TODO: fix the rounding error!
        assertEquals("23","+(200)+0.39999998(100)+1.5(010)+0.6(000)",a2.actLeft(a2,a3).toString());
        TemperleyLiebElement a34=a3.actLeft(a3,a4);
        assertEquals("34c","+{(1-6)(2-5)(3-4)}-0.5{(1-6)(2-4)(3-5)}+11{(1-6)(2-3)(4-5)}+2{(1-4)(2-6)(3-5)}+14.5{(1-4)(2-3)(5-6)}-6{(1-3)(2-6)(4-5)}-8.5{(1-3)(2-4)(5-6)}+4{(1-2)(3-6)(4-5)}+5{(1-2)(3-4)(5-6)}",a34.toString());
        a34.removeCrossings();
        assertEquals("34u","+2.5(210)+3.5(200)+6(100)-4(010)-5.5(000)",a34.toString());
    }
    
    /**
     * Test of getSymmetrizer method, of class scio.planar.TemperleyLiebAlgebra.
     */
    public void testGetSymmetrizer() {
        System.out.println("getSymmetrizer");
        TemperleyLiebElement i=TemperleyLiebElement.getSymmetrizer(1);
        assertEquals("1c","+(0)",i.toString());
        i.removeCrossings();
        assertEquals("1u","+(0)",i.toString());
        
        i=TemperleyLiebElement.getSymmetrizer(2);
        assertEquals("2c","+{(1-4)(2-3)}+{(1-3)(2-4)}",i.toString());
        i.removeCrossings();
        assertEquals("2u","+2(10)-(00)",i.toString());
        
        i=TemperleyLiebElement.getSymmetrizer(3);
        i.removeCrossings();
        assertEquals("3u","+6(210)-4(200)-2(100)-2(010)-4(000)",i.toString());
        
        i=TemperleyLiebElement.getSymmetrizer(4);
        i.removeCrossings();
        assertEquals("4u","+24(3210)-18(3200)-12(3100)-12(3010)-24(3000)-6(2100)-12(2000)+4(1010)+8(1000)-6(0210)-12(0200)-18(0100)+8(0010)+16(0000)",i.toString());
    }
    
    /**
     * Test of removeCrossings method, of class scio.planar.TemperleyLiebAlgebra.
     */
    public void testRemoveCrossings() {
        System.out.println("removeCrossings");
        a0.removeCrossings();
        assertEquals("a0","+(10)+(00)",a0.toString());
        TemperleyLiebElement t=new TemperleyLiebElement(2);
        TemperleyLiebTerm tt=new TemperleyLiebTerm(2);tt.clear();tt.addEdge(1,3);tt.addEdge(2,4);
        t.appendTerm(2,tt);
        t.removeCrossings();
        assertEquals("t","+2(10)-2(00)",t.toString());
        a4.removeCrossings();
        assertEquals("a4","+2.5(210)-1.5(200)-2(010)-2(000)",a4.toString());
    }
    
    /**
     * Test of getAntiSymmetrizer method, of class scio.planar.TemperleyLiebAlgebra.
     */
    public void testGetAntiSymmetrizer() {
        System.out.println("getAntiSymmetrizer: NOTE--> WILL NOT WORK YET");
//        TemperleyLiebElement i=TemperleyLiebElement.getSymmetrizer(1);
//        assertEquals("1c","+(0)",i.toString());
//        i.removeCrossings();
//        assertEquals("1u","+(0)",i.toString());
//        
//        i=TemperleyLiebElement.getSymmetrizer(2);
//        assertEquals("2c","+0.5{(1-4)(2-3)}-0.5{(1-3)(2-4)}",i.toString());
//        i.removeCrossings();
//        assertEquals("2u","+0.5(00)",i.toString());
//        
//        i=TemperleyLiebElement.getSymmetrizer(4);
//        assertEquals("4c","??",i.toString());
//        i.removeCrossings();
//        assertEquals("4u","??",i.toString());
    }
    
    /**
     * Test of factorial method, of class scio.planar.TemperleyLiebAlgebra.
     */
    public void testFactorial() {
        System.out.println("factorial");
        assertEquals(720,TemperleyLiebElement.factorial(6));
    }
}
