/*
 * TemperleyLiebElementTest.java
 * JUnit based test
 *
 * Created on May 22, 2007, 2:39 PM
 */

package scio.algebra.planar;

import scio.algebra.planar.TemperleyLiebTerm;
import junit.framework.*;
import scio.graph.Graph;
import scio.algebra.permutation.Permutation;

/**
 *
 * @author ae3263
 */
public class TemperleyLiebTermTest extends TestCase {
    
    public static Test suite(){return new TestSuite(TemperleyLiebTermTest.class);}
        
    TemperleyLiebTerm instance1,instance2,instance3,instance4,instance5,instance6,instance7,instance8,instance9;
 
    public TemperleyLiebTermTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
        int[][] i1={{-1,-1},{-1,-1},{1,10},{2,3},{4,6},{7,5},{8,9}};
        instance1=new TemperleyLiebTerm(i1,5);
        // Basis Element: ( () ) ( () ) () = 10100
        int[][] i2={{1,4},{2,3},{5,8},{10,9},{7,6}};
        instance2=new TemperleyLiebTerm(i2,5);
        int[][] i3={{1,6},{2,4},{3,5}};
        instance3=new TemperleyLiebTerm(i3,3);
        int[][] i4={{1,3},{2,5},{4,7},{6,8}};
        instance4=new TemperleyLiebTerm(i4,4);
        // Basis Element: ( ( () ) ) ( () ) = 21010
        int[][] i5={{1,6},{2,5},{3,4},{7,10},{8,9}};
        instance5=new TemperleyLiebTerm(i5,5);
        // First Basis Element ( ( ( ( () ) ) ) ) = 43210
        instance6=new TemperleyLiebTerm(5);
        
        int[] i7={6,3,1,0,0,1,0,1,0};
        instance7=new TemperleyLiebTerm(i7);
        
        int[] i8={2,1,0};
        instance8=new TemperleyLiebTerm(i8);
        
        int[] i9={2,0,0,1,0,0};
        instance9=new TemperleyLiebTerm(i9);
    }
    
    protected void tearDown() throws Exception {
    }
    
    /**
     * Test of initId method
     */
    public void testInitId(){
        System.out.println("initId");
        assertEquals("43210",instance6.toParenString());
    }
    
    /**
     * Test of initLUL method
     */
    public void testInitLUL(){
        System.out.println("initLUL");
        TemperleyLiebTerm x=new TemperleyLiebTerm();
        x.initLUL(2,3,3);
        assertEquals("233","{(1-8)(2-3)(4-7)(5-6)} in TL(5,3)",x.toLongString());   
    }
    
    /** test of flipVertical method */
    public void testFlipVertical(){
        System.out.println("flipVertical");
        TemperleyLiebTerm x=new TemperleyLiebTerm();
        x.initLUL(2,3,3);
        x.flipVertical();
        assertEquals("233","{(1-8)(2-5)(3-4)(6-7)} in TL(3,5)",x.toLongString());   
    }
    
    /**
     * Test of setTo method, of class scio.planar.TemperleyLiebElement.
     */
    public void testSetTo() {
        System.out.println("setTo");
        TemperleyLiebTerm instance3=new TemperleyLiebTerm();
        instance3.setTo("{  (2)(1-10)( 2-3)( 4- 6)(5-7  )(8-9) } ");
        assertEquals("{(2)(1-10)(2-3)(4-6)(5-7)(8-9)} in TL(5,5)",instance3.toLongString());
    }
    
    /**
     * Test of toString method, of class scio.planar.TemperleyLiebElement.
     */
    public void testToString() {
        System.out.println("toString/toLongString");
        assertEquals("{(2)(1-10)(2-3)(4-6)(5-7)(8-9)}",instance1.toString());
        assertEquals("{(1-4)(2-3)(5-8)(6-7)(9-10)} in TL(5,5)",instance2.toLongString());
    }
    
    /** Test of toLoopStrings method */
    public void testToLoopStrings(){
        System.out.println("toLoopStrings");
        int[] a={3,2,5};
        assertEquals("1","[a, a_a_, bb]",instance1.toLoopStrings(a).toString());
        assertEquals("6","[a, a, a, b, b]",instance6.toLoopStrings(a).toString());
        assertEquals("7","[ab_b_, a_c_c_a_, c_c_]",instance7.toLoopStrings(a).toString());
    }
    
    
    /** Test of toPolynomial method */
    public void testToPolynomial(){
        System.out.println("toPolynomial");
        int[] a={3,2,5};
        assertEquals("6","+x^3 y^2",instance6.toPolynomial(a).toString());
        assertEquals("7","+4x",instance7.toPolynomial(a).toString());
        
        int[] b={3,1};
        int[] c={2,2};
        
        int[] ip1={1,0,1,0};
        TemperleyLiebTerm p1=new TemperleyLiebTerm(ip1);
        assertEquals("ip1","+2z",p1.toPolynomial(b).toString());
        assertEquals("ip1","+z^2",p1.toPolynomial(c).toString());
        
        int[] ip2={3,1,0,0};
        TemperleyLiebTerm p2=new TemperleyLiebTerm(ip2);
        assertEquals("ip2","-x y",p2.toPolynomial(b).toString());
        assertEquals("ip2","-x^2",p2.toPolynomial(c).toString());
        
        int[] ip3={3,0,0,0};
        TemperleyLiebTerm p3=new TemperleyLiebTerm(ip3);
        assertEquals("ip2","+2x y",p3.toPolynomial(b).toString());
        assertEquals("ip2","+x y z",p3.toPolynomial(c).toString());
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
        assertEquals("1/1","{(5)(1-10)(2-3)(4-7)(5-6)(8-9)} in TL(5,5)",instance1.actLeft(instance1).toLongString());
        assertEquals("1/2","{(2)(1-4)(2-3)(5-10)(6-7)(8-9)} in TL(5,5)",instance1.actLeft(instance2).toLongString());
        assertEquals("2/1","{(3)(1-5)(2-3)(4-8)(6-7)(9-10)} in TL(5,5)",instance2.actLeft(instance1).toLongString());
        assertEquals("2/2","{(1-4)(2-3)(5-8)(6-7)(9-10)} in TL(5,5)",instance2.actLeft(instance2).toLongString());
        assertEquals("1/2/1","{(5)(1-5)(2-3)(4-10)(6-7)(8-9)} in TL(5,5)",instance1.actLeft(instance2).actLeft(instance1).toLongString());
        
        System.out.println("actLeft: varying strand numbers");
        TemperleyLiebTerm i1=new TemperleyLiebTerm();
        TemperleyLiebTerm i2=new TemperleyLiebTerm();
        i1.setTo("{(2)(1-2)(3-6)(4-5)}");
        i1.initPuts(2,4);
        i2.setTo("{(1)(1-2)(3-6)(4-5)}");
        i2.initPuts(4,2);
        assertEquals("1/1",null,i1.actLeft(i1));
        assertEquals("1/2","{(3)(1-2)(3-4)(5-8)(6-7)} in TL(4,4)",i1.actLeft(i2).toLongString());
        assertEquals("2/1","-{(3)(1-2)(3-4)} in TL(2,2)",i2.actLeft(i1).toLongString());
    }
    
    /**
     * Tests concatenate method
     */
    public void testConcatenate() {
        System.out.println("concatenate");
        assertEquals("{(1-4)(2-3)(5-14)(6-11)(7-10)(8-9)(12-13)(15-16)}",TemperleyLiebTerm.concatenate(instance2,instance8).toString());
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
        assertEquals("[1, 2, 3, 8, 5]",TemperleyLiebTerm.getStrandFromAdjacency(g,g.getAllAdjacencies(),5).toString());
        assertEquals("[4, 4]",TemperleyLiebTerm.getStrandFromAdjacency(g,g.getAllAdjacencies(),4).toString());
        assertEquals("[-1]",TemperleyLiebTerm.getStrandFromAdjacency(g,g.getAllAdjacencies(),-1).toString());
    }
    
    /**
     * Test of getAllStrandsFromAdjacency method, of class scio.planar.TemperleyLiebElement.
     */
    public void testGetAllStrandsFromAdjacency() {
        System.out.println("getStrandFromAdjacency");
        Graph g=new Graph();
        g.addEdge(1,2);g.addEdge(3,8);g.addEdge(2,3);g.addEdge(5,8);
        g.addEdge(4,4);g.addEdge(-1,-1);
        assertEquals("[[5, 8, 3, 2, 1], [4, 4]]",TemperleyLiebTerm.getAllStrandsFromAdjacency(g,g.getAllAdjacencies()).toString());
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
        assertEquals(true,instance1.crossed(4,5));
        assertEquals(false,instance1.crossed(1,7));
        assertEquals(false,instance1.crossed(2,3));
        assertEquals(false,instance1.crossed(7,8));
        assertEquals(true,instance1.crossed(6,7));
    }
    
    /**
     * Test of initPuts method, of class scio.planar.TemperleyLiebElement.
     */
    public void testInitPuts() {
        System.out.println("initPuts");
        TemperleyLiebTerm instance = new TemperleyLiebTerm();
        instance.initPuts(5);
        assertEquals("[1, 2, 3, 4, 5]",instance.inputs.toString());
        assertEquals("[10, 9, 8, 7, 6]",instance.outputs.toString());
    }
    
    /**
     * Test of setToParen method, of class scio.planar.TemperleyLiebElement.
     */
    public void testSetToParen() {
        System.out.println("setToParen");
        assertEquals("{(1-14)(2-9)(3-6)(4-5)(7-8)(10-13)(11-12)(15-18)(16-17)} in TL(9,9)",instance7.toLongString());
        assertEquals("{(1-6)(2-5)(3-4)} in TL(3,3)",instance8.toLongString());
        assertEquals("{(1-6)(2-3)(4-5)(7-10)(8-9)(11-12)} in TL(6,6)",instance9.toLongString());
    }
    
    /**
     * Test of toParen method, of class scio.planar.TemperleyLiebElement.
     */
    public void testToParen() {
        System.out.println("toParen");
        assertEquals("",instance1.toParenString());
        assertEquals("10100",instance2.toParenString());
        assertEquals("",instance3.toParenString());
        assertEquals("",instance4.toParenString());
        assertEquals("21010",instance5.toParenString());
        assertEquals("43210",instance6.toParenString());
        assertEquals("631001010",instance7.toParenString());
        assertEquals("210",instance8.toParenString());
        assertEquals("200100",instance9.toParenString());
    }
    public void testToParenString(){System.out.println("toParenString: see toParen test");}
    
    /**
     * Test of hasCrossings
     */
    public void testHasCrossings(){
        System.out.println("hasCrossings");
        assertEquals("1",true,instance1.hasCrossings());
        assertEquals("2",false,instance2.hasCrossings());
        assertEquals("3",true,instance3.hasCrossings());
        assertEquals("4",true,instance4.hasCrossings());
        assertEquals("5",false,instance5.hasCrossings());
        assertEquals("6",false,instance6.hasCrossings());
        assertEquals("7",false,instance7.hasCrossings());
    }
    
    /**
     * Test of isBasisElement method, of class scio.planar.TemperleyLiebElement.
     */
    public void testIsBasisElement() {
        System.out.println("isBasisElement");
        assertEquals("1",false,instance1.isBasisElement());
        assertEquals("3",false,instance3.isBasisElement());
        assertEquals("4",false,instance4.isBasisElement());
        assertEquals("5",true,instance5.isBasisElement());
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
        assertEquals("p1",true,TemperleyLiebTerm.validParen(p1,0,3));
        assertEquals("p2",true,TemperleyLiebTerm.validParen(p2,0,3));
        assertEquals("p3a",false,TemperleyLiebTerm.validParen(p3,0,3));
        assertEquals("p3b",true,TemperleyLiebTerm.validParen(p3,1,2));
        assertEquals("p4a",false,TemperleyLiebTerm.validParen(p4,0,3));
        assertEquals("p4b",true,TemperleyLiebTerm.validParen(p4,2,3));
    }
    
    /**
     * Test of getPermutationElement method, of class scio.planar.TemperleyLiebElement.
     */
    public void testGetPermutationElement() {
        int[] p2={0,1,3,4,5,2,6};
        int[] p3={0,5,3,6,4,2,1};
        Permutation instance1=new Permutation(6);
        Permutation instance2=new Permutation(p2);
        Permutation instance3=new Permutation(p3);
        assertEquals("{(1-12)(2-11)(3-10)(4-9)(5-8)(6-7)} in TL(6,6)",new TemperleyLiebTerm().setToPermutation(instance1).toLongString());
        assertEquals("{(1-12)(2-10)(3-9)(4-8)(5-11)(6-7)} in TL(6,6)",new TemperleyLiebTerm().setToPermutation(instance2).toLongString());
        assertEquals("{(1-8)(2-10)(3-7)(4-9)(5-11)(6-12)} in TL(6,6)",new TemperleyLiebTerm().setToPermutation(instance3).toLongString());
    }
    
    /**
     * Test of hasNext method, of class scio.planar.TemperleyLiebElement.
     */
    public void testHasNext(){
        System.out.println("hasNext");
        assertEquals("1",false,instance1.hasNext());
        assertEquals("2",true,instance2.hasNext());
        assertEquals("3",false,instance3.hasNext());
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
        TemperleyLiebTerm e=new TemperleyLiebTerm(3);
        while(e.hasNext()){System.out.println(e.toParenString());e=e.next();}
        System.out.println(e.toParenString());
        assertEquals("1",null,instance1.next());
        assertEquals("2","10010",instance2.next().toParenString());
        assertEquals("3",null,instance3.next());
        assertEquals("4",null,instance4.next());
        assertEquals("5","21000",instance5.next().toParenString());
        assertEquals("6","43200",instance6.next().toParenString());
        assertEquals("7","631001000",instance7.next().toParenString());
        assertEquals("8","200",instance8.next().toParenString());
        assertEquals("9","200010",instance9.next().toParenString());
    }
    public void testParenAfter(){System.out.println("parenAfter: see next");}
    
    /**
     * Test of remove method, of class scio.planar.TemperleyLiebElement.
     */
    public void testRemove(){System.out.println("remove: NO CODE!");}
}
